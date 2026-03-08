from pydantic import BaseModel,field_validator
from typing import Optional, List

class ProfileCreateUpdate(BaseModel):
    display_name: str
    creator_type: str
    organization_type: Optional[str] = None
    experience_level: Optional[str] = None
    platform:str
    audience_type:str
    goals: Optional[str] = None
    niches: List[str] = []

class ProfileResponse(BaseModel):
    model_config = {"from_attributes": True}  
    display_name: str
    creator_type: str
    organization_type: Optional[str]
    experience_level: Optional[str]
    platform:str
    audience_type:str
    goals: Optional[str]
    niches: List[str]

    @field_validator("niches", mode="before")
    @classmethod
    def extract_niche_names(cls, v):
        return [niche.name if hasattr(niche, "name") else niche for niche in v]