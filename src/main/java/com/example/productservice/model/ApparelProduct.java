package com.example.productservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApparelProduct extends Product {
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("brand")
    private String brand;
    
    @JsonProperty("color")
    private String color;
    
    @JsonProperty("size")
    private String size;
    
    @JsonProperty("material")
    private String material;
    
    @JsonProperty("price")
    private BigDecimal price;
    
    @JsonProperty("rating")
    private Double rating;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("review_text")
    private String reviewText;
    
    // Default constructor
    public ApparelProduct() {
        super();
        // Set default type for apparel products
        this.setType("ApparelProduct");
    }
    
    // Constructor with required fields
    public ApparelProduct(String id, String title, String brand, String color, String size, 
                         String material, BigDecimal price, String description) {
        super(id, title, description, price);
        this.title = title;
        this.brand = brand;
        this.color = color;
        this.size = size;
        this.material = material;
        this.price = price;
        this.description = description;
        this.setType("ApparelProduct");
    }
    
    // Full constructor with all fields
    public ApparelProduct(String id, String title, String brand, String color, String size, 
                         String material, BigDecimal price, Double rating, String description, 
                         String reviewText) {
        super(id, title, description, price);
        this.title = title;
        this.brand = brand;
        this.color = color;
        this.size = size;
        this.material = material;
        this.price = price;
        this.rating = rating;
        this.description = description;
        this.reviewText = reviewText;
        this.setType("ApparelProduct");
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
        // Also update the parent name field for consistency
        this.setName(title);
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
        // Also update the parent brand field for consistency
        super.setBrand(brand);
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public String getMaterial() {
        return material;
    }
    
    public void setMaterial(String material) {
        this.material = material;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
        // Also update the parent price field for consistency
        super.setPrice(price);
    }
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        // Also update the parent description field for consistency
        super.setDescription(description);
    }
    
    public String getReviewText() {
        return reviewText;
    }
    
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
    
    // Override toString for better debugging
    @Override
    public String toString() {
        return "ApparelProduct{" +
                "id='" + getId() + '\'' +
                ", title='" + title + '\'' +
                ", brand='" + brand + '\'' +
                ", color='" + color + '\'' +
                ", size='" + size + '\'' +
                ", material='" + material + '\'' +
                ", price=" + price +
                ", rating=" + rating +
                ", description='" + description + '\'' +
                ", reviewText='" + reviewText + '\'' +
                ", inStock=" + isInStock() +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
    
    // Helper method to check if all required apparel fields are present
}
