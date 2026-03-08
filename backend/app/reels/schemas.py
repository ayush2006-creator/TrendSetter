from pydantic import BaseModel
from typing import List

class ReelGenerateRequest(BaseModel):
    context: str
    tone: str
    style: str


class ReelGenerateResponse(BaseModel):
    hook: str
    script: List[str]
    hashtags: List[str]
    references: List[str]
