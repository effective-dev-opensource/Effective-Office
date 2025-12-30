# Leader-ID Module

Retrieve upcoming events from Leader-ID platform via REST API with clean architecture.

## Overview

This module provides a REST API for retrieving event information from the Leader-ID platform. It acts as a proxy service that fetches upcoming conferences, meetups, and other events with comprehensive error handling and data transformation following clean architecture principles.

**Key Capabilities:**

- Retrieve upcoming events from Leader-ID platform
- Configurable search criteria (date range, location, pagination)
- Automatic event detail fetching for each event
- Clean architecture with domain-driven design
- HTTP Interface for clean API integration
- Production-ready with structured logging and error handling

## Features

- RESTful API for event retrieval
- Clean architecture with clear layer separation
- Configurable search criteria
- Automatic batch event fetching
- HTTP Interface client for external API
- OpenAPI (Swagger) documentation
- Comprehensive exception handling with error codes
- Data transformation between layers
- Structured logging

## Quick Start

### Basic Usage

```bash
# Get upcoming events for display
curl "http://localhost:8080/api/v1/events"
```

### Response Example

```json
{
  "events": [
    {
      "id": 12345,
      "name": "Tech Conference 2024",
      "startDateTime": "2024-01-15T10:00:00",
      "finishDateTime": "2024-01-15T18:00:00",
      "isOnline": false,
      "photoUrl": "https://leader-id.ru/photo.jpg",
      "organizer": "Tech Company",
      "speakers": ["John Doe", "Jane Smith"],
      "endRegDate": "2024-01-10T23:59:59"
    }
  ]
}
```

## API Reference

### Get Events for TV

Retrieves upcoming events from Leader-ID platform with default search criteria.

```http
GET /v1/events
```

#### Parameters

No query parameters. Uses default configuration for search criteria.

#### Response Schema

```json
{
  "events": [
    {
      "id": "integer",
      "name": "string",
      "startDateTime": "datetime (ISO 8601)",
      "finishDateTime": "datetime (ISO 8601)",
      "isOnline": "boolean",
      "photoUrl": "string (url) | null",
      "organizer": "string | null",
      "speakers": "array[string]",
      "endRegDate": "datetime (ISO 8601) | null"
    }
  ]
}
```

#### Status Codes

| Code | Description                               |
| ---- | ----------------------------------------- |
| 200  | Success - Events retrieved                |
| 404  | Not Found - No events match criteria      |
| 408  | Request Timeout - API timeout             |
| 429  | Too Many Requests - Rate limit exceeded   |
| 500  | Internal Server Error - Processing failed |
| 503  | Service Unavailable - Leader-ID API down  |

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/v1/events" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer <YOUR_TOKEN_HERE>"
```

#### Example Response

```json
{
  "events": [
    {
      "id": 12345,
      "name": "Tech Conference 2024",
      "startDateTime": "2024-01-15T10:00:00",
      "finishDateTime": "2024-01-15T18:00:00",
      "isOnline": false,
      "photoUrl": "https://leader-id.ru/photo.jpg",
      "organizer": "Tech Company",
      "speakers": ["John Doe", "Jane Smith"],
      "endRegDate": "2024-01-10T23:59:59"
    }
  ]
}
```

## Architecture

This module follows clean architecture with clear layer separation:

```
leader-id/
├── api/
│   └── LeaderIdApi.kt                    # Spring HTTP Interface
├── config/
│   ├── LeaderIdConfig.kt                 # Module configuration
│   ├── LeaderIdClientConfig.kt           # HTTP client setup
│   └── LeaderIdParameters.kt             # Configuration parameters
├── constants/
│   └── LeaderIdApiConstants.kt           # API constants
├── controller/
│   ├── LeaderIdController.kt             # REST endpoints
│   └── LeaderIdExceptionHandler.kt       # Global error handler
├── domain/
│   ├── exception/
│   │   ├── LeaderIdException.kt          # Base exception
│   │   └── LeaderIdErrorCodes.kt         # Error codes
│   ├── model/
│   │   ├── LeaderIdEvent.kt              # Domain model
│   │   └── LeaderIdEventSearchCriteria.kt # Search criteria
│   ├── repository/
│   │   └── LeaderIdRepository.kt         # Repository interface
│   └── usecase/
│       └── GetLeaderIdEventsUseCase.kt   # Business logic
├── dto/
│   ├── LeaderIdEventDTO.kt               # Response DTO
│   ├── LeaderIdEventsResponseDTO.kt      # Response wrapper
│   └── LeaderIdApiDTO.kt                 # External API models
├── infrastructure/
│   ├── mapper/
│   │   └── LeaderIdMapper.kt             # Data transformation
│   └── repository/
│       └── LeaderIdRepositoryImpl.kt     # Repository implementation
└── service/
    └── LeaderIdService.kt                # Application service
```

### Component Responsibilities

**Controller Layer:**

- Handles HTTP requests and responses
- Provides OpenAPI documentation
- Delegates to service layer

**Service Layer:**

- Coordinates use cases
- Handles transaction boundaries
- Orchestrates business logic

**Use Case Layer:**

- Contains business logic
- Invokes repository operations
- Enforces business rules

**Domain Layer:**

- Defines domain models with validation
- Contains domain exceptions
- Defines repository contracts

**Infrastructure Layer:**

- Implements repository with HTTP client
- Handles external API communication
- Maps between external and domain models

**Exception Layer:**

- Centralized error handling
- Specific error codes for different scenarios
- Standardized error responses

## Configuration

### Application Properties

```yaml
leaderid:
  events-period-days: 14 # Days to look ahead for events
  city-id: 123 # Default city
  place-id: 123 # Default place
  pagination-size: 100 # Events per request
```

### Environment Variables

| Variable                           | Description                   | Required |
| ---------------------------------- | ----------------------------- | -------- |
| `LEADERID_EVENTS_PERIOD_DAYS`      | Days to look ahead for events | No       |
| `LEADERID_DEFAULT_CITY_ID`         | Default city ID               | No       |
| `LEADERID_DEFAULT_PLACE_ID`        | Default place ID              | No       |
| `LEADERID_DEFAULT_PAGINATION_SIZE` | Maximum events per request    | No       |

## Leader-ID Integration

### API Base URL

```
https://leader-id.ru
```

### Authentication

Leader-ID API is public and does not require authentication.

### Endpoints Used

**1. Search Events**

```http
GET /api/v4/events/search
```

Query Parameters:

- `paginationSize`: Results per page (integer)
- `dateFrom`: Start date (YYYY-MM-DD)
- `dateTo`: End date (YYYY-MM-DD)
- `cityId`: City filter (integer)
- `placeIds[]`: Place filter (array of integers)

Returns: List of event IDs matching criteria

**2. Get Event Details**

```http
GET /api/v4/events/{eventId}
```

Path Parameters:

- `eventId`: Event identifier (integer)

Returns: Detailed event information

### Request Flow

1. Create search criteria with date range and location
2. Call search endpoint to get list of event IDs
3. For each event ID, fetch detailed information
4. Transform external API data to domain models
5. Map domain models to DTOs
6. Return aggregated response

**Example Flow:**

```
Client -> GET /v1/events
Service -> Search API (1 call) -> [123, 456, 789]
Service -> Get Details (3 calls) -> Event data
Service -> Transform & Map
Client <- Response with events
```

### HTTP Client Implementation

Uses Spring HTTP Interface with WebClient:

```kotlin
@HttpExchange("/api/v4")
interface LeaderIdApi {
    @GetExchange("/events/search")
    fun searchEvents(
        @RequestParam paginationSize: Int,
        @RequestParam dateFrom: String,
        @RequestParam dateTo: String,
        @RequestParam cityId: Int,
        @RequestParam("placeIds[]") placeIds: List<Int>
    ): LeaderIdSearchResponseDTO

    @GetExchange("/events/{eventId}")
    fun getEventById(@PathVariable eventId: Int): LeaderIdEventInfoResponse
}
```

**Configuration:**

```kotlin
@Bean
fun leaderIdApi(webClientBuilder: WebClient.Builder): LeaderIdApi {
    val webClient = webClientBuilder
        .baseUrl("https://leader-id.ru")
        .defaultHeader("User-Agent", "EffectiveOffice/1.0")
        .build()

    return HttpServiceProxyFactory.builder()
        .exchangeAdapter(WebClientAdapter.create(webClient))
        .build()
        .createClient(LeaderIdApi::class.java)
}
```

## Error Handling

The module provides comprehensive error handling with specific error codes:

### Error Response Format

```json
{
  "message": "Error description",
  "code": 201
}
```

### Error Codes

**Resource Not Found (1xx):**

| Code | Description                  |
| ---- | ---------------------------- |
| 101  | Event not found              |
| 102  | No events found for criteria |

**API / External Service (2xx):**

| Code | Description               |
| ---- | ------------------------- |
| 201  | Leader-ID API unavailable |
| 202  | API request timeout       |
| 203  | Authentication failed     |
| 204  | Rate limit exceeded       |

**Validation (3xx):**

| Code | Description             |
| ---- | ----------------------- |
| 301  | Invalid search criteria |
| 302  | Invalid event ID        |

**Data Processing (4xx):**

| Code | Description             |
| ---- | ----------------------- |
| 401  | Data mapping failed     |
| 402  | Events retrieval failed |
| 403  | Events search failed    |

## Development

### Local Development

1. Run the application:

```bash
./gradlew :backend:app:bootRun
```

2. Access Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

3. Test endpoint:

```bash
curl http://localhost:8080/api/v1/events
```

## Dependencies

- Spring Boot Starter Web
- Spring Boot Starter WebFlux (WebClient)
- Spring Boot Starter Validation
- SpringDoc OpenAPI
- Jackson Kotlin Module
- SLF4J Logging

## Monitoring

The module provides structured logging for monitoring:

**Key Log Events:**

- Event fetch requests
- Search criteria used
- Number of events found
- API call durations
- Error conditions with stack traces

**Log Levels:**

- INFO: Successful operations
- WARN: Partial failures, missing data
- ERROR: Failed operations

**Example Logs:**

```
INFO  [LeaderIdController] Received request for events
INFO  [LeaderIdService] Searching events from 2024-10-21 to 2024-11-04
INFO  [LeaderIdRepositoryImpl] Found 15 event IDs
INFO  [LeaderIdRepositoryImpl] Fetched details for 15 events
INFO  [LeaderIdController] Successfully processed request for events
ERROR [LeaderIdExceptionHandler] API unavailable: Connection timeout
```

## Security

- Leader-ID API is public (no authentication required)
- All requests use HTTPS
- Input validation on search criteria
- No sensitive data stored
- Bearer token authentication on module endpoints
- Error messages don't expose internal details
