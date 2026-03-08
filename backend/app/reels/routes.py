from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.auth.deps import get_current_user
from app.db.database import SessionLocal
from app.profiles.models import Profile
from app.reels.schemas import ReelGenerateRequest, ReelGenerateResponse

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


@router.post("/reels/generate", response_model=ReelGenerateResponse)
def generate_reel(
    data: ReelGenerateRequest,
    user_id: int = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    profile = db.query(Profile).filter(Profile.user_id == user_id).first()

    if not profile:
        raise HTTPException(
            status_code=400,
            detail="Profile not completed"
        )

    # 🔹 STUB RESPONSE (AI comes later)
    return {
        "hook": f"POV: {data.context}",
        "script": [
            "This is line 1 of the reel",
            "This is line 2 of the reel",
            "This is line 3 of the reel"
        ],
        "hashtags": [f"#{n.name.replace(' ', '')}" for n in profile.niches],
        "references": []
    }
