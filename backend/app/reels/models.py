from sqlalchemy import Column, Integer, String, Text, ForeignKey
from app.db.database import Base
from sqlalchemy import Column, Integer, String, ForeignKey, Table,DateTime
from app.db.database import Base
from sqlalchemy.orm import relationship
from datetime import datetime, UTC

class Reel(Base):
    __tablename__ = "reels"
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    caption = Column(String)
    video_url = Column(String)
    created_at = Column(DateTime, default=datetime.utcnow)

    user = relationship("User")
