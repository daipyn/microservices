# Job Board Microservices Application

A distributed job board system built with Spring Boot microservices architecture, enabling job posting, company management, and review functionalities.

## Architecture Overview

The application consists of the following microservices:

- **Gateway Service** (Port: 8084): API Gateway handling routing and load balancing
- **Service Registry** (Port: 8761): Eureka Server for service discovery
- **Config Server** (Port: 8888): Centralized configuration management
- **Job Service** (Port: 8081): Manages job postings with Elasticsearch integration
- **Company Service** (Port: 8082): Handles company profiles and information
- **Review Service** (Port: 8083): Manages company reviews with async processing

### Technical Stack

- **Spring Boot**: Core framework for microservices
- **Spring Cloud**: Service discovery, configuration, and API gateway
- **PostgreSQL**: Primary database
- **Elasticsearch**: Search engine for job postings
- **RabbitMQ**: Message broker for asynchronous communication
- **Zipkin**: Distributed tracing
- **Docker**: Containerization and deployment
- **Resilience4j**: Circuit breaking and fault tolerance

## Prerequisites

- Docker and Docker Compose
- JDK 17 or later
- Maven
- Git

## Getting Started

1. Clone the repository:
```bash
git clone <repository-url>
cd <project-directory>
```

2. Build all services:
```bash
./mvnw clean package -DskipTests
```

3. Start the infrastructure services:
```bash
cd service-reg
docker-compose up -d
```

4. Verify the services:
```bash
docker-compose ps
```

## Service URLs

### Main Services
- Gateway: http://localhost:8084
- Eureka Dashboard: http://localhost:8761
- Config Server: http://localhost:8888

### Infrastructure
- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601
- RabbitMQ Management: http://localhost:15672
- Zipkin: http://localhost:9411
- PgAdmin: http://localhost:5050

## API Documentation

### Job Service
- `GET /jobs`: List all jobs
- `POST /jobs`: Create a new job
- `GET /jobs/{id}`: Get job details
- `PUT /jobs/{id}`: Update job
- `DELETE /jobs/{id}`: Delete job
- `GET /jobs/search`: Search jobs with filters

### Company Service
- `GET /companies`: List all companies
- `POST /companies`: Create a new company
- `GET /companies/{id}`: Get company details
- `PUT /companies/{id}`: Update company
- `DELETE /companies/{id}`: Delete company

### Review Service
- `GET /reviews`: List all reviews
- `POST /reviews`: Create a new review
- `GET /reviews/{id}`: Get review details
- `GET /reviews/company/{companyId}`: Get company reviews

## Configuration

### Database
PostgreSQL is configured with the following databases:
- `job`: Job service database
- `company`: Company service database
- `review`: Review service database

Default credentials:
- Username: postgres
- Password: 1234

### Elasticsearch
- Single-node configuration
- Security disabled for development
- Kibana included for visualization

### RabbitMQ
- Management interface enabled
- Default credentials:
  - Username: guest
  - Password: guest

## Monitoring and Tracing

- **Zipkin**: Distributed tracing visualization
- **Actuator**: Service health monitoring
- **Kibana**: Log visualization and analysis

## Resilience Patterns

- Circuit Breakers configured for inter-service communication
- Retry mechanisms for transient failures
- Rate limiting for API endpoints

## Development Notes

- Services use Docker profiles for containerized environments
- Local development can use application.properties
- Containerized deployment uses application-docker.properties

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

[Add your license here] 