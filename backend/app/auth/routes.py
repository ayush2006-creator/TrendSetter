from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from fastapi.security import OAuth2PasswordRequestForm  
from app.db.database import SessionLocal, engine
from app.auth import models, schemas, utils

models.Base.metadata.create_all(bind=engine)

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


@router.post("/signup", response_model=schemas.SignupResponse)
def signup(data: schemas.SignupRequest, db: Session = Depends(get_db)):
    existing_user = db.query(models.User).filter(models.User.email == data.email).first()
    if existing_user:
        raise HTTPException(status_code=400, detail="Email already registered")

    user = models.User(
        name=data.name,
        email=data.email,
        password_hash=utils.hash_password(data.password),
    )

    db.add(user)
    db.commit()
    db.refresh(user)

    token = utils.create_access_token({"user_id": user.id})

    return {
        "message": "User registered successfully",
        "user_id": f"USR{user.id}",
        "token": token
    }


# @router.post("/login", response_model=schemas.LoginResponse)
# def login(data: schemas.LoginRequest, db: Session = Depends(get_db)):
#     user = db.query(models.User).filter(models.User.email == data.email).first()

#     if not user or not utils.verify_password(data.password, user.password_hash):
#         raise HTTPException(status_code=401, detail="Invalid credentials")

#     token = utils.create_access_token({"user_id": user.id})

#     return {
#         "token": token,
#         "user": {
#             "id": f"USR{user.id}",
#             "name": user.name
#         }
#     }
@router.post("/login")
def login(
    data: schemas.LoginRequest,  # JSON body for frontend
    db: Session = Depends(get_db)
):
    user = db.query(models.User).filter(models.User.email == data.email).first()

    if not user or not utils.verify_password(data.password, user.password_hash):
        raise HTTPException(status_code=401, detail="Invalid credentials")

    token = utils.create_access_token({"user_id": user.id})

    return {
        "access_token": token,        # ✅ Swagger reads this
        "token_type": "bearer",       # ✅ Swagger reads this
        "token": token,               # ✅ your frontend reads this
        "user": {
            "id": f"USR{user.id}",
            "name": user.name
        }
    }

