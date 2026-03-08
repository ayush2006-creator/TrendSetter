"""
Configuration file for transcript search and mapping system
"""
import os
from pathlib import Path

# Base directory
BASE_DIR = Path(__file__).parent

# ChromaDB configuration
CHROMA_DB_PATH = BASE_DIR / "chroma_db"

# PKL files paths
TEXT_EMBEDDINGS_PKL = BASE_DIR / "text_embeddings.pkl"
IMAGE_EMBEDDINGS_PKL = BASE_DIR / "image_embeddings.pkl"

# Search configuration
DEFAULT_TOP_K = 10  # Default number of results to return
SIMILARITY_THRESHOLD = 0.0  # Minimum similarity score threshold

# Collection names (if you have specific collection names in ChromaDB)
DEFAULT_COLLECTION_NAME = None  # Will auto-detect if None