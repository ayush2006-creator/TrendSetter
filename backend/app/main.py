from fastapi import FastAPI
from app.auth.routes import router as auth_router
from app.profiles.routes import router as profile_router
from app.reels.routes import router as reels_router
from app.drafts.routes import router as drafts_router
from app.db.database import engine
from app.trends.routes import router as trends_router
from app.trends.models import TrendingKeyword
from app.db.database import Base, engine
from app.transcript.routes import router as transcript_router
from dotenv import load_dotenv
load_dotenv()

Base.metadata.create_all(bind=engine)
TrendingKeyword.metadata.create_all(bind=engine)
app = FastAPI(
    title="ReelForge API",
    version="v1"
)
@app.get("/")
def root():
    return {"message": "API running"}
app.include_router(
    auth_router,
    prefix="/auth",
    tags=["Auth"]
)
app.include_router(
    profile_router,
  
    tags=["Profile"]
)
app.include_router(
    reels_router,
  
    tags=["Reels"]
)
app.include_router(
    drafts_router,
  
    tags=["Drafts"]
)
app.include_router(
     trends_router,
    tags=["Trends"]
)
app.include_router(transcript_router, tags=["Transcript"])
@app.on_event("startup")
def on_startup():
    Base.metadata.create_all(bind=engine)