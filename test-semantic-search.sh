#!/bin/bash

# Test script for semantic search endpoints
# This script tests both the simple and advanced semantic search endpoints

set -e

# Configuration
BASE_URL="http://localhost:8080"
API_BASE="$BASE_URL/api/products"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔍 Testing Semantic Search Endpoints${NC}"
echo "================================================"

# Test 1: Simple semantic search with GET
echo -e "\n${YELLOW}Test 1: Simple Semantic Search (GET)${NC}"
echo "Query: 'soft warm hoodie for winter travel'"
echo "URL: $API_BASE/semantic-search?query=soft%20warm%20hoodie%20for%20winter%20travel&top=5"

curl -s -X GET "$API_BASE/semantic-search?query=soft%20warm%20hoodie%20for%20winter%20travel&top=5" \
  -H "Accept: application/json" | jq '.' || echo "Response received (jq not available)"

echo -e "\n${GREEN}✅ Test 1 completed${NC}"

# Test 2: Advanced semantic search with POST
echo -e "\n${YELLOW}Test 2: Advanced Semantic Search (POST)${NC}"
echo "Query: 'comfortable running shoes for marathon training'"

# Create the JSON payload
cat > /tmp/semantic_search_payload.json << 'EOF'
{
  "queryType": "semantic",
  "semanticConfiguration": "sem-config",
  "search": "comfortable running shoes for marathon training",
  "queryLanguage": "en-us",
  "speller": "lexicon",
  "answers": "extractive|count-3",
  "captions": "extractive|highlight-true",
  "facets": [
    "brand,count:10,sort:count",
    "category,count:10,sort:count",
    "color,count:10,sort:count",
    "size,count:10",
    "entities,count:15",
    "reviewSentimentLabel,count:3",
    "price,interval:25",
    "rating,interval:0.5"
  ],
  "select": "product_id,title,brand,category,color,size,price,rating,keyPhrases,entities,reviewSentimentLabel,reviewPositiveScore",
  "top": 10,
  "skip": 0,
  "count": true
}
EOF

curl -s -X POST "$API_BASE/semantic-search" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d @/tmp/semantic_search_payload.json | jq '.' || echo "Response received (jq not available)"

echo -e "\n${GREEN}✅ Test 2 completed${NC}"

# Test 3: Electronics search
echo -e "\n${YELLOW}Test 3: Electronics Semantic Search${NC}"
echo "Query: 'wireless bluetooth headphones with noise cancellation'"

cat > /tmp/electronics_search_payload.json << 'EOF'
{
  "queryType": "semantic",
  "semanticConfiguration": "sem-config",
  "search": "wireless bluetooth headphones with noise cancellation",
  "queryLanguage": "en-us",
  "speller": "lexicon",
  "answers": "extractive|count-2",
  "captions": "extractive|highlight-true",
  "facets": [
    "brand,count:5,sort:count",
    "category,count:5,sort:count",
    "price,interval:50"
  ],
  "select": "product_id,title,brand,category,price,rating,keyPhrases",
  "top": 5,
  "skip": 0,
  "count": true
}
EOF

curl -s -X POST "$API_BASE/semantic-search" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d @/tmp/electronics_search_payload.json | jq '.' || echo "Response received (jq not available)"

echo -e "\n${GREEN}✅ Test 3 completed${NC}"

# Test 4: Error handling - empty query
echo -e "\n${YELLOW}Test 4: Error Handling - Empty Query${NC}"
echo "Testing with empty search query..."

cat > /tmp/empty_search_payload.json << 'EOF'
{
  "queryType": "semantic",
  "semanticConfiguration": "sem-config",
  "search": "",
  "queryLanguage": "en-us",
  "speller": "lexicon",
  "answers": "extractive|count-3",
  "captions": "extractive|highlight-true",
  "top": 10,
  "skip": 0,
  "count": true
}
EOF

curl -s -X POST "$API_BASE/semantic-search" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d @/tmp/empty_search_payload.json | jq '.' || echo "Response received (jq not available)"

echo -e "\n${GREEN}✅ Test 4 completed${NC}"

# Test 5: Health check
echo -e "\n${YELLOW}Test 5: Health Check${NC}"
curl -s -X GET "$API_BASE/health"
echo -e "\n${GREEN}✅ Test 5 completed${NC}"

# Cleanup
rm -f /tmp/semantic_search_payload.json
rm -f /tmp/electronics_search_payload.json
rm -f /tmp/empty_search_payload.json

echo -e "\n${BLUE}🎉 All semantic search tests completed!${NC}"
echo "================================================"
echo ""
echo "Available endpoints:"
echo "  GET  $API_BASE/semantic-search?query=<query>&top=<number>"
echo "  POST $API_BASE/semantic-search"
echo "  GET  $API_BASE/health"
echo ""
echo "Swagger UI: $BASE_URL/swagger-ui.html"



