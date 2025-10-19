# 🚀 Azure Deployment Guide

This guide provides multiple options for deploying the Product Service to Azure with cost optimization.

## 💰 Cost Comparison

| Option | Monthly Cost | Best For | Complexity |
|--------|-------------|----------|------------|
| **Azure Container Apps** | $0.20-0.50 | Microservices, Auto-scaling | Medium |
| **Azure App Service (B1)** | $13-55 | Traditional web apps | Low |
| **Azure Spring Apps** | $0.20-0.50 | Spring Boot apps | Low |

## 🎯 Recommended: Azure Container Apps (Lowest Cost)

### Prerequisites
- Azure CLI installed
- Docker installed
- Azure account with active subscription

### Quick Deployment
```bash
# Run the automated deployment script
./deploy-azure.sh
```

### Manual Steps
1. **Create Resource Group**
   ```bash
   az group create --name product-service-rg --location eastus
   ```

2. **Create Container Registry**
   ```bash
   az acr create --resource-group product-service-rg --name productserviceregistry --sku Basic
   ```

3. **Build and Push Image**
   ```bash
   docker build -t product-service .
   az acr login --name productserviceregistry
   docker tag product-service productserviceregistry.azurecr.io/product-service:latest
   docker push productserviceregistry.azurecr.io/product-service:latest
   ```

4. **Create Container App**
   ```bash
   az containerapp create \
     --name product-service \
     --resource-group product-service-rg \
     --environment product-service-env \
     --image productserviceregistry.azurecr.io/product-service:latest \
     --target-port 8080 \
     --ingress external \
     --cpu 0.25 \
     --memory 0.5Gi \
     --min-replicas 0 \
     --max-replicas 3
   ```

## 🔧 Alternative: Azure App Service

### Quick Deployment
```bash
# Run the automated deployment script
./deploy-app-service.sh
```

### Manual Steps
1. **Create App Service Plan**
   ```bash
   az appservice plan create --name product-service-plan --resource-group product-service-rg --sku B1 --is-linux
   ```

2. **Create Web App**
   ```bash
   az webapp create --resource-group product-service-rg --plan product-service-plan --name product-service-app --runtime "JAVA:17-java17"
   ```

3. **Deploy Application**
   ```bash
   ./mvnw clean package -DskipTests
   az webapp deployment source config-zip --resource-group product-service-rg --name product-service-app --src target/*.jar
   ```

## ⚙️ Configuration

### Environment Variables
Set these in your Azure deployment:

```bash
# Azure AI Search Configuration
AZURE_SEARCH_ENDPOINT=https://your-search-service.search.windows.net
AZURE_SEARCH_API_KEY=your-api-key
AZURE_SEARCH_INDEX_NAME=products

# Application Configuration
SPRING_PROFILES_ACTIVE=azure
```

### Azure App Service Configuration
```bash
az webapp config appsettings set \
  --resource-group product-service-rg \
  --name product-service-app \
  --settings \
    AZURE_SEARCH_ENDPOINT=https://your-search-service.search.windows.net \
    AZURE_SEARCH_API_KEY=your-api-key \
    AZURE_SEARCH_INDEX_NAME=products \
    SPRING_PROFILES_ACTIVE=azure
```

### Container Apps Configuration
```bash
az containerapp update \
  --name product-service \
  --resource-group product-service-rg \
  --set-env-vars \
    AZURE_SEARCH_ENDPOINT=https://your-search-service.search.windows.net \
    AZURE_SEARCH_API_KEY=your-api-key \
    AZURE_SEARCH_INDEX_NAME=products \
    SPRING_PROFILES_ACTIVE=azure
```

## 🔍 Testing Your Deployment

### Health Check
```bash
curl https://your-app-url/api/products/health
```

### Search Test
```bash
curl -X POST https://your-app-url/api/products/search \
  -H "Content-Type: application/json" \
  -d '{"query": "laptop", "top": 5}'
```

### CSV Upload Test
```bash
curl -X POST https://your-app-url/api/products/upload-csv \
  -F "file=@product_feed.csv"
```

## 📊 Monitoring and Logs

### View Logs (Container Apps)
```bash
az containerapp logs show --name product-service --resource-group product-service-rg --follow
```

### View Logs (App Service)
```bash
az webapp log tail --name product-service-app --resource-group product-service-rg
```

## 💡 Cost Optimization Tips

1. **Use Container Apps** - Scales to 0 when not in use
2. **Set min replicas to 0** - Saves money during idle periods
3. **Use Basic SKU for ACR** - Sufficient for most use cases
4. **Monitor usage** - Set up alerts for unexpected costs
5. **Use Azure Free Tier** - 12 months free for new accounts

## 🚨 Troubleshooting

### Common Issues
1. **Image not found** - Ensure ACR login and image push completed
2. **Health check failing** - Check if app is starting properly
3. **Environment variables** - Verify all required variables are set
4. **Port configuration** - Ensure target port is 8080

### Debug Commands
```bash
# Check container status
az containerapp show --name product-service --resource-group product-service-rg

# View recent logs
az containerapp logs show --name product-service --resource-group product-service-rg --tail 100

# Check app settings
az webapp config appsettings list --name product-service-app --resource-group product-service-rg
```

## 🔄 Updates and Maintenance

### Update Application
1. Build new image
2. Push to ACR
3. Update container app with new image

```bash
# Build and push new version
docker build -t product-service:v2 .
docker tag product-service:v2 productserviceregistry.azurecr.io/product-service:v2
docker push productserviceregistry.azurecr.io/product-service:v2

# Update container app
az containerapp update \
  --name product-service \
  --resource-group product-service-rg \
  --image productserviceregistry.azurecr.io/product-service:v2
```

## 📞 Support

For issues with Azure deployment:
- Check Azure documentation
- Review application logs
- Verify resource group permissions
- Ensure all required services are enabled

