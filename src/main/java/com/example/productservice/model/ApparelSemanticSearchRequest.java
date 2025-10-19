package com.example.productservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Request model for apparel semantic search")
public class ApparelSemanticSearchRequest {
    
    @JsonProperty("queryType")
    @Schema(description = "Type of query", example = "semantic", defaultValue = "semantic")
    private String queryType = "semantic";
    
    @JsonProperty("semanticConfiguration")
    @Schema(description = "Semantic configuration name", example = "apparel-sem-config")
    private String semanticConfiguration = "apparel-sem-config";
    
    @JsonProperty("search")
    @Schema(description = "Search query text", example = "comfortable cotton t-shirt for summer", required = true)
    private String search;
    
    
    @JsonProperty("facets")
    @Schema(description = "Facet configurations for filtering and grouping")
    private List<String> facets;
    
    @JsonProperty("select")
    @Schema(description = "Fields to select in the response")
    private String select;
    
    @JsonProperty("top")
    @Schema(description = "Number of results to return", example = "10", defaultValue = "10")
    private Integer top = 10;
    
    @JsonProperty("skip")
    @Schema(description = "Number of results to skip", example = "0", defaultValue = "0")
    private Integer skip = 0;
    
    @JsonProperty("count")
    @Schema(description = "Whether to include total count", example = "true", defaultValue = "true")
    private Boolean count = true;
    
    // Apparel-specific filters
    @JsonProperty("brandFilter")
    @Schema(description = "Filter by specific brand")
    private String brandFilter;
    
    @JsonProperty("colorFilter")
    @Schema(description = "Filter by specific color")
    private String colorFilter;
    
    @JsonProperty("sizeFilter")
    @Schema(description = "Filter by specific size")
    private String sizeFilter;
    
    @JsonProperty("materialFilter")
    @Schema(description = "Filter by specific material")
    private String materialFilter;
    
    @JsonProperty("minPrice")
    @Schema(description = "Minimum price filter")
    private Double minPrice;
    
    @JsonProperty("maxPrice")
    @Schema(description = "Maximum price filter")
    private Double maxPrice;
    
    @JsonProperty("minRating")
    @Schema(description = "Minimum rating filter")
    private Double minRating;
    
    // Default constructor
    public ApparelSemanticSearchRequest() {}
    
    // Constructor with search query
    public ApparelSemanticSearchRequest(String search) {
        this.search = search;
        this.setDefaultFacets();
        this.setDefaultSelect();
    }
    
    // Getters and Setters
    public String getQueryType() {
        return queryType;
    }
    
    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }
    
    public String getSemanticConfiguration() {
        return semanticConfiguration;
    }
    
    public void setSemanticConfiguration(String semanticConfiguration) {
        this.semanticConfiguration = semanticConfiguration;
    }
    
    public String getSearch() {
        return search;
    }
    
    public void setSearch(String search) {
        this.search = search;
    }
    
    
    public List<String> getFacets() {
        return facets;
    }
    
    public void setFacets(List<String> facets) {
        this.facets = facets;
    }
    
    public String getSelect() {
        return select;
    }
    
    public void setSelect(String select) {
        this.select = select;
    }
    
    public Integer getTop() {
        return top;
    }
    
    public void setTop(Integer top) {
        this.top = top;
    }
    
    public Integer getSkip() {
        return skip;
    }
    
    public void setSkip(Integer skip) {
        this.skip = skip;
    }
    
    public Boolean getCount() {
        return count;
    }
    
    public void setCount(Boolean count) {
        this.count = count;
    }
    
    public String getBrandFilter() {
        return brandFilter;
    }
    
    public void setBrandFilter(String brandFilter) {
        this.brandFilter = brandFilter;
    }
    
    public String getColorFilter() {
        return colorFilter;
    }
    
    public void setColorFilter(String colorFilter) {
        this.colorFilter = colorFilter;
    }
    
    public String getSizeFilter() {
        return sizeFilter;
    }
    
    public void setSizeFilter(String sizeFilter) {
        this.sizeFilter = sizeFilter;
    }
    
    public String getMaterialFilter() {
        return materialFilter;
    }
    
    public void setMaterialFilter(String materialFilter) {
        this.materialFilter = materialFilter;
    }
    
    public Double getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }
    
    public Double getMaxPrice() {
        return maxPrice;
    }
    
    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    public Double getMinRating() {
        return minRating;
    }
    
    public void setMinRating(Double minRating) {
        this.minRating = minRating;
    }
    
    // Helper method to set default facets for apparel
    private void setDefaultFacets() {
        this.facets = List.of(
            "brand,count:10,sort:count",
            "color,count:10,sort:count",
            "size,count:10,sort:count",
            "material,count:10,sort:count",
            "rating,interval:0.5",
            "price,interval:25",
            "reviewSentimentLabel,count:3"
        );
    }
    
    // Helper method to set default select fields for apparel
    private void setDefaultSelect() {
        this.select = "product_id,title,brand,color,size,material,price,rating,description,review_text,keyPhrases,reviewSentimentLabel,reviewPositiveScore";
    }
    
    // Helper method to build OData filter string
    public String buildFilterString() {
        StringBuilder filter = new StringBuilder();
        
        if (brandFilter != null && !brandFilter.trim().isEmpty()) {
            if (filter.length() > 0) filter.append(" and ");
            filter.append("brand eq '").append(brandFilter).append("'");
        }
        
        if (colorFilter != null && !colorFilter.trim().isEmpty()) {
            if (filter.length() > 0) filter.append(" and ");
            filter.append("color eq '").append(colorFilter).append("'");
        }
        
        if (sizeFilter != null && !sizeFilter.trim().isEmpty()) {
            if (filter.length() > 0) filter.append(" and ");
            filter.append("size eq '").append(sizeFilter).append("'");
        }
        
        if (materialFilter != null && !materialFilter.trim().isEmpty()) {
            if (filter.length() > 0) filter.append(" and ");
            filter.append("material eq '").append(materialFilter).append("'");
        }
        
        if (minPrice != null) {
            if (filter.length() > 0) filter.append(" and ");
            filter.append("price ge ").append(minPrice);
        }
        
        if (maxPrice != null) {
            if (filter.length() > 0) filter.append(" and ");
            filter.append("price le ").append(maxPrice);
        }
        
        if (minRating != null) {
            if (filter.length() > 0) filter.append(" and ");
            filter.append("rating ge ").append(minRating);
        }
        
        return filter.length() > 0 ? filter.toString() : null;
    }
}
