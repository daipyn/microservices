#!/bin/bash

echo "🚀 Starting EmbarkX Microservices with Docker Compose..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check if ports are available
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null ; then
        echo "⚠️  Port $1 is already in use. Please free up the port or change the configuration."
        return 1
    fi
    return 0
}

echo "🔍 Checking port availability..."
check_port 5432 || exit 1
check_port 5050 || exit 1
check_port 8761 || exit 1
check_port 8081 || exit 1
check_port 8082 || exit 1
check_port 8083 || exit 1

echo "✅ All ports are available"

# Make init-db.sh executable
chmod +x init-db.sh

# Build and start services
echo "🏗️  Building and starting services..."
docker-compose up --build -d

echo "⏳ Waiting for services to start..."
sleep 30

# Check service status
echo "📊 Service Status:"
docker-compose ps

echo ""
echo "🎉 Services are starting up!"
echo ""
echo "📱 Access Points:"
echo "   • Eureka Dashboard: http://localhost:8761"
echo "   • pgAdmin: http://localhost:5050 (admin@embarkx.com / admin123)"
echo "   • Company API: http://localhost:8082/companies"
echo "   • Review API: http://localhost:8083/reviews"
echo "   • Job API: http://localhost:8081/jobs"
echo ""
echo "📋 Useful Commands:"
echo "   • View logs: docker-compose logs -f"
echo "   • Stop services: docker-compose down"
echo "   • Restart specific service: docker-compose restart jobms"
echo ""
echo "🔍 To check if all services are healthy:"
echo "   curl http://localhost:8081/actuator/health"
echo "   curl http://localhost:8082/actuator/health"
echo "   curl http://localhost:8083/actuator/health" 