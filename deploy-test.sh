#!/bin/bash

# Test Environment Deployment Script
# This script builds and runs the application in test mode

set -e

echo "🚀 Starting Test Environment Deployment..."

# Set environment variables
export SPRING_PROFILES_ACTIVE=test
export AZURE_SEARCH_API_KEY=${AZURE_SEARCH_API_KEY:-"test-api-key"}

# Clean and compile
echo "📦 Cleaning and compiling application..."
mvn clean compile -Ptest

# Run tests
echo "🧪 Running tests..."
mvn test -Ptest

# Build JAR
echo "🔨 Building JAR file for test..."
mvn package -Ptest -DskipTests

# Create test deployment directory
echo "📁 Creating test deployment directory..."
mkdir -p test-deployment
cp target/product-service-1.0.0.jar test-deployment/
cp src/main/resources/application-test.properties test-deployment/

# Create test startup script
cat > test-deployment/start-test.sh << 'EOF'
#!/bin/bash
export SPRING_PROFILES_ACTIVE=test
java -jar product-service-1.0.0.jar --spring.profiles.active=test
EOF

chmod +x test-deployment/start-test.sh

echo "✅ Test deployment package created in test-deployment/"
echo "To run test environment:"
echo "  cd test-deployment"
echo "  ./start-test.sh"
echo ""
echo "Test URLs:"
echo "  Application: http://localhost:8081"
echo "  Health Check: http://localhost:8081/api/products/health"
