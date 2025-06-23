# Notifications Module

This module provides functionality for sending notifications using Firebase Cloud Messaging (FCM).

## Features

- Send notifications about topic modifications
- Send notifications about workspace modifications
- Send notifications about user modifications
- Send notifications about booking modifications
- Receive Google Calendar push notifications

## Setup

### Firebase Configuration

To use this module, you need to configure Firebase credentials. There are two ways to do this:

1. **Environment Variable (Recommended for Production)**

   Set the `FIREBASE_SA_JSON` environment variable with the contents of your Firebase service account JSON file.

2. **File-based Configuration (Development)**

   Place your Firebase service account JSON file at `src/main/resources/firebase-credentials.json`.

   A sample file is provided, but you should replace it with your actual Firebase credentials.

## Usage

### Sending Notifications

Inject the `INotificationSender` interface into your service or controller:

```kotlin
@Service
class YourService(
    private val notificationSender: INotificationSender
) {
    fun someMethod() {
        // Send an empty notification
        notificationSender.sendEmptyMessage("your-topic")
        
        // Send a notification about a workspace modification
        notificationSender.sendContentMessage(HttpMethod.POST, workspaceDTO)
        
        // Send a notification about a user modification
        notificationSender.sendContentMessage(HttpMethod.PUT, userDto)
        
        // Send a notification about a booking modification
        notificationSender.sendContentMessage(HttpMethod.DELETE, bookingDto)
    }
}
```

### Receiving Google Calendar Notifications

The module provides an endpoint at `/api/notifications` that can be configured as a webhook URL for Google Calendar push notifications.

When a notification is received, it will:
1. Log the notification payload
2. Send an empty message with the topic "booking"

## Integration with Other Modules

This module depends on:
- User module
- Booking module
- Workspace module

Make sure these modules are properly configured in your application.