#!/bin/bash

echo "Testing Product Service API with Custom Attributes"
echo "=================================================="

echo "1. Testing health endpoint:"
curl -s http://localhost:8080/api/products/health
echo -e "\n"

echo "2. Testing product endpoint with custom attributes:"
curl -s http://localhost:8080/api/products/PROD-001 | python3 -m json.tool
echo -e "\n"

echo "3. Testing with different product ID:"
curl -s http://localhost:8080/api/products/TEST-123 | python3 -m json.tool

