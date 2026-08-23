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
- **AppNavHost**: the navigation graph and the state the modal overlays are driven by — see
  [Navigation](#navigation).
- **ModalHost**: the dim, the card and the `ModalHostState` the content inside it is given.
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

`ModalHost` also hands the content inside it a `ModalHostState` through `LocalModalHost`. Its slot
is composed inside the card's own box, after the content and without clipping, so content placed
there rides every transform applied to the card and may reach above its top edge. On Aurora that is
where the organizer list is drawn, a `Popup` there being a scene of its own — see the Aurora window
model in `clients/tablet/core/ui/README.md`.

### Keeping a modal clear of the on-screen keyboard

`modalKeyboardShift` works out what the keyboard costs the card and `ModalHost` applies it. It aims
at **the focused field, not the card**: the card is asked to keep its full height, and only the
field has to end up above the keyboard. Padding the box instead — which is what `imePadding()` did
here on Android — squashes the card into the room that is left, and its content then clips from the
inside: the header disappears, the date row loses 42 px and the confirm button is off the bottom.
The two cannot both be applied, so the padding is gone.

Everything positional is measured in the host container's own space. `positionInWindow()` is
unusable on Aurora, where `ForcedLandscape` sits between the window and the content and the window-Y
of a rotated node is its content-X — the tablet reported a field bottom of 557 in a 1200-tall
container against a 540 px keyboard, and the host duly decided nothing was in the way. So the field
writes its bottom edge into `ModalHostState.focusedFieldBottom` in container space while it holds
focus, the top of the keyboard is the container height less the overlap, and the two are compared
where the rotation cancels out. The field is the one that reports, because what has to clear the
keyboard is the field and only the field knows where it is.

The card is moved by a `graphicsLayer` translation and never by a layout offset: the field reports
its position from layout, so a layout offset would feed the shift back into its own input and the
card would jitter between two positions. For the same reason the resting position is captured once,
while the shift is still zero. The height cap is a `requiredHeightIn` against the tallest the
container has ever been, so that on iOS — where the host shortens the whole scene for the keyboard —
the card goes on being measured against the screen it had before.

`ModalHost` also reports both a height and a position for each of its two boxes, and one cannot be
derived from the other: heights feed composition and have to invalidate it, positions feed
measurement; the same `LayoutCoordinates` instance comes back every time, so a state holding one
never reports a change.

A keyboard that goes away behind the app's back — a real gesture on Aurora, which the fork does not
report — shows up as the overlap dropping back to zero. That is taken as the end of editing and
answered by dropping the focus, which is the door everything else leaves editing through.

#### What the shift cannot do

On the reference tablet the editor card is about 940 px tall, the keyboard leaves 583 px of the
container above it, and the organizer field's bottom edge sits 752 px down the card. To put that
edge above the keyboard the card has to hang at least 169 px off the top; the card's header ends
148 px down. The two do not overlap, so the header cannot be on screen at the moment the field is —
case 4.6 and case 4.7 of the checklist are mutually exclusive at this geometry, and no gap or clamp
setting reconciles them. The shift favours the field, because a field you cannot see is a field you
cannot type into; the header stays reachable by scrolling inside the card, which is what the
editor's `verticalScroll` is for. Making both fit would mean a shorter card while the keyboard is
up, which is a layout change and not a placement one.

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
