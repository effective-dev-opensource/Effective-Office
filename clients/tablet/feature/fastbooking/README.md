# Fast Booking Feature Module

## Overview
Fast Booking is the "book it now" path: one tap on the main screen picks a room and books it from
this minute for the requested number of minutes, without asking anything else. Nothing is chosen by
hand here — the modal opens straight into loading and reports what it did.

## Features
- Pick a room automatically: the current one if it is free for long enough, otherwise the free room
  closest to it in capacity
- Book it from now for the requested duration
- Report success, with the room and the time span, and offer to cancel the booking right back
- Report failure when nothing is free, naming the room that frees up soonest and in how many minutes
- Keep the clock on the card ticking while the modal is open

## Architecture

```
fastbooking/
├── di/                            # Koin module: the ViewModel and its assisted parameters
└── presentation/
    ├── FastBooking.kt             # The modal composable, a view per state
    ├── FastBookingViewModel.kt    # Room selection, booking and cancellation
    ├── Intent.kt                  # User actions
    └── State.kt                   # UI state and the FastBookingModal it is in
```

## Key Components
- **FastBooking**: the modal composable, hosted as a state-driven overlay by `AppNavHost`. It draws
  the loading, success, failure or error view according to `State.modal`.
- **FastBookingViewModel**: built by Koin with the minimum duration, the selected room and the room
  list as assisted parameters — the caller already holds them, so nothing is re-fetched here. Emits
  a close event when the flow is done.
- **FastBookingModal**: which of its three cases the flow is in — `Loading`, `Success` or
  `Failure`. The error view is not one of them: `State.isError` puts it up in place of the success
  or failure card.

The success and failure cards themselves — `SuccessFastSelectRoomView`, `FailureFastSelectRoomView`
— live in the core UI module.

## Integration
The Fast Booking module integrates with:
- Core domain module, for `SelectRoomUseCase`, booking creation and deletion
- Core UI module, for the result cards, the loader and the palette
- ComposeApp module, which hosts it as an overlay and passes the rooms in

## Development
### Changing How a Room Is Picked
The choice is `SelectRoomUseCase` in the domain module, not this one: it keeps the current room if
it is free for the whole duration and otherwise takes the free room nearest in capacity. Change it
there, and this module will follow.
