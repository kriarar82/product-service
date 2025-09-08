# Product Service

A Spring Boot microservice that provides product information via REST API with JSON-LD format and Azure AI Search integration.

## Features

- REST API endpoint to get product information by ID
- JSON-LD formatted responses following Schema.org Product schema
- Azure AI Search SDK integration for product search
- Mock data fallback when Azure AI Search is not configured
- Health check endpoint

## API Endpoints

### GET /api/products/{productId}
Retrieves product information by ID in JSON-LD format.

**Example Request:**
```bash
curl -X GET http://localhost:8080/api/products/PROD-001
```

**Example Response:**
```json
{
  "@context": "https://schema.org/",
  "@type": "Product",
  "id": "PROD-001",
  "name": "Sample Product PROD-001",
  "description": "This is a sample product description for product PROD-001",
  "brand": "Sample Brand",
  "category": "Electronics",
  "price": 99.99,
  "currency": "USD",
  "sku": "SKU-PROD-001",
  "image": "https://example.com/images/product-PROD-001.jpg",
  "tags": ["electronics", "sample", "demo"],
  "inStock": true,
  "stockQuantity": 100,
  "manufacturer": "Sample Manufacturer",
  "model": "Model-PROD-001",
  "specifications": ["Color: Black", "Weight: 1.5 lbs", "Dimensions: 10x8x2 inches"],
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-31T15:30:00"
}
```

### GET /api/products/health
Health check endpoint.

**Example Request:**
```bash
curl -X GET http://localhost:8080/api/products/health
```

**Example Response:**
```
Product Service is running
```

## Configuration

### Azure AI Search Integration

To enable Azure AI Search integration, configure the following properties in `application.properties`:

```properties
azure.search.endpoint=https://your-search-service.search.windows.net
azure.search.api-key=your-api-key
azure.search.index-name=products
```

When Azure AI Search is not configured, the service will return mock data.

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build the application
```bash
mvn clean compile
```

### Run the application
```bash
mvn spring-boot:run
```

The service will start on port 8080.

### Build JAR file
```bash
mvn clean package
```

### Run JAR file
```bash
java -jar target/product-service-1.0.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/productservice/
│   │       ├── ProductServiceApplication.java
│   │       ├── config/
│   │       │   └── AzureSearchConfig.java
│   │       ├── controller/
│   │       │   └── ProductController.java
│   │       ├── model/
│   │       │   └── Product.java
│   │       └── service/
│   │           └── ProductService.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/example/productservice/
```

## Dependencies

- Spring Boot 3.2.0
- Azure AI Search SDK 11.6.0
- Jackson for JSON processing
- Spring Boot Web for REST API

## JSON-LD Schema

The product responses follow the Schema.org Product schema with the following structure:
- `@context`: Points to Schema.org vocabulary
- `@type`: Specifies the type as "Product"
- Standard product properties like name, description, price, etc.
- Additional metadata like creation/update timestamps

