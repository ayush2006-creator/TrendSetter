from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session
from app.db.database import get_db
from app.trends.models import TrendingKeyword
from app.trends.service import fetch_and_store_trends
router = APIRouter(prefix="/trends", tags=["Trends"])
@router.post("/fetch")
def fetch_trends(
    niche: str = Query(..., description="Base keyword, e.g. fitness, dance"),
    region: str = Query("IN"),
    db: Session = Depends(get_db)
):
    count = fetch_and_store_trends(
        db=db,
        niche=niche,
        region=region
    )

    return {
        "message": "Trends fetched and stored",
        "niche": niche,
        "region": region,
        "count": count
    }
@router.get("/keywords")
def get_trending_keywords(
    niche: str = Query(...),
    region: str = Query("IN"),
    limit: int = Query(20),
    db: Session = Depends(get_db)
):
    rows = (
        db.query(TrendingKeyword)
        .filter(
            TrendingKeyword.niche == niche,
            TrendingKeyword.region == region
        )
        .order_by(TrendingKeyword.score.desc())
        .limit(limit)
        .all()
    )

    return [
        {
            "keyword": r.keyword,
            "score": r.score,
            "fetched_at": r.fetched_at
        }
        for r in rows
    ]
# @router.get("/test-pytrends")
# def test_pytrends(niche: str = "dance"):
#     from app.services.trends import fetch_google_trending_keywords
#     return fetch_google_trending_keywords(niche)