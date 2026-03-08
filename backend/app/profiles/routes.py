from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.db.database import SessionLocal, engine
from app.profiles import models, schemas
from app.auth.deps import get_current_user
from app.profiles.models import Niche
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session, joinedload
models.Base.metadata.create_all(bind=engine)

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.get("/profile", response_model=schemas.ProfileResponse)
def get_profile(
    current_user = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    profile = db.query(models.Profile).options(
        joinedload(models.Profile.niches)
    ).filter(
        models.Profile.user_id == current_user.id
    ).first()

    if profile is None:
        raise HTTPException(status_code=404, detail="Profile not found")
    
    return profile
    # return db.query(models.Profile).filter(
    #     models.Profile.user_id ==current_user.id
    # ).first()


@router.post("/profile")
def create_or_update_profile(
    data: schemas.ProfileCreateUpdate,
    current_user = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    profile = db.query(models.Profile).filter(
        models.Profile.user_id == current_user.id
    ).first()

    if profile:
        # Update existing profile
        profile.niches.clear()  # ✅ only clear if profile exists
        for key, value in data.model_dump(exclude={"niches"}).items():
            setattr(profile, key, value)
    else:
        # Create new profile
        profile = models.Profile(
            user_id=current_user.id,
            **data.model_dump(exclude={"niches"})
        )
        db.add(profile)
        db.flush()  # ✅ ensures profile.id exists

    # Handle niches (common for both cases)
    for niche_name in data.niches:
        niche = db.query(Niche).filter(Niche.name == niche_name).first()
        if not niche:
            niche = Niche(name=niche_name)
            db.add(niche)
            db.flush()
        profile.niches.append(niche)

    db.commit()
    return {"message": "Profile saved successfully"}
