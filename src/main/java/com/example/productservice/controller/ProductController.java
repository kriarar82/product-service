package com.example.productservice.controller;

import com.example.productservice.model.Product;
import com.example.productservice.model.ApparelProduct;
import com.example.productservice.model.ApparelSemanticSearchRequest;
import com.example.productservice.model.ApparelSemanticSearchResponse;
import com.example.productservice.service.ProductService;
import com.example.productservice.service.CsvParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@Tag(name = "Product Service", description = "API for product information with Azure AI Search integration")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CsvParserService csvParserService;
    
    /**
     * GET endpoint to retrieve product information by ID
     * Returns product data in JSON-LD format
     * 
     * @param productId The unique identifier of the product
     * @return ResponseEntity containing the product data in JSON-LD format
     */
    @Operation(summary = "Get product by ID", description = "Retrieves product information by ID in JSON-LD format")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "The unique identifier of the product", required = true)
            @PathVariable String productId) {
        try {
            Product product = productService.getProductById(productId);
            if (product != null) {
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * POST endpoint to upload and parse CSV file
     * 
     * @param file The CSV file to upload
     * @return ResponseEntity containing the parsed products
     */
    @PostMapping(value = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }
            
            if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
                return ResponseEntity.badRequest().body("File must be a CSV file");
            }
            
            // Use the product feed field mapping
            Map<String, String> fieldMapping = csvParserService.getProductFeedFieldMapping();
            List<Product> products = csvParserService.parseCsvFile(file, fieldMapping);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "CSV file parsed successfully");
            response.put("totalProducts", products.size());
            response.put("products", products);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error parsing CSV file: " + e.getMessage());
        }
    }
    
    /**
     * POST endpoint to upload CSV file and upload to Azure AI Search
     * 
     * @param file The CSV file to upload
     * @return ResponseEntity with upload status
     */
    @PostMapping(value = "/upload-csv-to-search", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCsvToSearch(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }
            
            if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
                return ResponseEntity.badRequest().body("File must be a CSV file");
            }
            
            // Parse CSV file
            Map<String, String> fieldMapping = csvParserService.getProductFeedFieldMapping();
            List<Product> products = csvParserService.parseCsvFile(file, fieldMapping);
            
            // Upload to Azure AI Search
            boolean uploadSuccess = productService.uploadProductsToSearch(products);
            
            Map<String, Object> response = new HashMap<>();
            if (uploadSuccess) {
                response.put("message", "CSV file uploaded to Azure AI Search successfully");
                response.put("totalProducts", products.size());
                response.put("status", "success");
            } else {
                response.put("message", "Failed to upload to Azure AI Search");
                response.put("status", "error");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing CSV file: " + e.getMessage());
        }
    }
    
    /**
     * Search for products using Azure AI Search
     * 
     * @param q Search query text
     * @param filter Optional OData filter
     * @param top Number of results to return (default 10)
     * @return ResponseEntity containing search results
     */
    @Operation(summary = "Search products", description = "Search for products using Azure AI Search with optional filters")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @Parameter(description = "Search query text")
            @RequestParam(required = false) String q,
            @Parameter(description = "Optional OData filter")
            @RequestParam(required = false) String filter,
            @Parameter(description = "Number of results to return (default 10)")
            @RequestParam(defaultValue = "10") int top) {
        try {
            String searchText = (q != null && !q.trim().isEmpty()) ? q : "*";
            List<Product> products = productService.searchProducts(searchText, filter, top);
            
            Map<String, Object> response = new HashMap<>();
            response.put("query", searchText);
            response.put("filter", filter);
            response.put("totalResults", products.size());
            response.put("products", products);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching products: " + e.getMessage());
        }
    }
    
    /**
     * Search for products by category
     * 
     * @param category The category to search for
     * @param top Number of results to return (default 10)
     * @return ResponseEntity containing search results
     */
    @GetMapping("/search/category/{category}")
    public ResponseEntity<?> searchProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "10") int top) {
        try {
            List<Product> products = productService.searchProductsByCategory(category, top);
            
            Map<String, Object> response = new HashMap<>();
            response.put("category", category);
            response.put("totalResults", products.size());
            response.put("products", products);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching products by category: " + e.getMessage());
        }
    }
    
    /**
     * Search for products by brand
     * 
     * @param brand The brand to search for
     * @param top Number of results to return (default 10)
     * @return ResponseEntity containing search results
     */
    @GetMapping("/search/brand/{brand}")
    public ResponseEntity<?> searchProductsByBrand(
            @PathVariable String brand,
            @RequestParam(defaultValue = "10") int top) {
        try {
            List<Product> products = productService.searchProductsByBrand(brand, top);
            
            Map<String, Object> response = new HashMap<>();
            response.put("brand", brand);
            response.put("totalResults", products.size());
            response.put("products", products);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching products by brand: " + e.getMessage());
        }
    }
    
    /**
     * Search for products by price range
     * 
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @param top Number of results to return (default 10)
     * @return ResponseEntity containing search results
     */
    @GetMapping("/search/price")
    public ResponseEntity<?> searchProductsByPriceRange(
            @RequestParam double minPrice,
            @RequestParam double maxPrice,
            @RequestParam(defaultValue = "10") int top) {
        try {
            List<Product> products = productService.searchProductsByPriceRange(minPrice, maxPrice, top);
            
            Map<String, Object> response = new HashMap<>();
            response.put("minPrice", minPrice);
            response.put("maxPrice", maxPrice);
            response.put("totalResults", products.size());
            response.put("products", products);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching products by price range: " + e.getMessage());
        }
    }
    
    /**
     * POST endpoint for keyword search with request body
     * 
     * @param searchRequest The search request containing query, filters, and options
     * @return ResponseEntity containing search results
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchProductsPost(@RequestBody Map<String, Object> searchRequest) {
        try {
            String searchText = (String) searchRequest.getOrDefault("query", "*");
            String filter = (String) searchRequest.get("filter");
            Integer top = (Integer) searchRequest.getOrDefault("top", 10);
            
            List<Product> products = productService.searchProducts(searchText, filter, top);
            
            Map<String, Object> response = new HashMap<>();
            response.put("query", searchText);
            response.put("filter", filter);
            response.put("top", top);
            response.put("totalResults", products.size());
            response.put("products", products);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching products: " + e.getMessage());
        }
    }
    
    /**
     * Apparel semantic search endpoint
     * 
     * @param request The apparel semantic search request
     * @return ResponseEntity containing apparel semantic search results
     */
    @Operation(summary = "Apparel semantic search", description = "Perform semantic search specifically for apparel products with advanced filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Apparel semantic search completed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApparelSemanticSearchResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/apparel/semantic-search")
    public ResponseEntity<ApparelSemanticSearchResponse> apparelSemanticSearch(@RequestBody ApparelSemanticSearchRequest request) {
        try {
            // Validate request
            if (request.getSearch() == null || request.getSearch().trim().isEmpty()) {
                ApparelSemanticSearchResponse errorResponse = new ApparelSemanticSearchResponse();
                errorResponse.setQuery("");
                errorResponse.setTotalResults(0L);
                errorResponse.setResults(new ArrayList<>());
                errorResponse.setSearchTime(0L);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Perform apparel semantic search
            ApparelSemanticSearchResponse response = productService.performApparelSemanticSearch(request);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error in apparel semantic search endpoint: " + e.getMessage());
            e.printStackTrace();
            
            ApparelSemanticSearchResponse errorResponse = new ApparelSemanticSearchResponse();
            errorResponse.setQuery(request.getSearch());
            errorResponse.setTotalResults(0L);
            errorResponse.setResults(new ArrayList<>());
            errorResponse.setSearchTime(0L);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Simple apparel semantic search endpoint with query parameter
     * 
     * @param query The search query
     * @param top Number of results to return (default 10)
     * @return ResponseEntity containing apparel semantic search results
     */
    @Operation(summary = "Simple apparel semantic search", description = "Perform semantic search for apparel with a simple query string")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Apparel semantic search completed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApparelSemanticSearchResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/apparel/semantic-search")
    public ResponseEntity<ApparelSemanticSearchResponse> apparelSemanticSearchSimple(
            @Parameter(description = "Search query", required = true)
            @RequestParam String query,
            @Parameter(description = "Number of results to return", example = "10")
            @RequestParam(defaultValue = "10") int top) {
        try {
            // Create apparel semantic search request from query parameters
            ApparelSemanticSearchRequest request = new ApparelSemanticSearchRequest(query);
            request.setTop(top);
            
            // Perform apparel semantic search
            ApparelSemanticSearchResponse response = productService.performApparelSemanticSearch(request);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error in simple apparel semantic search endpoint: " + e.getMessage());
            e.printStackTrace();
            
            ApparelSemanticSearchResponse errorResponse = new ApparelSemanticSearchResponse();
            errorResponse.setQuery(query);
            errorResponse.setTotalResults(0L);
            errorResponse.setResults(new ArrayList<>());
            errorResponse.setSearchTime(0L);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Search for apparel products by brand
     * 
     * @param brand The brand to search for
     * @param top Number of results to return (default 10)
     * @return ResponseEntity containing apparel search results
     */
    @Operation(summary = "Search apparel by brand", description = "Search for apparel products by specific brand")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Apparel search completed successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/apparel/search/brand/{brand}")
    public ResponseEntity<?> searchApparelByBrand(
            @Parameter(description = "The brand to search for", required = true)
            @PathVariable String brand,
            @Parameter(description = "Number of results to return", example = "10")
            @RequestParam(defaultValue = "10") int top) {
        try {
            List<ApparelProduct> products = productService.searchApparelByBrand(brand, top);
            
            Map<String, Object> response = new HashMap<>();
            response.put("brand", brand);
            response.put("totalResults", products.size());
            response.put("products", products);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching apparel by brand: " + e.getMessage());
        }
    }
    
    /**
     * Search for apparel products by color
     * 
     * @param color The color to search for
     * @param top Number of results to return (default 10)
     * @return ResponseEntity containing apparel search results
     */
    @Operation(summary = "Search apparel by color", description = "Search for apparel products by specific color")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Apparel search completed successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/apparel/search/color/{color}")
    public ResponseEntity<?> searchApparelByColor(
            @Parameter(description = "The color to search for", required = true)
            @PathVariable String color,
            @Parameter(description = "Number of results to return", example = "10")
            @RequestParam(defaultValue = "10") int top) {
        try {
            List<ApparelProduct> products = productService.searchApparelByColor(color, top);
            
            Map<String, Object> response = new HashMap<>();
            response.put("color", color);
            response.put("totalResults", products.size());
            response.put("products", products);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching apparel by color: " + e.getMessage());
        }
    }
    
    /**
     * Search for apparel products by material
     * 
     * @param material The material to search for
     * @param top Number of results to return (default 10)
     * @return ResponseEntity containing apparel search results
     */
    @Operation(summary = "Search apparel by material", description = "Search for apparel products by specific material")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Apparel search completed successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/apparel/search/material/{material}")
    public ResponseEntity<?> searchApparelByMaterial(
            @Parameter(description = "The material to search for", required = true)
            @PathVariable String material,
            @Parameter(description = "Number of results to return", example = "10")
            @RequestParam(defaultValue = "10") int top) {
        try {
            List<ApparelProduct> products = productService.searchApparelByMaterial(material, top);
            
            Map<String, Object> response = new HashMap<>();
            response.put("material", material);
            response.put("totalResults", products.size());
            response.put("products", products);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching apparel by material: " + e.getMessage());
        }
    }
    
    /**
     * Health check endpoint
     * 
     * @return ResponseEntity with service status
     */
    @Operation(summary = "Health check", description = "Check if the service is running")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is running")
    })
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Product Service is running");
    }
}
