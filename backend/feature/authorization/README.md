# Authorization Module

## Overview
The Authorization module provides a flexible and extensible authentication and authorization system for the Effective Office backend. It implements a Chain of Responsibility pattern to support multiple authorization methods, including API key-based authentication and JWT token authentication.

## Features
- Chain of Responsibility pattern for flexible authorization flow
- API key-based authentication
- JWT token authentication
- Integration with Spring Security
- Customizable error handling

## Architecture
The module follows a layered architecture:

```
authorization/
├── apikey/           # API key authentication components
│   ├── config/       # API key configuration
│   ├── entity/       # API key entity classes
│   └── repository/   # API key data access
├── config/           # Security and authorization configuration
├── core/             # Core authorization components
│   └── exception/    # Core-specific exceptions
├── exception/        # Common exception classes
├── security/         # Spring Security integration
└── service/          # Authorization services
```

## Key Components

### Core Components
- **Authorizer**: Interface for authorization chain elements
- **BaseAuthorizer**: Abstract base class for authorizers
- **AuthorizationChain**: Manages the chain of authorizers
- **AuthorizationResult**: Represents the result of an authorization attempt

### API Key Authorization
- **ApiKeyAuthorizer**: Authorizer implementation for API key-based authentication
- **ApiKeyRepository**: Repository for API key storage and retrieval

### Security Integration
- **JwtAuthenticationFilter**: Spring Security filter for JWT authentication
- **SecurityConfig**: Spring Security configuration

### Services
- **AuthorizationService**: Service that coordinates the authorization process

### Configuration
The module provides default configurations that can be customized:

1. **Authorization Chain Configuration**:
   ```kotlin
   @Configuration
   class CustomAuthorizationConfig {
       @Bean
       fun customAuthorizer(): Authorizer {
           return CustomAuthorizer()
       }
   }
   ```

2. **Security Configuration**:
   The module integrates with Spring Security and can be customized through properties or by extending the provided configurations.

### Creating Custom Authorizers
To create a custom authorizer, extend the `BaseAuthorizer` class:

```kotlin
@Component
class CustomAuthorizer : BaseAuthorizer() {
    override fun doAuthorize(request: HttpServletRequest): AuthorizationResult {
        // Custom authorization logic
        return AuthorizationResult(success = true)
    }
}
```

## Error Handling
The module provides custom exceptions for different authorization scenarios:
- `AuthorizationException`: Base exception for all authorization errors
- `UnauthorizedException`: Thrown when authorization fails
- `InvalidApiKeyException`: Thrown when an API key is invalid
- `MissingTokenException`: Thrown when a required token is missing

## Integration with Spring Security
The module integrates with Spring Security through the `JwtAuthenticationFilter` and `SecurityConfig` classes. The filter intercepts incoming requests and uses the authorization chain to authenticate them.

## Examples

### Basic Authorization Flow
1. A request comes in with an Authorization header
2. The `JwtAuthenticationFilter` intercepts the request
3. The filter passes the request to the `AuthorizationService`
4. The service uses the `AuthorizationChain` to process the request
5. Each authorizer in the chain attempts to authorize the request
6. If any authorizer succeeds, the request is authorized
7. If all authorizers fail, the request is rejected

### API Key Authorization
API keys are stored in the database in a hashed format. When a request comes in with an API key:
1. The `ApiKeyAuthorizer` extracts the key from the Authorization header
2. The key is hashed using SHA-256
3. The hashed key is compared with keys in the database
4. If a match is found, the request is authorized

## Development

### Adding a New Authorization Method
To add a new authorization method:
1. Create a new authorizer by implementing the `Authorizer` interface or extending `BaseAuthorizer`
2. Register the authorizer as a Spring bean
3. The authorizer will automatically be added to the authorization chain
