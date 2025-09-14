#!/bin/bash

echo "=== POST Search Endpoint Test ==="
echo "Testing keyword search with POST request body"
echo ""

# Wait for application to be ready
echo "Waiting for application to start..."
sleep 5

# Test 1: Health check
echo "1. Health Check:"
curl -s http://localhost:8080/api/products/health
echo -e "\n"

# Test 2: Simple keyword search POST
echo "2. Simple Keyword Search (POST):"
curl -X POST http://localhost:8080/api/products/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "shoes",
    "top": 3
  }' | jq '.query, .totalResults, .products[0].product_name'
echo -e "\n"

# Test 3: Advanced search with filters
echo "3. Advanced Search with Filters (POST):"
curl -X POST http://localhost:8080/api/products/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "running",
    "filter": "category_name eq \"Running Shoes\"",
    "top": 5
  }' | jq '.query, .filter, .totalResults, .products[0].product_name, .products[0].brand'
echo -e "\n"

# Test 4: Search by brand with POST
echo "4. Search by Brand (POST):"
curl -X POST http://localhost:8080/api/products/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "*",
    "filter": "brand eq \"Fabrikam\"",
    "top": 3
  }' | jq '.query, .filter, .totalResults, .products[0].product_name, .products[0].brand'
echo -e "\n"

# Test 5: Price range search with POST
echo "5. Price Range Search (POST):"
curl -X POST http://localhost:8080/api/products/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "*",
    "filter": "price ge 50 and price le 100",
    "top": 3
  }' | jq '.query, .filter, .totalResults, .products[0].product_name, .products[0].price'
echo -e "\n"

# Test 6: Complex search with multiple criteria
echo "6. Complex Search (POST):"
curl -X POST http://localhost:8080/api/products/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "blue",
    "filter": "inStock eq true",
    "top": 5
  }' | jq '.query, .filter, .totalResults, .products[0].product_name, .products[0].customAttributes.color'
echo -e "\n"

echo "POST Search endpoint tests completed!"
echo ""
echo "Available Search Endpoints:"
echo "GET  /api/products/search?q={query}&filter={filter}&top={count}"
echo "POST /api/products/search (with JSON body)"
echo "GET  /api/products/search/category/{category}?top={count}"
echo "GET  /api/products/search/brand/{brand}?top={count}"
echo "GET  /api/products/search/price?minPrice={min}&maxPrice={max}&top={count}"
echo ""
echo "POST Search Request Body Format:"
echo '{
  "query": "search term",
  "filter": "OData filter expression",
  "top": 10
}'
