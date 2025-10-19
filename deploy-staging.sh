#!/bin/bash

# Staging Environment Deployment Script
# This script builds and deploys the application to staging environment

set -e

echo "🚀 Starting Staging Environment Deployment..."

# Set environment variables
export SPRING_PROFILES_ACTIVE=staging
export AZURE_SEARCH_ENDPOINT=${AZURE_SEARCH_ENDPOINT:-"https://product-search-staging.search.windows.net"}
export AZURE_SEARCH_API_KEY=${AZURE_SEARCH_API_KEY:-"staging-api-key"}
export AZURE_SEARCH_INDEX_NAME=${AZURE_SEARCH_INDEX_NAME:-"productsearch-staging-index"}

# Clean and compile
echo "📦 Cleaning and compiling application..."
mvn clean compile -Pstaging

# Build JAR
echo "🔨 Building JAR file for staging..."
mvn package -Pstaging -DskipTests

# Create staging deployment directory
echo "📁 Creating staging deployment directory..."
mkdir -p staging-deployment
cp target/product-service-1.0.0.jar staging-deployment/
cp src/main/resources/application-staging.properties staging-deployment/

# Create staging startup script
cat > staging-deployment/start-staging.sh << 'EOF'
#!/bin/bash
export SPRING_PROFILES_ACTIVE=staging
java -jar product-service-1.0.0.jar --spring.profiles.active=staging
EOF

chmod +x staging-deployment/start-staging.sh

echo "✅ Staging deployment package created in staging-deployment/"
echo "To run staging environment:"
echo "  cd staging-deployment"
echo "  ./start-staging.sh"
echo ""
echo "Staging URLs:"
echo "  Application: http://localhost:8080"
echo "  Swagger UI: http://localhost:8080/swagger-ui.html"
echo "  API Docs: http://localhost:8080/api-docs"
