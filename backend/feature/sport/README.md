# Sport Module

Retrieve sport time tracking data from Clockify or other providers via REST API with provider abstraction.

## Overview

This module provides a REST API for retrieving sport activity time tracking data from various time tracking providers. It implements a provider pattern that allows flexible integration with different time tracking systems. Currently supports Clockify as the primary provider with a dummy provider for testing.

**Key Capabilities:**

- Retrieve sport users with time tracking statistics
- Automatic quarter-based date range calculation
- Provider abstraction for flexible integration
- Project-based filtering in Clockify
- User aggregation by email with total time calculation
- Production-ready with structured logging and error handling

## Features

- RESTful API for sport time tracking data
- Provider pattern for flexible integration
- Automatic current quarter date range calculation
- User-based time aggregation
- HTTP Interface for clean API integration
- OpenAPI (Swagger) documentation
- Comprehensive exception handling with error codes
- Structured logging

## Quick Start

### Basic Usage

```bash
# Get sport users with time tracking data
curl "http://localhost:8080/api/v1/sport"
```

### Response Example

```json
[
  {
    "name": "John Doe",
    "email": "john.doe@example.com",
    "totalSeconds": 7200
  },
  {
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "totalSeconds": 10800
  }
]
```

## API Reference

### Get Sport Users

Retrieves sport users with their time tracking data from the configured provider.

```http
GET /v1/sport
```

#### Parameters

No query parameters. Uses current quarter for data retrieval.

#### Response Schema

```json
[
  {
    "name": "string",
    "email": "string",
    "totalSeconds": "integer"
  }
]
```

#### Status Codes

| Code | Description                             |
| ---- | --------------------------------------- |
| 200  | Success - Sport users retrieved         |
| 401  | Unauthorized - Authentication failed    |
| 500  | Internal Server Error - Provider failed |
| 503  | Service Unavailable - Provider API down |

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/v1/sport" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer <YOUR_TOKEN_HERE>"
```

#### Example Response

```json
[
  {
    "name": "John Doe",
    "email": "john.doe@example.com",
    "totalSeconds": 7200
  },
  {
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "totalSeconds": 10800
  }
]
```

## Architecture

This module follows a layered architecture with provider abstraction:

```
sport/
├── core/                                    # Core module with domain logic
│   ├── controller/
│   │   └── SportController.kt              # REST endpoints
│   ├── service/
│   │   └── SportService.kt                 # Business logic
│   ├── domain/
│   │   ├── SportProvider.kt                # Provider interface
│   │   └── model/
│   │       └── SportUser.kt                # Domain model
│   ├── dto/
│   │   ├── SportUserDTO.kt                 # Response DTO
│   │   └── SportUserDtoMapper.kt           # DTO mapping
│   ├── exception/
│   │   ├── SportExceptions.kt              # Custom exceptions
│   │   ├── SportExceptionHandler.kt        # Global error handler
│   │   └── SportErrorCodes.kt              # Error code constants
│   └── config/
│       └── SportProviderConfig.kt          # Provider configuration
└── provider/                                # Provider implementations
    ├── clockify/                            # Clockify provider
    │   ├── service/
    │   │   ├── ClockifySportProvider.kt    # Clockify implementation
    │   │   └── ClockifySportService.kt     # API service
    │   ├── api/
    │   │   └── ClockifyApi.kt              # HTTP interface
    │   ├── mapper/
    │   │   └── ClockifySportMapper.kt      # Domain mapping
    │   ├── config/
    │   │   ├── ClockifyConfig.kt           # Configuration
    │   │   └── ClockifyClientConfig.kt     # HTTP client setup
    │   ├── model/
    │   │   ├── ClockifyRequest.kt          # Request model
    │   │   └── ClockifyResponse.kt         # Response model
    │   ├── util/
    │   │   └── QuarterDateRangeCalculator.kt # Date calculation
    │   └── constants/
    │       └── ClockifyConstants.kt        # API constants
    └── dummy/                               # Test provider
        └── DummySportProvider.kt           # Mock implementation
```

### Component Responsibilities

**Controller Layer:**

- Handles HTTP requests and responses
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

- Implements time tracking integration
- Clockify provider: integrates with Clockify Reports API
- Dummy provider: provides test data

**Exception Layer:**

- Centralized error handling
- Custom exception types
- Standardized error responses

## Configuration

### Application Properties

```yaml
sport:
  provider: clockify # Provider type: clockify, dummy
```

### Environment Variables

**Required for Clockify Provider:**

| Variable                | Description                       | Required |
| ----------------------- | --------------------------------- | -------- |
| `CLOCKIFY_API_KEY`      | Clockify API key                  | Yes      |
| `CLOCKIFY_WORKSPACE_ID` | Clockify workspace ID             | Yes      |
| `CLOCKIFY_PROJECT_ID`   | Clockify project ID for filtering | Yes      |

**Example .env file:**

```env
CLOCKIFY_API_KEY=your-api-key-here
CLOCKIFY_WORKSPACE_ID=your-workspace-id
CLOCKIFY_PROJECT_ID=your-project-id
```

## Clockify Integration

### API Base URL

```
https://reports.api.clockify.me
```

### Authentication

Clockify uses API key authentication via the `x-api-key` header.

### Endpoints Used

**Get Detailed Reports**

```http
POST /v1/workspaces/{workspaceId}/reports/detailed
```

Headers:

- `x-api-key`: Your Clockify API key

Request Body:

```json
{
  "amountShown": "HIDE_AMOUNT",
  "dateRangeStart": "2024-10-01T00:00:00Z",
  "dateRangeEnd": "2024-12-31T23:59:59Z",
  "exportType": "JSON",
  "rounding": false,
  "detailedFilter": {
    "sortColumn": "DATE",
    "pageSize": 1000
  },
  "projects": {
    "ids": ["project-id"]
  }
}
```

Returns: Detailed time entries for the specified date range and project

### Date Range Calculation

The module automatically calculates the current quarter date range:

- **Q1**: January 1 - March 31
- **Q2**: April 1 - June 30
- **Q3**: July 1 - September 30
- **Q4**: October 1 - December 31

**Example:**

- Current date: October 29, 2024
- Quarter: Q4 2024
- Date range: 2024-10-01T00:00:00Z to 2024-12-31T23:59:59Z

### Request Flow

1. Calculate current quarter date range
2. Build Clockify API request with filters
3. Call detailed reports endpoint
4. Group time entries by user email
5. Calculate total seconds per user
6. Transform to domain models
7. Map domain models to DTOs
8. Return aggregated response

**Example Flow:**

```
Client -> GET /v1/sport
Service -> Calculate Q4 2024 (Oct 1 - Dec 31)
Service -> POST /v1/workspaces/{id}/reports/detailed
Clockify -> Returns time entries
Service -> Group by email, sum duration
Service -> Transform & Map
Client <- Response with sport users
```

### HTTP Client Implementation

Uses Spring HTTP Interface with WebClient:

```kotlin
interface ClockifyApi {
    @PostExchange("/v1/workspaces/{workspaceId}/reports/detailed")
    fun getDetailedReports(
        @PathVariable workspaceId: String,
        @RequestHeader("x-api-key") apiKey: String,
        @RequestBody request: ClockifyRequest
    ): ClockifyResponse
}
```

**Configuration:**

```kotlin
@Bean
fun clockifyApi(webClientBuilder: WebClient.Builder): ClockifyApi {
    val webClient = webClientBuilder
        .baseUrl("https://reports.api.clockify.me")
        .defaultHeader("Content-Type", "application/json")
        .build()

    return HttpServiceProxyFactory.builder()
        .exchangeAdapter(WebClientAdapter.create(webClient))
        .build()
        .createClient(ClockifyApi::class.java)
}
```

## Provider Implementation

### Clockify Provider

The Clockify provider integrates with Clockify Reports API to retrieve time tracking data.

**Features:**

- Automatic quarter date range calculation
- Project-based filtering
- User aggregation by email
- Duration summing across all time entries
- HTTP interface for clean API integration
- Comprehensive error handling

**Data Aggregation:**

The provider groups time entries by user email and calculates total time:

```kotlin
timeEntries
    .groupBy { it.userEmail }
    .map { (email, entries) ->
        SportUser(
            name = entries.first().userName,
            email = email,
            totalSeconds = entries.sumOf { it.timeInterval?.duration ?: 0 }
        )
    }
```

**Quarter Calculation Logic:**

Uses Java Time API with ISO quarter fields:

```kotlin
val quarter = date.get(IsoFields.QUARTER_OF_YEAR)
val startDate = date
    .with(IsoFields.QUARTER_OF_YEAR, quarter.toLong())
    .with(IsoFields.DAY_OF_QUARTER, 1)
```

### Dummy Provider

A test implementation that returns sample sport users.

**Configuration:**

```yaml
sport:
  provider: dummy # Options: clockify, dummy
```

**Use Cases:**

- Local development without Clockify credentials
- Integration testing
- Demo environments
- CI/CD pipelines

**Sample Response:**

```json
[
  {
    "name": "John Doe",
    "email": "john.doe@example.com",
    "totalSeconds": 7200
  },
  {
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "totalSeconds": 10800
  }
]
```

### Creating Custom Provider

To implement a custom provider:

1. Implement the `SportProvider` interface:

```kotlin
@Component
@ConditionalOnProperty(
    name = ["sport.provider"],
    havingValue = "custom"
)
class CustomSportProvider : SportProvider {
    override fun getSportUsers(): List<SportUser> {
        // Your implementation
        return listOf(
            SportUser(
                name = "User Name",
                email = "user@example.com",
                totalSeconds = 3600
            )
        )
    }
}
```

2. Add provider configuration in `application.yml`:

```yaml
sport:
  provider: custom
```

## Error Handling

The module provides structured error handling with specific error codes:

### Error Response Format

```json
{
  "message": "Error description",
  "errorCode": 501,
  "timestamp": "2024-10-29T12:34:56"
}
```

### Error Codes

| Code | Description                    |
| ---- | ------------------------------ |
| 401  | Provider unavailable           |
| 501  | Failed to retrieve sport users |
| 601  | Invalid request parameters     |

### Common Error Scenarios

**Provider Unavailable:**

- HTTP 503
- Error code: `401`
- Logs full stack trace for debugging

**Authentication Failed:**

- HTTP 401
- Check Clockify API key configuration
- Verify workspace and project IDs

**No Time Entries Found:**

- HTTP 500
- Error: "No time entries received from Clockify"
- Verify project has time entries in current quarter

**Invalid Credentials:**

- Application logs error on startup
- Check environment variables are set correctly

## Development

### Local Development

1. Set up environment variables:

```bash
export CLOCKIFY_API_KEY=your-api-key-here
export CLOCKIFY_WORKSPACE_ID=your-workspace-id
export CLOCKIFY_PROJECT_ID=your-project-id
```

2. Configure provider in `application.yml`:

```yaml
sport:
  provider: clockify
```

3. Run the application:

```bash
./gradlew :backend:app:bootRun
```

4. Access Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

5. Test endpoint:

```bash
curl http://localhost:8080/api/v1/sport
```

### Testing with Dummy Provider

For development without Clockify access:

```yaml
sport:
  provider: dummy
```

The dummy provider returns sample users with mock time tracking data.

### Getting Clockify Credentials

1. **API Key:**

   - Log in to Clockify: https://app.clockify.me
   - Go to Settings → Profile
   - Scroll to API section
   - Generate or copy API key

2. **Workspace ID:**

   - In Clockify, go to any workspace
   - Check URL: `https://app.clockify.me/workspaces/{WORKSPACE_ID}/...`
   - Copy the workspace ID from URL

3. **Project ID:**
   - Go to Projects in your workspace
   - Click on the sport/activity project
   - Check URL: `https://app.clockify.me/projects/{PROJECT_ID}`
   - Copy the project ID from URL

## Dependencies

### Core Module

- Spring Boot Starter Web
- Spring Boot Starter Validation
- SpringDoc OpenAPI
- Jakarta Validation API

### Clockify Provider

- Spring Boot Starter WebFlux (HTTP client)
- Jackson Kotlin Module
- SLF4J Logging
