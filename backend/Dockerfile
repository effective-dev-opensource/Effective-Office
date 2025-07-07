# Stage 1: Build the application
FROM gradle:7.6.1-jdk17 AS build

WORKDIR /app

# Copy the gradle files first to leverage Docker cache
COPY gradle/ gradle/
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY gradle.properties .
COPY build-logic/ build-logic/

# Copy the source code
COPY backend/ backend/

# Build the application
RUN ./gradlew :backend:app:bootJar --no-daemon

# Stage 2: Run the application
FROM eclipse-temurin:17-jre

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/backend/app/build/libs/*.jar app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
