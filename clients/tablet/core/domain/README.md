# Domain Module

## Overview
The Domain module holds the business rules of the Effective Office tablet application: the models
the screens speak in, the use cases that operate on them, and the repository interfaces the data
module implements. It depends on no UI framework.

## Architecture

```
domain/
├── di/              # Koin module wiring the use cases
├── manager/         # DateResetManager
├── model/           # Domain models and the settings storage
├── platform/        # expect/actual platform values
├── repository/      # Repository interfaces
├── useCase/         # Use cases
├── util/            # BootstrapperTimer, slot helpers
├── ErrorWithData.kt # An error that still carries the last good data
└── OfficeTime.kt    # Working hours of the office
```

## Key Components

### Models
- **RoomInfo**, **EventInfo**, **Organizer**, **Booking**: what a room, a booking and its organizer
  look like to the rest of the app.
- **Slot**: a stretch of the day — free, taken by an event, or a group of several.
- **RoomsEnum**: the room names shown in Settings before the server answers.
- **SettingsManager**: the one persisted setting, the room this tablet was set up with, over
  Multiplatform Settings.

### Repositories
Interfaces only: `RoomRepository`, `BookingRepository`, `OrganizerRepository` for the network, and
`LocalRoomRepository`, `LocalBookingRepository` for the in-memory state. Implementations live in the
data module.

### Use Cases
- Bookings: `CreateBookingUseCase`, `UpdateBookingUseCase`, `DeleteBookingUseCase`,
  `CheckBookingUseCase`.
- Rooms: `RoomInfoUseCase` as the aggregate front door, over `GetRoomsInfoUseCase`,
  `GetRoomByNameUseCase`, `GetRoomNamesUseCase`, `GetCurrentRoomInfosUseCase`,
  `GetEventsFlowUseCase` and `RefreshDataUseCase`.
- Refresh: `PeriodicRoomRefreshUseCase` polls where push is unavailable,
  `ResourceDisposerUseCase` starts the subscriptions the app lives on, `UpdateUseCase` re-reads on a
  timer.
- Settings: `CheckSettingsUseCase` and `SetRoomUseCase` over `SettingsManager`.
- Slots and time: `SlotUseCase` cuts a working day into slots, `TimerUseCase` provides the delays
  the presenters schedule on.
- Fast booking: `SelectRoomUseCase` picks the free room closest in capacity to the current one.

### Other
- **DateResetManager**: carries an inactivity timeout from whoever detects it to whoever holds the
  selected date.
- **BootstrapperTimer**: a restartable timer the presenters use to re-read on a schedule.
- **roomRefreshInterval**: `expect` value, `null` on Android because push covers it there and a
  polling interval elsewhere.
- **OfficeTime**: the working day, 8:00 to 22:00, that the slot grid is built over.

## Error Handling
Results are returned as `Either`, defined in the shared core module together with `unbox`, `map`,
`fold` and friends. Room results carry `ErrorWithData`, which pairs the error with the last good
data so a disconnected tablet keeps showing a schedule instead of an empty screen.

## Integration
The Domain module is used by:
- the feature modules, which drive it from their ViewModels and presenters;
- the Data module, which implements its repository interfaces.

## Development
### Adding a New Use Case
1. Add the models it needs under `model/`
2. Add the use case under `useCase/`, taking the repositories it needs as constructor parameters
3. Register it in `di/domainModule.kt`
