#!/bin/bash

echo "=== ObjectMapper Document Mapping Test ==="
echo "Testing Jackson ObjectMapper-based mapping"
echo ""

# Start the application in background
echo "Starting application..."
./mvnw spring-boot:run > app.log 2>&1 &
APP_PID=$!

# Wait for application to start
echo "Waiting for application to start..."
sleep 15

# Test 1: Health check
echo "1. Health Check:"
curl -s http://localhost:8080/api/products/health
echo -e "\n"

# Test 2: Get product by ID (uses ObjectMapper mapping)
echo "2. Get Product by ID (P0001) - ObjectMapper mapping:"
curl -s http://localhost:8080/api/products/P0001 | jq '.id, .name, .customAttributes'
echo -e "\n"

# Test 3: Upload CSV and test mapping
echo "3. Upload CSV and test ObjectMapper mapping:"
curl -X POST \
  -F "file=@src/main/resources/product_feed.csv" \
  http://localhost:8080/api/products/upload-csv \
  -H "Content-Type: multipart/form-data" | jq '.totalProducts, .products[0].id, .products[0].name, .products[0].customAttributes'
echo -e "\n"

# Test 4: Search with ObjectMapper mapping
echo "4. Search with ObjectMapper mapping:"
curl -s "http://localhost:8080/api/products/search?q=shoes&top=2" | jq '.totalResults, .products[0].id, .products[0].name, .products[0].customAttributes'
echo -e "\n"

# Clean up
echo "Stopping application..."
kill $APP_PID 2>/dev/null

echo ""
echo "ObjectMapper mapping test completed!"
echo ""
echo "Benefits of ObjectMapper approach:"
echo "✅ Cleaner code with Jackson annotations"
echo "✅ Automatic type conversion"
echo "✅ Built-in date/time handling"
echo "✅ Null safety with @JsonInclude"
echo "✅ Unknown property handling with @JsonIgnoreProperties"
echo "✅ Less boilerplate code"
echo "✅ Better maintainability"
