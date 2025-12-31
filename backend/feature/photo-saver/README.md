# Photo Saver Module

Automatically save photos from Mattermost channels to Synology NAS based on reactions.

## Overview

This module provides an automated photo synchronization service that monitors Mattermost channels for posts with specific emoji reactions and uploads the associated photos to Synology NAS. It implements a provider pattern allowing flexible integration with different messaging platforms and storage systems.

**Key Capabilities:**

- Monitor Mattermost channels for posts with specific emoji reactions
- Download photos from selected posts
- Upload photos to Synology NAS with album management
- Scheduled automatic synchronization
- Session validation and auto-refresh for reliable operation
- Provider abstraction for flexible integration

## Features

- Scheduled photo synchronization (configurable interval)
- Reaction-based photo filtering (e.g., 📸 :camera:)
- Multi-channel monitoring
- Automatic album creation and management
- Session validation with auto-refresh
- Duplicate photo handling
- OpenAPI (Swagger) documentation
- Structured exception handling
- Production-ready logging and monitoring
- Manual sync trigger via REST API

## Quick Start

### Basic Usage

```bash
# Trigger manual photo sync
curl -X POST "http://localhost:8080/api/v1/photo-saver/sync"

# Check sync status
curl "http://localhost:8080/api/v1/photo-saver/status"
```

### Response Example

```json
{
  "success": true,
  "message": "Photo sync completed successfully",
  "photosSaved": 15,
  "syncTimestamp": "2024-10-21T14:30:00"
}
```

## API Reference

### Trigger Photo Sync

Manually triggers a photo synchronization from Mattermost to Synology.

```http
POST /v1/photo-saver/sync
```

#### Response Schema

```json
{
  "success": "boolean",
  "message": "string",
  "photosSaved": "integer",
  "syncTimestamp": "datetime (ISO 8601)"
}
```

#### Status Codes

| Code | Description                            |
| ---- | -------------------------------------- |
| 200  | Success - Sync completed               |
| 500  | Internal Server Error - Sync failed    |
| 503  | Service Unavailable - Provider offline |

#### Example Request

```bash
curl -X POST "http://localhost:8080/api/v1/photo-saver/sync" \
  -H "Accept: application/json" \
  -H "Authorization: Bearer <YOUR_TOKEN_HERE>"
```

## Architecture

This module follows a layered architecture with provider abstraction:

```
photo-saver/
├── core/                                    # Core module with domain logic
│   ├── controller/
│   │   └── PhotoSaverController.kt         # REST endpoints
│   ├── service/
│   │   └── PhotoSyncService.kt             # Orchestration logic
│   ├── scheduler/
│   │   └── PhotoSyncScheduler.kt           # Scheduled tasks
│   ├── domain/
│   │   ├── PhotoSaverProvider.kt           # Provider interface
│   │   └── model/
│   │       ├── Post.kt                     # Post domain model
│   │       ├── Reaction.kt                 # Reaction model
│   │       └── FileInfo.kt                 # File metadata
│   ├── exception/
│   │   ├── PhotoSaverExceptions.kt         # Custom exceptions
│   │   ├── PhotoSaverExceptionHandler.kt   # Global error handler
│   │   └── PhotoSaverErrorCodes.kt         # Error code constants
│   └── config/
│       ├── PhotoSaverProviderConfig.kt     # Provider configuration
│       └── SchedulerConfig.kt              # Scheduler setup
└── provider/                                # Provider implementations
    ├── mattermost/                          # Mattermost-Synology provider
    │   ├── service/
    │   │   ├── MattermostPhotoSaverProvider.kt  # Main provider
    │   │   ├── coordination/
    │   │   │   └── PhotoSyncCoordinator.kt      # Workflow orchestration
    │   │   ├── mattermost/
    │   │   │   ├── MattermostAuthService.kt     # Mattermost auth
    │   │   │   ├── MattermostPostService.kt     # Post fetching
    │   │   │   ├── MattermostReactionService.kt # Reaction filtering
    │   │   │   └── MattermostFileDownloadService.kt # File downloads
    │   │   └── synology/
    │   │       ├── PhotoSaverSessionService.kt  # Session management
    │   │       ├── PhotoSaverAlbumService.kt    # Album operations
    │   │       └── SynologyPhotoUploadService.kt # Photo uploads
    │   ├── api/
    │   │   ├── MattermostApi.kt            # Mattermost HTTP interface
    │   │   └── SynologyApi.kt              # Synology HTTP interface
    │   ├── mapper/
    │   │   └── MattermostPhotoSaverMapper.kt # Data transformation
    │   ├── config/
    │   │   ├── PhotoSaverConfig.kt         # Configuration properties
    │   │   └── PhotoSaverClientConfig.kt   # HTTP client setup
    │   ├── dto/
    │   │   ├── MattermostDTO.kt            # Mattermost API models
    │   │   └── SynologyDTO.kt              # Synology API models
    │   ├── util/
    │   │   ├── PostFilters.kt              # Post filtering utilities
    │   │   └── SynologyRequestBuilder.kt   # Request builder
    │   └── constants/
    │       ├── MattermostConstants.kt      # Mattermost API constants
    │       └── SynologyConstants.kt        # Synology API constants
    └── dummy/                               # Test provider
        └── DummyPhotoSaverProvider.kt      # Mock implementation
```

### Component Responsibilities

**Controller Layer:**
- Handles HTTP requests for manual sync triggers
- Provides sync status endpoints
- OpenAPI documentation

**Service Layer:**
- Orchestrates photo synchronization workflow
- Coordinates between provider and scheduler
- Handles business logic

**Scheduler Layer:**
- Automatic periodic photo synchronization
- Configurable sync intervals
- Background task execution

**Domain Layer:**
- Defines provider interface contract
- Contains domain models
- Business rule enforcement

**Provider Layer:**
- **Mattermost Provider**: Fetches posts and reactions from Mattermost
- **Synology Provider**: Uploads photos to Synology NAS
- **Coordinator**: Orchestrates the full workflow
- **Dummy Provider**: Test implementation

**Exception Layer:**
- Centralized error handling
- Specific error codes
- Standardized error responses

## Configuration

### Application Properties

```yaml
photo-saver:
  scheduler:
    enabled: true
    cron: "0 0 */6 * * *"  # Every 6 hours
  mattermost:
    base-url: ${MATTERMOST_BASE_URL}
    token: ${MATTERMOST_TOKEN}
    channel-ids: ${MATTERMOST_CHANNEL_IDS}
    reaction-emojis: ${MATTERMOST_REACTION_EMOJIS}
  synology:
    base-url: ${SYNOLOGY_BASE_URL}
    username: ${SYNOLOGY_USERNAME}
    password: ${SYNOLOGY_PASSWORD}
    album-name: ${SYNOLOGY_ALBUM_NAME}
```

### Environment Variables

**Required Variables:**

| Variable                       | Description                              | Example                         |
| ------------------------------ | ---------------------------------------- | ------------------------------- |
| `MATTERMOST_BASE_URL`          | Mattermost server URL                    | `https://chat.example.com`      |
| `MATTERMOST_TOKEN`             | Mattermost API token                     | `Bearer token123...`            |
| `MATTERMOST_CHANNEL_IDS`       | Comma-separated channel IDs to monitor   | `channel1,channel2,channel3`    |
| `MATTERMOST_REACTION_EMOJIS`   | Comma-separated emoji names for filter   | `camera,star,heart`             |
| `SYNOLOGY_BASE_URL`            | Synology NAS URL                         | `https://nas.example.com:5001`  |
| `SYNOLOGY_USERNAME`            | Synology account username                | `admin`                         |
| `SYNOLOGY_PASSWORD`            | Synology account password                | `SecurePass123`                 |
| `SYNOLOGY_ALBUM_NAME`          | Target album name for photos             | `Mattermost Photos`             |

**Optional Variables:**

| Variable                   | Description              | Default           |
| -------------------------- | ------------------------ | ----------------- |
| `PHOTO_SAVER_ENABLED`      | Enable/disable scheduler | `true`            |
| `PHOTO_SAVER_CRON`         | Cron expression for sync | `0 0 */6 * * *`   |

**Example .env file:**

```env
MATTERMOST_BASE_URL=https://chat.example.com
MATTERMOST_TOKEN=Bearer xxxxxxxxxxxxxxxxx
MATTERMOST_CHANNEL_IDS=abc123def,xyz789ghi
MATTERMOST_REACTION_EMOJIS=camera,star
SYNOLOGY_BASE_URL=https://nas.example.com:5001
SYNOLOGY_USERNAME=admin
SYNOLOGY_PASSWORD=SecurePass123
SYNOLOGY_ALBUM_NAME=Team Photos
```

## Mattermost Integration

### Authentication

Uses bearer token authentication with Mattermost API v4.

**Token Setup:**
1. Login to Mattermost
2. Go to Account Settings → Security → Personal Access Tokens
3. Create token with read permissions
4. Copy token and add to configuration

### Channel Monitoring

Monitors specified channels for new posts since last sync.

**Channel ID Retrieval:**
- Right-click channel → View Info → Copy Channel ID
- Or extract from channel URL: `/channels/{channel-id}`

### Reaction Filtering

Only posts with specified emoji reactions are processed.

**Supported Reactions:**
- Any standard emoji (`:camera:`, `:star:`, `:heart:`)
- Custom emoji from your Mattermost instance

**Example:**
```env
MATTERMOST_REACTION_EMOJIS=camera,star,thumbsup
```

Posts must have at least one of these reactions to be selected.

### API Endpoints Used

**1. Get User Channels**
```http
GET /api/v4/users/me/channels
Authorization: Bearer {token}
```

**2. Get Posts from Channel**
```http
GET /api/v4/channels/{channel_id}/posts
Authorization: Bearer {token}
```

**3. Get File**
```http
GET /api/v4/files/{file_id}
Authorization: Bearer {token}
```

## Synology Integration

### Authentication

Uses session-based authentication with Synology Foto API.

**Session Management:**
- Automatic login with username/password
- Session validation before each request
- Auto-refresh when session expires
- Thread-safe session caching

### Album Management

Automatically creates and manages photo albums.

**Features:**
- Create album if doesn't exist
- Use configured album name
- List existing albums
- Add photos to specific album

### Photo Upload

Uploads photos with duplicate detection.

**Features:**
- Upload with original filename
- Ignore duplicates policy
- Automatic album association
- Error handling and retry

### API Endpoints Used

**1. Authentication**
```http
GET /webapi/auth.cgi?api=SYNO.API.Auth&method=login
```

**2. Session Validation**
```http
GET /webapi/auth.cgi?api=SYNO.API.Auth&method=info
```

**3. List Albums**
```http
GET /webapi/entry.cgi?api=SYNO.Foto.Browse.Album&method=list
```

**4. Create Album**
```http
POST /webapi/entry.cgi/SYNO.Foto.Browse.NormalAlbum
```

**5. Upload Photo**
```http
POST /webapi/entry.cgi/SYNO.Foto.Upload.Item
```

**6. Add to Album**
```http
POST /webapi/entry.cgi/SYNO.Foto.Browse.NormalAlbum
```

### Session Validation

Implements robust session management inspired by the photos module (commit 79b4177):

```kotlin
fun getValidCookie(): String {
    val cached = sessionCache[SESSION_CACHE_KEY]
    
    // Validate cached session
    if (cached != null && isSessionValid(cached)) {
        return cached
    }
    
    // Create new session if expired
    return login()
}

fun invalidateSession() {
    // Force refresh on next request
    sessionCache.remove(SESSION_CACHE_KEY)
}
```

**Error Handling:**
- Automatic session invalidation on API errors
- Retry with fresh session on next request
- Thread-safe operations with `ConcurrentHashMap`

## Workflow

### Synchronization Process

1. **Fetch Posts**
   - Query configured Mattermost channels
   - Filter posts since last sync timestamp
   - Extract post metadata

2. **Filter by Reactions**
   - Check each post for configured emoji reactions
   - Include only posts with matching reactions
   - Log filtered posts count

3. **Download Photos**
   - Extract file info from filtered posts
   - Download each file from Mattermost
   - Validate file types (images only)

4. **Upload to Synology**
   - Authenticate with Synology (or use cached session)
   - Ensure target album exists
   - Upload each photo
   - Associate with album
   - Handle duplicates

5. **Update Sync Timestamp**
   - Record successful sync time
   - Use for next sync iteration

### Example Flow

```
Scheduler (every 6h) or Manual Trigger
         ↓
Query Mattermost Channels
         ↓
Fetch posts since last sync
         ↓
Filter posts by reactions (📸, ⭐)
         ↓
Download photo files
         ↓
Validate Synology session
         ↓
Ensure album exists
         ↓
Upload photos to album
         ↓
Update last sync timestamp
```

## Error Handling

### Error Response Format

```json
{
  "message": "Error description",
  "errorCode": "ERROR_CODE",
  "timestamp": "2024-10-21T14:30:00"
}
```

### Error Codes

**Resource Errors (1xx):**

| Code | Description                    |
| ---- | ------------------------------ |
| 101  | Photo not found                |
| 102  | Post not found                 |

**Provider Errors (2xx):**

| Code | Description                    |
| ---- | ------------------------------ |
| 201  | Mattermost API unavailable     |
| 202  | Synology API unavailable       |
| 203  | Authentication failed          |
| 204  | Session expired                |

**Data Errors (3xx):**

| Code | Description                    |
| ---- | ------------------------------ |
| 301  | Data retrieval failed          |
| 302  | Photo upload failed            |
| 303  | Invalid file format            |

### Graceful Degradation

- Single photo upload failures don't stop the batch
- Failed operations are logged
- Partial success is possible
- Next sync will retry failed items

## Scheduling

### Default Schedule

Runs every 6 hours by default:

```yaml
photo-saver:
  scheduler:
    cron: "0 0 */6 * * *"  # At minute 0 of every 6th hour
```

### Custom Schedules

Common cron expressions:

```yaml
# Every hour
cron: "0 0 * * * *"

# Every day at 3 AM
cron: "0 0 3 * * *"

# Every Monday at 9 AM
cron: "0 0 9 * * MON"

# Every 30 minutes
cron: "0 */30 * * * *"
```

### Disable Scheduler

```yaml
photo-saver:
  scheduler:
    enabled: false
```

Use manual API trigger only.

## Development

### Local Development

1. Set up environment variables:

```bash
export MATTERMOST_BASE_URL=https://chat.example.com
export MATTERMOST_TOKEN=Bearer token123
export MATTERMOST_CHANNEL_IDS=channel1,channel2
export MATTERMOST_REACTION_EMOJIS=camera,star
export SYNOLOGY_BASE_URL=https://nas.example.com:5001
export SYNOLOGY_USERNAME=admin
export SYNOLOGY_PASSWORD=password
export SYNOLOGY_ALBUM_NAME="Test Photos"
```

2. Run the application:

```bash
./gradlew :backend:app:bootRun
```

3. Access Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

4. Test manual sync:

```bash
curl -X POST http://localhost:8080/api/v1/photo-saver/sync
```

### Testing with Dummy Provider

For development without Mattermost/Synology access:

```yaml
photo-saver:
  provider: dummy
```

The dummy provider simulates the sync process with mock data.

## Dependencies

### Core Module
- Spring Boot Starter Web
- Spring Boot Starter Validation
- Spring Boot Starter Scheduling
- SpringDoc OpenAPI

### Mattermost-Synology Provider
- Spring Boot Starter WebFlux (HTTP client)
- Jackson Kotlin Module
- SLF4J Logging
- Jakarta Servlet API

## Monitoring

### Logging

Structured logging for all operations:

**Key Log Events:**
- Sync start/completion
- Number of posts fetched
- Filtered posts count
- Photos downloaded/uploaded
- Session validation events
- Error conditions with stack traces

**Log Levels:**
- INFO: Successful operations
- WARN: Recoverable issues (session expired, skipped files)
- ERROR: Failed operations

**Example Logs:**

```
INFO  [PhotoSyncScheduler] Starting scheduled photo sync
INFO  [MattermostPostService] Retrieved 150 posts from 3 channels
INFO  [MattermostReactionService] Filtered to 15 posts with reactions
INFO  [PhotoSaverSessionService] Cached session is valid, reusing
INFO  [SynologyPhotoUploadService] Uploaded photo IMG_1234.jpg, item ID: 98765
INFO  [PhotoSyncCoordinator] Successfully synchronized 15 photos
WARN  [PhotoSaverSessionService] Cached session is invalid or expired, creating new session
ERROR [SynologyPhotoUploadService] Failed to upload photo IMG_5678.jpg: Connection timeout
```

## Performance Considerations

- Session caching reduces authentication overhead
- Batch processing of posts
- Parallel file downloads (configurable)
- Album lookup caching
- Duplicate detection at Synology level
- Minimal memory footprint (streaming downloads)

## Security

- Credentials stored as environment variables
- HTTPS recommended for all connections
- Session IDs are temporary and auto-expire
- No sensitive data in logs
- Bearer token authentication on endpoints
- Input validation on all parameters
- File type validation before upload

## Troubleshooting

### Issue: No Photos Synced

**Check:**
1. Verify channel IDs are correct
2. Ensure posts have configured reactions
3. Check Mattermost token permissions
4. Verify posts contain image files

```bash
# Test Mattermost connection
curl -H "Authorization: Bearer ${MATTERMOST_TOKEN}" \
  ${MATTERMOST_BASE_URL}/api/v4/users/me

# Check logs
tail -f logs/application.log | grep "photo-saver"
```

### Issue: Synology Upload Failed

**Check:**
1. Verify Synology credentials
2. Test network connectivity to NAS
3. Ensure Synology Photos package is installed
4. Check album name configuration

```bash
# Test Synology connection
curl -k "${SYNOLOGY_BASE_URL}/webapi/auth.cgi?api=SYNO.API.Auth&version=3&method=login&account=${SYNOLOGY_USERNAME}&passwd=${SYNOLOGY_PASSWORD}&session=FileStation&format=cookie"
```

### Issue: Session Keeps Expiring

**Solution:**
- Session auto-refresh is automatic
- Check Synology system time is correct
- Verify network stability
- Review logs for authentication errors

### Common Error Messages

| Error | Cause | Solution |
|-------|-------|----------|
| "Authentication failed" | Invalid Mattermost token | Regenerate token |
| "Channel not found" | Invalid channel ID | Verify channel IDs |
| "Album creation failed" | Synology permissions | Check user permissions |
| "Session expired" | Network issues | Auto-retry will handle |

## Best Practices

1. **Reaction Strategy**: Use specific, unique emojis to avoid false positives
2. **Album Naming**: Use descriptive album names with dates
3. **Sync Frequency**: Balance between freshness and API load
4. **Monitoring**: Set up alerts for sync failures
5. **Testing**: Test with dummy provider first
6. **Backups**: Synology should have backup configured

## Future Enhancements

- [ ] Support for video files
- [ ] Configurable file size limits
- [ ] Metrics dashboard
- [ ] Multiple target albums based on channels
- [ ] Webhook-based real-time sync
- [ ] Slack/Discord provider implementations
- [ ] S3/Cloud storage providers
