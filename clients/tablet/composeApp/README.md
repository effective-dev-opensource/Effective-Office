# ComposeApp Module

## Overview
The ComposeApp module is the main entry point for the Effective Office tablet application. It integrates all features and core components into a cohesive Compose Multiplatform application that can run on multiple platforms.

## Features
- Compose Multiplatform UI implementation
- Application entry point
- Navigation and routing
- Dependency injection setup
- Theme and styling configuration

## Architecture
The module follows a modular architecture, integrating various features and core components:

```
composeApp/
├── src/                # Source code
│   ├── androidMain/    # Android-specific implementations
│   ├── commonMain/     # Cross-platform shared code
│   ├── iosMain/        # iOS-specific implementations
│   └── desktopMain/    # Desktop-specific implementations (if applicable)
├── build.gradle.kts    # Build configuration
└── resources/          # Shared resources
```

## Key Components
- **AppRoot**: Main application composable that provides the root `ViewModelStoreOwner`, theme, and `AppNavHost`
- **AppNavHost**: Compose Navigation graph (`NavHost`/`NavController`) with type-safe routes; only the two full-screen `composable<>` destinations (Settings/Main). The modals (FreeRoom/BookingEditor/FastBooking) are state-driven overlays hosted by `ModalHost` alongside the graph, not destinations — calf's native iOS pickers do not receive touches inside a Compose dialog window
- **ModalHost**: the dim behind a modal overlay, its own `ViewModelStoreOwner`, the back gesture, and the shift that keeps the field being typed into clear of the on-screen keyboard
- **DI**: Dependency injection setup for the application
- **Theme**: Application-wide styling and theming

## Integration
The ComposeApp module integrates with:
- Core modules (data, domain, ui)
- Feature modules (bookingEditor, fastbooking, main, settings, slot)

## Development
### Adding a New Feature
To integrate a new feature into the application:
1. Add the feature module as a dependency in the build.gradle.kts file
2. Register the feature's navigation routes
3. Add the feature's screens to the navigation graph

### Platform-Specific Considerations
The module handles platform-specific implementations through the different source sets:
- androidMain: Android-specific code
- iosMain: iOS-specific code
- commonMain: Shared code across all platforms

## How rooms stay up to date, per platform

Two mechanisms, and which one a platform gets depends on whether Firebase exists there at all.

**Android is pushed to.** FCM works and is the primary path; verified on real hardware.

**iOS and Aurora are not.** There is no Firebase in `iosMain`, and Aurora has no FCM to begin with,
so `Collector.emit` is never called on either. Both rely on polling instead:
`PeriodicRoomRefreshUseCase` calls `RefreshDataUseCase` on a timer — a minute on Aurora — and the
refresh writes into the local repository's buffer that the main screen is already subscribed to, so
the existing chain carries the update the rest of the way.

Worth knowing what push-only costs, because it was seen during testing: the Android emulator showed
a slot as free for an hour after another client had booked it, and the backend had the booking the
whole time. A push that does not arrive is indistinguishable from nothing having changed, and the
screen stays wrong until the app is restarted — which on a wall-mounted tablet can be weeks. A
deliberately low-frequency backstop on Android would close that off.

## Clock and timezone changes

`TimeReceiver` is an `expect class` with one implementation per platform, because each system has
its own way of waking an app once a minute and using it is what keeps a wall-mounted tablet off the
battery.

- **Android** registers a `BroadcastReceiver` for `ACTION_TIME_TICK` — the system's own minute
  cadence, so no timer at all — plus `ACTION_TIME_CHANGED` and `ACTION_TIMEZONE_CHANGED`.
- **iOS** puts an `NSTimer` on the main run loop and observes `NSSystemClockDidChange`.
- **Aurora** has neither, so a coroutine sleeps to the next whole minute, pushes the time and
  repeats. A manual clock change or a timezone switch is therefore not observed — it is picked up at
  the next tick, up to a minute late. Acceptable for a room tablet, but a polyfill, not an
  equivalent.

The instance comes from Koin via `timeReceiverModule()`, an expect module shaped like
`settingsStoreModule()`, because only Android's implementation needs a `Context`. `AppRoot` starts
and stops it — the one root all three platforms share. An earlier version constructed it in
`AppActivity` alone, and the clock silently never moved on iOS or Aurora.

**Still unverified on real hardware:** that Android really does react to a clock change and a
timezone switch at once. The receivers are registered for it and the code path is short, but nobody
has moved a device's clock and watched.