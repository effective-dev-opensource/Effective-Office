# App Module

## Overview
The App Module is the main application module that ties together all components of the Effective Office backend. It serves as the entry point for the application, configures the Spring Boot environment, and orchestrates the integration of all other modules.

## Features
- Application bootstrapping and initialization
- Module integration and coordination
- Global configuration management
- API endpoint exposure
- Security configuration
- Error handling and exception management

## Architecture
The module follows a standard Spring Boot application structure:

```
app/
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── band/
│       │       └── effective/
│       │           └── office/
│       │               └── backend/
│       │                   └── app/
│       │                       ├── EffectiveOfficeApplication.kt  # Main application class
│       │                       ├── config/                        # Application-wide configuration
│       │                       │   ├── SecurityConfig.kt          # Security configuration
│       │                       │   ├── WebConfig.kt               # Web configuration
│       │                       │   └── SwaggerConfig.kt           # API documentation configuration
│       │                       └── exception/                     # Global exception handling
│       └── resources/
│           ├── application.yml                # Main application properties
│           ├── application-dev.yml            # Development environment properties
│           ├── application-prod.yml           # Production environment properties
│           └── application-local.yml          # Local development properties
```

## Key Components

### Main Application
- **EffectiveOfficeApplication**: The main Spring Boot application class with the `@SpringBootApplication` annotation that serves as the entry point for the application.

### Configuration
- **SecurityConfig**: Configures Spring Security for authentication and authorization
- **WebConfig**: Configures web-related settings like CORS, content negotiation, etc.
- **SwaggerConfig**: Configures Swagger/OpenAPI documentation

### Exception Handling
- **GlobalExceptionHandler**: Provides centralized exception handling for the entire application

## Integration
The App Module integrates all other modules of the Effective Office backend:

- **Core Modules**:
  - Data Module: For data access and persistence
  - Domain Module: For business logic and domain models
  - Repository Module: For database access

- **Feature Modules**:
  - Authorization Module: For user authentication and authorization
  - Booking Module: For resource booking management
  - Calendar Subscription Module: For calendar integration
  - Notifications Module: For user notifications
  - User Module: For user management
  - Workspace Module: For workspace and office space management

## Configuration
The module provides environment-specific configurations:

- **application.yml**: Default configuration properties
- **application-local.yml**: Local development configuration

## Usage
To run the application:

```bash
./gradlew :backend:app:bootRun
```

To run with a specific profile:

```bash
./gradlew :backend:app:bootRun --args='--spring.profiles.active=local'
```

To build the application:

```bash
./gradlew :backend:app:build
```

## Development
When extending this module:
1. Maintain clear separation of concerns
2. Add new configurations in the appropriate configuration classes
3. Ensure proper integration with other modules
4. Follow Spring Boot best practices for application structure