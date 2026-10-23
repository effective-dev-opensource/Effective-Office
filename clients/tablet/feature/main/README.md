# Main Feature Module

## Overview
The Main feature is the screen the tablet stands on all day: the state of the room it was set up
with, that room's schedule for the selected day, and the buttons that start every other flow.

The tablet is a kiosk. It has no users, no login and no notifications; the only "profile" it has is
the room name kept in settings.

## Features
- The current state of the selected room — free or busy, by whom, and until when
- How long until the next booking starts
- The room's slots for the selected day, drawn by the slot module
- Step to the next or previous day, and back to today after a while of no one touching the tablet
- Switch to another room from the list on the right, to look at its schedule
- Fast booking for 15, 30 or 60 minutes
- Free up the room by cancelling the booking running right now
- A disconnect banner while the server is unreachable, over the last data that did arrive

## Architecture

```
main/
├── components/      # Room state views and the two panels the screen is split into
├── di/              # Koin module
├── domain/          # CurrentTimeHolder and the use cases only this screen needs
└── presentation/
    ├── main/        # MainScreen, MainScreenView, MainViewModel, Intent, State, Label
    └── freeuproom/  # The "free up the room" confirmation modal
```

## Key Components

### Screens
- **MainScreen**: reads the state and forwards the ViewModel's navigation events to the host.
- **MainScreenView**: splits the screen into `RoomInfoLeftPanel` — the room state, the date strip
  and the slots — and `FastBookingRightSide` — the duration buttons and the room list.
- **FreeSelectRoom**: the confirmation modal for cancelling the running booking, hosted as an
  overlay like the other modals.

### ViewModels
- **MainViewModel**: holds the room list, the selected room and the selected date; drives the slot
  presenter it creates through `SlotComponentFactory`; re-reads the rooms on push or poll updates
  and on every minute of the clock. It navigates by emitting `MainNavEvent` — `OpenFastBooking`,
  `OpenFreeRoom`, `OpenBookingEditor` — which `AppNavHost` turns into overlays. It also registers
  the callback `DateResetManager` fires on inactivity, which returns the screen to today, to the
  configured room and to a collapsed slot list.
- **FreeSelectRoomViewModel**: built by Koin with the event and the room name as assisted
  parameters; deletes the booking and asks to close.

### Domain
- **CurrentTimeHolder**: the wall clock the screens read, moved by the platform's time receiver.
- **GetRoomIndexUseCase**: finds the configured room in the list the server returned.
- **GetTimeToNextEventUseCase**: minutes until the next booking of the selected room.
- **RefreshOnTimeZoneChangeUseCase**: re-reads the rooms when the device's time zone moves.

## Integration
The Main module integrates with:
- Core domain and data modules, for rooms, bookings and the refresh use cases
- Core UI module, for the shared views, the loading and error screens and the palette
- Slot module, whose presenter it owns and whose taps it turns into editor requests
- ComposeApp module, which hosts the screen as the `MainRoute` destination and opens the modals it
  asks for

## Development
### Adding a New Modal Request
1. Add a case to `MainNavEvent` in `MainViewModel.kt`, carrying what the modal needs
2. Emit it from the intent handler
3. Handle it in `AppNavHost`, which owns the overlay state — modals are not destinations
