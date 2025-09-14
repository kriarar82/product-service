#!/bin/bash

# Test script for CSV upload functionality
# Make sure the application is running on port 8080

echo "Testing CSV Upload to Product Service"
echo "====================================="

# Test 1: Upload CSV file and parse it
echo "Test 1: Uploading CSV file for parsing..."
curl -X POST \
  -F "file=@src/main/resources/product_feed.csv" \
  http://localhost:8080/api/products/upload-csv \
  -H "Content-Type: multipart/form-data" | jq .

echo -e "\n"

# Test 2: Upload CSV file to Azure Search (if configured)
echo "Test 2: Uploading CSV file to Azure Search..."
curl -X POST \
  -F "file=@src/main/resources/product_feed.csv" \
  http://localhost:8080/api/products/upload-csv-to-search \
  -H "Content-Type: multipart/form-data" | jq .

echo -e "\n"

# Test 3: Health check
echo "Test 3: Health check..."
curl http://localhost:8080/api/products/health

echo -e "\n"
echo "Tests completed!"
