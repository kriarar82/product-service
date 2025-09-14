#!/bin/bash

echo "=== Product Service Test ==="
echo "Application is running on http://localhost:8080"
echo ""

echo "1. Health Check:"
curl -s http://localhost:8080/api/products/health
echo -e "\n"

echo "2. Get Sample Product (P0001):"
curl -s http://localhost:8080/api/products/P0001 | head -c 200
echo -e "\n...\n"

echo "3. Test CSV Upload (first 3 products only):"
# Create a small test CSV with just the header and first 3 rows
head -4 src/main/resources/product_feed.csv > test_sample.csv

curl -X POST \
  -F "file=@test_sample.csv" \
  http://localhost:8080/api/products/upload-csv \
  -H "Content-Type: multipart/form-data" | head -c 500
echo -e "\n...\n"

# Clean up
rm -f test_sample.csv

echo "Test completed!"
