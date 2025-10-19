# Apparel Search Configuration

This document explains how to configure the separate Azure AI Search service for apparel products.

## Overview

The Product Service now supports two separate Azure AI Search configurations:

1. **General Product Search** - Uses the original `AzureSearchConfig` for general products
2. **Apparel Product Search** - Uses the new `ApparelAzureSearchConfig` for apparel-specific semantic search

## Configuration

### 1. Azure AI Search Service Setup

Create a separate Azure AI Search service for apparel products:

1. Go to the Azure Portal
2. Create a new "Azure AI Search" resource
3. Choose a unique name (e.g., `apparel-search`)
4. Note down the endpoint URL and create an API key

### 2. Application Properties Configuration

Add the following properties to your `application.properties` file:

```properties
# Azure AI Search Configuration for Apparel Products
azure.apparel.search.endpoint=https://your-apparel-search.search.windows.net
azure.apparel.search.api-key=your-apparel-api-key
azure.apparel.search.index-name=apparel-products
```

### 3. Index Configuration

Create an index in your apparel search service with the following fields:

```json
{
  "name": "apparel-products",
  "fields": [
    {
      "name": "product_id",
      "type": "Edm.String",
      "key": true,
      "searchable": false,
      "filterable": true,
      "sortable": true
    },
    {
      "name": "title",
      "type": "Edm.String",
      "searchable": true,
      "filterable": true,
      "sortable": true
    },
    {
      "name": "brand",
      "type": "Edm.String",
      "searchable": true,
      "filterable": true,
      "sortable": true,
      "facetable": true
    },
    {
      "name": "color",
      "type": "Edm.String",
      "searchable": true,
      "filterable": true,
      "sortable": true,
      "facetable": true
    },
    {
      "name": "size",
      "type": "Edm.String",
      "searchable": true,
      "filterable": true,
      "sortable": true,
      "facetable": true
    },
    {
      "name": "material",
      "type": "Edm.String",
      "searchable": true,
      "filterable": true,
      "sortable": true,
      "facetable": true
    },
    {
      "name": "price",
      "type": "Edm.Double",
      "searchable": false,
      "filterable": true,
      "sortable": true,
      "facetable": true
    },
    {
      "name": "rating",
      "type": "Edm.Double",
      "searchable": false,
      "filterable": true,
      "sortable": true,
      "facetable": true
    },
    {
      "name": "description",
      "type": "Edm.String",
      "searchable": true,
      "filterable": false,
      "sortable": false
    },
    {
      "name": "review_text",
      "type": "Edm.String",
      "searchable": true,
      "filterable": false,
      "sortable": false
    },
    {
      "name": "keyPhrases",
      "type": "Collection(Edm.String)",
      "searchable": true,
      "filterable": false,
      "sortable": false
    },
    {
      "name": "entities",
      "type": "Collection(Edm.String)",
      "searchable": true,
      "filterable": false,
      "sortable": false
    },
    {
      "name": "reviewSentimentLabel",
      "type": "Edm.String",
      "searchable": false,
      "filterable": true,
      "sortable": false,
      "facetable": true
    },
    {
      "name": "reviewPositiveScore",
      "type": "Edm.Double",
      "searchable": false,
      "filterable": true,
      "sortable": true
    }
  ]
}
```

### 4. Semantic Configuration

Configure semantic search for apparel products:

1. In your Azure AI Search service, go to "Semantic search"
2. Create a new semantic configuration named "apparel-sem-config"
3. Select the fields you want to use for semantic search (title, description, review_text, etc.)

## Usage

### API Endpoints

The following endpoints will use the apparel search service:

- `POST /api/products/apparel/semantic-search` - Full semantic search
- `GET /api/products/apparel/semantic-search` - Simple semantic search
- `GET /api/products/apparel/search/brand/{brand}` - Search by brand
- `GET /api/products/apparel/search/color/{color}` - Search by color
- `GET /api/products/apparel/search/material/{material}` - Search by material

### Example Requests

#### Semantic Search
```bash
POST /api/products/apparel/semantic-search
{
  "search": "comfortable cotton t-shirt for summer",
  "brandFilter": "Nike",
  "colorFilter": "blue",
  "minPrice": 20.0,
  "maxPrice": 100.0,
  "minRating": 4.0,
  "top": 10
}
```

#### Simple Search
```bash
GET /api/products/apparel/semantic-search?query=warm winter jacket&top=5
```

## Configuration Classes

### ApparelAzureSearchConfig
- **Bean Name**: `apparelSearchClient`
- **Condition**: Only created if `azure.apparel.search.endpoint` and `azure.apparel.search.api-key` are configured
- **Properties**: 
  - `azure.apparel.search.endpoint`
  - `azure.apparel.search.api-key`
  - `azure.apparel.search.index-name` (default: "apparel-products")

### ProductService Integration
- Uses `@Qualifier("apparelSearchClient")` to inject the apparel-specific search client
- All apparel search methods use the apparel search client instead of the general search client
- Graceful fallback if apparel search client is not configured

## Benefits

1. **Separation of Concerns**: Apparel products have their own dedicated search service
2. **Independent Scaling**: Scale apparel search independently from general product search
3. **Specialized Configuration**: Optimize search settings specifically for apparel products
4. **Semantic Search**: Dedicated semantic search configuration for apparel-specific queries
5. **Flexible Deployment**: Deploy and manage search services independently

## Troubleshooting

### Apparel Search Client Not Available
If you see "Apparel Azure Search client is not configured" in the logs:
1. Check that `azure.apparel.search.endpoint` is configured
2. Check that `azure.apparel.search.api-key` is configured
3. Verify the API key has the correct permissions
4. Ensure the search service is running and accessible

### Index Not Found
If you get index-related errors:
1. Verify the index name in `azure.apparel.search.index-name`
2. Ensure the index exists in your apparel search service
3. Check that the index has the required fields

### Semantic Search Not Working
If semantic search returns no results:
1. Verify the semantic configuration exists in your search service
2. Check that the semantic configuration name matches `apparel-sem-config`
3. Ensure the semantic configuration includes the appropriate fields

