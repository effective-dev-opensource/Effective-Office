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

## Technology Stack
- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **API Documentation**: Swagger/OpenAPI
- **Authentication**: JWT-based authentication
- **Build Tool**: Gradle with Kotlin DSL

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
