from sqlalchemy import Column, Integer, String, Text, ForeignKey
from app.db.database import Base
from sqlalchemy import Column, Integer, String, ForeignKey, Table
from app.db.database import Base
from sqlalchemy.orm import relationship
profile_niches = Table(
    "profile_niches",
    Base.metadata,
    Column("profile_id", ForeignKey("profiles.id")),
    Column("niche_id", ForeignKey("niches.id"))
)

class Niche(Base):
    __tablename__ = "niches"

    id = Column(Integer, primary_key=True)
    name = Column(String, unique=True, nullable=False)

class Profile(Base):
    __tablename__ = "profiles"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), unique=True)

    display_name = Column(String, nullable=False)

    creator_type = Column(String)         # individual / college_society / startup
    organization_type = Column(String)    # college_club / fest / brand
    experience_level = Column(String) 
    audience_type=Column(String)
    platform=Column(String)    # beginner / intermediate / advanced
    user = relationship("User", back_populates="profile")
    goals = Column(Text, nullable=True)
    niches = relationship(
        "Niche",
        secondary=profile_niches,
        backref="profiles"
    )
