# Google Calendar API Integration

## Overview
Effective Office integrates with Google Calendar API to provide robust calendar functionality for workspace booking and event management. This document provides detailed information about how Google Calendar API is used in the project, configuration requirements, and implementation details for developers.

## Features
- **Workspace Booking Management**: Create, update, delete, and find booking events in Google Calendar
- **Real-time Calendar Notifications**: Subscribe to Google Calendar push notifications for real-time updates
- **Recurring Events Support**: Handle recurring booking patterns with Google Calendar recurrence rules
- **User Integration**: Associate bookings with users as organizers and attendees
- **Availability Checking**: Prevent overlapping bookings by checking calendar availability

## Architecture
The Google Calendar integration is implemented in two main modules:

1. **Booking Calendar Module** (`backend/feature/booking/calendar/google/`):
   - Provides implementation of the `CalendarProvider` interface using Google Calendar API
   - Handles CRUD operations for booking events
   - Manages workspace calendar associations

2. **Calendar Subscription Module** (`backend/feature/calendar-subscription/`):
   - Manages subscriptions to Google Calendar push notifications
   - Periodically renews subscriptions to ensure they don't expire
   - Processes incoming notification events

## Configuration

### Required Properties
The following properties must be configured in your application configuration files or environment variables:

#### Booking Calendar Configuration
```yaml
calendar:
  provider: google                      # Set to "google" to enable Google Calendar integration
  application-name: Effective Office    # Application name for Google API requests
  delegated-user: user@example.com      # User to delegate access to (optional)
  credentials.file: classpath:google-credentials.json  # Path to credentials file
  default-calendar: primary             # Default calendar ID to use
```

#### Calendar Subscription Configuration
```yaml
calendar.subscription:
  default-app-email: service-account@example.com  # Default application email for Google Calendar API
  google-credentials: classpath:google-credentials.json  # JSON credentials for Google API authentication
  application-url: https://your-app.example.com  # Application URL for production environment
  test-application-url: https://test-app.example.com  # Application URL for test environment
  calendars: calendar1@group.calendar.google.com,calendar2@group.calendar.google.com  # Comma-separated list of calendar IDs
  test-calendars: test-calendar@group.calendar.google.com  # Comma-separated list of test calendar IDs
```

### Google API Credentials
To use Google Calendar API, you need to:

1. Create a project in the [Google Cloud Console](https://console.cloud.google.com/)
2. Enable the Google Calendar API for your project
3. Create OAuth 2.0 credentials or a service account with appropriate permissions
4. Download the credentials JSON file and place it in your project's resources directory
5. Configure the application to use these credentials

## Implementation Details

### Booking Management
The `GoogleCalendarProvider` class implements the `CalendarProvider` interface and provides methods for:

- **Creating Events**: Convert booking data to Google Calendar events and insert them
- **Updating Events**: Modify existing events with updated booking information
- **Deleting Events**: Remove events from Google Calendar
- **Finding Events**: Query events by workspace, user, or time range
- **Handling Recurrence**: Convert between application recurrence models and Google Calendar recurrence rules

### Push Notifications
The `GoogleCalendarService` class manages Google Calendar push notifications:

- **Subscription Setup**: Create notification channels for specified calendars
- **Channel Management**: Store channel information in the database and renew before expiration
- **Notification Processing**: Handle incoming notifications via a dedicated endpoint

The `CalendarSubscriptionScheduler` runs every 6 days to renew subscriptions before they expire (Google Calendar subscriptions expire after 7 days).

### Notification Handling
The `CalendarNotificationsController` provides an endpoint at `/notifications` that receives push notifications from Google Calendar when events change.

## Usage Examples

### Creating a Booking Event
```kotlin
// Inject the CalendarProvider (will be GoogleCalendarProvider when calendar.provider=google)
@Autowired
private lateinit var calendarProvider: CalendarProvider

// Create a booking
val booking = Booking(
    id = "",  // Will be set by Google Calendar
    owner = user,
    participants = listOf(participant1, participant2),
    workspace = workspace,
    beginBooking = startTime,
    endBooking = endTime,
    recurrence = recurrenceModel  // Optional
)

// Save to Google Calendar
val savedBooking = calendarProvider.createEvent(booking)
```

### Subscribing to Calendar Notifications
The subscription process is handled automatically by the `CalendarSubscriptionScheduler`, which runs periodically to ensure subscriptions are active.

## Troubleshooting

### Common Issues
1. **Authentication Failures**: Verify that your credentials file is correctly formatted and has the necessary permissions
2. **Missing Calendar**: Ensure the calendar IDs specified in configuration exist and are accessible
3. **Webhook Errors**: Verify that your application is publicly accessible for receiving push notifications
4. **Rate Limiting**: Google Calendar API has usage limits; implement retries with exponential backoff for high-volume operations

### Debugging
Enable debug logging for Google Calendar related classes:

```yaml
logging:
  level:
    band.effective.office.backend.feature.booking.calendar.google: DEBUG
    band.effective.office.backend.feature.calendar.subscription: DEBUG
```

## Custom Calendar Implementation

The application is designed with flexibility in mind, allowing you to replace the Google Calendar API with your own custom calendar service implementation. This is particularly useful for:

- Development and testing without requiring Google Calendar credentials
- Implementing integration with different calendar services
- Creating a simplified calendar implementation for specific use cases

### Using the Dummy Calendar Provider

The project includes a `dummy-calendar` module that provides a simple in-memory implementation of the `CalendarProvider` interface. This implementation:

- Stores bookings in memory using a ConcurrentHashMap
- Provides all the required functionality without external dependencies
- Is ideal for development, testing, and demonstration purposes

To use the dummy calendar provider instead of Google Calendar:

1. Set the `calendar.provider` property to `dummy` in your application configuration:

```yaml
calendar:
  provider: dummy
```

2. No additional configuration is required for the dummy provider

### Creating Your Own Calendar Provider

You can create your own custom calendar service by implementing the `CalendarProvider` interface:

1. Create a new module or package for your implementation
2. Implement the `CalendarProvider` interface with your custom logic
3. Annotate your implementation with `@Component` and `@ConditionalOnProperty`:

```kotlin
@Component("yourCustomCalendarProvider")
@ConditionalOnProperty(name = ["calendar.provider"], havingValue = "custom")
class YourCustomCalendarProvider : CalendarProvider {
    // Implement all required methods
}
```

4. Set the `calendar.provider` property to your custom value in the application configuration

The application will automatically use your custom implementation when the appropriate property is set.

## References
- [Google Calendar API Documentation](https://developers.google.com/calendar/api/guides/overview)
- [Push Notifications Guide](https://developers.google.com/calendar/api/guides/push)
- [Recurring Events](https://developers.google.com/calendar/api/concepts/events-calendars#recurring_events)
