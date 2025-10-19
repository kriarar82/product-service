package com.example.productservice.service;

import com.azure.search.documents.models.SearchResult;
import com.example.productservice.model.Product;
import com.example.productservice.model.ApparelProduct;
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
    
    // ==================== APPAREL PRODUCT MAPPING METHODS ====================
    
    /**
     * Maps a search result document to an ApparelProduct object using ObjectMapper
     * 
     * @param searchResult The search result from Azure AI Search
     * @return ApparelProduct object mapped from the search document
     */
    public ApparelProduct mapSearchResultToApparelProduct(SearchResult searchResult) {
        @SuppressWarnings("unchecked")
        Map<String, Object> document = (Map<String, Object>) searchResult.getDocument(Map.class);
        return mapDocumentToApparelProduct(document);
    }
    
    /**
     * Maps a document Map to an ApparelProduct object using ObjectMapper
     * 
     * @param document The search document from Azure AI Search as a Map
     * @return ApparelProduct object
     */
    public ApparelProduct mapDocumentToApparelProduct(Map<String, Object> document) {
        if (document == null) {
            return null;
        }
        
        try {
            // Convert Map to JSON string, then deserialize to ApparelProduct
            String json = objectMapper.writeValueAsString(document);
            return objectMapper.readValue(json, ApparelProduct.class);
        } catch (Exception e) {
            System.err.println("Error mapping document to ApparelProduct: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Maps an ApparelProduct object to a search document format using ObjectMapper
     * 
     * @param apparelProduct The ApparelProduct object to convert
     * @return Map representing the search document
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> mapApparelProductToDocument(ApparelProduct apparelProduct) {
        if (apparelProduct == null) {
            return null;
        }
        
        try {
            // Convert ApparelProduct to JSON string, then deserialize to Map
            String json = objectMapper.writeValueAsString(apparelProduct);
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            System.err.println("Error mapping ApparelProduct to document: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Maps multiple search results to ApparelProduct objects
     * 
     * @param searchResults Iterable of search results
     * @return List of ApparelProduct objects
     */
    public List<ApparelProduct> mapSearchResultsToApparelProducts(Iterable<SearchResult> searchResults) {
        List<ApparelProduct> apparelProducts = new ArrayList<>();
        
        for (SearchResult searchResult : searchResults) {
            ApparelProduct apparelProduct = mapSearchResultToApparelProduct(searchResult);
            if (apparelProduct != null) {
                apparelProducts.add(apparelProduct);
            }
        }
        
        return apparelProducts;
    }
    
    /**
     * Maps a Product object to an ApparelProduct object
     * This is useful when you have a Product that should be treated as apparel
     * 
     * @param product The Product object to convert
     * @return ApparelProduct object
     */
    public ApparelProduct mapProductToApparelProduct(Product product) {
        if (product == null) {
            return null;
        }
        
        try {
            ApparelProduct apparelProduct = new ApparelProduct();
            
            // Map common fields
            apparelProduct.setId(product.getId());
            apparelProduct.setTitle(product.getName());
            apparelProduct.setBrand(product.getBrand());
            apparelProduct.setPrice(product.getPrice());
            apparelProduct.setDescription(product.getDescription());
            
            // Map custom attributes to apparel-specific fields
            if (product.getCustomAttributes() != null) {
                apparelProduct.setColor((String) product.getCustomAttribute("color"));
                apparelProduct.setSize((String) product.getCustomAttribute("size"));
                apparelProduct.setMaterial((String) product.getCustomAttribute("material"));
                
                Object ratingObj = product.getCustomAttribute("rating");
                if (ratingObj instanceof Number) {
                    apparelProduct.setRating(((Number) ratingObj).doubleValue());
                }
                
                apparelProduct.setReviewText((String) product.getCustomAttribute("review_text"));
            }
            
            // Copy other relevant fields
            apparelProduct.setCurrency(product.getCurrency());
            apparelProduct.setSku(product.getSku());
            apparelProduct.setImage(product.getImage());
            apparelProduct.setTags(product.getTags());
            apparelProduct.setCreatedAt(product.getCreatedAt());
            apparelProduct.setUpdatedAt(product.getUpdatedAt());
            apparelProduct.setInStock(product.isInStock());
            apparelProduct.setStockQuantity(product.getStockQuantity());
            apparelProduct.setManufacturer(product.getManufacturer());
            apparelProduct.setModel(product.getModel());
            apparelProduct.setSpecifications(product.getSpecifications());
            apparelProduct.setCustomAttributes(product.getCustomAttributes());
            
            return apparelProduct;
            
        } catch (Exception e) {
            System.err.println("Error mapping Product to ApparelProduct: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
}
