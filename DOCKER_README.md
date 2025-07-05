# Docker Setup for EmbarkX Microservices

This document explains how to run the entire microservices architecture using Docker Compose.

## Architecture Overview

The Docker setup includes:
- **PostgreSQL Database** (port 5432)
- **pgAdmin** (port 5050) - Database management tool
- **Service Registry** (port 8761) - Eureka server
- **Company Microservice** (port 8082)
- **Review Microservice** (port 8083)
- **Job Microservice** (port 8081)

## Prerequisites

- Docker and Docker Compose installed
- At least 4GB of available RAM
- Ports 5432, 5050, 8761, 8081, 8082, 8083 available

## Quick Start

### 1. Build and Start All Services

```bash
# Build all services and start them
docker-compose up --build

# Or run in detached mode
docker-compose up --build -d
```

### 2. Check Service Status

```bash
# View running containers
docker-compose ps

# View logs
docker-compose logs -f

# View logs for specific service
docker-compose logs -f jobms
```

### 3. Access Services

- **Eureka Dashboard**: http://localhost:8761
- **pgAdmin**: http://localhost:5050 (admin@embarkx.com / admin123)
- **Company API**: http://localhost:8082/companies
- **Review API**: http://localhost:8083/reviews
- **Job API**: http://localhost:8081/jobs

## Database Configuration

### PostgreSQL Setup

The database initialization script (`init-db.sh`) creates three databases:
- `job` - for job microservice
- `company` - for company microservice
- `review` - for review microservice

### pgAdmin Configuration

1. Access pgAdmin at http://localhost:5050
2. Login with:
   - Email: admin@embarkx.com
   - Password: admin123
3. Add new server connection:
   - Host: postgres_container
   - Port: 5432
   - Username: postgres
   - Password: 1234

## Service Dependencies

The services start in this order:
1. **PostgreSQL** (with health check)
2. **Service Registry** (waits for PostgreSQL)
3. **Company & Review Services** (wait for Service Registry)
4. **Job Service** (waits for all other services)

## Environment Variables

### Database Configuration
```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/[database_name]
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: 1234
```

### Eureka Configuration
```yaml
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE: http://service-registry:8761/eureka/
```

## Troubleshooting

### Common Issues

1. **Port Conflicts**
   ```bash
   # Check what's using the ports
   netstat -tulpn | grep :8081
   ```

2. **Database Connection Issues**
   ```bash
   # Check PostgreSQL logs
   docker-compose logs postgres
   
   # Connect to PostgreSQL directly
   docker exec -it postgres_container psql -U postgres
   ```

3. **Service Discovery Issues**
   ```bash
   # Check Eureka logs
   docker-compose logs service-registry
   
   # Check if services are registered
   curl http://localhost:8761/eureka/apps
   ```

### Useful Commands

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: deletes all data)
docker-compose down -v

# Rebuild specific service
docker-compose build jobms

# View resource usage
docker stats

# Access container shell
docker exec -it company_microservice /bin/bash
```

## Development Workflow

### 1. Local Development
```bash
# Start only database and pgAdmin
docker-compose up postgres pgadmin

# Run services locally with IDE
# They will connect to the Docker PostgreSQL
```

### 2. Full Docker Development
```bash
# Start everything
docker-compose up --build

# Make code changes
# Rebuild specific service
docker-compose build jobms
docker-compose up jobms
```

### 3. Production-like Testing
```bash
# Start with production-like settings
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up
```

## Monitoring

### Health Checks
- PostgreSQL: `pg_isready -U postgres`
- All services have health endpoints: `/actuator/health`

### Logs
```bash
# View all logs
docker-compose logs

# Follow specific service
docker-compose logs -f jobms

# View last 100 lines
docker-compose logs --tail=100
```

### Metrics
- Eureka Dashboard: http://localhost:8761
- Service health: http://localhost:8081/actuator/health

## Security Notes

- Default passwords are used for development
- In production, use environment variables for secrets
- Consider using Docker secrets for sensitive data
- pgAdmin should not be exposed in production

## Performance Tips

1. **Resource Limits**: Add memory limits to services
2. **Database Optimization**: Use connection pooling
3. **Caching**: Consider Redis for caching
4. **Monitoring**: Add Prometheus and Grafana

## Next Steps

1. Add API Gateway (Zuul/Spring Cloud Gateway)
2. Add Circuit Breaker (Hystrix/Resilience4j)
3. Add Distributed Tracing (Zipkin/Jaeger)
4. Add Monitoring (Prometheus/Grafana)
5. Add Log Aggregation (ELK Stack) 