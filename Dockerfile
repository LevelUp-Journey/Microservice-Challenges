# Multi-stage build for optimized production image
FROM --platform=linux/amd64 eclipse-temurin:24-jdk AS build

# Set working directory
WORKDIR /app

# Copy Maven configuration files first (better caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies (cached layer if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code and proto files
COPY src ./src

# Build the application (skip tests for faster builds)
RUN ./mvnw clean package -DskipTests -B

# Runtime stage
FROM --platform=linux/amd64 eclipse-temurin:24-jre

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/target/microservice-challenges-*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appuser /app
USER appuser

# Expose port (Azure Container Apps and Render use $PORT env variable)
EXPOSE ${PORT:-8083}

# Health check (use PORT env variable if set)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8083}/actuator/health || exit 1

# Run the application with optimized JVM settings for containers
# Use shell form to allow environment variable substitution
CMD java -server \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -Djava.security.egd=file:/dev/./urandom \
    -Dserver.port=${PORT:-8083} \
    -jar app.jar
