#!/bin/bash

# Azure Container Apps Deployment Script - Scaled Down Configuration
# This script deploys the Product Service to Azure Container Apps with min 0, max 1 replicas

set -e

# Configuration
RESOURCE_GROUP="product-service-rg"
LOCATION="eastus"
CONTAINER_APP_NAME="product-service-scaled"
IMAGE_NAME="product-service"
REGISTRY_NAME="productservice$(date +%s)"

echo "🚀 Starting Azure deployment with scaled-down configuration..."

# Check if Azure CLI is installed
if ! command -v az &> /dev/null; then
    echo "❌ Azure CLI is not installed. Please install it first:"
    echo "   https://docs.microsoft.com/en-us/cli/azure/install-azure-cli"
    exit 1
fi

# Login to Azure (if not already logged in)
echo "🔐 Checking Azure login status..."
if ! az account show &> /dev/null; then
    echo "Please log in to Azure..."
    az login
fi

# Create resource group
echo "📦 Creating resource group: $RESOURCE_GROUP"
az group create --name $RESOURCE_GROUP --location $LOCATION

# Create Azure Container Registry
echo "🐳 Creating Azure Container Registry: $REGISTRY_NAME"
az acr create --resource-group $RESOURCE_GROUP --name $REGISTRY_NAME --sku Basic --admin-enabled true

# Get ACR login server
ACR_LOGIN_SERVER=$(az acr show --name $REGISTRY_NAME --resource-group $RESOURCE_GROUP --query loginServer --output tsv)
echo "📋 ACR Login Server: $ACR_LOGIN_SERVER"

# Login to ACR
echo "🔑 Logging into Azure Container Registry..."
az acr login --name $REGISTRY_NAME

# Build application with production profile
echo "🔨 Building application with production profile..."
./mvnw clean package -Pprod -DskipTests

# Build and push Docker image
echo "🐳 Building Docker image..."
docker build -t $IMAGE_NAME .

# Tag image for ACR
docker tag $IMAGE_NAME $ACR_LOGIN_SERVER/$IMAGE_NAME:latest

# Push image to ACR
echo "⬆️ Pushing image to Azure Container Registry..."
docker push $ACR_LOGIN_SERVER/$IMAGE_NAME:latest

# Create Container Apps environment
echo "🌍 Creating Container Apps environment..."
az containerapp env create \
  --name "product-service-env" \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION

# Create Container App with scaled-down configuration
echo "🚀 Creating Container App with scaled-down configuration..."
az containerapp create \
  --name $CONTAINER_APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --environment "product-service-env" \
  --image $ACR_LOGIN_SERVER/$IMAGE_NAME:latest \
  --target-port 8080 \
  --ingress external \
  --cpu 0.25 \
  --memory 0.5Gi \
  --min-replicas 0 \
  --max-replicas 1 \
  --env-vars "SPRING_PROFILES_ACTIVE=prod"

# Get the app URL
APP_URL=$(az containerapp show --name $CONTAINER_APP_NAME --resource-group $RESOURCE_GROUP --query properties.configuration.ingress.fqdn --output tsv)

echo "✅ Scaled-down deployment completed successfully!"
echo "🌐 Your app is available at: https://$APP_URL"
echo "🔍 Health check: https://$APP_URL/api/products/health"
echo "📊 Search endpoint: https://$APP_URL/api/products/search"
echo "📚 Swagger UI: https://$APP_URL/swagger-ui.html"

# Display cost information
echo ""
echo "💰 Estimated monthly cost: $0.05 - $0.15 (ultra-scaled configuration)"
echo "💡 This uses Azure Container Apps with auto-scaling (min: 0, max: 1)"
echo "💡 Perfect for development, testing, or very low-traffic scenarios"
echo "💡 App will scale to 0 when not in use, saving costs"
echo ""
echo "📊 Scaling behavior:"
echo "   - Scales to 0 when no requests for 5+ minutes"
echo "   - Scales to 1 when first request arrives (cold start ~10-30s)"
echo "   - Maximum 1 instance at any time"






