package com.example.productservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Response model for apparel semantic search")
public class ApparelSemanticSearchResponse {
    
    @JsonProperty("query")
    @Schema(description = "The original search query")
    private String query;
    
    @JsonProperty("totalResults")
    @Schema(description = "Total number of results found")
    private Long totalResults;
    
    @JsonProperty("results")
    @Schema(description = "List of apparel search results")
    private List<ApparelSearchResult> results;
    
    @JsonProperty("facets")
    @Schema(description = "Facet information for filtering")
    private Map<String, List<FacetValue>> facets;
    
    
    @JsonProperty("searchTime")
    @Schema(description = "Search execution time in milliseconds")
    private Long searchTime;
    
    // Default constructor
    public ApparelSemanticSearchResponse() {}
    
    // Constructor with basic fields
    public ApparelSemanticSearchResponse(String query, Long totalResults, List<ApparelSearchResult> results) {
        this.query = query;
        this.totalResults = totalResults;
        this.results = results;
    }
    
    // Getters and Setters
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public Long getTotalResults() {
        return totalResults;
    }
    
    public void setTotalResults(Long totalResults) {
        this.totalResults = totalResults;
    }
    
    public List<ApparelSearchResult> getResults() {
        return results;
    }
    
    public void setResults(List<ApparelSearchResult> results) {
        this.results = results;
    }
    
    public Map<String, List<FacetValue>> getFacets() {
        return facets;
    }
    
    public void setFacets(Map<String, List<FacetValue>> facets) {
        this.facets = facets;
    }
    
    
    public Long getSearchTime() {
        return searchTime;
    }
    
    public void setSearchTime(Long searchTime) {
        this.searchTime = searchTime;
    }
    
    // Inner classes for structured data
    
    @Schema(description = "Individual apparel search result")
    public static class ApparelSearchResult {
        @JsonProperty("product_id")
        private String productId;
        
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
        private Double price;
        
        @JsonProperty("rating")
        private Double rating;
        
        @JsonProperty("description")
        private String description;
        
        @JsonProperty("review_text")
        private String reviewText;
        
        @JsonProperty("keyPhrases")
        private List<String> keyPhrases;
        
        @JsonProperty("entities")
        private List<String> entities;
        
        @JsonProperty("reviewSentimentLabel")
        private String reviewSentimentLabel;
        
        @JsonProperty("reviewPositiveScore")
        private Double reviewPositiveScore;
        
        @JsonProperty("score")
        private Double score;
        
        @JsonProperty("highlights")
        private Map<String, List<String>> highlights;
        
        // Getters and Setters
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        
        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }
        
        public String getMaterial() { return material; }
        public void setMaterial(String material) { this.material = material; }
        
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getReviewText() { return reviewText; }
        public void setReviewText(String reviewText) { this.reviewText = reviewText; }
        
        public List<String> getKeyPhrases() { return keyPhrases; }
        public void setKeyPhrases(List<String> keyPhrases) { this.keyPhrases = keyPhrases; }
        
        public List<String> getEntities() { return entities; }
        public void setEntities(List<String> entities) { this.entities = entities; }
        
        public String getReviewSentimentLabel() { return reviewSentimentLabel; }
        public void setReviewSentimentLabel(String reviewSentimentLabel) { this.reviewSentimentLabel = reviewSentimentLabel; }
        
        public Double getReviewPositiveScore() { return reviewPositiveScore; }
        public void setReviewPositiveScore(Double reviewPositiveScore) { this.reviewPositiveScore = reviewPositiveScore; }
        
        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }
        
        public Map<String, List<String>> getHighlights() { return highlights; }
        public void setHighlights(Map<String, List<String>> highlights) { this.highlights = highlights; }
    }
    
    @Schema(description = "Facet value for filtering")
    public static class FacetValue {
        @JsonProperty("value")
        private String value;
        
        @JsonProperty("count")
        private Long count;
        
        // Getters and Setters
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }
    
}

