# Effective Office

A Spring Boot Kotlin multi-module project for managing office resources.

## Project Structure

This project is organized as a multi-module Gradle project:

- **backend**: Contains all backend-related modules
  - **app**: Main application module with REST API controllers
  - **domain**: Domain model module with business entities
  - **repository**: Database access module with JPA entities and repositories

## Technologies

- **Language**: Kotlin 2.1.21
- **Framework**: Spring Boot 3.5.0
- **Build Tool**: Gradle with Kotlin DSL
- **Database**: PostgreSQL
- **Migration**: Flyway
- **Documentation**: SpringDoc OpenAPI
- **Testing**: JUnit 5, MockK, Testcontainers
- **Containerization**: Docker, Docker Compose

## Dependencies

This project uses a version catalog (`libs.versions.toml`) for centralized dependency management. Below is an explanation of why each library is used:

### Kotlin Libraries
- **kotlin-stdlib**: Standard library for Kotlin that provides essential functions and types
- **kotlin-reflect**: Kotlin reflection library for runtime type inspection and manipulation
- **kotlinx-coroutines-core**: Library for asynchronous programming with coroutines
- **kotlinx-coroutines-reactor**: Integration between Kotlin coroutines and Project Reactor for reactive programming

### Spring Boot Libraries
- **spring-boot-starter-web**: Starter for building web applications with Spring MVC
- **spring-boot-starter-webflux**: Starter for building reactive web applications
- **spring-boot-starter-data-jpa**: Starter for using Spring Data JPA with Hibernate
- **spring-boot-starter-validation**: Starter for using Java Bean Validation with Hibernate Validator
- **spring-boot-starter-actuator**: Starter for using Spring Boot's Actuator for monitoring and management
- **spring-boot-starter-test**: Starter for testing Spring Boot applications
- **spring-boot-configuration-processor**: Annotation processor for generating metadata for custom configuration properties

### Database Libraries
- **postgresql**: PostgreSQL JDBC driver for connecting to PostgreSQL databases
- **flyway-core**: Database migration tool for version control of database schemas
- **HikariCP**: High-performance JDBC connection pool

### Documentation Libraries
- **springdoc-openapi-starter-webmvc-ui**: Integration of SpringDoc with OpenAPI for API documentation

### Testing Libraries
- **junit-jupiter**: JUnit 5 testing framework
- **mockk**: Mocking library for Kotlin
- **testcontainers-junit-jupiter**: JUnit 5 extension for Testcontainers
- **testcontainers-postgresql**: Testcontainers module for PostgreSQL

### Utility Libraries
- **jackson-module-kotlin**: Jackson module for Kotlin, providing serialization/deserialization support
- **jackson-datatype-jsr310**: Jackson module for Java 8 date/time types
- **slf4j-api**: Simple Logging Facade for Java API
- **logback-classic**: Logging implementation used with SLF4J

### Gradle Plugins
- **kotlin-jvm**: Plugin for compiling Kotlin code to JVM bytecode
- **kotlin-spring**: Plugin that makes Spring-annotated classes open by default for Spring AOP
- **kotlin-jpa**: Plugin that makes JPA entity classes open by default for proxying
- **spring-boot**: Spring Boot Gradle plugin for packaging executable jars and wars
- **spring-dependency-management**: Plugin for managing dependencies using Spring's dependency management

## Features

- Multi-module architecture for separation of concerns
- Gradle convention plugins for consistent build configuration
- Version catalog for centralized dependency management
- REST API with CRUD operations
- Database migrations with Flyway
- API documentation with OpenAPI/Swagger
- Docker setup for easy deployment

## Getting Started

### Prerequisites

- JDK 17 or higher
- Docker and Docker Compose (for containerized deployment)
- PostgreSQL (for local development without Docker)

### Running Locally

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/effective-office.git
   cd effective-office
   ```

2. Build the project:
   ```
   ./gradlew clean build
   ```

3. Start PostgreSQL database:

   The application requires a PostgreSQL database running on localhost:5432. You can start it using Docker:

   ```bash
   docker run --name postgres-effectiveoffice -e POSTGRES_DB=effectiveoffice -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:15-alpine
   ```

   Alternatively, if you have PostgreSQL installed locally, ensure:
   - Database name: `effectiveoffice`
   - Username: `postgres`
   - Password: `postgres`
   - Port: `5432`

4. Run the application:
   ```bash
   ./gradlew :backend:app:bootRun
   ```

   **Note:** If you encounter a connection error like `Connection to localhost:5432 refused`, make sure your PostgreSQL database is running and accessible.

5. Access the API at http://localhost:8080/api
6. Access the API documentation at http://localhost:8080/api/swagger-ui.html

### Running with Docker

1. Build and start the containers:
   ```
   cd backend && docker-compose up -d
   ```

2. Access the API at http://localhost:8080/api
3. Access the API documentation at http://localhost:8080/api/swagger-ui.html

## Runtime Profiles

The application supports different runtime profiles for various environments:

### Available Profiles

- **local**: For disconnected development. Uses hardcoded database connection settings and enables detailed logging.
  ```bash
  ./gradlew :backend:app:bootRun --args='--spring.profiles.active=local'
  ```

- **local-dev**: For connecting to the DEV environment from a local machine.
  ```bash
  ./gradlew :backend:app:bootRun --args='--spring.profiles.active=local-dev'
  ```

- **dev**: For deploying Development environments.
  ```bash
  ./gradlew :backend:app:bootRun --args='--spring.profiles.active=dev'
  ```

  With Docker:
  ```bash
  cd backend && SPRING_PROFILES_ACTIVE=dev docker-compose up -d
  ```

- **staging**: For deploying Staging environments.
  ```bash
  SPRING_PROFILES_ACTIVE=staging docker-compose up -d
  ```

- **live**: For deploying Live (production) environments. This is the default profile in Docker.
  ```bash
  docker-compose up -d
  ```

### Profile Configuration

Each profile has specific configurations optimized for its environment:

- **local**: Automatic schema updates, detailed SQL logging, in-memory database option
- **local-dev**: Connection to remote DEV database, detailed logging
- **dev**: Development environment settings with SQL logging
- **staging**: Pre-production settings with minimal logging
- **live**: Production settings with optimized performance and minimal logging

### Environment Variables

All sensitive data is configurable via environment variables. The application uses the following environment variables:

#### Database Configuration

- `SPRING_DATASOURCE_URL`: JDBC URL for the database connection
  - Default for local: `jdbc:postgresql://localhost:5432/effectiveoffice`
  - Default for local-dev: `jdbc:postgresql://dev-db-host:5432/effectiveoffice`
  - Default for dev/staging/live: `jdbc:postgresql://db:5432/effectiveoffice`
- `SPRING_DATASOURCE_USERNAME`: Database username (default: `postgres`)
- `SPRING_DATASOURCE_PASSWORD`: Database password (default: `postgres`)

#### Docker Database Configuration

When using Docker Compose, you can also configure the PostgreSQL container:

- `POSTGRES_DB`: Database name (default: `effectiveoffice`)
- `POSTGRES_USER`: Database username (default: `postgres`)
- `POSTGRES_PASSWORD`: Database password (default: `postgres`)

#### Example: Running with Custom Database Credentials

```bash
# Running locally with custom database credentials
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/effectiveoffice \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
./gradlew :backend:app:bootRun --args='--spring.profiles.active=local'

# Running with Docker Compose with custom database credentials
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/mydb \
SPRING_DATASOURCE_USERNAME=myuser \
SPRING_DATASOURCE_PASSWORD=mypassword \
POSTGRES_DB=mydb \
POSTGRES_USER=myuser \
POSTGRES_PASSWORD=mypassword \
SPRING_PROFILES_ACTIVE=dev \
docker-compose up -d
```

## API Endpoints

The application provides the following API endpoints:

### Users

- `GET /api/users`: Get all users
- `GET /api/users/{id}`: Get user by ID
- `GET /api/users/by-username/{username}`: Get user by username
- `GET /api/users/active`: Get all active users
- `POST /api/users`: Create a new user
- `PUT /api/users/{id}`: Update an existing user
- `DELETE /api/users/{id}`: Delete a user

## Development

### Adding a New Module

1. Create a new directory for the module
2. Add the module to `settings.gradle.kts`
3. Create a `build.gradle.kts` file in the module directory
4. Apply the appropriate convention plugins

### Building the Project

```
./gradlew clean build
```

### Running Tests

```
./gradlew test
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
