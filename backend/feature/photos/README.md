# Photos Module

Retrieve photos from Synology NAS or other storage providers via REST API with provider abstraction.

## Overview

This module provides a REST API for retrieving photos from various storage providers. It implements a provider pattern that allows flexible integration with different photo storage systems. Currently supports Synology NAS Photo Station as the primary provider with a dummy provider for testing.

**Key Capabilities:**

- Retrieve photos from configured storage provider
- Support for photo count and pagination
- Provider abstraction for flexible storage integration
- Session management and authentication handling
- Album filtering with regex patterns
- Production-ready with structured logging and error handling

## Features

- RESTful API for photo retrieval
- Provider pattern for flexible storage integration
- Configurable photo limits and pagination
- Album filtering capabilities
- Session-based authentication (Synology)
- OpenAPI (Swagger) documentation
- Structured exception handling
- Automatic session renewal
- Photo shuffling for variety

## Quick Start

### Basic Usage

```bash
# Get all photos
curl "http://localhost:8080/api/v1/photos"

# Get limited number of photos
curl "http://localhost:8080/api/v1/photos?limit=20"
```

### Response Example

```json
{
  "success": true,
  "message": "Photos retrieved successfully",
  "data": {
    "photos": [
      {
        "id": "photo-12345",
        "thumbnailUrl": "https://nas.example.com/photo/thumb/12345"
      },
      {
        "id": "photo-12346",
        "thumbnailUrl": "https://nas.example.com/photo/thumb/12346"
      }
    ],
    "totalCount": 150,
    "limit": 20
  }
}
```

## API Reference

### Get Photos

Retrieves photos from the configured storage provider with optional limit.

```http
GET /v1/photos?limit={integer}
```

#### Parameters

| Name    | Type    | Required | Default | Description                        |
| ------- | ------- | -------- | ------- | ---------------------------------- |
| `limit` | integer | No       | null    | Maximum number of photos to return |

#### Response Schema

```json
{
  "success": "boolean",
  "message": "string",
  "data": {
    "photos": [
      {
        "id": "string",
        "thumbnailUrl": "string (url)"
      }
    ],
    "totalCount": "integer",
    "limit": "integer | null"
  }
}
```

#### Status Codes

| Code | Description                             |
| ---- | --------------------------------------- |
| 200  | Success - Photos retrieved              |
| 400  | Bad Request - Invalid limit parameter   |
| 500  | Internal Server Error - Provider failed |
| 503  | Service Unavailable - Provider offline  |

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/v1/photos?limit=20" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer <YOUR_TOKEN_HERE>"
```

#### Example Response

```json
{
  "success": true,
  "message": "Photos retrieved successfully",
  "data": {
    "photos": [
      {
        "id": "photo-12345",
        "thumbnailUrl": "https://nas.example.com/photo/thumb/12345"
      },
      {
        "id": "photo-12346",
        "thumbnailUrl": "https://nas.example.com/photo/thumb/12346"
      }
    ],
    "totalCount": 150,
    "limit": 20
  }
}
```

## Architecture

This module follows a layered architecture with provider abstraction:

```
photos/
├── core/                                 # Core module with domain logic
│   ├── controller/
│   │   └── PhotoController.kt           # REST endpoints
│   ├── service/
│   │   └── PhotoService.kt              # Business logic
│   ├── domain/
│   │   ├── PhotoProvider.kt             # Provider interface
│   │   └── model/
│   │       └── Photo.kt                 # Domain model
│   ├── dto/
│   │   ├── PhotoDTO.kt                  # Response DTO
│   │   ├── PhotosResponseDTO.kt         # Wrapper DTO
│   │   └── PhotosDataDTO.kt             # Data DTO
│   ├── exception/
│   │   ├── PhotoExceptions.kt           # Custom exceptions
│   │   ├── PhotosExceptionHandler.kt    # Global error handler
│   │   └── PhotosErrorCodes.kt          # Error code constants
│   └── config/
│       └── PhotoProviderConfig.kt       # Provider configuration
└── provider/                             # Provider implementations
    ├── synology/                         # Synology NAS provider
    │   ├── service/
    │   │   ├── SynologyPhotoProvider.kt # Synology implementation
    │   │   ├── SynologyAuthService.kt   # Authentication
    │   │   ├── SynologySessionService.kt # Session management
    │   │   ├── SynologyAlbumService.kt  # Album operations
    │   │   └── SynologyPhotoFetchService.kt # Photo fetching
    │   ├── api/
    │   │   └── SynologyApi.kt           # HTTP interface
    │   ├── mapper/
    │   │   └── SynologyPhotoMapper.kt   # Domain mapping
    │   ├── config/
    │   │   ├── SynologyConfig.kt        # Configuration
    │   │   └── SynologyClientConfig.kt  # HTTP client setup
    │   ├── dto/
    │   │   └── Synology*.kt             # API DTOs
    │   ├── model/
    │   │   └── SynologyAuthModel.kt     # Auth model
    │   └── constants/
    │       └── SynologyApiConstants.kt  # API constants
    └── dummy/                            # Test provider
        └── DummyPhotoProvider.kt        # Mock implementation
```

### Component Responsibilities

**Controller Layer:**

- Handles HTTP requests and responses
- Validates query parameters
- Maps domain models to DTOs
- Provides OpenAPI documentation

**Service Layer:**

- Orchestrates business logic
- Delegates to provider implementation
- Handles data transformation

**Domain Layer:**

- Defines provider interface contract
- Contains minimal domain model
- Ensures business rule enforcement

**Provider Layer:**

- Implements storage integration
- Synology provider: integrates with Synology Foto API
- Dummy provider: provides test data
- Uses `SYNOLOGY_IP`, `SYNOLOGY_LOGIN`, `SYNOLOGY_PASSWORD` for authentication
- Filters by `SYNOLOGY_ALBUM_NAME` if specified

**Exception Layer:**

- Centralized error handling
- Custom exception types
- Standardized error responses

## Configuration

### Application Properties

```yaml
photos:
  provider: synology # Provider type: synology, dummy
```

### Environment Variables

**Example .env file:**

```env
SYNOLOGY_IP=https://synology.example:443/
SYNOLOGY_LOGIN=admin
SYNOLOGY_PASSWORD=Pass123
SYNOLOGY_ALBUM_NAME=Best of 2024
```

## Provider Implementation

### Synology Provider

The Synology provider integrates with Synology Foto API to retrieve photos from Synology NAS.


**Features:**

- Session-based authentication with automatic renewal
- Album filtering by album name
- Photo shuffling for variety
- Thumbnail URL generation
- HTTP interface for clean API integration
- Automatic session management
- Error handling and retries

**Authentication Flow:**

1. Authenticate with username/password via `/webapi/entry.cgi`
2. Receive session ID (SID)
3. Cache SID for subsequent requests
4. Automatically re-authenticate when session expires

**Album Filtering:**
Specify album name to retrieve photos from:

```env
SYNOLOGY_ALBUM_NAME=Best of 2024
```

If not specified, photos from all albums will be retrieved.

### Dummy Provider

A test implementation that returns placeholder images.

**Configuration:**

```yaml
photos:
  provider: dummy # Options: synology, dummy
```

**Use Cases:**

- Local development without Synology NAS
- Integration testing
- Demo environments
- CI/CD pipelines

**Sample Response:**

```json
{
  "photos": [
    { "id": "1", "thumbnailUrl": "https://picsum.photos/300/200?random=1" },
    { "id": "2", "thumbnailUrl": "https://picsum.photos/300/200?random=2" }
  ],
  "totalCount": 10
}
```

### Creating Custom Provider

To implement a custom provider:

1. Implement the `PhotoProvider` interface:

```kotlin
@Component
@ConditionalOnProperty(
    name = ["photos.provider"],
    havingValue = "custom"
)
class CustomPhotoProvider : PhotoProvider {
    override fun getPhotos(limit: Int?): List<Photo> {
        // Your implementation
    }

    override fun getPhotosCount(): Int {
        // Your implementation
    }
}
```

2. Add provider configuration in `application.yml`:

```yaml
photos:
  provider: custom
```


## Error Handling

The module provides structured error handling with specific error codes:

### Error Response Format

```json
{
  "message": "Error description",
  "errorCode": "PHOTO_ERROR_CODE",
  "timestamp": "2024-10-21T12:34:56"
}
```

### Error Codes

| Code                     | Description                   |
| ------------------------ | ----------------------------- |
| `PHOTO_PROVIDER_ERROR`   | Provider failed to fetch data |
| `PHOTO_RETRIEVAL_ERROR`  | Failed to retrieve photos     |
| `PHOTO_COUNT_ERROR`      | Failed to get photo count     |
| `PHOTO_VALIDATION_ERROR` | Invalid request parameters    |

### Common Error Scenarios

**Provider Unavailable:**

- HTTP 503
- Error code: `PHOTO_PROVIDER_ERROR`
- Logs full stack trace for debugging

**Authentication Failed:**

- HTTP 500
- Error code: `PHOTO_PROVIDER_ERROR`
- Check credentials configuration

**No Photos Found:**

- HTTP 200 with empty array
- Check album filter configuration
- Verify albums exist in provider

## Development

### Local Development

1. Set up environment variables:

```bash
export SYNOLOGY_IP=https://synology.example:443/
export SYNOLOGY_LOGIN=admin
export SYNOLOGY_PASSWORD=Pass123
export SYNOLOGY_ALBUM_NAME="Best of 2024"
```

2. Run the application:

```bash
./gradlew :backend:app:bootRun
```

3. Access Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

### Testing with Dummy Provider

For development without Synology access:

```yaml
photos:
  provider: dummy
```

The dummy provider returns Lorem Picsum placeholder images.

## Dependencies

### Core Module

- Spring Boot Starter Web
- Spring Boot Starter Validation
- SpringDoc OpenAPI
- Jakarta Servlet API

### Synology Provider

- Spring Boot Starter WebFlux (HTTP client)
- Jackson Kotlin Module
- SLF4J Logging

## Monitoring

The module provides structured logging for monitoring:

**Key Log Events:**

- Photo fetch requests with limit parameters
- Provider invocation and response times
- Synology authentication attempts
- Session renewals
- Error conditions with stack traces

**Log Levels:**

- INFO: Successful operations
- WARN: Recoverable issues (session expired)
- ERROR: Failed operations

**Example Logs:**

```
INFO  [PhotoService] Fetching photos with limit: 20
INFO  [SynologySessionService] Using cached session ID
INFO  [SynologyPhotoProvider] Retrieved 20 photos from 3 albums
ERROR [PhotosExceptionHandler] Provider error: Authentication failed
```

## Performance Considerations

- Synology Foto API response time depends on NAS performance
- Session caching reduces authentication overhead
- Album filtering reduces processing time
- Consider caching photo URLs on client side

## Security

- Credentials stored as environment variables
- HTTPS recommended for Synology connections
- Session IDs are temporary and expire
- No sensitive data in logs
- Bearer token authentication on endpoints
- Input validation on all parameters
