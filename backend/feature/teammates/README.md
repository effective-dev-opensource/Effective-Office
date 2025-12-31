# Teammates Module

Manage teammate information from Notion database via REST API with provider abstraction.

## Overview

This module provides a REST API for retrieving teammate information from a Notion database. It implements a provider pattern that allows for multiple data source implementations. Currently supports Notion as the primary data source with a dummy provider for testing.

**Key Capabilities:**

- Retrieve all teammates or filter by active status
- Fetch teammate score data from the same Notion database
- Provider abstraction for flexible data source integration
- Automatic filtering by status and employment
- Production-ready with structured logging and error handling

## Features

- RESTful API for teammate data retrieval
- Provider pattern for flexible data source integration
- Filter teammates by active status
- Separate endpoint for score data
- OpenAPI (Swagger) documentation
- Structured exception handling
- Notion API integration with official SDK
- Batch data processing

## Quick Start

### Basic Usage

```bash
# Fetch all teammates
curl "http://localhost:8080/api/v1/teammates"

# Fetch only active teammates
curl "http://localhost:8080/api/v1/teammates?active=true"

# Fetch teammate scores
curl "http://localhost:8080/api/v1/teammates/score"
```

### Response Example

```json
[
  {
    "id": "page-id-123",
    "name": "John Doe",
    "positions": ["Backend Developer", "Team Lead"],
    "employment": "Full-time",
    "startDate": "2023-01-15",
    "nextBDay": "2024-06-20",
    "duolingo": "john_doe",
    "photo": "https://notion.so/image/photo.jpg",
    "status": "Active"
  }
]
```

## API Reference

### Get Teammates

Retrieves teammate information with optional filtering by active status.

```http
GET /v1/teammates?active={boolean}
```

#### Parameters

| Name     | Type    | Required | Default | Description                          |
| -------- | ------- | -------- | ------- | ------------------------------------ |
| `active` | boolean | No       | false   | Filter to show only active teammates |

#### Response Schema

```json
[
  {
    "id": "string",
    "name": "string",
    "positions": "array[string]",
    "employment": "string",
    "startDate": "date (YYYY-MM-DD)",
    "nextBDay": "date (YYYY-MM-DD)",
    "duolingo": "string | null",
    "photo": "string (url) | null",
    "status": "string"
  }
]
```

#### Status Codes

| Code | Description                             |
| ---- | --------------------------------------- |
| 200  | Success - Teammates retrieved           |
| 500  | Internal Server Error - Provider failed |

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/v1/teammates?active=true" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer <YOUR_TOKEN_HERE>"
```

### Get Teammate Scores

Retrieves teammate score data from the Notion database.

```http
GET /v1/teammates/score
```

#### Response Schema

```json
[
  {
    "id": "string",
    "name": "string",
    "duolingo": "string | null",
    "leader_id": "string | null",
    "scoreMonth": "integer | null"
  }
]
```

#### Status Codes

| Code | Description                             |
| ---- | --------------------------------------- |
| 200  | Success - Scores retrieved              |
| 500  | Internal Server Error - Provider failed |

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/v1/teammates/score" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer <YOUR_TOKEN_HERE>"
```

## Architecture

This module follows a layered architecture with provider abstraction:

```
teammates/
├── core/                                    # Core module with domain logic
│   ├── controller/
│   │   └── TeammateController.kt           # REST endpoints
│   ├── service/
│   │   └── TeammateService.kt              # Business logic
│   ├── domain/
│   │   ├── TeammateProvider.kt             # Provider interface
│   │   └── model/
│   │       ├── Teammate.kt                 # Domain model
│   │       └── TeammateScore.kt            # Score model
│   ├── dto/
│   │   ├── TeammateDTO.kt                  # Response DTO
│   │   ├── TeammateScoreDTO.kt             # Score DTO
│   │   └── TeammateDtoMapper.kt            # DTO mapping
│   ├── exception/
│   │   ├── TeammateExceptions.kt           # Custom exceptions
│   │   ├── TeammateExceptionHandler.kt     # Global error handler
│   │   └── TeammateErrorCodes.kt           # Error code constants
│   └── config/
│       └── TeammateProviderConfig.kt       # Provider configuration
└── provider/                                # Provider implementations
    ├── notion/                              # Notion provider
    │   ├── service/
    │   │   └── NotionTeammateProvider.kt   # Notion implementation
    │   ├── client/
    │   │   └── NotionTeammateClient.kt     # Notion API client
    │   ├── mapper/
    │   │   └── NotionTeammateMapper.kt     # Notion to domain mapping
    │   ├── config/
    │   │   └── NotionTeammateConfig.kt     # Notion configuration
    │   └── constants/
    │       └── NotionTeammateProperties.kt # Property name constants
    └── dummy/                               # Test provider
        └── DummyTeammateProvider.kt        # Mock implementation
```

### Component Responsibilities

**Controller Layer:**

- Handles HTTP requests and responses
- Validates input parameters
- Maps domain models to DTOs
- Provides OpenAPI documentation

**Service Layer:**

- Orchestrates business logic
- Delegates to provider implementation
- Handles data transformation

**Domain Layer:**

- Defines provider interface contract
- Contains domain models with validation
- Ensures business rule enforcement

**Provider Layer:**

- Implements data source integration
- Notion provider: integrates with Notion API
- Dummy provider: provides test data

**Exception Layer:**

- Centralized error handling
- Custom exception types
- Standardized error responses

## Provider Implementation

### Notion Provider

The Notion provider integrates with Notion databases to retrieve teammate data.

**Environment Variables:**

```env
NOTION_TOKEN=secret_xxxxxxxxxxxxxxxxxxxx
NOTION_TEAMMATES_DB_ID=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
NOTION_SUPERNOVA_DB_ID=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

**Features:**

- Queries Notion teammates database via official Notion SDK
- Uses separate Supernova database for score data
- Maps Notion page properties to domain models
- Filters by status and employment type
- Handles multi-select properties (positions)
- Extracts dates and converts to LocalDate

### Dummy Provider

A test implementation that returns mock data.

**Configuration:**

```yaml
teammates:
  provider: dummy # Options: notion, dummy
```

**Use Cases:**

- Local development without Notion credentials
- Integration testing
- Demo environments

### Creating Custom Provider

To implement a custom provider:

1. Implement the `TeammateProvider` interface:

```kotlin
@Component
@ConditionalOnProperty(
    name = ["teammates.provider"],
    havingValue = "custom"
)
class CustomTeammateProvider : TeammateProvider {
    override fun getTeammates(active: Boolean): List<Teammate> {
        // Your implementation
    }

    override fun getTeammateScores(): List<TeammateScore> {
        // Your implementation
    }
}
```

2. Add provider configuration in `application.yml`:

```yaml
teammates:
  provider: custom
```

## Configuration

### Environment Variables


**Example .env file:**

```env
NOTION_TOKEN=secret_xxxxxxxxxxxxxxxxxxxx
NOTION_TEAMMATES_DB_ID=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
NOTION_SUPERNOVA_DB_ID=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

### Notion Database Schema

The Notion database must contain these properties:

| Property Name | Type         | Required | Description                     |
| ------------- | ------------ | -------- | ------------------------------- |
| Name          | Title        | Yes      | Teammate full name              |
| Positions     | Multi-select | Yes      | List of positions/roles         |
| Employment    | Select       | Yes      | Employment type                 |
| Start date    | Date         | Yes      | Employment start date           |
| Next B-Day    | Date         | Yes      | Next birthday date              |
| Duolingo      | Text         | No       | Duolingo username               |
| Photo         | Files        | No       | Photo URL                       |
| Status        | Select       | Yes      | Status (Active, Inactive, etc.) |

For scores endpoint, additional properties:

| Property Name | Type   | Required | Description         |
| ------------- | ------ | -------- | ------------------- |
| leader_id     | Text   | No       | LeaderId username   |
| scoreMonth    | Number | No       | Monthly score value |

## Usage

### Spring Boot Integration

Add the module to your application:

**build.gradle.kts:**

```kotlin
dependencies {
    implementation(project(":backend:feature:teammates:core"))
    implementation(project(":backend:feature:teammates:provider:notion"))
}
```

## Error Handling

The module provides structured error handling with specific error codes:

### Error Response Format

```json
{
  "message": "Error description",
  "errorCode": "TEAMMATE_ERROR_CODE",
  "timestamp": "2024-10-21T12:34:56"
}
```

### Error Codes

| Code                        | Description                   |
| --------------------------- | ----------------------------- |
| `TEAMMATE_PROVIDER_ERROR`   | Provider failed to fetch data |
| `TEAMMATE_MAPPING_ERROR`    | Failed to map data to domain  |
| `TEAMMATE_VALIDATION_ERROR` | Invalid data received         |

### Common Error Scenarios

**Provider Unavailable:**

- HTTP 500
- Error code: `TEAMMATE_PROVIDER_ERROR`
- Logs full stack trace for debugging

**Invalid Configuration:**

- Application fails to start
- Missing required properties logged

**Notion API Errors:**

- Handled gracefully with logging
- Returns 500 with descriptive message

## Development

### Local Development

1. Set up environment variables:

```bash
export NOTION_TOKEN=secret_xxxxxxxxxxxxxxxxxxxx
export NOTION_TEAMMATES_DB_ID=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
export NOTION_SUPERNOVA_DB_ID=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
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

For development without Notion access:

```yaml
teammates:
  provider: dummy
```

## Dependencies

### Core Module

- Spring Boot Starter Web
- Spring Boot Starter Validation
- SpringDoc OpenAPI
- Jakarta Servlet API

### Notion Provider

- Notion SDK Java (latest)
- Spring Boot Starter WebFlux (for HTTP client)
- Jackson Kotlin Module

## Monitoring

The module provides structured logging for monitoring:

**Key Log Events:**

- Teammate fetch requests with filter parameters
- Provider invocation and response times
- Notion API calls and responses
- Error conditions with stack traces

**Log Levels:**

- INFO: Successful operations
- WARN: Recoverable issues
- ERROR: Failed operations

**Example Logs:**

```
INFO  [TeammateService] Fetching teammates with active filter: true
INFO  [NotionTeammateProvider] Retrieved 25 teammates from Notion
ERROR [TeammateExceptionHandler] Provider error: Connection timeout
```

## Performance Considerations

- Notion API has rate limits
- Consider caching for frequently accessed data
- Batch processing supported by Notion SDK
- Lazy loading of teammate photos

## Security

- API token stored as environment variable
- No sensitive data in logs
- Bearer token authentication on endpoints
- Input validation on all parameters
