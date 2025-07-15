# Data Module

## Overview
The Data Module provides common data structures and data transfer objects (DTOs) used across the Effective Office backend. It defines standardized formats for data exchange between different parts of the application.

## Features
- Standardized error response format
- Common data transfer objects
- Shared data structures

## Architecture
The module follows a simple structure:

```
data/
├── src/
│   └── main/
│       └── kotlin/
│           └── band/
│               └── effective/
│                   └── office/
│                       └── backend/
│                           └── core/
│                               └── data/
│                                   └── ErrorDto.kt  # Error data transfer object
```

## Key Components

### Data Transfer Objects
- **ErrorDto**: A standardized error response format used across all services
  - Contains a message describing the error
  - Includes an error code for categorization

## Integration
The Data Module is used by:
- API controllers for standardized error responses
- Service layers for data exchange
- Other modules that need to use common data structures

## Usage Examples

### Error Response Example
```kotlin
// Creating a standard error response
val errorResponse = ErrorDto(
    message = "Resource not found",
    code = 404
)

// Using in a controller
@ExceptionHandler(ResourceNotFoundException::class)
fun handleResourceNotFoundException(ex: ResourceNotFoundException): ResponseEntity<ErrorDto> {
    val errorDto = ErrorDto(
        message = ex.message ?: "Resource not found",
        code = 404
    )
    return ResponseEntity(errorDto, HttpStatus.NOT_FOUND)
}
```

## Configuration
The module has minimal configuration requirements as it primarily consists of data structures.

## Development
When extending this module:
1. Follow the established patterns for creating DTOs
2. Ensure backward compatibility when modifying existing DTOs
3. Add proper documentation for new data structures