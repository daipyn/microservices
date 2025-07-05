FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create a non-root user
RUN addgroup --system app && adduser --system --ingroup app app

# Change ownership of the app directory
RUN chown -R app:app /app
USER app

# Expose the port
EXPOSE 8761

# Run the application
ENTRYPOINT ["java", "-jar", "target/service-reg-0.0.1-SNAPSHOT.jar"] 