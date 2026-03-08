from sqlalchemy.orm import Session
from app.trends.models import TrendingKeyword
from app.services.trends import fetch_google_trending_keywords
from pytrends.exceptions import TooManyRequestsError
import requests
def fetch_and_store_trends(
    db: Session,
    niche: str,
    region: str = "IN"
)->int:
   # trends = fetch_google_trending_keywords(niche, region)
    try:
        trends = fetch_google_trending_keywords(
            keyword=niche,
            region=region
        )
    except TooManyRequestsError:
        raise RuntimeError("Google Trends rate-limited the request. Try later.")
    except requests.exceptions.RequestException:
        raise RuntimeError("Network error while contacting Google Trends.")

    if not trends:
        return 0
    # Optional: clear old trends for this niche
    db.query(TrendingKeyword).filter(
        TrendingKeyword.niche == niche,
        TrendingKeyword.region == region
    ).delete()

    for item in trends:
        row = TrendingKeyword(
            niche=niche,
            keyword=item["keyword"],
            score=item["score"],
            region=region
        )
        db.add(row)

    db.commit()
    return len(trends)