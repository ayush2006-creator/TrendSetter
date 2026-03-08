"""
Main Search Engine that combines ChromaDB and PKL data for searching and mapping
"""
from typing import Dict, List, Optional, Any
from app.transcript.chroma_manager import ChromaManager
from app.transcript.pkl_loader import PKLLoader
from app.transcript.mapper import DataMapper
from app.transcript import config


class SearchEngine:
    """Unified search engine for transcript embeddings and vectorized data"""
    
    def __init__(self):
        """Initialize search engine with ChromaDB and PKL loaders"""
        print("Initializing Search Engine...")
        self.chroma_manager = ChromaManager()
        self.pkl_loader = PKLLoader()
        self.mapper = DataMapper(self.chroma_manager, self.pkl_loader)
        print("Search Engine initialized successfully!")
    
    def search(
        self,
        query: str,
        top_k: int = config.DEFAULT_TOP_K,
        collection_name: Optional[str] = None,
        include_text_pkl: bool = True,
        include_image_pkl: bool = True,
        filter_dict: Optional[Dict[str, Any]] = None,
        unified_format: bool = True
    ) -> Dict[str, Any]:
        """
        Main search function that searches ChromaDB and maps to PKL data
        
        Args:
            query: Search query text
            top_k: Number of results to return
            collection_name: Specific collection to search (None for default)
            include_text_pkl: Whether to include text PKL data in results
            include_image_pkl: Whether to include image PKL data in results
            filter_dict: Optional metadata filters for ChromaDB
            unified_format: If True, returns unified format; if False, returns separate sections
        
        Returns:
            Dictionary with search results
        """
        # Search ChromaDB
        chroma_results = self.chroma_manager.search(
            query_text=query,
            collection_name=collection_name,
            top_k=top_k,
            filter_dict=filter_dict
        )
        
        if unified_format:
            # Return unified format
            return {
                "query": query,
                "results": self.mapper.get_unified_result(
                    chroma_results,
                    include_text=include_text_pkl,
                    include_image=include_image_pkl
                ),
                "total_results": len(chroma_results.get('ids', [[]])[0] if chroma_results.get('ids') else [])
            }
        else:
            # Return mapped results
            if include_text_pkl and include_image_pkl:
                mapped = self.mapper.map_chroma_to_both(chroma_results)
            elif include_text_pkl:
                mapped = self.mapper.map_chroma_to_text(chroma_results)
            elif include_image_pkl:
                mapped = self.mapper.map_chroma_to_image(chroma_results)
            else:
                mapped = {"chroma": chroma_results}
            
            return {
                "query": query,
                **mapped
            }
    
    def search_by_embedding(
        self,
        query_embeddings: List[List[float]],
        top_k: int = config.DEFAULT_TOP_K,
        collection_name: Optional[str] = None,
        include_text_pkl: bool = True,
        include_image_pkl: bool = True,
        filter_dict: Optional[Dict[str, Any]] = None,
        unified_format: bool = True
    ) -> Dict[str, Any]:
        """
        Search using pre-computed embeddings
        
        Args:
            query_embeddings: List of embedding vectors
            top_k: Number of results to return
            collection_name: Specific collection to search
            include_text_pkl: Whether to include text PKL data
            include_image_pkl: Whether to include image PKL data
            filter_dict: Optional metadata filters
            unified_format: If True, returns unified format
        
        Returns:
            Dictionary with search results
        """
        # Search ChromaDB with embeddings
        chroma_results = self.chroma_manager.search(
            query_embeddings=query_embeddings,
            collection_name=collection_name,
            top_k=top_k,
            filter_dict=filter_dict
        )
        
        if unified_format:
            return {
                "query": "embedding_search",
                "results": self.mapper.get_unified_result(
                    chroma_results,
                    include_text=include_text_pkl,
                    include_image=include_image_pkl
                ),
                "total_results": len(chroma_results.get('ids', [[]])[0] if chroma_results.get('ids') else [])
            }
        else:
            if include_text_pkl and include_image_pkl:
                mapped = self.mapper.map_chroma_to_both(chroma_results)
            elif include_text_pkl:
                mapped = self.mapper.map_chroma_to_text(chroma_results)
            elif include_image_pkl:
                mapped = self.mapper.map_chroma_to_image(chroma_results)
            else:
                mapped = {"chroma": chroma_results}
            
            return {
                "query": "embedding_search",
                **mapped
            }
    
    def get_by_ids(
        self,
        ids: List[str],
        include_text_pkl: bool = True,
        include_image_pkl: bool = True,
        collection_name: Optional[str] = None
    ) -> Dict[str, Any]:
        """
        Retrieve specific documents by IDs from both ChromaDB and PKL files
        
        Args:
            ids: List of document IDs
            include_text_pkl: Whether to include text PKL data
            include_image_pkl: Whether to include image PKL data
            collection_name: Specific collection to query
        
        Returns:
            Dictionary with retrieved data
        """
        # Get from ChromaDB
        chroma_data = self.chroma_manager.get_by_ids(ids, collection_name)
        
        # Get from PKL files
        result = {"chroma": chroma_data}
        
        if include_text_pkl:
            result["text_pkl"] = self.pkl_loader.get_text_by_ids(ids)
        
        if include_image_pkl:
            result["image_pkl"] = self.pkl_loader.get_image_by_ids(ids)
        
        return result
    
    def get_collection_info(self, collection_name: Optional[str] = None) -> Dict[str, Any]:
        """Get information about collections and data"""
        info = {
            "chroma": self.chroma_manager.get_collection_info(collection_name),
            "text_pkl": {
                "count": len(self.pkl_loader.get_all_text_ids()),
                "loaded": self.pkl_loader.text_data is not None
            },
            "image_pkl": {
                "count": len(self.pkl_loader.get_all_image_ids()),
                "loaded": self.pkl_loader.image_data is not None
            },
            "available_collections": self.chroma_manager.get_all_collections()
        }
        return info
    
    def create_id_mapping(self) -> Dict[str, Dict[str, Any]]:
        """Create comprehensive ID mapping across all data sources"""
        return self.mapper.create_id_mapping()