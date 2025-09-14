package com.example.productservice.controller;

import com.example.productservice.model.Product;
import com.example.productservice.service.ProductService;
import com.example.productservice.service.CsvParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
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
    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> getProductById(@PathVariable String productId) {
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
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String filter,
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
     * Health check endpoint
     * 
     * @return ResponseEntity with service status
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Product Service is running");
    }
}
