# Notifications Module

This module provides functionality for sending notifications using Firebase Cloud Messaging (FCM) and managing kiosk mode for tablet devices.

## Features

- Send notifications about topic modifications
- Send notifications about workspace modifications
- Send notifications about user modifications
- Send notifications about booking modifications
- Receive Google Calendar push notifications
- Remote kiosk mode management for tablet devices
- Bulk device operations for kiosk mode control
- Device registration and management

## Setup

### Firebase Configuration

To use this module, you need to configure Firebase credentials. There are two ways to do this:

1. **Environment Variable (Recommended for Production)**

   Set the `FIREBASE_SA_JSON` environment variable with the contents of your Firebase service account JSON file.

2. **File-based Configuration (Development)**

   Place your Firebase service account JSON file at `src/main/resources/firebase-credentials.json`.

   A sample file is provided, but you should replace it with your actual Firebase credentials.

### Kiosk Mode Configuration

The kiosk mode functionality uses the existing Firebase configuration from the calendar subscription module.
The kiosk commands are sent to the `kiosk-commands` FCM topic with message type `KIOSK_TOGGLE`.

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
    }
}
```

### Kiosk Mode Management

The module provides REST endpoints for managing kiosk mode on tablet devices. The easiest way to use these endpoints is through Swagger UI:

1. **Access Swagger UI**: Navigate to `https://.../api/swagger-ui.html`
2. **Authenticate**: Click "Authorize" and enter your JWT token
3. **Find Kiosk API**: Look for the "Kiosk" section in the API documentation
4. **Execute Commands**: Use the interactive interface to enable/disable kiosk mode on devices

#### Available Endpoints

- `POST /api/v1/kiosk/device/enable` - Enable kiosk mode for specific device
- `POST /api/v1/kiosk/device/disable` - Disable kiosk mode for specific device
- `POST /api/v1/kiosk/all/enable` - Enable kiosk mode for all devices
- `POST /api/v1/kiosk/all/disable` - Disable kiosk mode for all devices
- `GET /api/v1/kiosk/devices` - Get list of all registered devices

### Receiving Google Calendar Notifications

The module provides an endpoint at `/api/notifications` that can be configured as a webhook URL for Google Calendar push notifications.

When a notification is received, it will:

1. Log the notification payload
2. Send an empty message with the topic "booking"

## Kiosk Mode Architecture

The kiosk mode system consists of several key components:

### Backend Components

- **KioskController**: REST API endpoints for kiosk management
- **DeviceService**: Device registration and management
- **INotificationSender**: FCM message sending interface

### Client Components (Tablet App)

- **KioskManager**: Android Device Owner kiosk operations
- **KioskCommandBus**: Command flow management
- **KioskLifecycleObserver**: Lifecycle-aware command processing
- **ServerMessagingService**: FCM message reception and processing

### Command Flow

1. Administrator sends kiosk command via REST API
2. Backend validates request and sends FCM message
3. Tablet receives FCM message and validates command
4. Device executes kiosk mode operation
5. Command result is logged and processed

## API Reference

### Kiosk Management Endpoints

| Method | Endpoint                       | Description                            | Authentication |
| ------ | ------------------------------ | -------------------------------------- | -------------- |
| POST   | `/api/v1/kiosk/device/enable`  | Enable kiosk mode for specific device  | Required       |
| POST   | `/api/v1/kiosk/device/disable` | Disable kiosk mode for specific device | Required       |
| POST   | `/api/v1/kiosk/all/enable`     | Enable kiosk mode for all devices      | Required       |
| POST   | `/api/v1/kiosk/all/disable`    | Disable kiosk mode for all devices     | Required       |
| GET    | `/api/v1/kiosk/devices`        | Get list of all registered devices     | Required       |

### Request/Response Models

#### KioskToggleRequest

```kotlin
data class KioskToggleRequest(
    val deviceId: String  // Unique Android device ID
)
```

#### KioskMessageDto

```kotlin
data class KioskMessageDto(
    val message: String  // Success message
)
```

## Integration with Other Modules

This module depends on:

- User module
- Booking module
- Workspace module

Make sure these modules are properly configured in your application.

## Troubleshooting

### Common Issues

1. **Kiosk Commands Not Working**

   - Verify Device Owner status on target devices
   - Check FCM token registration
   - Ensure proper Firebase configuration
   - **For Android 9+**: Ensure devices were set up as Device Owner immediately after factory reset
   - **For modern devices**: Check if manufacturer has additional restrictions or requirements

2. **Device Not Found Errors**

   - Verify device registration in database
   - Check device ID format and validity
   - Ensure device has sent registration information

3. **FCM Messages Not Received**
   - Verify Firebase project configuration
   - Check network connectivity
   - Validate FCM token and topic subscriptions

### Debugging

Enable debug logging for notifications and kiosk functionality:

## Security Considerations

- All kiosk management endpoints require JWT authentication
- Device commands are validated before processing
- FCM messages are encrypted in transit
- Device Owner privileges are required for kiosk operations
