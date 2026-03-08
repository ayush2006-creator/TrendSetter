import json
import os
import pickle
import numpy as np
import torch
import chromadb
from sentence_transformers import SentenceTransformer
from transformers import CLIPProcessor, CLIPModel
from PIL import Image
from collections import defaultdict
from typing import Optional
from pathlib import Path
from pathlib import Path
BASE_DIR = Path(__file__).parent

CHROMA_DB_PATH       = str(BASE_DIR / "chroma_db")
PROCESSED_JSON_PATH  = str(BASE_DIR / "processed.json")
TEXT_EMBEDDINGS_PKL  = str(BASE_DIR / "text_embeddings.pkl")
IMAGE_EMBEDDINGS_PKL = str(BASE_DIR / "image_embeddings.pkl")
# ─────────────────────────────────────────────────────────────────────────────
# CONFIG — update these paths if your files live elsewhere
# ─────────────────────────────────────────────────────────────────────────────

# ─────────────────────────────────────────────────────────────────────────────
# 1️⃣  Device
# ─────────────────────────────────────────────────────────────────────────────
device = "cuda" if torch.cuda.is_available() else "cpu"
print(f"[pipeline] Device: {device.upper()}")


# ─────────────────────────────────────────────────────────────────────────────
# 2️⃣  Models  (loaded once at import time)
# ─────────────────────────────────────────────────────────────────────────────
print("[pipeline] Loading models...")
text_model     = SentenceTransformer("all-MiniLM-L6-v2", device=device)
clip_model     = CLIPModel.from_pretrained("openai/clip-vit-base-patch32").to(device)
clip_processor = CLIPProcessor.from_pretrained("openai/clip-vit-base-patch32")
clip_model.eval()
print("[pipeline] Models ready!")


# ─────────────────────────────────────────────────────────────────────────────
# 3️⃣  ChromaDB collections
# ─────────────────────────────────────────────────────────────────────────────
_chroma_client   = chromadb.PersistentClient(path=CHROMA_DB_PATH)
text_collection  = _chroma_client.get_or_create_collection(
    name="reel_text",   metadata={"hnsw:space": "cosine"})
image_collection = _chroma_client.get_or_create_collection(
    name="reel_images", metadata={"hnsw:space": "cosine"})

print(f"[pipeline] Text collection : {text_collection.count()} docs")
print(f"[pipeline] Image collection: {image_collection.count()} docs")


# ─────────────────────────────────────────────────────────────────────────────
# 4️⃣  Processed reels JSON  →  fast lookup dict
# ─────────────────────────────────────────────────────────────────────────────
with open(PROCESSED_JSON_PATH, encoding="utf-8") as f:
    _reels = json.load(f)

reel_lookup: dict = {str(r["id"]): r for r in _reels}
print(f"[pipeline] Loaded {len(_reels)} reels from processed.json")


# ─────────────────────────────────────────────────────────────────────────────
# 5️⃣  PKL embeddings  (for redundancy/dedup similarity)
# ─────────────────────────────────────────────────────────────────────────────
def _load_pkl(path: str, label: str) -> dict:
    """Normalises any pkl format to {reel_id: np.ndarray}."""
    if not os.path.exists(path):
        print(f"[pipeline] ⚠️  {label} pkl not found at {path} — dedup will be skipped")
        return {}
    with open(path, "rb") as f:
        data = pickle.load(f)

    if isinstance(data, dict):
        return {str(k): np.array(v) for k, v in data.items()}

    if isinstance(data, (list, np.ndarray)):
        arr     = np.array(data)
        id_list = [str(r["id"]) for r in _reels]
        n       = min(len(arr), len(id_list))
        return {id_list[i]: arr[i] for i in range(n)}

    print(f"[pipeline] ⚠️  Unrecognised pkl format for {label}: {type(data)}")
    return {}


text_embeds_pkl  = _load_pkl(TEXT_EMBEDDINGS_PKL,  "text")
image_embeds_pkl = _load_pkl(IMAGE_EMBEDDINGS_PKL, "image")
print(f"[pipeline] Text pkl : {len(text_embeds_pkl)} entries")
print(f"[pipeline] Image pkl: {len(image_embeds_pkl)} entries")


# ─────────────────────────────────────────────────────────────────────────────
# 6️⃣  Metadata filter builder
#
#  ─────────────────────────────────────────────────────────────────────────────
def ingest_reels():
    """Populate ChromaDB from processed.json"""
    print(f"[ingest] Starting ingestion of {len(_reels)} reels...")
    
    batch_size = 100
    texts, text_ids, text_metas = [], [], []
    
    for reel in _reels:
        rid = str(reel["id"])
        caption = reel.get("caption", "") or ""
        transcript = reel.get("transcript", "") or ""
        text = f"{caption} {transcript}".strip()
        if not text:
            continue
        
        texts.append(text)
        text_ids.append(rid)
        text_metas.append({
            "owner":    str(reel.get("owner", "")),
            "likes":    int(reel.get("likesCount", 0)),
            "duration": int(reel.get("videoDuration", 0)),
            "caption":  caption[:500],
            "video_url": str(reel.get("url", "")),
        })
        
        if len(texts) >= batch_size:
            embeds = text_model.encode(texts, normalize_embeddings=True).tolist()
            text_collection.add(ids=text_ids, embeddings=embeds, metadatas=text_metas, documents=texts)
            print(f"[ingest] Added batch, total so far: {text_collection.count()}")
            texts, text_ids, text_metas = [], [], []
    
    if texts:
        embeds = text_model.encode(texts, normalize_embeddings=True).tolist()
        text_collection.add(ids=text_ids, embeddings=embeds, metadatas=text_metas, documents=texts)
    
    print(f"[ingest] Done! Text collection: {text_collection.count()} docs")

if __name__ == "__main__":
    ingest_reels()
def build_where_filter(
    min_likes:    Optional[int] = None,
    max_duration: Optional[int] = None,
    owner:        Optional[str] = None,
    source:       Optional[str] = None,
) -> Optional[dict]:
    conditions = []
    if min_likes    is not None: conditions.append({"likes":    {"$gte": min_likes}})
    if max_duration is not None: conditions.append({"duration": {"$lte": max_duration}})
    if owner        is not None: conditions.append({"owner":    {"$eq":  owner}})
    if source       is not None: conditions.append({"source":   {"$eq":  source}})

    if not conditions:       return None
    if len(conditions) == 1: return conditions[0]
    return {"$and": conditions}


# ─────────────────────────────────────────────────────────────────────────────
# 7️⃣  Core search helpers
# ─────────────────────────────────────────────────────────────────────────────
def _parse_chroma(res: dict) -> list[dict]:
    out = []
    for reel_id, dist, meta in zip(
        res["ids"][0], res["distances"][0], res["metadatas"][0]
    ):
        out.append({
            "id":       reel_id,
            "score":    round(1 - dist, 4),
            "metadata": meta,
            "reel":     reel_lookup.get(reel_id, {}),
        })
    return out


def text_search(
    query: str,
    top_k: int = 50,
    where: Optional[dict] = None,
) -> list[dict]:
    embed  = text_model.encode(query, normalize_embeddings=True).tolist()
    kwargs = dict(
        query_embeddings=[embed],
        n_results=min(top_k, text_collection.count()),
        include=["distances", "metadatas"],
    )
    if where: kwargs["where"] = where
    return _parse_chroma(text_collection.query(**kwargs))


def image_text_search(
    query: str,
    top_k: int = 50,
    where: Optional[dict] = None,
) -> list[dict]:
    if image_collection.count() == 0:
        return []
    inputs = clip_processor(text=[query], return_tensors="pt", padding=True).to(device)
    with torch.no_grad():
        feats = clip_model.get_text_features(**inputs)
        feats = feats / feats.norm(dim=-1, keepdim=True)
    embed  = feats[0].cpu().numpy().tolist()
    kwargs = dict(
        query_embeddings=[embed],
        n_results=min(top_k, image_collection.count()),
        include=["distances", "metadatas"],
    )
    if where: kwargs["where"] = where
    return _parse_chroma(image_collection.query(**kwargs))


# ─────────────────────────────────────────────────────────────────────────────
# 8️⃣  Hybrid RRF fusion
# ─────────────────────────────────────────────────────────────────────────────
def hybrid_search(
    query:       str,
    top_k:       int   = 60,
    text_weight: float = 0.6,
    where:       Optional[dict] = None,
) -> list[dict]:
    text_results  = text_search(query,       top_k=top_k, where=where)
    image_results = image_text_search(query, top_k=top_k, where=where)

    K = 60
    rrf_scores: dict[str, float] = defaultdict(float)

    for rank, r in enumerate(text_results):
        rrf_scores[r["id"]] += text_weight * (1 / (K + rank + 1))
    for rank, r in enumerate(image_results):
        rrf_scores[r["id"]] += (1 - text_weight) * (1 / (K + rank + 1))

    all_results = {r["id"]: r for r in text_results}
    for r in image_results:
        if r["id"] not in all_results:
            all_results[r["id"]] = r

    merged = []
    for reel_id, rrf_score in sorted(rrf_scores.items(), key=lambda x: -x[1]):
        entry = all_results[reel_id].copy()
        entry["rrf_score"] = round(rrf_score, 6)
        merged.append(entry)

    return merged[:top_k]


# ─────────────────────────────────────────────────────────────────────────────
# 9️⃣  Redundancy / dedup filter
# ─────────────────────────────────────────────────────────────────────────────
def deduplicate(
    results:            list[dict],
    text_sim_threshold: float = 0.92,
    same_owner_gap:     int   = 2,
) -> list[dict]:
    ids = [r["id"] for r in results]

    # Pass 1 — near-duplicate text removal via cosine sim on pkl embeddings
    embeds, valid_ids = [], []
    for rid in ids:
        if rid in text_embeds_pkl:
            embeds.append(text_embeds_pkl[rid])
            valid_ids.append(rid)

    dropped = set()
    if len(embeds) > 1:
        mat = np.stack(embeds)
        sim = mat @ mat.T
        for i in range(len(valid_ids)):
            if valid_ids[i] in dropped: continue
            for j in range(i + 1, len(valid_ids)):
                if sim[i, j] >= text_sim_threshold:
                    dropped.add(valid_ids[j])

    deduped = [r for r in results if r["id"] not in dropped]

    # Pass 2 — owner spreading
    final: list[dict]        = []
    delayed: list[dict]      = []
    owner_last: dict[str, int] = {}

    def flush_delayed():
        still = []
        for item in delayed:
            o = item["metadata"].get("owner", "")
            if len(final) - owner_last.get(o, -999) >= same_owner_gap:
                owner_last[o] = len(final)
                final.append(item)
            else:
                still.append(item)
        return still

    for r in deduped:
        o = r["metadata"].get("owner", "")
        delayed = flush_delayed()
        if len(final) - owner_last.get(o, -999) >= same_owner_gap:
            owner_last[o] = len(final)
            final.append(r)
        else:
            delayed.append(r)

    final.extend(delayed)

    print(f"[pipeline] Dedup: {len(results)} → {len(final)} "
          f"({len(dropped)} near-dupes removed)")
    return final


# ─────────────────────────────────────────────────────────────────────────────
# 🔟  Main entry point
# ─────────────────────────────────────────────────────────────────────────────
def query_reels(
    query:              str,
    top_k:              int   = 10,
    text_weight:        float = 0.6,
    min_likes:          Optional[int]   = None,
    max_duration:       Optional[int]   = None,
    owner:              Optional[str]   = None,
    source:             Optional[str]   = None,
    text_sim_threshold: float = 0.92,
    same_owner_gap:     int   = 2,
) -> list[dict]:
    """
    Full pipeline:
      1. Build metadata filter
      2. Hybrid RRF search (text + CLIP)
      3. Redundancy filter (near-dupes + owner spread)
      4. Return top_k clean results
    """
    where = build_where_filter(
        min_likes=min_likes, max_duration=max_duration,
        owner=owner, source=source,
    )

    print(f'[pipeline] Query: "{query}"' + (f" | Filter: {where}" if where else ""))

    candidates = hybrid_search(
        query, top_k=top_k * 5,
        text_weight=text_weight, where=where,
    )
    print(f"[pipeline] Hybrid candidates: {len(candidates)}")

    clean = deduplicate(
        candidates,
        text_sim_threshold=text_sim_threshold,
        same_owner_gap=same_owner_gap,
    )
    return clean[:top_k]


# ─────────────────────────────────────────────────────────────────────────────
# Quick test when run directly:  python query_pipeline.py
# ─────────────────────────────────────────────────────────────────────────────
if __name__ == "__main__":
    results = query_reels("funny student struggles", top_k=5)
    for i, r in enumerate(results, 1):
        print(f"{i}. [{r['rrf_score']}] @{r['metadata'].get('owner','')} — "
              f"{r['metadata'].get('caption','')[:80]}")