from fastapi import Depends, HTTPException, status,Header
from sqlalchemy.orm import Session
from app.db.database import get_db,SessionLocal
from app.auth.utils import decode_token
from app.auth import models
from typing import Optional

def get_current_user(
    #token: str = Depends(oauth2_scheme),
    authorization: Optional[str]  = Header(default=None, alias="Authorization"),
    db: Session = Depends(get_db)
):
    if authorization is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Authorization header missing"
        )
    if not authorization.startswith("Bearer "):   # ✅ validate format first
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid token format. Expected: Bearer <token>"
        )

    token = authorization.split(" ")[1]
    payload = decode_token(token)
    if payload is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or expired token",
        )

    user_id = payload.get("user_id")
    if user_id is None:
        raise HTTPException(status_code=401, detail="Invalid token payload")

    user = db.query(models.User).filter(models.User.id == user_id).first()
    if user is None:
        raise HTTPException(status_code=404, detail="User not found")

    return user
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()