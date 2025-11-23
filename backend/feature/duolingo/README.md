# Duolingo Module

Retrieve Duolingo user statistics (XP, streaks, courses) via REST API.

## Overview

This module provides a REST API for retrieving Duolingo user statistics, including XP scores, streak data, and course enrollment details. It acts as a proxy to the Duolingo public API with enhanced error handling and batch processing capabilities.

**Key Capabilities:**

- Fetch user statistics by username
- Batch processing for multiple users
- Resilient error handling (partial failures don't break the entire request)
- Production-ready logging and monitoring

## Features

- RESTful API for Duolingo user data
- Batch user queries (multiple usernames per request)
- Graceful degradation on partial failures
- OpenAPI (Swagger) documentation
- Structured logging
- Zero external dependencies (uses public API)

## Quick Start

### Basic Usage

```bash
# Fetch single user
curl "http://localhost:8080/api/v1/duolingo/users?usernames=johndoe123"

# Fetch multiple users
curl "http://localhost:8080/api/v1/duolingo/users?usernames=user1&usernames=user2"
```

### Response Example

```json
{
  "users": [
    {
      "username": "johndoe123",
      "name": "John Doe",
      "picture": "https://duolingo.com/pictures/johndoe.jpg",
      "streak": 150,
      "totalXp": 25000,
      "courses": []
    }
  ]
}
```

## API Reference

### Get Duolingo Users

Retrieves user statistics from Duolingo for one or more usernames.

```http
GET /v1/duolingo/users?usernames={username1}&usernames={username2}
```

#### Parameters

| Name        | Type          | Required | Description                         |
| ----------- | ------------- | -------- | ----------------------------------- |
| `usernames` | array[string] | Yes      | List of Duolingo usernames to query |

#### Response Schema

```json
{
  "users": [
    {
      "username": "string",
      "name": "string",
      "picture": "string (url)",
      "streak": "integer",
      "totalXp": "integer",
      "courses": "array[object]"
    }
  ]
}
```

#### Status Codes

| Code | Description                                |
| ---- | ------------------------------------------ |
| 200  | Success - User data retrieved              |
| 400  | Bad Request - Missing or invalid usernames |
| 500  | Internal Server Error - API failure        |

#### Example Request

With Bearer token:

```bash
curl -X GET "http://localhost:8080/api/v1/duolingo/users?usernames=johndoe123" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer <YOUR_TOKEN_HERE>"
```

#### Example Response

```json
{
  "users": [
    {
      "username": "johndoe123",
      "name": "John Doe",
      "picture": "https://duolingo.com/pictures/johndoe.jpg",
      "streak": 150,
      "totalXp": 25000,
      "courses": [
        {
          "language": "Spanish",
          "level": 15
        }
      ]
    }
  ]
}
```

## Architecture

This module follows a lightweight service-controller pattern:

```
duolingo/
├── config/
│   └── DuolingoConfig.kt          # WebClient setup
├── controller/
│   └── DuolingoController.kt      # REST endpoints
├── dto/
│   ├── DuolingoUserDTO.kt         # User data model
│   └── DuolingoResponseDTO.kt     # Response wrapper
└── service/
    └── DuolingoService.kt         # Business logic & API client
```

### Component Overview

| Component            | Responsibility                                             |
| -------------------- | ---------------------------------------------------------- |
| `DuolingoController` | HTTP endpoint handling, request validation                 |
| `DuolingoService`    | Duolingo API integration, error handling, data aggregation |
| `DuolingoConfig`     | WebClient configuration with base URL                      |
| `DuolingoUserDTO`    | User data serialization                                    |

## Configuration

No configuration required by default. The module works out of the box.

### Optional: Custom WebClient

If you need to customize the HTTP client (e.g., timeouts, proxy):

```kotlin
@Configuration
class CustomDuolingoConfig {
    @Bean
    fun duolingoWebClient(): WebClient = WebClient.builder()
        .baseUrl("https://www.duolingo.com")
        .defaultHeader("User-Agent", "YourApp/1.0")
        .build()
}
```

### Integration Details

- **Base URL**: `https://www.duolingo.com`
- **API Version**: `2017-06-30` (stable public API)
- **Authentication**: Require a Bearer token
- **Rate Limits**: Subject to Duolingo's public API limits

## Error Handling

### Graceful Degradation

The module implements resilient error handling.

Example flow:

```
Request: [user1, user2, user3]
         ↓
user1: success
user2: api error (logged, skipped)
user3: success
         ↓
Response: [user1, user3]
```

Behavior:

- Failed usernames are logged and skipped
- Processing continues for remaining usernames
- Partial results are always returned
- No exceptions are thrown to the client

### Error Scenarios

| Scenario         | Behavior                   |
| ---------------- | -------------------------- |
| Invalid username | Skipped, logged as warning |
| Network timeout  | Skipped, logged as error   |
| API rate limit   | Skipped, logged as error   |
| JSON parse error | Skipped, logged as error   |

### Logging

All errors are logged with context. Example:

```
ERROR: Failed to fetch data for username 'johndoe': Connection timeout
```

## Development

### Run Locally

```bash
./gradlew :backend:app:bootRun
```

Then access the API at `http://localhost:8080/api/v1/duolingo/users`

You can also use the Swagger UI to explore and test the endpoint:

`http://localhost:8080/api/swagger-ui/index.html#/Duolingo/getDuolingoUsers`

If the server enforces authentication, open the Swagger UI, click "Authorize" and paste `Bearer <YOUR_TOKEN>` into the value field, then try the `getDuolingoUsers` operation.

### Dependencies

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
```

## Troubleshooting

### Issue: Empty Response

```json
{ "users": [] }
```

# Check logs for errors

tail -f logs/application.log | grep "Failed to fetch"

# Test single username

curl "http://localhost:8080/api/v1/duolingo/users?usernames=testuser"

**Common Causes:**

- Invalid usernames
- Duolingo API temporarily unavailable
- Network firewall blocking requests

**Solution:** Verify username exists by visiting `https://www.duolingo.com/profile/{username}`

## Security

### Data Privacy

- Only public profile data is accessed
- No authentication credentials required
- HTTPS for all requests
- No data stored locally
