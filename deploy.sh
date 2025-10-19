#!/bin/bash

# Master Deployment Script
# This script provides options to deploy to different environments

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to display usage
usage() {
    echo -e "${BLUE}Product Service Deployment Script${NC}"
    echo ""
    echo "Usage: $0 [ENVIRONMENT]"
    echo ""
    echo "Environments:"
    echo "  dev       - Development environment (default)"
    echo "  test      - Test environment"
    echo "  staging   - Staging environment"
    echo "  prod      - Production environment (Azure)"
    echo "  scaled    - Scaled-down production (min: 0, max: 1)"
    echo ""
    echo "Examples:"
    echo "  $0 dev      # Deploy to development"
    echo "  $0 test     # Deploy to test"
    echo "  $0 staging  # Deploy to staging"
    echo "  $0 prod     # Deploy to production (Azure)"
    echo "  $0 scaled   # Deploy scaled-down production (min: 0, max: 1)"
    echo ""
    echo "Environment-specific URLs:"
    echo "  Development: http://localhost:8080/swagger-ui.html"
    echo "  Test:        http://localhost:8081/api/products/health"
    echo "  Staging:     http://localhost:8080/swagger-ui.html"
    echo "  Production:  https://your-app.azurewebsites.net/swagger-ui.html"
    echo "  Scaled:      https://your-app-scaled.azurewebsites.net/swagger-ui.html"
}

# Function to deploy to development
deploy_dev() {
    echo -e "${GREEN}🚀 Deploying to Development Environment...${NC}"
    ./deploy-dev.sh
}

# Function to deploy to test
deploy_test() {
    echo -e "${GREEN}🧪 Deploying to Test Environment...${NC}"
    ./deploy-test.sh
}

# Function to deploy to staging
deploy_staging() {
    echo -e "${GREEN}🎭 Deploying to Staging Environment...${NC}"
    ./deploy-staging.sh
}

# Function to deploy to production
deploy_prod() {
    echo -e "${GREEN}🏭 Deploying to Production Environment (Azure)...${NC}"
    ./deploy-azure.sh
}

# Function to deploy to scaled production
deploy_scaled() {
    echo -e "${GREEN}⚖️ Deploying to Scaled-Down Production Environment (Azure)...${NC}"
    ./deploy-azure-scaled.sh
}

# Main script logic
ENVIRONMENT=${1:-dev}

case $ENVIRONMENT in
    dev|development)
        deploy_dev
        ;;
    test|testing)
        deploy_test
        ;;
    staging|stage)
        deploy_staging
        ;;
    prod|production)
        deploy_prod
        ;;
    scaled|scale)
        deploy_scaled
        ;;
    help|--help|-h)
        usage
        ;;
    *)
        echo -e "${RED}❌ Invalid environment: $ENVIRONMENT${NC}"
        echo ""
        usage
        exit 1
        ;;
esac
