# Slot Feature Module

## Overview
The Slot feature module provides functionality for managing and visualizing time slots for resource booking in the Effective Office tablet application. It enables users to view available time slots, understand resource availability, and select appropriate time periods for their bookings.

## Features
- Time slot visualization
- Resource availability display
- Slot selection and interaction
- Time grid navigation
- Day, week, and month views
- Conflict detection
- Visual indicators for different slot states
- Time zone handling

## Architecture
The module follows a feature-based architecture:

```
slot/
├── component/       # Feature-specific UI components
├── di/              # Dependency injection setup
├── model/           # UI models and state holders
├── navigation/      # Navigation routes and arguments
├── screen/          # Screens and composables
│   ├── day/         # Day view screen
│   ├── week/        # Week view screen
│   └── month/       # Month view screen
└── viewmodel/       # ViewModels for the screens
```

## Key Components

### Screens
- **DayViewScreen**: Detailed view of slots for a single day
- **WeekViewScreen**: Overview of slots for a week
- **MonthViewScreen**: Monthly calendar view with availability indicators

### ViewModels
- **DayViewModel**: Provides slot data for the day view
- **WeekViewModel**: Manages slot data for the week view
- **MonthViewModel**: Handles slot data for the month view

### Components
- **TimeSlot**: Visual representation of a time slot
- **TimeGrid**: Grid layout for organizing time slots
- **SlotLegend**: Legend explaining slot status indicators
- **TimeSelector**: Component for selecting time ranges

## Integration
The Slot module integrates with:
- Core domain module for business logic
- Core data module for data operations
- Core UI module for shared UI components
- Booking Editor module for creating bookings
- Fast Booking module for quick slot selection

## Bookings the tablet may not touch

Every tablet books through one shared account, and the calendar only lets that account change what
it organised. The backend says which is which per booking, in `isEditable`: true for what was booked
from a tablet, false for what someone made from their own calendar. A booking of the second kind is
the tablet's to show and nothing more.

So an event slot whose booking is not editable **takes no taps at all** (`SlotView`), rather than
opening the editor on one — the editor would come up with its "Confirm changes" and "Delete booking"
already dead, since `bookingEditor` gates both on the same flag, leaving the reader to guess why.
Free slots and the tablet's own bookings are unaffected; a multi-slot still expands, and the rule
applies to each booking inside it.

This is what testers reported as a regression against a much older build: the slot list only became
interactive in July 2025, and until then no slot took taps, so someone else's booking could not be
opened for the trivial reason that nothing could.

### Seeing it on an offline stand

`DummyCalendarProvider` never sets `isEditable`, so it stays at the model default — on a stand
without Google **every booking looks editable** and this path cannot be exercised at all. Google
derives the flag from the event's organizer, and nothing of that survives the REST API: a booking
arrives the same way whoever made it. A dummy stand therefore has to name the distinction itself,
e.g. a `@Value("\${calendar.dummy.external-owners:}")` list of emails and a
`copy(isEditable = owner?.email !in externalOwners)` on the provider's read and return paths, with
the property set when the stand is started. Verified that way on the Android emulator: a booking by
a listed owner stops responding to taps, one by anybody else still opens the editor.

## User Flows

### Viewing Available Slots
1. User navigates to the slot view screen
2. User selects a date or date range
3. System displays available and occupied slots
4. User can filter by resource type or specific resources

### Selecting a Slot for Booking
1. User browses available slots
2. User selects a slot or time range
3. System validates the selection
4. User is directed to the booking creation flow with the selected slot

## Time Handling
The Slot module handles various time-related complexities:
- Time zone conversions
- Business hours vs. 24-hour display
- Recurring availability patterns
- Holiday and special day handling

## Development
### Adding a New Slot View
To add a new way to visualize slots:
1. Create a new screen in the screen package
2. Implement the corresponding ViewModel
3. Design the UI components needed for the view
4. Add navigation routes and integrate with existing views

### Customizing Slot Appearance
To modify how slots are displayed:
1. Update the TimeSlot component or create a variant
2. Modify the styling in the theme
3. Ensure the changes are consistent across all views