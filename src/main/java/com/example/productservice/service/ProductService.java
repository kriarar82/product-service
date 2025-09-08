package com.example.productservice.service;

import com.azure.search.documents.SearchClient;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.models.SearchResult;
import com.example.productservice.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    
    @Autowired(required = false)
    private SearchClient searchClient;
    
    /**
     * Retrieves a product by its ID
     * In a real implementation, this would query a database or search index
     * 
     * @param productId The unique identifier of the product
     * @return Product object or null if not found
     */
    public Product getProductById(String productId) {
        // For demonstration purposes, return mock data
        // In a real implementation, you would:
        // 1. Query Azure AI Search using the searchClient
        // 2. Or query a database
        // 3. Or call another microservice
        
        if (searchClient != null) {
            return searchProductById(productId);
        } else {
            return getMockProduct(productId);
        }
    }
    
    /**
     * Search for product using Azure AI Search
     * 
     * @param productId The product ID to search for
     * @return Product object or null if not found
     */
    private Product searchProductById(String productId) {
        try {
            SearchOptions searchOptions = new SearchOptions()
                .setFilter("id eq '" + productId + "'")
                .setTop(1);
            
            Iterable<SearchResult> searchResults = 
                searchClient.search(productId, searchOptions, null);
            
            for (SearchResult result : searchResults) {
                // Get the document as a Map for easier handling
                @SuppressWarnings("unchecked")
                Map<String, Object> document = (Map<String, Object>) result.getDocument(Map.class);
                return mapDocumentToProduct(document);
            }
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error searching for product: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Maps a document Map to a Product object
     * 
     * @param document The search document from Azure AI Search as a Map
     * @return Product object
     */
    private Product mapDocumentToProduct(Map<String, Object> document) {
        Product product = new Product();
        
        product.setId((String) document.get("id"));
        product.setName((String) document.get("name"));
        product.setDescription((String) document.get("description"));
        product.setBrand((String) document.get("brand"));
        product.setCategory((String) document.get("category"));
        
        // Handle price conversion
        Object priceObj = document.get("price");
        if (priceObj instanceof Number) {
            product.setPrice(new BigDecimal(priceObj.toString()));
        }
        
        product.setCurrency((String) document.get("currency"));
        product.setSku((String) document.get("sku"));
        product.setImage((String) document.get("image"));
        
        // Handle tags array
        Object tagsObj = document.get("tags");
        if (tagsObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> tags = (List<String>) tagsObj;
            product.setTags(tags);
        }
        
        Object inStockObj = document.get("inStock");
        if (inStockObj instanceof Boolean) {
            product.setInStock((Boolean) inStockObj);
        }
        
        Object stockQuantityObj = document.get("stockQuantity");
        if (stockQuantityObj instanceof Number) {
            product.setStockQuantity(((Number) stockQuantityObj).intValue());
        }
        
        product.setManufacturer((String) document.get("manufacturer"));
        product.setModel((String) document.get("model"));
        
        // Handle specifications array
        Object specsObj = document.get("specifications");
        if (specsObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> specifications = (List<String>) specsObj;
            product.setSpecifications(specifications);
        }
        
        // Handle custom attributes
        Object customAttrsObj = document.get("customAttributes");
        if (customAttrsObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> customAttributes = (Map<String, Object>) customAttrsObj;
            product.setCustomAttributes(customAttributes);
        }
        
        return product;
    }
    
    /**
     * Returns mock product data for demonstration
     * 
     * @param productId The product ID
     * @return Mock Product object
     */
    private Product getMockProduct(String productId) {
        // Mock data for demonstration
        Product product = new Product();
        product.setId(productId);
        product.setName("Sample Product " + productId);
        product.setDescription("This is a sample product description for product " + productId);
        product.setBrand("Sample Brand");
        product.setCategory("Electronics");
        product.setPrice(new BigDecimal("99.99"));
        product.setCurrency("USD");
        product.setSku("SKU-" + productId);
        product.setImage("https://example.com/images/product-" + productId + ".jpg");
        
        List<String> tags = new ArrayList<>();
        tags.add("electronics");
        tags.add("sample");
        tags.add("demo");
        product.setTags(tags);
        
        product.setInStock(true);
        product.setStockQuantity(100);
        product.setManufacturer("Sample Manufacturer");
        product.setModel("Model-" + productId);
        
        List<String> specifications = new ArrayList<>();
        specifications.add("Color: Black");
        specifications.add("Weight: 1.5 lbs");
        specifications.add("Dimensions: 10x8x2 inches");
        product.setSpecifications(specifications);
        
        product.setCreatedAt(LocalDateTime.now().minusDays(30));
        product.setUpdatedAt(LocalDateTime.now());
        
        // Add some custom attributes as examples
        product.addCustomAttribute("warranty", "2 years");
        product.addCustomAttribute("color", "Black");
        product.addCustomAttribute("weight", "1.5 lbs");
        product.addCustomAttribute("dimensions", "10x8x2 inches");
        product.addCustomAttribute("batteryLife", "24 hours");
        product.addCustomAttribute("isWaterproof", true);
        product.addCustomAttribute("rating", 4.5);
        product.addCustomAttribute("reviewCount", 150);
        
        return product;
    }
}
