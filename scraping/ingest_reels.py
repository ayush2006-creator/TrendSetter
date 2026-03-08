"""
Run this once to populate ChromaDB from processed.json
Usage: python ingest_reels.py
"""
import json
import numpy as np
from pathlib import Path
from sentence_transformers import SentenceTransformer
import chromadb

BASE_DIR             = Path("app/transcript")
CHROMA_DB_PATH       = str(BASE_DIR / "chroma_db")
PROCESSED_JSON_PATH  = str(BASE_DIR / "processed.json")

print("[ingest] Loading processed.json...")
with open(PROCESSED_JSON_PATH, encoding="utf-8") as f:
    reels = json.load(f)
print(f"[ingest] Loaded {len(reels)} reels")

print("[ingest] Loading text model...")
text_model = SentenceTransformer("all-MiniLM-L6-v2")
print("[ingest] Model ready!")

client = chromadb.PersistentClient(path=CHROMA_DB_PATH)
try:
    client.delete_collection("reel_text")
    print("[ingest] Deleted old collection")
except:
    pass

text_collection = client.create_collection(
    name="reel_text", metadata={"hnsw:space": "cosine"}
)

BATCH_SIZE = 100
texts, ids, metas = [], [], []
skipped = 0

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

    short_code = reel.get("shortCode", rid)
    local_video_path = f"/static/videos/{short_code}.mp4"

    texts.append(text)
    ids.append(rid)
    metas.append({
        "owner":      str(reel.get("owner", "") or reel.get("ownerUsername", "") or ""),
        "likes":      int(reel.get("likesCount", 0) or reel.get("likes", 0) or 0),
        "duration":   int(reel.get("videoDuration", 0) or reel.get("duration", 0) or 0),
        "caption":    caption[:500],
        "video_url":  local_video_path,
        "short_code": short_code,
    })

    if len(texts) >= BATCH_SIZE:
        embeds = text_model.encode(texts, normalize_embeddings=True).tolist()
        text_collection.add(ids=ids, embeddings=embeds, metadatas=metas, documents=texts)
        print(f"[ingest] Batch added. Total: {text_collection.count()}")
        texts, ids, metas = [], [], []

if texts:
    embeds = text_model.encode(texts, normalize_embeddings=True).tolist()
    text_collection.add(ids=ids, embeddings=embeds, metadatas=metas, documents=texts)

print(f"\n[ingest] Done!")
print(f"[ingest] reel_text collection: {text_collection.count()} docs")
print(f"[ingest] Skipped: {skipped} reels (no text)")
