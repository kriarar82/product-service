package com.example.productservice.service;

import com.azure.search.documents.SearchClient;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.models.SearchResult;
import com.azure.search.documents.models.SemanticSearchOptions;
import com.example.productservice.model.Product;
import com.example.productservice.model.ApparelProduct;
import com.example.productservice.model.ApparelSemanticSearchRequest;
import com.example.productservice.model.ApparelSemanticSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Autowired(required = false)
    @Qualifier("apparelSearchClient")
    private SearchClient apparelSearchClient;
    
    @Autowired
    private DocumentMappingService documentMappingService;
    
    @Value("${azure.search.endpoint:}")
    private String searchEndpoint;
    
    @Value("${azure.search.api-key:}")
    private String searchApiKey;
    
    @Value("${azure.search.index-name:products}")
    private String indexName;
    
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
                .setFilter("product_id eq '" + productId + "'")
                .setTop(1);
            
            Iterable<SearchResult> searchResults = 
                searchClient.search(productId, searchOptions, null);
            
            for (SearchResult result : searchResults) {
                return documentMappingService.mapSearchResultToProduct(result);
            }
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error searching for product: " + e.getMessage());
        }
        
        return null;
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
    
    /**
     * Search for products using Azure AI Search
     * 
     * @param searchText The search text
     * @param filters Optional filters to apply
     * @param top Number of results to return
     * @return List of Product objects
     */
    public List<Product> searchProducts(String searchText, String filters, int top) {
        if (searchClient == null) {
            System.err.println("Azure Search client is not configured");
            return new ArrayList<>();
        }
        
        try {
            SearchOptions searchOptions = new SearchOptions()
                .setTop(top);
            
            if (filters != null && !filters.trim().isEmpty()) {
                searchOptions.setFilter(filters);
            }
            
            Iterable<SearchResult> searchResults = 
                searchClient.search(searchText, searchOptions, null);
            
            return documentMappingService.mapSearchResultsToProducts(searchResults);
            
        } catch (Exception e) {
            System.err.println("Error searching for products: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Search for products by category
     * 
     * @param category The category to search for
     * @param top Number of results to return
     * @return List of Product objects
     */
    public List<Product> searchProductsByCategory(String category, int top) {
        String filter = "category eq '" + category + "'";
        return searchProducts("*", filter, top);
    }
    
    /**
     * Search for products by brand
     * 
     * @param brand The brand to search for
     * @param top Number of results to return
     * @return List of Product objects
     */
    public List<Product> searchProductsByBrand(String brand, int top) {
        String filter = "brand eq '" + brand + "'";
        return searchProducts("*", filter, top);
    }
    
    /**
     * Search for products in price range
     * 
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @param top Number of results to return
     * @return List of Product objects
     */
    public List<Product> searchProductsByPriceRange(double minPrice, double maxPrice, int top) {
        String filter = "price ge " + minPrice + " and price le " + maxPrice;
        return searchProducts("*", filter, top);
    }
    
    /**
     * Upload products to Azure AI Search
     * Note: This is a placeholder implementation
     * 
     * @param products List of products to upload
     * @return true if successful, false otherwise
     */
    public boolean uploadProductsToSearch(List<Product> products) {
        if (searchClient == null) {
            System.err.println("Azure Search client is not configured");
            return false;
        }
        
        try {
            // TODO: Implement Azure Search upload functionality
            // For now, just log the products that would be uploaded
            System.out.println("Would upload " + products.size() + " products to Azure AI Search");
            for (Product product : products) {
                System.out.println("Product: " + product.getName() + " (ID: " + product.getId() + ")");
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error uploading products to Azure AI Search: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Helper method to get string value from search result
     */
    private String getStringValue(SearchResult searchResult, String fieldName) {
        @SuppressWarnings("unchecked")
        Map<String, Object> document = (Map<String, Object>) searchResult.getDocument(Map.class);
        Object value = document.get(fieldName);
        return value != null ? value.toString() : null;
    }
    
    /**
     * Helper method to get double value from search result
     */
    private Double getDoubleValue(SearchResult searchResult, String fieldName) {
        @SuppressWarnings("unchecked")
        Map<String, Object> document = (Map<String, Object>) searchResult.getDocument(Map.class);
        Object value = document.get(fieldName);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }
    
    /**
     * Helper method to get string list value from search result
     */
    @SuppressWarnings("unchecked")
    private List<String> getStringListValue(SearchResult searchResult, String fieldName) {
        Map<String, Object> document = (Map<String, Object>) searchResult.getDocument(Map.class);
        Object value = document.get(fieldName);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return new ArrayList<>();
    }
    
    // ==================== APPAREL-SPECIFIC METHODS ====================
    
    /**
     * Perform semantic search specifically for apparel products
     * 
     * @param request The apparel semantic search request
     * @return ApparelSemanticSearchResponse with results
     */
    public ApparelSemanticSearchResponse performApparelSemanticSearch(ApparelSemanticSearchRequest request) {
        if (apparelSearchClient == null) {
            System.err.println("Apparel Azure Search client is not configured");
            return createApparelErrorResponse(request.getSearch(), "Apparel Azure Search client is not configured");
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Log the incoming request
            System.out.println("=== AZURE SEARCH REQUEST ===");
            System.out.println("Query: " + request.getSearch());
            System.out.println("Top: " + request.getTop());
            System.out.println("Skip: " + request.getSkip());
            System.out.println("Count: " + request.getCount());
            System.out.println("Select: " + request.getSelect());
            System.out.println("Facets: " + request.getFacets());
            System.out.println("Semantic Configuration: " + request.getSemanticConfiguration());
            
            // Create search options with semantic configuration
            SearchOptions searchOptions = new SearchOptions()
                .setTop(request.getTop())
                .setSkip(request.getSkip())
                .setIncludeTotalCount(request.getCount());
            
            // Set select fields if specified
            if (request.getSelect() != null && !request.getSelect().trim().isEmpty()) {
                searchOptions.setSelect(request.getSelect().split(","));
            }
            
            // Set facets if specified - Fix the rating facet issue
            if (request.getFacets() != null && !request.getFacets().isEmpty()) {
                // Filter out invalid facets (like rating with decimal values)
                List<String> validFacets = new ArrayList<>();
                for (String facet : request.getFacets()) {
                    if (facet.contains("rating")) {
                        // For rating field, use count instead of value for facets
                        validFacets.add("rating,count:10");
                    } else {
                        validFacets.add(facet);
                    }
                }
                searchOptions.setFacets(validFacets.toArray(new String[0]));
                System.out.println("Valid Facets: " + validFacets);
            }
            
            // Apply apparel-specific filters
            String filterString = request.buildFilterString();
            if (filterString != null && !filterString.trim().isEmpty()) {
                searchOptions.setFilter(filterString);
                System.out.println("Filter: " + filterString);
            }
            
            // Configure semantic search options
            SemanticSearchOptions semanticOptions = new SemanticSearchOptions()
                .setSemanticConfigurationName(request.getSemanticConfiguration());
            
            searchOptions.setSemanticSearchOptions(semanticOptions);
            
            // Log the complete search options
            System.out.println("Search Options - Top: " + searchOptions.getTop());
            System.out.println("Search Options - Skip: " + searchOptions.getSkip());
            System.out.println("Search Options - IncludeTotalCount: " + request.getCount());
            System.out.println("Search Options - Select: " + (searchOptions.getSelect() != null ? searchOptions.getSelect().toString() : "null"));
            System.out.println("Search Options - Facets: " + (searchOptions.getFacets() != null ? searchOptions.getFacets().toString() : "null"));
            System.out.println("Search Options - Filter: " + searchOptions.getFilter());
            System.out.println("Search Options - Semantic Config: " + searchOptions.getSemanticSearchOptions().getSemanticConfigurationName());
            
            // Perform the search
            System.out.println("Executing Azure Search request...");
            Iterable<SearchResult> searchResults = 
                apparelSearchClient.search(request.getSearch(), searchOptions, null);
            
            // Log search results
            System.out.println("=== AZURE SEARCH RESPONSE ===");
            int resultCount = 0;
            for (SearchResult result : searchResults) {
                resultCount++;
                System.out.println("Result " + resultCount + ":");
                System.out.println("  Score: " + result.getScore());
                System.out.println("  Document: " + result.getDocument(Map.class));
                if (resultCount >= 5) { // Limit to first 5 results for logging
                    System.out.println("  ... (showing first 5 results)");
                    break;
                }
            }
            System.out.println("Total results processed: " + resultCount);
            
            // Process results
            ApparelSemanticSearchResponse response = processApparelSemanticSearchResults(
                request.getSearch(), 
                searchResults, 
                System.currentTimeMillis() - startTime
            );
            
            System.out.println("=== FINAL RESPONSE ===");
            System.out.println("Query: " + response.getQuery());
            System.out.println("Total Results: " + response.getTotalResults());
            System.out.println("Search Time: " + response.getSearchTime() + "ms");
            System.out.println("Results Count: " + (response.getResults() != null ? response.getResults().size() : 0));
            
            return response;
            
        } catch (Exception e) {
            System.err.println("Apparel semantic search error: " + e.getMessage());
            e.printStackTrace();
            return createApparelErrorResponse(request.getSearch(), "Error performing apparel semantic search: " + e.getMessage());
        }
    }
    
    /**
     * Process apparel semantic search results and create response
     */
    private ApparelSemanticSearchResponse processApparelSemanticSearchResults(String query, Iterable<SearchResult> searchResults, long searchTime) {
        ApparelSemanticSearchResponse response = new ApparelSemanticSearchResponse();
        response.setQuery(query);
        response.setSearchTime(searchTime);
        
        List<ApparelSemanticSearchResponse.ApparelSearchResult> results = new ArrayList<>();
        Long totalCount = 0L;
        
        for (SearchResult searchResult : searchResults) {
            // Get total count from the first result
            if (totalCount == 0L) {
                // For now, we'll count the results as we process them
                // In a real implementation, you might get this from the search response
                totalCount = 1L; // This will be updated to actual count
            }
            
            // Convert search result to apparel search result
            ApparelSemanticSearchResponse.ApparelSearchResult result = convertToApparelSearchResult(searchResult);
            results.add(result);
        }
        
        response.setResults(results);
        response.setTotalResults(totalCount);
        
        return response;
    }
    
    /**
     * Convert Azure Search result to apparel search result
     */
    private ApparelSemanticSearchResponse.ApparelSearchResult convertToApparelSearchResult(SearchResult searchResult) {
        ApparelSemanticSearchResponse.ApparelSearchResult result = new ApparelSemanticSearchResponse.ApparelSearchResult();
        
        // Map apparel-specific fields
        result.setProductId(getStringValue(searchResult, "product_id"));
        result.setTitle(getStringValue(searchResult, "title"));
        result.setBrand(getStringValue(searchResult, "brand"));
        result.setColor(getStringValue(searchResult, "color"));
        result.setSize(getStringValue(searchResult, "size"));
        result.setMaterial(getStringValue(searchResult, "material"));
        result.setPrice(getDoubleValue(searchResult, "price"));
        result.setRating(getDoubleValue(searchResult, "rating"));
        result.setDescription(getStringValue(searchResult, "description"));
        result.setReviewText(getStringValue(searchResult, "review_text"));
        result.setReviewSentimentLabel(getStringValue(searchResult, "reviewSentimentLabel"));
        result.setReviewPositiveScore(getDoubleValue(searchResult, "reviewPositiveScore"));
        result.setScore(searchResult.getScore());
        
        // Map list fields
        result.setKeyPhrases(getStringListValue(searchResult, "keyPhrases"));
        result.setEntities(getStringListValue(searchResult, "entities"));
        
        // Map highlights
        result.setHighlights(searchResult.getHighlights());
        
        return result;
    }
    
    /**
     * Create error response for apparel semantic search
     */
    private ApparelSemanticSearchResponse createApparelErrorResponse(String query, String errorMessage) {
        ApparelSemanticSearchResponse response = new ApparelSemanticSearchResponse();
        response.setQuery(query);
        response.setTotalResults(0L);
        response.setResults(new ArrayList<>());
        response.setSearchTime(0L);
        
        // Log the error
        System.err.println("Apparel semantic search error: " + errorMessage);
        
        return response;
    }
    
    /**
     * Search for apparel products by brand
     * 
     * @param brand The brand to search for
     * @param top Number of results to return
     * @return List of ApparelProduct objects
     */
    public List<ApparelProduct> searchApparelByBrand(String brand, int top) {
        String filter = "brand eq '" + brand + "'";
        return searchApparelProducts("*", filter, top);
    }
    
    /**
     * Search for apparel products by color
     * 
     * @param color The color to search for
     * @param top Number of results to return
     * @return List of ApparelProduct objects
     */
    public List<ApparelProduct> searchApparelByColor(String color, int top) {
        String filter = "color eq '" + color + "'";
        return searchApparelProducts("*", filter, top);
    }
    
    /**
     * Search for apparel products by material
     * 
     * @param material The material to search for
     * @param top Number of results to return
     * @return List of ApparelProduct objects
     */
    public List<ApparelProduct> searchApparelByMaterial(String material, int top) {
        String filter = "material eq '" + material + "'";
        return searchApparelProducts("*", filter, top);
    }
    
    /**
     * Search for apparel products using Azure AI Search
     * 
     * @param searchText The search text
     * @param filters Optional filters to apply
     * @param top Number of results to return
     * @return List of ApparelProduct objects
     */
    public List<ApparelProduct> searchApparelProducts(String searchText, String filters, int top) {
        if (apparelSearchClient == null) {
            System.err.println("Apparel Azure Search client is not configured");
            return new ArrayList<>();
        }
        
        try {
            SearchOptions searchOptions = new SearchOptions()
                .setTop(top);
            
            if (filters != null && !filters.trim().isEmpty()) {
                searchOptions.setFilter(filters);
            }
            
            Iterable<SearchResult> searchResults = 
                apparelSearchClient.search(searchText, searchOptions, null);
            
            return documentMappingService.mapSearchResultsToApparelProducts(searchResults);
            
        } catch (Exception e) {
            System.err.println("Error searching for apparel products: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
}
