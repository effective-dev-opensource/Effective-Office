# Calendar Subscription Module

This module provides functionality to subscribe to Google Calendar notifications for specified calendars. It periodically renews the subscriptions to ensure they don't expire.

## Features

- Automatic subscription to Google Calendar notifications
- Support for both production and test environments
- Scheduled renewal of subscriptions (every 6 days)
- Configurable through application properties or environment variables

## Configuration

The module can be configured through the following properties in `application.yml` , `application-local.yml` or environment variables:

| Property | Environment Variable | Description |
|----------|---------------------|-------------|
| `calendar.subscription.default-app-email` | `DEFAULT_APP_EMAIL` | Default application email used for Google Calendar API |
| `calendar.subscription.google-credentials` | `JSON_GOOGLE_CREDENTIALS` | JSON credentials for Google API authentication |
| `calendar.subscription.application-url` | `APPLICATION_URL` | Application URL for production environment |
| `calendar.subscription.test-application-url` | `TEST_APPLICATION_URL` | Application URL for test environment |
| `calendar.subscription.calendars` | `CALENDARS` | Comma-separated list of calendar IDs for production environment |
| `calendar.subscription.test-calendars` | `TEST_CALENDARS` | Comma-separated list of calendar IDs for test environment |

## Usage

The module automatically starts when the application is launched. It subscribes to the specified calendars and renews the subscriptions every 6 days.

To receive notifications, the application must expose a `/notifications` endpoint that can handle Google Calendar notification events.

## Dependencies

- Google Calendar API
- Spring Boot
- Spring Scheduling
