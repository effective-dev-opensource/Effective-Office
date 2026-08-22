# ComposeApp Module

## Overview
The entry point of the Effective Office tablet application. It wires the feature and core modules
together, starts Koin, installs the theme and the inactivity tracking the whole screen shares, and
hosts the navigation graph.

The tablet is a kiosk hung on a meeting room wall. It has no users, no accounts and no login: the
room it shows is picked once in Settings and kept in local storage.

## Architecture

```
composeApp/
├── src/
│   ├── commonMain/     # AppRoot, navigation graph, DI, logger, version overlay
│   ├── androidMain/    # Application/Activity, Firebase messaging, kiosk mode
│   └── iosMain/        # ComposeUIViewController entry point and initializers
└── build.gradle.kts    # Targets, BuildKonfig fields, module dependencies
```

## Key Components
- **AppRoot**: the composition root. Provides a root `ViewModelStoreOwner`, the theme, the
  `InactivityTracking` instance and the `InactivityTracker` that feeds it; starts the platform
  `TimeReceiver` and the periodic room refresh; reads the configured room to pick the start
  destination; draws `VersionOverlay` on top.
- **AppNavHost**: the navigation graph and the modal host — see [Navigation](#navigation).
- **Routes**: the two destinations, `SettingsRoute` and `MainRoute`.
- **ModalBackHandler**: `expect`/`actual` back-gesture hook for the modal overlays, which are not on
  the nav back stack.
- **KoinInitializer**, **appModule**, **firebaseTopicsModule**: DI setup; `appModule` also builds
  `ApiConfig` out of the BuildKonfig fields and the debug flag.
- **TimeReceiver**: `expect` class that advances the clock once a minute on the wake-up each
  platform already provides, rather than on a timer of ours.
- **LoggerInitializer**: Napier setup.

## Navigation

There is one `NavHost` with two destinations. `SettingsRoute` is the start destination until a room
is configured, `MainRoute` afterwards; picking a room in Settings navigates to Main and pops
Settings off the stack.

Everything else the tablet shows — fast booking, freeing up a room, the booking editor — is a modal
overlay driven by state held in `AppNavHost`, not a destination. `ModalHost` draws the dimmed
backdrop, owns a modal-scoped `ViewModelStoreOwner` cleared on dispose, dismisses on a tap outside
the card and routes the back gesture to the same dismissal. The inactivity timeout closes whatever
overlay is up, so a modal is never left addressing one room over another room's schedule.

### Why the modals are overlays and not `dialog<>` destinations

A `dialog<>` destination is a nested window, and calf's date and time pickers are native UIKit views
on iOS. Inside such a window they receive no touches at all: the calendar will not take a date and
the wheels will not spin.

A dialog is also a scene of its own. What the application root installs — the inactivity tracking,
the theme wrappers, the composition locals — is not inherited inside it, so every extra dialog
window has to be furnished again from scratch.

### Why the date picker is still a `Dialog`

The date/time picker is the one place that has to sit above the booking editor, which is itself an
overlay, so it stays the only `Dialog` in the chain. It pays the price named above: it re-applies
`InactivityTracker` itself, because the root's tracking does not reach into its scene.

## Integration
The module depends on the core modules (`data`, `domain`, `ui`) and on every feature module
(`main`, `settings`, `bookingEditor`, `fastBooking`, `slot`).

## Platform Notes
- **Android**: `App` starts Koin, initializes `SettingsManager` on shared preferences and subscribes
  to the Firebase topics; `AppActivity` provides the `KioskManager` and sets `AppRoot` as content.
  Kiosk mode requires the app to be a Device Owner — see `clients/README.md`.
- **iOS**: `Initializers` starts Koin and initializes `SettingsManager` on the keychain;
  `rootViewController()` wraps `AppRoot` in a `ComposeUIViewController`.
- **Aurora**: `main` starts Koin, initializes `SettingsManager` on `ak-shared-preferences` and hands
  off to `application { AppRoot() }` — the fork creates the window itself, so there is no `Window`
  here and `AppRoot` is what provides the root `LocalViewModelStoreOwner`. It installs the `Antilog`
  itself rather than through `LoggerInitializer`: the Aurora variant links a *release* binary, so
  `isDebug` is false and the gate there would leave the device with no log at all.

### Resource packaging on Aurora
Aurora packages compose resources flat and without a namespace: `<qualifier>/<file>` becomes
`<qualifier>_<file>`. Two files that flatten alike collapse into a single `.cvr`, and whichever
module's `Res` reaches it first reads the other module's bytes at its own offsets. That is why every
tablet module names its string file differently, and why `stageAuroraResources` gathers them with
`duplicatesStrategy = FAIL`: a future clash is a build error instead of a wrong read at runtime.
