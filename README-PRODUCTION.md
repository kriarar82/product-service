# Production Setup Guide

This guide explains how to set up and run the Product Service in production mode.

## Environment Configuration

### 1. Copy Environment Template
```bash
cp production.env.template production.env
```

### 2. Update Environment Variables
Edit `production.env` with your actual Azure Search configuration:

```bash
# Azure AI Search Configuration
AZURE_SEARCH_ENDPOINT=https://your-actual-search-service.search.windows.net
AZURE_SEARCH_API_KEY=your-actual-api-key
AZURE_SEARCH_INDEX_NAME=your-index-name

# Azure AI Search Configuration for Apparel Products
AZURE_APPAREL_SEARCH_ENDPOINT=https://your-actual-apparel-search-service.search.windows.net
AZURE_APPAREL_SEARCH_API_KEY=your-actual-apparel-api-key
AZURE_APPAREL_SEARCH_INDEX_NAME=your-apparel-index-name
```

### 3. Load Environment Variables
```bash
export $(cat production.env | xargs)
```

## Building and Running

### Build Production JAR
```bash
./mvnw clean package -Pprod -DskipTests
```

### Run Production Server
```bash
# Option 1: Using the deployment script
./deploy-production.sh

# Option 2: Direct execution
java -Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseStringDeduplication \
     -jar target/product-service-1.0.0.jar \
     --spring.profiles.active=prod
```

## Production Features

- **Optimized JVM Settings**: 512MB-1GB heap, G1GC, String deduplication
- **Production Logging**: INFO level, structured logging
- **Performance Tuning**: 200 max threads, 8192 max connections
- **Security**: Error details hidden, CORS configured
- **Monitoring**: Health checks, metrics enabled

## Service Endpoints

- **Health Check**: `http://localhost:8080/api/products/health`
- **API Documentation**: `http://localhost:8080/swagger-ui/index.html`
- **Search API**: `http://localhost:8080/api/products/search`
- **Apparel Search**: `http://localhost:8080/api/products/apparel/semantic-search`

## Management Commands

```bash
# View logs
tail -f logs/production.log

# Check service status
ps aux | grep product-service

# Stop service
kill $(cat .service.pid)

# Restart service
./deploy-production.sh
```

## Security Notes

- Never commit `production.env` or any files containing API keys
- Use environment variables for all sensitive configuration
- The `.gitignore` file excludes sensitive files from version control
- API keys are masked in the configuration files using environment variable placeholders

## Troubleshooting

### Service Won't Start
1. Check if port 8080 is available: `lsof -i:8080`
2. Verify environment variables are set: `echo $AZURE_SEARCH_ENDPOINT`
3. Check logs: `tail -f logs/production.log`

### Azure Search Connection Issues
1. Verify API keys are correct
2. Check network connectivity to Azure Search endpoints
3. Ensure Azure Search service is running and accessible

### Performance Issues
1. Monitor JVM memory usage
2. Check thread pool utilization
3. Review Azure Search query performance
