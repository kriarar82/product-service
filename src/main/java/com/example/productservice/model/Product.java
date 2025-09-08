package com.example.productservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Product {
    
    @JsonProperty("@context")
    private String context = "https://schema.org/";
    
    @JsonProperty("@type")
    private String type = "Product";
    
    private String id;
    private String name;
    private String description;
    private String brand;
    private String category;
    private BigDecimal price;
    private String currency;
    private String sku;
    private String image;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean inStock;
    private int stockQuantity;
    private String manufacturer;
    private String model;
    private List<String> specifications;
    
    // Custom attributes - flexible key-value pairs for additional product data
    private Map<String, Object> customAttributes;
    
    // Default constructor
    public Product() {}
    
    // Constructor with required fields
    public Product(String id, String name, String description, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = "USD";
        this.inStock = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getContext() {
        return context;
    }
    
    public void setContext(String context) {
        this.context = context;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isInStock() {
        return inStock;
    }
    
    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
    
    public int getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public List<String> getSpecifications() {
        return specifications;
    }
    
    public void setSpecifications(List<String> specifications) {
        this.specifications = specifications;
    }
    
    // Custom Attributes Getters and Setters
    public Map<String, Object> getCustomAttributes() {
        return customAttributes;
    }
    
    public void setCustomAttributes(Map<String, Object> customAttributes) {
        this.customAttributes = customAttributes;
    }
    
    // Helper methods for custom attributes
    public void addCustomAttribute(String key, Object value) {
        if (this.customAttributes == null) {
            this.customAttributes = new HashMap<>();
        }
        this.customAttributes.put(key, value);
    }
    
    public Object getCustomAttribute(String key) {
        if (this.customAttributes == null) {
            return null;
        }
        return this.customAttributes.get(key);
    }
    
    public void removeCustomAttribute(String key) {
        if (this.customAttributes != null) {
            this.customAttributes.remove(key);
        }
    }
    
    public boolean hasCustomAttribute(String key) {
        return this.customAttributes != null && this.customAttributes.containsKey(key);
    }
}
