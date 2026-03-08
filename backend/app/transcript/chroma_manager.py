"""
ChromaDB Manager for loading and searching transcript embeddings
"""
import chromadb
from chromadb.config import Settings
from pathlib import Path
from typing import List, Dict, Optional, Any
from app.transcript import config


class ChromaManager:
    """Manages ChromaDB operations for transcript embeddings"""
    
    def __init__(self, db_path: Optional[Path] = None):
        """
        Initialize ChromaDB client
        
        Args:
            db_path: Path to ChromaDB directory. If None, uses config default.
        """
        self.db_path = db_path or config.CHROMA_DB_PATH
        self.client = chromadb.PersistentClient(
            path=str(self.db_path),
            settings=Settings(anonymized_telemetry=False)
        )
        self.collections = {}
        self._load_collections()
    
    def _load_collections(self):
        """Load all available collections from ChromaDB"""
        try:
            collections = self.client.list_collections()
            for collection in collections:
                self.collections[collection.name] = collection
            print(f"Loaded {len(self.collections)} collection(s) from ChromaDB")
        except Exception as e:
            print(f"Warning: Could not load collections: {e}")
    
    def get_collection(self, collection_name: Optional[str] = None):
        """
        Get a specific collection or the first available collection
        
        Args:
            collection_name: Name of the collection. If None, returns first available.
        
        Returns:
            ChromaDB collection object
        """
        if collection_name:
            if collection_name in self.collections:
                return self.collections[collection_name]
            else:
                return self.client.get_collection(name=collection_name)
        else:
            # Prefer reel_text collection if available (works with text queries)
            if 'reel_text' in self.collections:
                return self.collections['reel_text']
            # Otherwise return first available collection
            elif self.collections:
                return list(self.collections.values())[0]
            else:
                # Try to get or create default collection
                return self.client.get_or_create_collection(name="transcripts")
    
    def search(
        self,
        query_text: str,
        query_embeddings: Optional[List[List[float]]] = None,
        collection_name: Optional[str] = None,
        top_k: int = config.DEFAULT_TOP_K,
        filter_dict: Optional[Dict[str, Any]] = None
    ) -> Dict[str, Any]:
        """
        Search for similar transcripts in ChromaDB
        
        Args:
            query_text: Text query string
            query_embeddings: Optional pre-computed embeddings for the query
            collection_name: Name of collection to search. If None, uses default.
            top_k: Number of results to return
            filter_dict: Optional metadata filters
        
        Returns:
            Dictionary with search results containing ids, distances, metadatas, documents
        """
        collection = self.get_collection(collection_name)
        
        if query_embeddings:
            # Use provided embeddings
            results = collection.query(
                query_embeddings=query_embeddings,
                n_results=top_k,
                where=filter_dict
            )
        else:
            # Use query text (ChromaDB will compute embeddings)
            results = collection.query(
                query_texts=[query_text],
                n_results=top_k,
                where=filter_dict
            )
        
        return results
    
    def get_by_ids(
        self,
        ids: List[str],
        collection_name: Optional[str] = None
    ) -> Dict[str, Any]:
        """
        Retrieve specific documents by their IDs
        
        Args:
            ids: List of document IDs to retrieve
            collection_name: Name of collection. If None, uses default.
        
        Returns:
            Dictionary with ids, embeddings, metadatas, documents
        """
        collection = self.get_collection(collection_name)
        return collection.get(ids=ids)
    
    def get_all_collections(self) -> List[str]:
        """Get list of all collection names"""
        return list(self.collections.keys())
    
    def get_collection_info(self, collection_name: Optional[str] = None) -> Dict[str, Any]:
        """
        Get information about a collection
        
        Args:
            collection_name: Name of collection. If None, uses default.
        
        Returns:
            Dictionary with collection metadata
        """
        collection = self.get_collection(collection_name)
        count = collection.count()
        return {
            "name": collection.name,
            "count": count,
            "metadata": collection.metadata
        }