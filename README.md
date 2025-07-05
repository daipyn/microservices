# Microservices Architecture - EmbarkX

This project consists of 4 Spring Boot microservices with Eureka service discovery.

## Services Overview

### 1. Service Registry (Eureka Server)
- **Port**: 8761
- **Application Name**: service-reg
- **Purpose**: Service discovery and registration

### 2. Company Microservice
- **Port**: 8082
- **Application Name**: companyms
- **Purpose**: Manages company information
- **Database**: PostgreSQL (company database)

### 3. Review Microservice
- **Port**: 8083
- **Application Name**: reviewms
- **Purpose**: Manages company reviews
- **Database**: PostgreSQL (review database)

### 4. Job Microservice
- **Port**: 8081
- **Application Name**: jobms
- **Purpose**: Manages job listings with company and review integration
- **Database**: PostgreSQL (job database)
- **Features**: Uses Feign clients to communicate with other services

## Fixed Issues

### 1. POM.xml Issues
- ✅ Removed duplicate `spring-boot-starter-data-jpa` dependencies
- ✅ Added proper Lombok configuration to all services
- ✅ Consistent build plugin configuration

### 2. Feign Client Issues
- ✅ Fixed service names in Feign clients to match actual application names
- ✅ Proper injection of Feign clients in JobServiceImpl
- ✅ Removed unused RestTemplate code

### 3. Configuration Issues
- ✅ Added proper JPA dialect configuration
- ✅ Enhanced Eureka client configuration
- ✅ Improved application properties structure
- ✅ Added `eureka.instance.prefer-ip-address=true` for better service discovery

### 4. Model Issues
- ✅ Removed JPA annotations from external models used by Feign clients
- ✅ Added proper Lombok annotations to external models

## Database Setup

### PostgreSQL Configuration
All services use PostgreSQL with the following configuration:
- **Host**: localhost
- **Port**: 5432
- **Username**: postgres
- **Password**: 1234

### Required Databases
1. `company` - for companyms service
2. `review` - for reviewms service  
3. `job` - for jobms service

## Running the Services

### Prerequisites
1. Java 17
2. Maven
3. PostgreSQL running on localhost:5432
4. Create the required databases

### Startup Order
1. **Service Registry** (port 8761)
2. **Company Microservice** (port 8082)
3. **Review Microservice** (port 8083)
4. **Job Microservice** (port 8081)

### Commands to Run
```bash
# Start Service Registry
cd service-reg
mvn spring-boot:run

# Start Company Microservice
cd companyms
mvn spring-boot:run

# Start Review Microservice
cd reviewms
mvn spring-boot:run

# Start Job Microservice
cd jobms
mvn spring-boot:run
```

## API Endpoints

### Company Service (8082)
- `GET /companies` - Get all companies
- `POST /companies` - Create a company
- `GET /companies/{id}` - Get company by ID
- `PUT /companies/{id}` - Update company
- `DELETE /companies/{id}` - Delete company

### Review Service (8083)
- `GET /reviews?companyId={id}` - Get reviews by company ID
- `POST /reviews?companyId={id}` - Create a review
- `GET /reviews/{reviewId}?companyId={id}` - Get review by ID
- `PUT /reviews/{reviewId}?companyId={id}` - Update review
- `DELETE /reviews/{reviewId}?companyId={id}` - Delete review

### Job Service (8081)
- `GET /jobs` - Get all jobs with company and review data
- `POST /jobs` - Create a job
- `GET /jobs/{id}` - Get job by ID with company and review data
- `PUT /jobs/{id}` - Update job
- `DELETE /jobs/{id}` - Delete job

## Service Discovery
- **Eureka Dashboard**: http://localhost:8761
- All services will register automatically with the Eureka server

## Development Notes
- All services use Hibernate with `ddl-auto=update`
- SQL logging is enabled for development
- Services are configured to prefer IP addresses for better containerization support 