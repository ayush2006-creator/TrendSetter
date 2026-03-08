from sqlalchemy import Column, Integer, ForeignKey, DateTime, JSON
from sqlalchemy.orm import relationship
from datetime import datetime

from app.db.database import Base

class Draft(Base):
    __tablename__ = "drafts"

    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
 # Full AI / idea response stored as-is
    content = Column(JSON, nullable=False)
    # hook = Column(JSON)
    # script = Column(JSON)
    # hashtags = Column(JSON)
    # references = Column(JSON)

    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    user = relationship("User")
