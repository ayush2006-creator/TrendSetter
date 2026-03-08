import subprocess
import json
from pathlib import Path

VIDEO_DIR = Path('videos')
VIDEO_DIR.mkdir(exist_ok=True)

with open('app/transcript/processed.json', 'r') as f:
    reels = json.load(f)

print(f'[download] Found {len(reels)} reels')

downloaded = 0
failed = 0
skipped = 0

for i, reel in enumerate(reels):
    short_code = reel.get('shortCode', str(reel.get('id', '')))
    if not short_code:
        skipped += 1
        continue
    filepath = VIDEO_DIR / (short_code + '.mp4')
    if filepath.exists():
        skipped += 1
        continue
    try:
        print(f'[{i+1}/{len(reels)}] {short_code}...', end=' ')
        url = f'https://www.instagram.com/reel/{short_code}/'
        result = subprocess.run(
            ['yt-dlp', '-o', str(filepath), url],
            capture_output=True, text=True, timeout=120
        )
        if result.returncode == 0:
            downloaded += 1
            print('OK')
        else:
            failed += 1
            print(f'FAILED: {result.stderr.strip()[:100]}')
    except Exception as e:
        failed += 1
        print(f'FAILED: {e}')

print(f'\nDone: {downloaded} downloaded, {failed} failed, {skipped} skipped')