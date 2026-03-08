"""
Run this once to populate ChromaDB from processed.json
Usage: python ingest_reels.py
"""
import json
import sys
import numpy as np
from pathlib import Path
from sentence_transformers import SentenceTransformer
import chromadb

# ── Paths (adjust if needed) ──────────────────────────────────────────────────
BASE_DIR             = Path("app/transcript")
CHROMA_DB_PATH       = str(BASE_DIR / "chroma_db")
PROCESSED_JSON_PATH  = str(BASE_DIR / "processed.json")

# ── Load data ─────────────────────────────────────────────────────────────────
print("[ingest] Loading processed.json...")
with open(PROCESSED_JSON_PATH, encoding="utf-8") as f:
    reels = json.load(f)
print(f"[ingest] Loaded {len(reels)} reels")
print(f"[ingest] Sample keys: {list(reels[0].keys())}")

# ── Load model ────────────────────────────────────────────────────────────────
print("[ingest] Loading text model...")
text_model = SentenceTransformer("all-MiniLM-L6-v2")
print("[ingest] Model ready!")

# ── Connect to ChromaDB ───────────────────────────────────────────────────────
client = chromadb.PersistentClient(path=CHROMA_DB_PATH)
text_collection = client.get_or_create_collection(
    name="reel_text", metadata={"hnsw:space": "cosine"}
)
print(f"[ingest] Current reel_text count: {text_collection.count()}")

# ── Ingest ────────────────────────────────────────────────────────────────────
BATCH_SIZE = 100
texts, ids, metas = [], [], []
skipped = 0

# NOTE: Update these field names based on your actual JSON keys printed above
for reel in reels:
    rid = str(reel.get("id", ""))
    if not rid:
        skipped += 1
        continue

    caption    = str(reel.get("caption", "") or "")
    transcript = str(reel.get("transcript", "") or reel.get("text", "") or "")
    text       = f"{caption} {transcript}".strip()

    if not text:
        skipped += 1
        continue

    texts.append(text)
    ids.append(rid)
    metas.append({
        "owner":     str(reel.get("owner", "") or reel.get("ownerUsername", "") or ""),
        "likes":     int(reel.get("likesCount", 0) or reel.get("likes", 0) or 0),
        "duration":  int(reel.get("videoDuration", 0) or reel.get("duration", 0) or 0),
        "caption":   caption[:500],
        "video_url": str(reel.get("url", "") or reel.get("videoUrl", "") or ""),
    })

    if len(texts) >= BATCH_SIZE:
        embeds = text_model.encode(texts, normalize_embeddings=True).tolist()
        text_collection.add(ids=ids, embeddings=embeds, metadatas=metas, documents=texts)
        print(f"[ingest] Batch added. Total: {text_collection.count()}")
        texts, ids, metas = [], [], []

# Final batch
if texts:
    embeds = text_model.encode(texts, normalize_embeddings=True).tolist()
    text_collection.add(ids=ids, embeddings=embeds, metadatas=metas, documents=texts)

print(f"\n[ingest] ✅ Done!")
print(f"[ingest] reel_text collection: {text_collection.count()} docs")
print(f"[ingest] Skipped: {skipped} reels (no text)")