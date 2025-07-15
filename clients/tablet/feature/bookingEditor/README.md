# Booking Editor Feature Module

## Overview
The Booking Editor feature module provides functionality for creating, editing, and managing bookings within the Effective Office tablet application. It allows users to select resources, specify time slots, add participants, and configure booking details.

## Features
- Create new bookings
- Edit existing bookings
- Select resources (rooms, desks, equipment)
- Choose date and time slots
- Add and remove participants
- Set booking title and description
- Configure recurring bookings
- Validate booking constraints

## Architecture
The module follows a feature-based architecture:

```
bookingEditor/
├── di/              # Dependency injection setup
├── domain/          # Domain models and business logic
└── presentation/    # UI components and screens
    ├── BookingEditor.kt         # Main UI component
    ├── BookingEditorComponent.kt # Component implementation
    ├── datetimepicker/          # Date and time picker components
    ├── Intent.kt                # User actions/intents
    ├── mapper/                  # Model mappers
    └── State.kt                 # UI state definitions
```

## Key Components

### Main Components
- **BookingEditor**: Main UI component for the booking editor screen
- **BookingEditorComponent**: Implementation of the booking editor component following MVI architecture

### State Management
- **Intent**: Defines user actions and events that can occur in the booking editor
- **State**: Defines the UI state for the booking editor screen

### UI Components
- **DateTimePicker**: Component for selecting date and time
- **Mappers**: Utilities for mapping between domain and UI models

## Integration
The Booking Editor module integrates with:
- Core domain module for business logic
- Core data module for data operations
- Core UI module for shared UI components
- Other feature modules for navigation

## User Flows

### Creating a New Booking
1. User navigates to the create booking screen
2. User selects a resource (room, desk, etc.)
3. User chooses date and time slot
4. User adds participants (optional)
5. User enters booking title and description
6. User configures recurrence (optional)
7. User submits the booking

### Editing an Existing Booking
1. User navigates to the booking details screen
2. User selects the edit option
3. User modifies booking details
4. User saves the changes

## Development
### Adding a New Booking Field
To add a new field to the booking form:
1. Update the domain models in the domain directory
2. Update the State.kt file to include the new field in the UI state
3. Modify the Intent.kt file if new user actions are needed
4. Update the BookingEditor.kt UI component to display the new field
5. Update the BookingEditorComponent.kt to handle the new field's logic
6. Update any mappers if needed to convert between domain and UI models
