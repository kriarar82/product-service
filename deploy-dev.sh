#!/bin/bash

# Development Environment Deployment Script
# This script builds and runs the application in development mode

set -e

echo "🚀 Starting Development Environment Deployment..."

# Set environment variables
export SPRING_PROFILES_ACTIVE=dev
export AZURE_SEARCH_API_KEY=${AZURE_SEARCH_API_KEY:-"dev-api-key"}

# Clean and compile
echo "📦 Cleaning and compiling application..."
mvn clean compile -Pdev

# Run tests
echo "🧪 Running tests..."
mvn test -Pdev

# Build JAR
echo "🔨 Building JAR file..."
mvn package -Pdev -DskipTests

# Start the application
echo "🏃 Starting application in development mode..."
echo "Profile: dev"
echo "Port: 8080"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo "API Docs: http://localhost:8080/api-docs"

java -jar target/product-service-1.0.0.jar --spring.profiles.active=dev
