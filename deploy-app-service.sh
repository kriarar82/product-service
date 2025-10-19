#!/bin/bash

# Azure App Service Deployment Script (Alternative - Higher cost but simpler)
# This script deploys the Product Service to Azure App Service

set -e

# Configuration
RESOURCE_GROUP="product-service-rg"
APP_NAME="product-service-app"
PLAN_NAME="product-service-plan"
LOCATION="eastus"

echo "🚀 Starting Azure App Service deployment..."

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

# Create App Service plan (B1 tier for low cost)
echo "📋 Creating App Service plan: $PLAN_NAME"
az appservice plan create \
  --name $PLAN_NAME \
  --resource-group $RESOURCE_GROUP \
  --sku B1 \
  --is-linux

# Create web app
echo "🌐 Creating web app: $APP_NAME"
az webapp create \
  --resource-group $RESOURCE_GROUP \
  --plan $PLAN_NAME \
  --name $APP_NAME \
  --runtime "JAVA:17-java17"

# Configure app settings
echo "⚙️ Configuring app settings..."
az webapp config appsettings set \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --settings \
    SPRING_PROFILES_ACTIVE=azure \
    JAVA_OPTS="-Xms512m -Xmx1024m"

# Build the application
echo "🔨 Building application..."
./mvnw clean package -DskipTests

# Deploy the application
echo "⬆️ Deploying application..."
az webapp deployment source config-zip \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --src target/*.jar

# Get the app URL
APP_URL=$(az webapp show --name $APP_NAME --resource-group $RESOURCE_GROUP --query defaultHostName --output tsv)

echo "✅ Deployment completed successfully!"
echo "🌐 Your app is available at: https://$APP_URL"
echo "🔍 Health check: https://$APP_URL/api/products/health"
echo "📊 Search endpoint: https://$APP_URL/api/products/search"

# Display cost information
echo ""
echo "💰 Estimated monthly cost: $13-55 (B1 tier)"
echo "💡 This uses Azure App Service Basic tier"

