"""
Mapping utilities to link ChromaDB results with PKL data
"""
from typing import Dict, List, Any, Optional
from app.transcript.chroma_manager import ChromaManager
from app.transcript.pkl_loader import PKLLoader



class DataMapper:
    """Maps and combines data from ChromaDB and PKL files"""
    
    def __init__(self, chroma_manager: ChromaManager, pkl_loader: PKLLoader):
        """
        Initialize mapper with ChromaDB and PKL loaders
        
        Args:
            chroma_manager: ChromaManager instance
            pkl_loader: PKLLoader instance
        """
        self.chroma_manager = chroma_manager
        self.pkl_loader = pkl_loader
    
    def map_chroma_to_text(self, chroma_results: Dict[str, Any]) -> Dict[str, Any]:
        """
        Map ChromaDB search results to text embeddings from PKL
        
        Args:
            chroma_results: Results from ChromaDB search
        
        Returns:
            Combined results with ChromaDB and text PKL data
        """
        if not chroma_results or not chroma_results.get('ids'):
            return {"chroma": chroma_results, "text_pkl": {}}
        
        # Get IDs from ChromaDB results
        chroma_ids = []
        for id_list in chroma_results.get('ids', []):
            chroma_ids.extend(id_list if isinstance(id_list, list) else [id_list])
        
        # Get corresponding text data from PKL
        text_data = self.pkl_loader.get_text_by_ids(chroma_ids)
        
        return {
            "chroma": chroma_results,
            "text_pkl": text_data,
            "mapped_ids": chroma_ids
        }
    
    def map_chroma_to_image(self, chroma_results: Dict[str, Any]) -> Dict[str, Any]:
        """
        Map ChromaDB search results to image embeddings from PKL
        
        Args:
            chroma_results: Results from ChromaDB search
        
        Returns:
            Combined results with ChromaDB and image PKL data
        """
        if not chroma_results or not chroma_results.get('ids'):
            return {"chroma": chroma_results, "image_pkl": {}}
        
        # Get IDs from ChromaDB results
        chroma_ids = []
        for id_list in chroma_results.get('ids', []):
            chroma_ids.extend(id_list if isinstance(id_list, list) else [id_list])
        
        # Get corresponding image data from PKL
        image_data = self.pkl_loader.get_image_by_ids(chroma_ids)
        
        return {
            "chroma": chroma_results,
            "image_pkl": image_data,
            "mapped_ids": chroma_ids
        }
    
    def map_chroma_to_both(self, chroma_results: Dict[str, Any]) -> Dict[str, Any]:
        """
        Map ChromaDB search results to both text and image embeddings from PKL
        
        Args:
            chroma_results: Results from ChromaDB search
        
        Returns:
            Combined results with ChromaDB, text PKL, and image PKL data
        """
        if not chroma_results or not chroma_results.get('ids'):
            return {
                "chroma": chroma_results,
                "text_pkl": {},
                "image_pkl": {}
            }
        
        # Get IDs from ChromaDB results
        chroma_ids = []
        for id_list in chroma_results.get('ids', []):
            chroma_ids.extend(id_list if isinstance(id_list, list) else [id_list])
        
        # Get corresponding data from both PKL files
        text_data = self.pkl_loader.get_text_by_ids(chroma_ids)
        image_data = self.pkl_loader.get_image_by_ids(chroma_ids)
        
        return {
            "chroma": chroma_results,
            "text_pkl": text_data,
            "image_pkl": image_data,
            "mapped_ids": chroma_ids
        }
    
    def create_id_mapping(self) -> Dict[str, Dict[str, Any]]:
        """
        Create a comprehensive mapping of all IDs across ChromaDB and PKL files
        
        Returns:
            Dictionary mapping IDs to their locations (chroma, text_pkl, image_pkl)
        """
        mapping = {}
        
        # Get ChromaDB IDs
        try:
            collection = self.chroma_manager.get_collection()
            chroma_data = collection.get()
            chroma_ids = chroma_data.get('ids', [])
            for doc_id in chroma_ids:
                if doc_id not in mapping:
                    mapping[doc_id] = {}
                mapping[doc_id]['chroma'] = True
        except Exception as e:
            print(f"Warning: Could not get ChromaDB IDs: {e}")
        
        # Get text PKL IDs
        text_ids = self.pkl_loader.get_all_text_ids()
        for doc_id in text_ids:
            if doc_id not in mapping:
                mapping[doc_id] = {}
            mapping[doc_id]['text_pkl'] = True
        
        # Get image PKL IDs
        image_ids = self.pkl_loader.get_all_image_ids()
        for doc_id in image_ids:
            if doc_id not in mapping:
                mapping[doc_id] = {}
            mapping[doc_id]['image_pkl'] = True
        
        return mapping
    
    def get_unified_result(
        self,
        chroma_results: Dict[str, Any],
        include_text: bool = True,
        include_image: bool = True
    ) -> List[Dict[str, Any]]:
        """
        Create unified result format combining ChromaDB and PKL data
        
        Args:
            chroma_results: Results from ChromaDB search
            include_text: Whether to include text PKL data
            include_image: Whether to include image PKL data
        
        Returns:
            List of unified result dictionaries
        """
        if not chroma_results or not chroma_results.get('ids'):
            return []
        
        unified_results = []
        
        # Extract ChromaDB results
        chroma_ids = chroma_results.get('ids', [])
        chroma_distances = chroma_results.get('distances', [])
        chroma_metadatas = chroma_results.get('metadatas', [])
        chroma_documents = chroma_results.get('documents', [])
        
        # Flatten if nested
        if chroma_ids and isinstance(chroma_ids[0], list):
            chroma_ids = chroma_ids[0]
            chroma_distances = chroma_distances[0] if chroma_distances else []
            chroma_metadatas = chroma_metadatas[0] if chroma_metadatas else []
            chroma_documents = chroma_documents[0] if chroma_documents else []
        
        # Get PKL data
        text_data = {}
        image_data = {}
        if include_text:
            text_data = self.pkl_loader.get_text_by_ids(chroma_ids)
            text_id_to_idx = {doc_id: i for i, doc_id in enumerate(text_data.get('ids', []))}
        if include_image:
            image_data = self.pkl_loader.get_image_by_ids(chroma_ids)
            image_id_to_idx = {doc_id: i for i, doc_id in enumerate(image_data.get('ids', []))}
        
        # Combine results
        for i, doc_id in enumerate(chroma_ids):
            result = {
                "id": doc_id,
                "chroma": {
                    "distance": chroma_distances[i] if i < len(chroma_distances) else None,
                    "metadata": chroma_metadatas[i] if i < len(chroma_metadatas) else None,
                    "document": chroma_documents[i] if i < len(chroma_documents) else None
                }
            }
            
            if include_text and doc_id in text_data.get('ids', []):
                idx = text_data['ids'].index(doc_id)
                result["text_pkl"] = {
                    "embedding": text_data['embeddings'][idx] if 'embeddings' in text_data else None,
                    "metadata": text_data['metadatas'][idx] if 'metadatas' in text_data else None,
                    "document": text_data['documents'][idx] if 'documents' in text_data else None
                }
            
            if include_image and doc_id in image_data.get('ids', []):
                idx = image_data['ids'].index(doc_id)
                result["image_pkl"] = {
                    "embedding": image_data['embeddings'][idx] if 'embeddings' in image_data else None,
                    "metadata": image_data['metadatas'][idx] if 'metadatas' in image_data else None,
                    "document": image_data['documents'][idx] if 'documents' in image_data else None
                }
            
            unified_results.append(result)
        
        return unified_results