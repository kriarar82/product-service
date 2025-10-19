# Environment Configuration Guide

This document describes the different environment configurations available for the Product Service application.

## Available Environments

### 1. Development (`dev`)
- **Profile**: `dev`
- **Port**: 8080
- **Purpose**: Local development and testing
- **Features**:
  - Debug logging enabled
  - Swagger UI enabled with try-it-out
  - CORS configured for local frontend development
  - H2 console available (if database is added)
  - All management endpoints exposed

**Configuration File**: `application-dev.properties`

**Deploy**: `./deploy.sh dev` or `./deploy-dev.sh`

**URLs**:
- Application: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

### 2. Test (`test`)
- **Profile**: `test`
- **Port**: 8081
- **Purpose**: Automated testing and CI/CD
- **Features**:
  - Minimal logging
  - Swagger UI disabled
  - Limited CORS configuration
  - Health check only
  - Optimized for test performance

**Configuration File**: `application-test.properties`

**Deploy**: `./deploy.sh test` or `./deploy-test.sh`

**URLs**:
- Application: http://localhost:8081
- Health Check: http://localhost:8081/api/products/health

### 3. Staging (`staging`)
- **Profile**: `staging`
- **Port**: 8080
- **Purpose**: Pre-production testing and validation
- **Features**:
  - Production-like logging
  - Swagger UI enabled
  - Staging-specific CORS
  - Azure monitoring enabled
  - Limited management endpoints

**Configuration File**: `application-staging.properties`

**Deploy**: `./deploy.sh staging` or `./deploy-staging.sh`

**URLs**:
- Application: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

### 4. Production (`prod`)
- **Profile**: `prod`
- **Port**: 8080
- **Purpose**: Live production environment
- **Features**:
  - Production logging levels
  - Swagger UI enabled (try-it-out disabled)
  - Azure monitoring and metrics
  - Optimized performance settings
  - Security-focused configuration

**Configuration File**: `application-azure.properties`

**Deploy**: `./deploy.sh prod` or `./deploy-azure.sh`

**URLs**:
- Application: https://your-app.azurewebsites.net
- Swagger UI: https://your-app.azurewebsites.net/swagger-ui.html
- API Docs: https://your-app.azurewebsites.net/api-docs

## Environment Variables

Each environment can be configured using environment variables:

### Azure AI Search Configuration
```bash
export AZURE_SEARCH_ENDPOINT="https://your-search-service.search.windows.net"
export AZURE_SEARCH_API_KEY="your-api-key"
export AZURE_SEARCH_INDEX_NAME="your-index-name"
```

### Spring Profile
```bash
export SPRING_PROFILES_ACTIVE="dev|test|staging|prod"
```

## Maven Profiles

The application uses Maven profiles to build for different environments:

```bash
# Development (default)
mvn clean package -Pdev

# Test
mvn clean package -Ptest

# Staging
mvn clean package -Pstaging

# Production
mvn clean package -Pprod
```

## Deployment Commands

### Quick Deploy
```bash
# Deploy to development (default)
./deploy.sh

# Deploy to specific environment
./deploy.sh dev
./deploy.sh test
./deploy.sh staging
./deploy.sh prod
```

### Individual Environment Scripts
```bash
# Development
./deploy-dev.sh

# Test
./deploy-test.sh

# Staging
./deploy-staging.sh

# Production (Azure)
./deploy-azure.sh
```

## Configuration Differences

| Feature | Dev | Test | Staging | Prod |
|---------|-----|------|---------|------|
| Port | 8080 | 8081 | 8080 | 8080 |
| Logging Level | DEBUG | WARN | INFO | INFO |
| Swagger UI | ✅ (try-it-out) | ❌ | ✅ (try-it-out) | ✅ (no try-it-out) |
| CORS | Local dev | Limited | Staging domains | Production domains |
| Management Endpoints | All | Health only | Limited | Limited |
| Monitoring | Basic | None | Azure | Azure |
| Thread Pool | 50 max | 25 max | 100 max | 200 max |

## Environment-Specific Features

### Development
- Full debugging capabilities
- Interactive Swagger UI
- H2 database console (if configured)
- All actuator endpoints

### Test
- Optimized for speed
- Minimal resource usage
- No external dependencies
- Focused on testing

### Staging
- Production-like configuration
- Monitoring enabled
- Performance testing ready
- Pre-production validation

### Production
- Security hardened
- Performance optimized
- Full monitoring
- Azure integration

## Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure the correct port is available for each environment
2. **Profile not active**: Check `SPRING_PROFILES_ACTIVE` environment variable
3. **Azure credentials**: Verify Azure Search credentials for staging/prod
4. **CORS issues**: Check CORS configuration for your frontend domain

### Debug Commands

```bash
# Check active profile
echo $SPRING_PROFILES_ACTIVE

# Check application properties
java -jar target/product-service-1.0.0.jar --spring.profiles.active=dev --debug

# View configuration
curl http://localhost:8080/actuator/env
```

## Best Practices

1. **Always use environment variables** for sensitive configuration
2. **Test in staging** before production deployment
3. **Monitor logs** in each environment
4. **Use appropriate logging levels** for each environment
5. **Keep configurations minimal** and focused on environment needs
