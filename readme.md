# API Factory - Insurance Management System

## Overview

API Factory is a RESTful backend system for managing insurance clients and contracts. It provides endpoints for creating, reading, updating, and deleting clients and their associated contracts, with a focus on clean architecture and performance optimization.

## Tech Stack

### Backend Framework
- **Java 25** - Latest Java version with modern language features
- **Spring Boot 3.5.6** - Main application framework with auto-configuration
- **Spring Web MVC** - RESTful API framework
- **Spring Data JPA** - Database abstraction layer
- **Spring Validation** - Input validation framework
- **Spring Boot Actuator** - Application monitoring and management

### Database & Persistence
- **PostgreSQL 16** - Primary database for data persistence
- **Hibernate 6.6.29** - JPA implementation for ORM
- **Flyway** - Database migration tool
- **HikariCP** - High-performance JDBC connection pool

### API Documentation
- **SpringDoc OpenAPI 3** - Automatic API documentation generation
- **Swagger UI** - Interactive API documentation interface

### Testing
- **JUnit 5** - Unit testing framework
- **Mockito 5** - Mocking framework for unit tests
- **Spring Boot Test** - Integration testing support
- **MockMvc** - Spring MVC testing framework
- **AssertJ** - Fluent assertion library
- **TestContainers** - Integration testing with real databases

### Build & Dependency Management
- **Apache Maven 3.9** - Build automation and dependency management
- **Maven Wrapper** - Consistent Maven version across environments
- **Spotless** - Code formatting and style checking

### Containerization & Deployment
- **Docker** - Container platform for application packaging
- **Docker Compose** - Multi-container application orchestration


## Architecture

This application follows a clean architecture pattern with domain-driven design principles. It consists of three main layers:

1. **Domain Layer**: Contains business entities, value objects, and domain services
2. **Application Layer**: Implements use cases and application services
3. **Infrastructure Layer**: Handles persistence, web controllers, and external integrations

The system uses Spring Boot for dependency injection and configuration, JPA/Hibernate for data persistence, and follows RESTful API design principles with proper HTTP status codes.

## Quick Start

The application starts with a single command on any platform. No setup required!

### Windows Users
Open Command Prompt or PowerShell and run:
```cmd
start.bat
```

### Linux/macOS Users
Open your terminal and run:
```bash
chmod +x start.sh
./start.sh
```

### All Platforms (Universal)
If you have Docker installed, you can run from any terminal:
```bash
POSTGRES_DB=newdb POSTGRES_USER=newuser POSTGRES_PASSWORD=newpass docker-compose up --build
```
## API Documentation

### Interactive Swagger UI
Once the application is running, you can access the interactive API documentation at:
**[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

### OpenAPI Specification
The raw OpenAPI 3.0 specification is available at:
**[http://localhost:8080/api-docs](http://localhost:8080/api-docs)**

### What You Can Do with Swagger
- **Explore all API endpoints** with detailed descriptions
- **Try out API requests** directly from your browser
- **View request/response schemas** and examples
- **Download the OpenAPI specification** for client generation
- **Test all endpoints** without needing Postman or curl


## Features

### Client Management
- Create person and company clients with validation
- Retrieve client details with their contracts
- Update client information (excluding birthdate and company identifier)
- Delete clients (automatically terminates all contracts)

### Contract Management
- Create contracts with custom or default dates
- Update contract costs with automatic update date tracking
- Retrieve active contracts with pagination and filtering
- Calculate total cost of active contracts with optimized performance

### Data Validation
- Email format validation
- Phone number validation (E.164 format)
- Date validation (ISO 8601 format)
- Number validation with range checks

## API Documentation

Once the application is running, you can access the Swagger UI at:
`http://localhost:8080/swagger-ui.html`

## Testing

### Running Tests
```bash
./mvnw test
```

### Postman Collection
A comprehensive Postman collection is available in the repository. Import it to test all API endpoints:

1. Open Postman
2. Click "Import" and select the `API Factory - Insurance Management.postman_collection.json` file
3. Update the `baseUrl` environment variable to `http://localhost:8080`
4. Run the requests in the collection to test the API

### Key Test Scenarios
1. **Client Management**:
    - Create person and company clients
    - Retrieve client details
    - Update client information
    - Delete clients (which terminates their contracts)

2. **Contract Management**:
    - Create contracts with custom or default dates
    - Update contract costs
    - Retrieve active contracts
    - Calculate total cost of active contracts

3. **Error Handling**:
    - Invalid email formats
    - Invalid date formats
    - Negative cost amounts
    - Non-existent resources

## Database Schema

The application uses PostgreSQL with the following main tables:
- `clients`: Stores person and company client information
- `contracts`: Stores contract details linked to clients

## Environment Variables

The application uses the following environment variables:
- `POSTGRES_DB`: Database name (default: vaudoise)
- `POSTGRES_USER`: Database username (default: postgres)
- `POSTGRES_PASSWORD`: Database password (default: blabla)

## Performance Considerations

The endpoint for calculating the total cost of active contracts (`/api/v1/contracts/active/total-cost`) is optimized for performance with database-level aggregation.

## Troubleshooting

### Build Issues
If you encounter a Spotless formatting error during the build:
```bash
./mvnw spotless:apply
./mvnw clean package -DskipTests
```

### Database Connection Issues
If you encounter a "password authentication failed" error:
```bash
docker-compose down
docker volume rm api-factory_postgres-data
docker-compose up --build
```

### API Testing Issues
If requests work in Swagger but not in Postman:
1. Add these headers to your Postman requests:
   ```
   Content-Type: application/json
   Accept: application/json
   ```
2. Ensure you're using the correct base URL: `http://localhost:8080/api/v1`

