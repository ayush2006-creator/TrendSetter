import subprocess
import json
import time
import random
import boto3
from pathlib import Path
from botocore.exceptions import BotoCoreError, ClientError

# ── CONFIG ──────────────────────────────────────────────────────────────────
VIDEO_DIR   = Path("videos_raw")
VIDEO_DIR.mkdir(exist_ok=True)

S3_BUCKET  = "hackathon-reels-ayush-2026"
S3_PREFIX  = "reels/"
AWS_REGION = "us-east-1"

DATA_JSON = "/Users/pixelforge/Desktop/reels/processed_with_s3.json"

# Retry / pacing
MAX_RETRIES          = 3
RETRY_DELAY          = 5    # seconds between retries
DOWNLOAD_TIMEOUT     = 600  # per yt-dlp call (10 min handles large re els)
REEL_DELAY_MIN       = 4    # min jitter between reels (seconds)
REEL_DELAY_MAX       = 12   # max jitter between reels (seconds)
BLOCK_BACKOFF        = 60   # sleep on 429 / block detection
CONSEC_FAIL_LIMIT    = 5    # consecutive failures before cooldown
CONSEC_FAIL_COOLDOWN = 120  # cooldown duration (seconds)
# ────────────────────────────────────────────────────────────────────────────

# ── User-agent pool (3 mobile, 2 desktop) ────────────────────────────────────
USER_AGENTS = [
    # Android Chrome
    "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36",
    # iPhone Safari
    "Mozilla/5.0 (iPhone; CPU iPhone OS 16_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.4 Mobile/15E148 Safari/604.1",
    # Samsung Internet
    "Mozilla/5.0 (Linux; Android 12; SM-G991B) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/21.0 Chrome/110.0.0.0 Mobile Safari/537.36",
    # Windows Chrome
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36",
    # macOS Firefox
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 14.4; rv:125.0) Gecko/20100101 Firefox/125.0",
]

s3 = boto3.client("s3", region_name=AWS_REGION)

with open(DATA_JSON, "r") as f:
    reels = json.load(f)

print(f"[pipeline] {len(reels)} reels to process\n")

downloaded = failed_dl = uploaded = failed_ul = skipped = 0
consecutive_failures = 0


# ── Helpers ──────────────────────────────────────────────────────────────────

def with_retry(fn, retries=MAX_RETRIES, delay=RETRY_DELAY):
    for attempt in range(1, retries + 1):
        if fn():
            return True
        if attempt < retries:
            print(f"  ↳ Retry {attempt}/{retries - 1} in {delay}s...")
            time.sleep(delay)
    return False


def generate_presigned_url(s3_key: str, expires_in: int = 604800) -> str:
    return s3.generate_presigned_url(
        "get_object",
        Params={"Bucket": S3_BUCKET, "Key": s3_key},
        ExpiresIn=expires_in,
    )


def upload_to_s3(local_path: Path, s3_key: str) -> bool:
    try:
        s3.upload_file(
            str(local_path),
            S3_BUCKET,
            s3_key,
            ExtraArgs={"ContentType": "video/mp4"},
        )
        return True
    except (BotoCoreError, ClientError) as e:
        print(f"  S3 error: {e}")
        return False


def save_progress():
    with open(DATA_JSON, "w") as f:
        json.dump(reels, f, indent=2)


def reel_jitter():
    """Random sleep between reels to avoid bot-pattern detection."""
    delay = random.uniform(REEL_DELAY_MIN, REEL_DELAY_MAX)
    print(f"  ⏱  Waiting {delay:.1f}s before next reel...")
    time.sleep(delay)


# ── Main loop ────────────────────────────────────────────────────────────────

for i, reel in enumerate(reels):
    short_code = reel.get("shortCode", str(reel.get("id", "")))
    if not short_code:
        skipped += 1
        continue

    raw_path = VIDEO_DIR / f"{short_code}.mp4"
    s3_key   = f"{S3_PREFIX}{short_code}.mp4"

    print(f"[{i+1}/{len(reels)}] {short_code}")

    # ── Consecutive failure cooldown ─────────────────────────────────────────
    if consecutive_failures >= CONSEC_FAIL_LIMIT:
        print(f"\n  🔴 {consecutive_failures} consecutive failures — cooling down for {CONSEC_FAIL_COOLDOWN}s...\n")
        time.sleep(CONSEC_FAIL_COOLDOWN)
        consecutive_failures = 0

    # ── 1. Download ───────────────────────────────────────────────────────────
    if raw_path.exists():
        print("  ✓ Already on disk — skipping download")
        consecutive_failures = 0  # disk hit resets failure streak
    else:
        ua = random.choice(USER_AGENTS)

        def do_download():
            url = f"https://www.instagram.com/reel/{short_code}/"
            cmd = [
                "yt-dlp",
                "--user-agent", ua,
                "--sleep-interval", "3",
                "--max-sleep-interval", "8",
                "--retries", "5",
                "-o", str(raw_path),
                url,
            ]
            proc = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
            try:
                _, stderr = proc.communicate(timeout=DOWNLOAD_TIMEOUT)
            except subprocess.TimeoutExpired:
                proc.kill()
                proc.communicate()  # drain pipes
                # clean up partial file so it won't be reused as "already on disk"
                if raw_path.exists():
                    raw_path.unlink()
                print(f"  ⚠  Timed out after {DOWNLOAD_TIMEOUT}s — killed")
                return False
            if proc.returncode != 0:
                stderr = stderr.strip()
                print(f"  yt-dlp: {stderr[-300:]}")
                if any(kw in stderr.lower() for kw in
                       ["429", "rate", "login", "checkpoint", "blocked", "private"]):
                    print(f"  ⚠  Block/rate-limit detected — sleeping {BLOCK_BACKOFF}s")
                    time.sleep(BLOCK_BACKOFF)
                return False
            return True

        print(f"  Downloading... (UA: ...{ua[-40:]})", end=" ", flush=True)
        if with_retry(do_download):
            downloaded += 1
            consecutive_failures = 0
            size_mb = raw_path.stat().st_size / 1_048_576
            print(f"✓  {size_mb:.1f} MB")
        else:
            failed_dl += 1
            consecutive_failures += 1
            print("✗ Download failed — skipping")
            reel_jitter()
            continue

    # ── 2. Upload to S3 (skip if already there) ──────────────────────────────
    try:
        s3.head_object(Bucket=S3_BUCKET, Key=s3_key)
        print("  ✓ Already in S3 — refreshing presigned URL")
        reel["videoUrl"] = generate_presigned_url(s3_key)
        uploaded += 1
        save_progress()
        # already done — no delay needed
    except ClientError:
        size_mb = raw_path.stat().st_size / 1_048_576
        print(f"  Uploading {size_mb:.1f} MB...", end=" ", flush=True)
        if with_retry(lambda: upload_to_s3(raw_path, s3_key)):
            uploaded += 1
            reel["videoUrl"] = generate_presigned_url(s3_key)
            print("✓")
            save_progress()
        else:
            failed_ul += 1
            print("✗ Upload failed")
        reel_jitter()  # only delay when we actually hit Instagram

print("\n──────────── SUMMARY ────────────")
print(f"  Downloaded  : {downloaded}")
print(f"  Uploaded    : {uploaded}")
print(f"  Failed DL   : {failed_dl}")
print(f"  Failed UL   : {failed_ul}")
print(f"  Skipped     : {skipped}")
print(f"\n  Saved → {DATA_JSON}")