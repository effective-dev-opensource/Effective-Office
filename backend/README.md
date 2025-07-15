# Backend Documentation

## Overview
The backend of Effective Office is built with Spring Boot and provides a robust API for managing office resources, user authentication, and business logic. It follows a modular architecture for maintainability and scalability.

## Architecture
The backend is structured into several modules:

```
backend/
├── app/               # Main application module that ties everything together
├── core/              # Core functionality and shared components
│   ├── data/          # Data sources, repositories, and models
│   ├── domain/        # Business logic, use cases, and domain models
│   └── repository/    # Repository implementations
├── feature/           # Feature-specific modules
│   ├── authorization/ # User authentication and authorization
│   ├── booking/       # Resource booking management
│   ├── calendar-subscription/ # Calendar integration and subscriptions
│   ├── notifications/ # User notifications system
│   ├── user/          # User management
│   └── workspace/     # Workspace and office space management
├── docs/              # API documentation
└── scripts/           # Backend-specific utility scripts
```
For detailed documentation:
- [Feature authorization](./backend/feature/authorization/README.md)
- [Feature booking](./backend/feature/booking/README.md)
- [Feature calendar-subscription](./backend/feature/calendar-subscription/README.md)
- [Feature notifications](./backend/feature/notifications/README.md)
- [Feature user](./backend/feature/user/README.md)
- [Feature workspace](./backend/feature/workspace/README.md)

## Technology Stack

### Core Technologies
- **Language**: Kotlin 2.1.21
- **Framework**: Spring Boot 3.5.0
- **Build Tool**: Gradle 8.x with Kotlin DSL
- **Java Version**: JDK 17

### Database
- **RDBMS**: PostgreSQL 42.7.6
- **Migration Tool**: Flyway 11.8.2
- **Connection Pool**: HikariCP 6.3.0
- **ORM**: Spring Data JPA

### Security
- **Framework**: Spring Security
- **Authentication**: JWT (JSON Web Token) 0.11.5
- **Authorization**: Role-based access control

### API & Documentation
- **API Style**: RESTful
- **Documentation**: SpringDoc OpenAPI 2.8.8 (Swagger UI)
- **Validation**: Jakarta Bean Validation 3.0.2

### Integration
- **Google Calendar**: Google API Client 1.33.0
- **OAuth**: Google OAuth Client 1.33.3

### Testing
- **Unit Testing**: JUnit 5.12.2
- **Mocking**: MockK 1.14.2
- **Integration Testing**: Testcontainers 1.20.2

### Utilities
- **JSON Processing**: Jackson 2.19.0
- **Logging**: SLF4J 2.0.17 with Logback 1.5.18
- **Environment Variables**: Spring dotenv for .env file support

## API Endpoints
The backend exposes RESTful APIs for:
- User management and authentication
- Resource booking and management
- Office space management
- Notifications and alerts
- System configuration

## Database Schema
The database schema includes tables for:
- Users and roles
- Resources and bookings
- Office spaces and layouts
- System configurations
- Audit logs

## Development Setup
1. Ensure you have JDK 17+ installed
2. Set up the database:
   ```
   cp deploy/dev/.env.example deploy/dev/.env
   ```
   Edit the `.env` file with your database configuration.

3. Build the application:
   ```
   ./gradlew :backend:app:build
   ```

4. Run the application:
   ```
   ./gradlew :backend:app:bootRun
   ```

## Local Development
1. Create a Docker container with PostgreSQL:
   ```
   docker run --name postgres-effectiveoffice -e POSTGRES_DB=effectiveoffice -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:15-alpine
   ```

2. Launch the backend application with the local profile:
   ```
   ./gradlew :backend:app:bootRun --args='--spring.profiles.active=local'
   ```
   This will use the configuration from `application-local.yml`.

## Deployment
### Using Docker
The backend can be deployed using Docker:
```
cd deploy/dev
docker-compose up -d
```

### Deployment to Development Environment
To deploy the application to the development environment, use the deployDev Gradle task:
```
./gradlew deployDev -PremoteUser=user -PremoteHost=host -PremotePath=path
```

### Deployment to Production Environment
To deploy the application to the production environment, use the deployProd Gradle task:
```
./gradlew deployProd -PremoteUser=user -PremoteHost=host -PremotePath=path
```

### Docker Container Management
After deployment, you may need to restart Docker containers to apply changes:

1. Stop the running containers:
```
cd deploy/dev  # or deploy/prod for production environment
docker compose down
```

2. Start the containers in detached mode:
```
docker compose up -d
```

3. Verify the containers are running:
```
docker ps
```

4. View container logs:
```
# View logs for a specific container
docker logs [container_name_or_id]

# Follow log output in real-time
docker logs -f [container_name_or_id]

# Show only the last N lines of logs
docker logs --tail [number_of_lines] [container_name_or_id]

# Show logs since a specific time
docker logs --since [time] [container_name_or_id]
# Example: docker logs --since 2023-10-01T10:00:00 backend-app
```

## Configuration
Configuration is managed through:
- Application properties files
- Environment variables
- External configuration server (for production)

## Security
- Token-based authentication
- Input validation
- HTTPS enforcement

## Monitoring
The application includes:
- Health check endpoints
- Metrics collection
- Logging and tracing
- Error reporting

## Troubleshooting
Common issues and solutions:
- Database connection issues: check database credentials and network connectivity
- Authentication failures: Verify authorization key configuration and user credentials
