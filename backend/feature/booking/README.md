# Booking Module

## Overview
The Booking module provides functionality for managing resource bookings within the Effective Office system. It enables users to book office resources such as meeting rooms, desks, and equipment, and integrates with calendar systems for scheduling and notifications.

## Features
- Resource booking management
- Calendar integration (Google Calendar and dummy implementation for testing)
- Booking validation and conflict resolution
- Booking notifications
- RESTful API for booking operations

## Architecture
The module follows a layered architecture:

```
booking/
├── calendar/          # Calendar integration components
│   ├── dummy/         # Dummy calendar implementation for testing
│   └── google/        # Google Calendar integration
├── core/              # Core booking functionality
│   ├── config/        # Configuration classes
│   ├── controller/    # REST controllers
│   ├── domain/        # Domain models
│   ├── dto/           # Data Transfer Objects
│   ├── exception/     # Exception handling
│   └── service/       # Business logic services
```

## Key Components

### Core Components
- **BookingController**: REST API endpoints for booking operations
- **BookingService**: Business logic for booking management
- **BookingDomain**: Domain models representing bookings and related entities
- **BookingDTO**: Data Transfer Objects for API communication

### Calendar Integration
- **GoogleCalendarIntegration**: Integration with Google Calendar
- **DummyCalendarImplementation**: Mock implementation for testing and development

## Configuration
The module provides default configurations that can be customized:

1. **Booking Configuration**:
   ```kotlin
   @Configuration
   class BookingConfig {
       // Configuration properties and beans
   }
   ```

2. **Calendar Integration Configuration**:
   The module supports configuration for different calendar providers.

## Error Handling
The module provides custom exceptions for different booking scenarios:
- `BookingException`: Base exception for all booking errors
- `BookingConflictException`: Thrown when a booking conflicts with an existing booking
- `ResourceUnavailableException`: Thrown when a resource is not available for booking
- `InvalidBookingException`: Thrown when booking parameters are invalid

## Integration
The Booking module integrates with:
- Calendar systems (Google Calendar)
- Notification module for booking alerts
- User module for user information
- Workspace module for resource availability

## Examples

### Basic Booking Flow
1. A user requests to book a resource for a specific time period
2. The system validates the booking request
3. If the resource is available, the booking is created
4. The booking is synchronized with the user's calendar

### Booking Conflict Resolution
When a booking conflicts with an existing booking:
1. The system identifies the conflict
2. The user is notified of the conflict

## Development

### Adding a New Calendar Integration
To add a new calendar integration:
1. Create a new implementation in the calendar package
2. Implement the required interfaces
3. Register the implementation in the configuration