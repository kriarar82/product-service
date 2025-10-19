#!/bin/bash

# Production Deployment Script for Product Service
# This script deploys the Product Service with production configuration

set -e  # Exit on any error

echo "🚀 Starting Production Deployment of Product Service..."

# Configuration
JAR_FILE="target/product-service-1.0.0.jar"
SERVICE_NAME="product-service"
PROFILE="prod"
PORT=8080

# Check if JAR file exists
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ JAR file not found: $JAR_FILE"
    echo "Please run: ./mvnw clean package -Pprod -DskipTests"
    exit 1
fi

# Check if required environment variables are set
echo "🔍 Checking environment variables..."

if [ -z "$AZURE_SEARCH_ENDPOINT" ]; then
    echo "⚠️  WARNING: AZURE_SEARCH_ENDPOINT not set, using default placeholder"
    export AZURE_SEARCH_ENDPOINT="https://your-search-service.search.windows.net"
fi

if [ -z "$AZURE_SEARCH_API_KEY" ]; then
    echo "⚠️  WARNING: AZURE_SEARCH_API_KEY not set, using default placeholder"
    export AZURE_SEARCH_API_KEY="your-api-key"
fi

if [ -z "$AZURE_APPAREL_SEARCH_ENDPOINT" ]; then
    echo "⚠️  WARNING: AZURE_APPAREL_SEARCH_ENDPOINT not set, using default placeholder"
    export AZURE_APPAREL_SEARCH_ENDPOINT="https://your-apparel-search-service.search.windows.net"
fi

if [ -z "$AZURE_APPAREL_SEARCH_API_KEY" ]; then
    echo "⚠️  WARNING: AZURE_APPAREL_SEARCH_API_KEY not set, using default placeholder"
    export AZURE_APPAREL_SEARCH_API_KEY="your-apparel-api-key"
fi

# Set production environment variables
export SPRING_PROFILES_ACTIVE=$PROFILE
export JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseStringDeduplication"
export SERVER_PORT=$PORT

echo "📋 Production Configuration:"
echo "   Profile: $PROFILE"
echo "   Port: $PORT"
echo "   JAR: $JAR_FILE"
echo "   Azure Search Endpoint: $AZURE_SEARCH_ENDPOINT"
echo "   Azure Apparel Search Endpoint: $AZURE_APPAREL_SEARCH_ENDPOINT"

# Kill any existing process on the port
echo "🔄 Stopping any existing service on port $PORT..."
lsof -ti:$PORT | xargs kill -9 2>/dev/null || echo "No existing process found on port $PORT"

# Wait a moment for the port to be released
sleep 2

# Create logs directory if it doesn't exist
mkdir -p logs

# Start the service
echo "🚀 Starting $SERVICE_NAME in production mode..."
echo "   Command: java $JAVA_OPTS -jar $JAR_FILE --spring.profiles.active=$PROFILE"

# Run in background and capture PID
nohup java $JAVA_OPTS -jar $JAR_FILE --spring.profiles.active=$PROFILE > logs/production.log 2>&1 &
SERVICE_PID=$!

# Save PID for later use
echo $SERVICE_PID > .service.pid

echo "✅ Service started with PID: $SERVICE_PID"
echo "📝 Logs are being written to: logs/production.log"

# Wait for service to start
echo "⏳ Waiting for service to start..."
sleep 10

# Check if service is running
if ps -p $SERVICE_PID > /dev/null; then
    echo "✅ Service is running successfully!"
    
    # Test health endpoint
    echo "🔍 Testing health endpoint..."
    if curl -s http://localhost:$PORT/api/products/health > /dev/null; then
        echo "✅ Health check passed!"
        echo "🌐 Service is available at: http://localhost:$PORT"
        echo "📚 API Documentation: http://localhost:$PORT/swagger-ui/index.html"
    else
        echo "⚠️  Health check failed, but service is running. Check logs for details."
    fi
else
    echo "❌ Service failed to start. Check logs for details."
    exit 1
fi

echo ""
echo "🎉 Production deployment completed successfully!"
echo ""
echo "📊 Service Information:"
echo "   PID: $SERVICE_PID"
echo "   Port: $PORT"
echo "   Profile: $PROFILE"
echo "   Logs: logs/production.log"
echo ""
echo "🔧 Management Commands:"
echo "   Stop service: kill $SERVICE_PID"
echo "   View logs: tail -f logs/production.log"
echo "   Check status: ps -p $SERVICE_PID"
echo ""
echo "🌐 Access URLs:"
echo "   Health Check: http://localhost:$PORT/api/products/health"
echo "   API Docs: http://localhost:$PORT/swagger-ui/index.html"
echo "   API Endpoint: http://localhost:$PORT/api/products/search"
