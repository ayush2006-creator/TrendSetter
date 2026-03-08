from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from fastapi import APIRouter, Depends, HTTPException
from app.auth.deps import get_current_user
from app.db.database import get_db
from app.drafts import models, schemas
from app.drafts.schemas import DraftUpdate
router = APIRouter(prefix="/drafts", tags=["Drafts"])
@router.post("/", response_model=schemas.DraftResponse)
def create_draft(
    data: schemas.DraftCreate,
    current_user = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    draft = models.Draft(
        user_id=current_user.id,
        content=data.content,
        # hook=data.hook,
        # script=data.script,
        # hashtags=data.hashtags,
        # references=data.references,
    )

    db.add(draft)
    db.commit()
    db.refresh(draft)
    return draft
@router.get("/", response_model=list[schemas.DraftResponse])
def list_my_drafts(
    current_user = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    return (
        db.query(models.Draft)
        .filter(models.Draft.user_id == current_user.id)
        .order_by(models.Draft.created_at.desc())
        .all()
    )
@router.get("/{draft_id}", response_model=schemas.DraftResponse)
def get_draft(
    draft_id: int,
    current_user = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    draft = (
        db.query(models.Draft)
        .filter(
            models.Draft.id == draft_id,
            models.Draft.user_id == current_user.id
        )
        .first()
    )

    if not draft:
        raise HTTPException(status_code=404, detail="Draft not found")

    return draft
@router.delete("/{draft_id}")
def delete_draft(
    draft_id: int,
    current_user = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    draft = (
        db.query(models.Draft)
        .filter(
            models.Draft.id == draft_id,
            models.Draft.user_id == current_user.id
        )
        .first()
    )

    if not draft:
        raise HTTPException(status_code=404, detail="Draft not found")

    db.delete(draft)
    db.commit()
    return {"message": "Draft deleted"}
@router.put("/{draft_id}", response_model=schemas.DraftResponse)
def update_draft(
    draft_id: int,
    data: DraftUpdate,
    current_user = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    # 1️⃣ Fetch the draft (ownership check included)
    draft = (
        db.query(models.Draft)
        .filter(
            models.Draft.id == draft_id,
            models.Draft.user_id == current_user.id
        )
        .first()
    )

    if not draft:
        raise HTTPException(status_code=404, detail="Draft not found")

    # 2️⃣ Update only provided fields
    update_data = data.dict(exclude_unset=True)

    for key, value in update_data.items():
        setattr(draft, key, value)

    # 3️⃣ Save changes
    db.commit()
    db.refresh(draft)

    return draft
