from pydantic import BaseModel
from typing import List, Optional,Any
from datetime import datetime
from typing import List, Optional

class DraftUpdate(BaseModel):
    content: Optional[dict] = None
    # hook: Optional[str] = None
    # script: Optional[List[str]] = None
    # hashtags: Optional[List[str]] = None
    # references: Optional[List[str]] = None

class DraftCreate(BaseModel):
    content:dict
    # hook: str
    # script: List[str]
    # hashtags: List[str]
    # references: Optional[List[str]] = []

class DraftResponse(DraftCreate):
    id: int
    content: dict
    created_at: datetime
    updated_at: datetime

    class Config:
        orm_mode = True
