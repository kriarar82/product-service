package com.example.productservice.service;

import com.example.productservice.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CsvParserService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Parse CSV file and convert to Product objects
     * 
     * @param file The uploaded CSV file
     * @param fieldMapping Configuration for mapping CSV columns to Product fields
     * @return List of Product objects
     */
    public List<Product> parseCsvFile(MultipartFile file, Map<String, String> fieldMapping) throws IOException {
        List<Product> products = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            String[] headers = null;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Parse headers from first line
                if (headers == null) {
                    headers = parseCsvLine(line);
                    continue;
                }
                
                // Parse data line
                String[] values = parseCsvLine(line);
                if (values.length != headers.length) {
                    System.err.println("Warning: Line " + lineNumber + " has " + values.length + 
                                     " columns but expected " + headers.length);
                    continue;
                }
                
                Product product = mapCsvRowToProduct(headers, values, fieldMapping);
                if (product != null) {
                    products.add(product);
                }
            }
        }
        
        return products;
    }
    
    /**
     * Parse a CSV line handling quoted values and commas within quotes
     * 
     * @param line The CSV line to parse
     * @return Array of field values
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        // Add the last field
        fields.add(currentField.toString().trim());
        
        return fields.toArray(new String[0]);
    }
    
    /**
     * Map a CSV row to a Product object using field mapping configuration
     * 
     * @param headers Array of column headers
     * @param values Array of field values
     * @param fieldMapping Mapping from CSV columns to Product fields
     * @return Product object or null if mapping fails
     */
    private Product mapCsvRowToProduct(String[] headers, String[] values, Map<String, String> fieldMapping) {
        try {
            Product product = new Product();
            
            // Create a map of header to value for easier lookup
            Map<String, String> rowData = new HashMap<>();
            for (int i = 0; i < headers.length && i < values.length; i++) {
                rowData.put(headers[i].trim(), values[i].trim());
            }
            
            // Map each field using the field mapping configuration
            for (Map.Entry<String, String> mapping : fieldMapping.entrySet()) {
                String csvColumn = mapping.getKey();
                String productField = mapping.getValue();
                String value = rowData.get(csvColumn);
                
                if (value != null && !value.isEmpty()) {
                    setProductField(product, productField, value);
                }
            }
            
            // Set default values if not provided
            if (product.getId() == null || product.getId().isEmpty()) {
                product.setId(UUID.randomUUID().toString());
            }
            
            if (product.getCreatedAt() == null) {
                product.setCreatedAt(LocalDateTime.now());
            }
            
            if (product.getUpdatedAt() == null) {
                product.setUpdatedAt(LocalDateTime.now());
            }
            
            if (product.getCurrency() == null || product.getCurrency().isEmpty()) {
                product.setCurrency("USD");
            }
            
            return product;
            
        } catch (Exception e) {
            System.err.println("Error mapping CSV row to Product: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Set a product field value using reflection-like approach
     * 
     * @param product The product object
     * @param fieldName The field name to set
     * @param value The value to set
     */
    private void setProductField(Product product, String fieldName, String value) {
        try {
            switch (fieldName.toLowerCase()) {
                case "id":
                    product.setId(value);
                    break;
                case "name":
                    product.setName(value);
                    break;
                case "description":
                    product.setDescription(value);
                    break;
                case "brand":
                    product.setBrand(value);
                    break;
                case "category":
                    product.setCategory(value);
                    break;
                case "price":
                    if (!value.isEmpty()) {
                        product.setPrice(new BigDecimal(value.replaceAll("[^\\d.-]", "")));
                    }
                    break;
                case "currency":
                    product.setCurrency(value);
                    break;
                case "sku":
                    product.setSku(value);
                    break;
                case "image":
                    product.setImage(value);
                    break;
                case "tags":
                    if (!value.isEmpty()) {
                        product.setTags(Arrays.asList(value.split(";")));
                    }
                    break;
                case "instock":
                case "in_stock":
                case "in-stock":
                    product.setInStock(Boolean.parseBoolean(value) || 
                                     value.equalsIgnoreCase("yes") || 
                                     value.equalsIgnoreCase("y") ||
                                     value.equals("1"));
                    break;
                case "stockquantity":
                case "stock_quantity":
                case "stock-quantity":
                    if (!value.isEmpty()) {
                        product.setStockQuantity(Integer.parseInt(value));
                    }
                    break;
                case "manufacturer":
                    product.setManufacturer(value);
                    break;
                case "model":
                    product.setModel(value);
                    break;
                case "specifications":
                case "specs":
                    if (!value.isEmpty()) {
                        product.setSpecifications(Arrays.asList(value.split(";")));
                    }
                    break;
                case "createdat":
                case "created_at":
                case "created-at":
                    if (!value.isEmpty()) {
                        product.setCreatedAt(LocalDateTime.parse(value, DATE_FORMATTER));
                    }
                    break;
                case "updatedat":
                case "updated_at":
                case "updated-at":
                    if (!value.isEmpty()) {
                        product.setUpdatedAt(LocalDateTime.parse(value, DATE_FORMATTER));
                    }
                    break;
                case "color":
                    product.addCustomAttribute("color", value);
                    break;
                case "rating":
                    if (!value.isEmpty()) {
                        try {
                            product.addCustomAttribute("rating", Double.parseDouble(value));
                        } catch (NumberFormatException e) {
                            product.addCustomAttribute("rating", value);
                        }
                    }
                    break;
                case "categoryid":
                case "category_id":
                    product.addCustomAttribute("categoryId", value);
                    break;
                default:
                    // Handle custom attributes
                    product.addCustomAttribute(fieldName, value);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error setting field " + fieldName + " with value " + value + ": " + e.getMessage());
        }
    }
    
    /**
     * Get default field mapping configuration for product_feed.csv format
     * This maps the specific CSV columns to Product model fields
     * 
     * @return Default field mapping for product_feed.csv
     */
    public Map<String, String> getDefaultFieldMapping() {
        Map<String, String> mapping = new HashMap<>();
        
        // Product Feed CSV specific mappings
        mapping.put("product_id", "id");
        mapping.put("product_name", "name");
        mapping.put("brand", "brand");
        mapping.put("category_name", "category");
        mapping.put("category_description", "description");
        mapping.put("sku_id", "sku");
        mapping.put("sku_name", "name"); // Use SKU name as the product name for this variant
        mapping.put("sku_image", "image");
        mapping.put("sku_description", "description");
        mapping.put("color", "color"); // Will be stored as custom attribute
        mapping.put("aggregateRating", "rating"); // Will be stored as custom attribute
        mapping.put("category_id", "categoryId"); // Will be stored as custom attribute
        
        return mapping;
    }
    
    /**
     * Get field mapping for product_feed.csv format
     * This is specifically designed for the uploaded CSV structure
     * 
     * @return Field mapping for product_feed.csv
     */
    public Map<String, String> getProductFeedFieldMapping() {
        Map<String, String> mapping = new HashMap<>();
        
        // Map CSV columns to Product fields
        mapping.put("product_id", "id");
        mapping.put("product_name", "name");
        mapping.put("brand", "brand");
        mapping.put("category_name", "category");
        mapping.put("category_description", "description");
        mapping.put("sku_id", "sku");
        mapping.put("sku_name", "name"); // Use SKU name as the product name
        mapping.put("sku_image", "image");
        mapping.put("sku_description", "description");
        mapping.put("color", "color"); // Custom attribute
        mapping.put("aggregateRating", "rating"); // Custom attribute
        mapping.put("category_id", "categoryId"); // Custom attribute
        
        return mapping;
    }
    
    /**
     * Create custom field mapping from a configuration string
     * Format: "column1:field1,column2:field2,column3:field3"
     * 
     * @param mappingConfig Configuration string
     * @return Field mapping map
     */
    public Map<String, String> createCustomFieldMapping(String mappingConfig) {
        Map<String, String> mapping = new HashMap<>();
        
        if (mappingConfig != null && !mappingConfig.trim().isEmpty()) {
            String[] pairs = mappingConfig.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    mapping.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        }
        
        return mapping;
    }
}
