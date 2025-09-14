package com.example.productservice.service;

import com.azure.search.documents.models.SearchResult;
import com.example.productservice.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DocumentMappingService {
    
    private final ObjectMapper objectMapper;
    
    public DocumentMappingService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    /**
     * Maps a search result document to a Product object using ObjectMapper
     * 
     * @param searchResult The search result from Azure AI Search
     * @return Product object mapped from the search document
     */
    public Product mapSearchResultToProduct(SearchResult searchResult) {
        @SuppressWarnings("unchecked")
        Map<String, Object> document = (Map<String, Object>) searchResult.getDocument(Map.class);
        return mapDocumentToProduct(document);
    }
    
    /**
     * Maps a document Map to a Product object using ObjectMapper
     * 
     * @param document The search document from Azure AI Search as a Map
     * @return Product object
     */
    public Product mapDocumentToProduct(Map<String, Object> document) {
        if (document == null) {
            return null;
        }
        
        try {
            // Convert Map to JSON string, then deserialize to Product
            String json = objectMapper.writeValueAsString(document);
            return objectMapper.readValue(json, Product.class);
        } catch (Exception e) {
            System.err.println("Error mapping document to Product: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Maps a Product object to a search document format using ObjectMapper
     * 
     * @param product The Product object to convert
     * @return Map representing the search document
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> mapProductToDocument(Product product) {
        if (product == null) {
            return null;
        }
        
        try {
            // Convert Product to JSON string, then deserialize to Map
            String json = objectMapper.writeValueAsString(product);
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            System.err.println("Error mapping Product to document: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Maps multiple search results to Product objects
     * 
     * @param searchResults Iterable of search results
     * @return List of Product objects
     */
    public List<Product> mapSearchResultsToProducts(Iterable<SearchResult> searchResults) {
        List<Product> products = new ArrayList<>();
        
        for (SearchResult searchResult : searchResults) {
            Product product = mapSearchResultToProduct(searchResult);
            if (product != null) {
                products.add(product);
            }
        }
        
        return products;
    }
    
}
