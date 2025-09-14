#!/bin/bash

echo "=== Product Service Search API Test ==="
echo "Testing document mapping and search functionality"
echo ""

# Wait for application to be ready
echo "Waiting for application to start..."
sleep 5

# Test 1: Health check
echo "1. Health Check:"
curl -s http://localhost:8080/api/products/health
echo -e "\n"

# Test 2: Get product by ID (uses document mapping)
echo "2. Get Product by ID (P0001):"
curl -s http://localhost:8080/api/products/P0001 | head -c 300
echo -e "\n...\n"

# Test 3: General search
echo "3. General Search (shoes):"
curl -s "http://localhost:8080/api/products/search?q=shoes&top=3" | head -c 400
echo -e "\n...\n"

# Test 4: Search by category
echo "4. Search by Category (Running Shoes):"
curl -s "http://localhost:8080/api/products/search/category/Running%20Shoes?top=2" | head -c 400
echo -e "\n...\n"

# Test 5: Search by brand
echo "5. Search by Brand (Fabrikam):"
curl -s "http://localhost:8080/api/products/search/brand/Fabrikam?top=2" | head -c 400
echo -e "\n...\n"

# Test 6: Search by price range
echo "6. Search by Price Range (50-100):"
curl -s "http://localhost:8080/api/products/search/price?minPrice=50&maxPrice=100&top=2" | head -c 400
echo -e "\n...\n"

# Test 7: Search with filter
echo "7. Search with Filter (inStock=true):"
curl -s "http://localhost:8080/api/products/search?q=*&filter=inStock%20eq%20true&top=2" | head -c 400
echo -e "\n...\n"

# Test 8: CSV Upload (to populate search index)
echo "8. Upload CSV to populate search index:"
curl -X POST \
  -F "file=@src/main/resources/product_feed.csv" \
  http://localhost:8080/api/products/upload-csv-to-search \
  -H "Content-Type: multipart/form-data" | head -c 200
echo -e "\n...\n"

echo "Search API tests completed!"
echo ""
echo "Available Search Endpoints:"
echo "- GET /api/products/search?q={query}&filter={filter}&top={count}"
echo "- GET /api/products/search/category/{category}?top={count}"
echo "- GET /api/products/search/brand/{brand}?top={count}"
echo "- GET /api/products/search/price?minPrice={min}&maxPrice={max}&top={count}"
echo "- GET /api/products/{productId}"
echo "- POST /api/products/upload-csv"
echo "- POST /api/products/upload-csv-to-search"
