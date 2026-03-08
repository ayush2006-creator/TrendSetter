"""
PKL Loader for vectorized data (text and image embeddings)
"""
import pickle
from pathlib import Path
from typing import Dict, List, Optional, Any
import numpy as np
from app.transcript import config


class PKLLoader:
    """Manages loading and accessing PKL files with vectorized data"""
    
    def __init__(self):
        """Initialize PKL loader and load data"""
        self.text_data = None
        self.image_data = None
        self._load_all()
    
    def _load_all(self):
        """Load all PKL files"""
        self._load_text_embeddings()
        self._load_image_embeddings()
    
    def _load_text_embeddings(self):
        """Load text embeddings from PKL file"""
        try:
            with open(config.TEXT_EMBEDDINGS_PKL, 'rb') as f:
                self.text_data = pickle.load(f)
            print(f"Loaded text embeddings: {len(self.text_data.get('ids', []))} items")
        except FileNotFoundError:
            print(f"Warning: {config.TEXT_EMBEDDINGS_PKL} not found")
            self.text_data = None
        except Exception as e:
            print(f"Error loading text embeddings: {e}")
            self.text_data = None
    
    def _load_image_embeddings(self):
        """Load image embeddings from PKL file"""
        try:
            with open(config.IMAGE_EMBEDDINGS_PKL, 'rb') as f:
                self.image_data = pickle.load(f)
            print(f"Loaded image embeddings: {len(self.image_data.get('ids', []))} items")
        except FileNotFoundError:
            print(f"Warning: {config.IMAGE_EMBEDDINGS_PKL} not found")
            self.image_data = None
        except Exception as e:
            print(f"Error loading image embeddings: {e}")
            self.image_data = None
    
    def get_text_data(self) -> Optional[Dict[str, Any]]:
        """Get text embeddings data"""
        return self.text_data
    
    def get_image_data(self) -> Optional[Dict[str, Any]]:
        """Get image embeddings data"""
        return self.image_data
    
    def get_text_by_ids(self, ids: List[str]) -> Dict[str, Any]:
        """
        Get text embeddings data for specific IDs
        
        Args:
            ids: List of IDs to retrieve
        
        Returns:
            Dictionary with filtered data for the given IDs
        """
        if not self.text_data:
            return {"ids": [], "embeddings": [], "metadatas": [], "documents": []}
        
        indices = []
        for i, doc_id in enumerate(self.text_data.get('ids', [])):
            if doc_id in ids:
                indices.append(i)
        
        result = {
            "ids": [self.text_data['ids'][i] for i in indices],
            "embeddings": [self.text_data['embeddings'][i] for i in indices] if 'embeddings' in self.text_data else [],
            "metadatas": [self.text_data['metadatas'][i] for i in indices] if 'metadatas' in self.text_data else [],
            "documents": [self.text_data['documents'][i] for i in indices] if 'documents' in self.text_data else []
        }
        
        return result
    
    def get_image_by_ids(self, ids: List[str]) -> Dict[str, Any]:
        """
        Get image embeddings data for specific IDs
        
        Args:
            ids: List of IDs to retrieve
        
        Returns:
            Dictionary with filtered data for the given IDs
        """
        if not self.image_data:
            return {"ids": [], "embeddings": [], "metadatas": [], "documents": []}
        
        indices = []
        for i, doc_id in enumerate(self.image_data.get('ids', [])):
            if doc_id in ids:
                indices.append(i)
        
        result = {
            "ids": [self.image_data['ids'][i] for i in indices],
            "embeddings": [self.image_data['embeddings'][i] for i in indices] if 'embeddings' in self.image_data else [],
            "metadatas": [self.image_data['metadatas'][i] for i in indices] if 'metadatas' in self.image_data else [],
            "documents": [self.image_data['documents'][i] for i in indices] if 'documents' in self.image_data else []
        }
        
        return result
    
    def get_text_id_to_index(self) -> Dict[str, int]:
        """Create mapping from text ID to index"""
        if not self.text_data:
            return {}
        return {doc_id: i for i, doc_id in enumerate(self.text_data.get('ids', []))}
    
    def get_image_id_to_index(self) -> Dict[str, int]:
        """Create mapping from image ID to index"""
        if not self.image_data:
            return {}
        return {doc_id: i for i, doc_id in enumerate(self.image_data.get('ids', []))}
    
    def get_all_text_ids(self) -> List[str]:
        """Get all text embedding IDs"""
        if not self.text_data:
            return []
        return self.text_data.get('ids', [])
    
    def get_all_image_ids(self) -> List[str]:
        """Get all image embedding IDs"""
        if not self.image_data:
            return []
        return self.image_data.get('ids', [])