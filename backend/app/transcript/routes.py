from fastapi import APIRouter, HTTPException, Query
from pydantic import BaseModel
from typing import List, Optional, Dict, Any
import os
from dotenv import load_dotenv

#from app.transcript.rag_chain import conversational_rag

load_dotenv()

router = APIRouter(prefix="/transcript", tags=["Transcript"])

# Lazy-load so it doesn't crash on startup if PKL files are missing
_engine = None
_rag = None

def get_rag():
    global _rag
    if _rag is None:
        from app.transcript.rag_chain import run_optimized_pipeline
        _rag = run_optimized_pipeline
    return _rag


class ReelQueryRequest(BaseModel):
    query: str
    top_k: int = 10
    min_likes: Optional[int] = None
    max_duration: Optional[int] = None
    text_weight: float = 0.6


@router.post("/reels/query")
def query_reels_endpoint(request: ReelQueryRequest):
    try:
        result = get_rag()(request.query)
        answer = result.get("answer")
        sources = result.get("sources", [])
        return {
            "answer": answer.model_dump() if hasattr(answer, "model_dump") else answer,
            "sources": sources
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

def get_engine():
    global _engine
    if _engine is None:
        from app.transcript.search_engine import SearchEngine
        _engine = SearchEngine()
    return _engine


class SearchRequest(BaseModel):
    query: str
    top_k: int = 10
    collection_name: Optional[str] = None
    include_text_pkl: bool = True
    include_image_pkl: bool = True
    filter_dict: Optional[Dict[str, Any]] = None


class GetByIdsRequest(BaseModel):
    ids: List[str]
    include_text_pkl: bool = True
    include_image_pkl: bool = True
    collection_name: Optional[str] = None


@router.get("/health")
def health():
    try:
        engine = get_engine()
        return {"status": "healthy", "engine_loaded": engine is not None}
    except Exception as e:
        return {"status": "unhealthy", "error": str(e)}


@router.post("/search")
def search_post(request: SearchRequest):
    try:
        return get_engine().search(
            query=request.query,
            top_k=request.top_k,
            collection_name=request.collection_name,
            include_text_pkl=request.include_text_pkl,
            include_image_pkl=request.include_image_pkl,
            filter_dict=request.filter_dict
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/search")
def search_get(
    query: str = Query(...),
    top_k: int = Query(10),
    collection_name: Optional[str] = Query(None),
    include_text_pkl: bool = Query(True),
    include_image_pkl: bool = Query(True)
):
    try:
        return get_engine().search(
            query=query,
            top_k=top_k,
            collection_name=collection_name,
            include_text_pkl=include_text_pkl,
            include_image_pkl=include_image_pkl
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/get-by-ids")
def get_by_ids(request: GetByIdsRequest):
    try:
        return get_engine().get_by_ids(
            ids=request.ids,
            include_text_pkl=request.include_text_pkl,
            include_image_pkl=request.include_image_pkl,
            collection_name=request.collection_name
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/info")
def get_info(collection_name: Optional[str] = None):
    try:
        return get_engine().get_collection_info(collection_name)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/map-ids")
def map_ids():
    try:
        mapping = get_engine().create_id_mapping()
        return {"total_ids": len(mapping), "mapping": mapping}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))