from sqlalchemy import Column, Integer, String, DateTime
from sqlalchemy.sql import func
from app.db.database import Base

class TrendingKeyword(Base):
    __tablename__ = "trending_keywords"

    id = Column(Integer, primary_key=True, index=True)
    niche = Column(String, index=True)
    keyword = Column(String, index=True)
    score = Column(Integer)
    region = Column(String, default="IN")
    fetched_at = Column(DateTime(timezone=True), server_default=func.now())