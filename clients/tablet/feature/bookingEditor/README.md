# Booking Editor Feature Module

## Overview
The Booking Editor is the modal the tablet opens on a slot: it creates a booking on a free slot and
edits or deletes an existing one. The room is not chosen here — it is the room the tablet was set up
with, handed in by the caller together with the event.

## Features
- Create a booking on a free slot
- Change the day of a booking, a day at a time, or pick a date and time in the picker
- Change the duration, +30 or −15 minutes at a step, no further than the end of the working day
- Choose the organizer from the list, or type a name and have it matched
- Update an existing booking
- Delete an existing booking
- Refuse to confirm a time that is taken or already in the past

There is no title, description, participant list or recurrence: a booking is a room, a time span and
an organizer.

## Architecture

```
bookingEditor/
├── di/                            # Koin module: ViewModel and picker factory
└── presentation/
    ├── BookingEditor.kt           # The modal composable
    ├── BookingEditorViewModel.kt  # State, intents and the use case calls
    ├── Intent.kt                  # User actions
    ├── State.kt                   # UI state
    ├── datetimepicker/            # Date and time picker: presenter, dialog and its views
    └── mapper/                    # Event to state and back
```

## Key Components
- **BookingEditor**: the modal composable, hosted as a state-driven overlay by `AppNavHost` rather
  than as a window of its own.
- **BookingEditorViewModel**: built by Koin with the event and the room name as assisted parameters.
  Validates every change against `CheckBookingUseCase` and emits a close event when the work is
  done.
- **DateTimePickerComponent**: a presenter, not a ViewModel — it has no lifecycle of its own and
  runs on the scope the editor hands it. Koin builds it through `DateTimePickerComponentFactory`,
  supplying the use case while the owner supplies the scope, the callbacks and the event.
- **DateTimePicker**: the one `Dialog` left in the chain. Why it is a dialog while the modals around
  it are not is written down in `clients/tablet/composeApp/README.md`, under Navigation.

## Bookings the tablet may not touch
An event the tablet did not book arrives with `isEditable` false, and both "Confirm changes" and
"Delete booking" are disabled for it. The slot list refuses to open the editor on such a booking at
all — see `clients/tablet/feature/slot/README.md`.

## Integration
The Booking Editor module integrates with:
- Core domain module, for the booking, organizer and validation use cases
- Core UI module, for the field rows, buttons and the palette
- ComposeApp module, which hosts it as an overlay and supplies the event and the room

## Development
### Adding a New Field
1. Add the field to `State.kt`, and an action for it to `Intent.kt`
2. Handle the action in `BookingEditorViewModel`
3. Show the field in `BookingEditor.kt`
4. Extend the mappers if the field has to survive a round trip to `EventInfo`
