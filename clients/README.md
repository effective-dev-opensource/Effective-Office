# Client Applications Documentation

## Overview

The client applications for Effective Office provide user interfaces for interacting with the office management system. The clients include web applications and tablet interfaces designed for different use cases and user roles.

## Client Types

### Tablet Application

The tablet application is a Compose Multiplatform app designed for:

- Meeting room displays
- Office space status indicators
- Check-in kiosks
- Resource availability displays

### TV Application

The TV application is a Compose Multiplatform app designed for large-screen devices, providing a seamless user experience for office management tasks. It integrates with the Effective Office ecosystem to display information and enable interactions tailored for TV screens.

## Technology Stack

### Tablet Application (Compose Multiplatform)

- **Framework**: Kotlin Multiplatform with Jetpack Compose
- **Platforms**: Android and iOS
- **UI**: Jetpack Compose
- **Navigation**: Compose Navigation (NavHost/NavController with type-safe routes)
- **Dependency Injection**: Koin
- **State Management**: Koin ViewModels + MVI pattern (StateFlow)
- **API Communication**: Ktor Client
- **Date/Time Handling**: Kotlinx.datetime
- **Logging**: Napier
- **Configuration**: BuildKonfig
- **Push Notifications**: Firebase Messaging
- **Settings Storage**: Multiplatform Settings

### TV Application (Compose Multiplatform)

- **Framework**: Kotlin Multiplatform with Jetpack Compose
- **Navigation**: Custom navigation for remote control
- **Dependency Injection**: Koin
- **State Management**: MVI pattern with StateFlow
- **API Communication**: Ktor Client
- **Build System**: Gradle with Kotlin DSL

## Features

- Resource booking and management
- Office space visualization
- Calendar integration

## Development Setup

### Tablet Application (Compose Multiplatform)

#### Prerequisites

- JDK 17 or higher
- Android Studio Meerkat or IntelliJ IDEA 2025 or newer
- Xcode 16 or newer (for iOS development)
- Git
- Gradle 8.0 or newer

#### Installation

1. Clone the repository (if not already done)
2. Open the project in Android Studio or IntelliJ IDEA
3. Sync the Gradle project to download dependencies

#### Running on Android

1. Select an Android device or emulator
2. Run the `composeApp` configuration

#### Running on iOS

1. Open the generated Xcode project:
   ```
   ./gradlew :clients:tablet:composeApp:podInstall
   open clients/tablet/composeApp/build/xcode-frameworks/ComposeApp.xcodeproj
   ```
2. Select an iOS device or simulator
3. Run the project in Xcode

### TV Application (Compose Multiplatform)

#### Prerequisites

- JDK 17 or higher
- Android Studio Meerkat or IntelliJ IDEA 2025 or newer
- Git
- Gradle 8.0 or newer

#### Installation

1. Clone the repository (if not already done)
2. Open the project in Android Studio or IntelliJ IDEA
3. Sync the Gradle project to download dependencies

#### Running on Android TV

1. Select an Android TV device or emulator
2. Run the `composeApp` configuration

## Building for Production

### Android

```
./gradlew :clients:tablet:composeApp:assembleRelease
```

### iOS

```
./gradlew :clients:tablet:composeApp:podInstall
cd clients/tablet/composeApp/build/xcode-frameworks
xcodebuild -project ComposeApp.xcodeproj -scheme ComposeApp -configuration Release
```

## Aurora OS build variant

The tablet also builds for Aurora OS. It is the same modules, not a fork of the app:
`-PbuildVariant=aurora` switches `settings.gradle.kts` over to the Aurora fork of Compose,
includes only the tablet modules, and points every one of them at its own
`build.aurora.gradle.kts` (linux targets, fork dependencies). The upstream build files are
never touched, so Android and iOS builds are unaffected.

### Setup

`local.properties` needs, on top of the usual `api.url.debug` / `api.url.release` / `apiKey`:

- `auroraMavenPath` — path (relative to the repo root) to the local maven fork holding the
  Aurora Compose plugin and libraries. It lives outside git.
- `AURORA_DEVICE_IP` — the device to deploy to over SSH. Can also be passed as
  `-PAURORA_DEVICE_IP=…`, which wins.

Packaging and deploy additionally need Docker (the Aurora build tools image) and an SSH key
at `~/.ssh/qtc_id`.

### Commands

```
# fastest loop — no Docker needed
./gradlew -PbuildVariant=aurora :clients:tablet:composeApp:compileKotlinLinuxX64

# build and sign the rpm
./gradlew -PbuildVariant=aurora :clients:tablet:composeApp:buildReleasePipeline

# build, install and launch on the device
./gradlew -PbuildVariant=aurora :clients:tablet:composeApp:runReleaseOnDevice
```

Logs come out through journald on the device: `ssh defaultuser@<ip> journalctl -f`.

### Aurora-specific pieces

Each of these exists for a reason that cost at least one round of on-device debugging.

- **An exception thrown from a composable is swallowed by the fork.** The frame is rolled
  back, the screen does not change and nothing appears in the log. It reads as "navigation
  did not work". Two separate bugs presented this way: date formatting and a `-1` room index.
- **The Aurora binary is a release binary,** so `Platform.isDebugBinary` is false. Napier is
  therefore installed directly from `Main.kt` rather than through `LoggerInitializer`, and
  note that the same flag selects `API_URL_RELEASE` — an Aurora build talks to the release
  backend unless you change that.
- **stdout is fully buffered under journald,** so the Napier stub flushes after every line.
  Without it the lines printed just before a crash are lost.
- **Date formatting is hand-rolled** (`DateTimeUtils.linux.kt`). kotlinx-datetime's
  `byUnicodePattern` rejects locale-dependent directives such as `MMMM` on Kotlin/Native.
- **Drawables go through a polyfill** in `core:ui/res`. The fork's loader renders SVG only
  and crashes on Android vector XML with "Can't wrap nullptr", so the linux actual resolves
  the bytes itself and dispatches on the byte signature, parsing vector XML with a vendored
  AOSP parser. Android and iOS delegate straight back to compose-resources.
- **Resources are packaged flat,** `<qualifier>/<file>` becomes `<qualifier>_<file>` with no
  package namespace, so any module's `Res` finds a file by name alone. Hence the per-module
  strings file names (`strings_main.xml`, `strings_settings.xml`, …) and the
  `stageAuroraResources` task that collects every tablet module's `composeResources` into one
  directory — `aurora-build` only packages its own module's.
- **Every popup and dialog is its own scene, in the untouched window.** Nothing applied at
  the root reaches them, so `ForcedLandscape` and `ScaledUiDensity` are re-applied in
  `DialogBackgroundDim` and in the organizer popup. The popup's position provider returns
  `0,0` and the list is placed by hand.
- **The window arrives portrait** on every device seen so far, so `ForcedLandscape` rotates
  everywhere; it is not a phone-only path. What does differ between devices is where the
  organizer list ends up — next to the field on the tablet, off to the side on the dev
  phone — and that is not explained yet.
- **The UI scale is fixed by the app.** The fork builds its scene as
  `ComposeScene(density = Density(window.contentScale))` and `contentScale` comes from the
  system, so `ScaledUiDensity` normalises the dp space to a baseline short side instead.

#### Choosing the UI scale baseline

Measured with the overlay (`win`/`d`/`fs`/`ui`):

| device | window px | system density | dp space |
|---|---|---|---|
| Quadro T (Aurora) | 1200x2000, rotated to 2000x1200 | 1.80 | 1111x667 |
| reference Android tablet | 1920x1200 | 1.75 | 1097x686 |
| Android emulator (tablet profile) | 2560x1600 | 2.00 | 1280x800 |

The two real devices differ by about 3%, not the 20–30% the first screenshots suggested —
those compared different builds, and the fork draws text wider than Android does (most
likely a different fallback font for Cyrillic). So the baseline is not correcting a density
mismatch; it buys room for wider text.

Three candidates, with what each costs:

- **`800.dp` — what is in the code.** Aurora lays out in 1333x800 dp, ~15% more room than the
  Android reference; the wrapping is gone, verified on the device. Price: everything is ~15%
  smaller than drawn.
- **`686.dp`** — exact parity with the reference tablet. The UI is the size it was designed
  at, but the wrapping comes back and has to be fixed in the texts and layout.
- **`~740.dp`** — the middle, ~8% smaller than the reference. Untested.

### Not covered yet

- **Keyboard input.** The fork delivers maliit input as ordinary key events and fills
  `codePoint` only for `Char` events; whether the field actually types is unverified. The key
  events are logged under the `OrganizerPicker` tag.
- **Settings live in memory** — multiplatform-settings has no linux target, so the selected
  room does not survive a restart. `ru.auroraos.kmp:ak-shared-preferences` exists in the fork
  and is the intended replacement.
- **No FCM,** so room updates do not arrive by push.
- **No kiosk mode** — the Android device-admin/lock-task path has no Aurora equivalent here.

## Architecture

### Tablet Application Architecture

The tablet application follows a modular, clean architecture approach:

#### Module Structure

```
tablet/
├── composeApp/        # Main application module that ties everything together
├── core/              # Core functionality and shared components
│   ├── data/          # Data sources, repositories, and models
│   ├── domain/        # Business logic, use cases, and domain models
│   └── ui/            # Reusable UI components and resources
└── feature/           # Feature-specific modules
    ├── bookingEditor/ # Booking creation and editing
    ├── fastbooking/   # Streamlined booking process
    ├── main/          # Main screen and navigation
    ├── settings/      # Application settings
    └── slot/          # Time slot management
```

#### Architectural Patterns

- **Clean Architecture**: Separation of concerns with data, domain, and presentation layers
- **MVI (Model-View-Intent)**: Unidirectional data flow for predictable state management
- **Feature Modularization**: Independent feature modules for better maintainability and scalability
- **Dependency Injection**: Koin for service locator pattern implementation
- **Reactive Programming**: Flow and StateFlow for reactive state management

### TV Application Architecture

The TV application follows a modular architecture with the following structure:

```
tv/
├── composeApp/        # Main application module that integrates all features
├── core/              # Core functionality and shared components
│   ├── data/          # Data sources, repositories, and models
│   ├── domain/        # Business logic, use cases, and domain models
│   └── ui/            # Reusable UI components and resources
└── feature/           # Feature-specific modules
    ├── events/        # Event browsing and QR code registration
    ├── menu/          # Central navigation hub
    ├── photos/        # Photo carousel with autoplay
    └── stories/       # Story highlights and playback
```

## Responsive Design

The applications are designed to work on:

- Tablets
- TVs

## Kiosk Mode Management

The tablet application includes comprehensive kiosk mode functionality for remote device management:

### Features

- **Remote Control**: Enable/disable kiosk mode via Firebase push notifications
- **Device Targeting**: Support for single device or bulk device operations
- **Android Device Owner**: Full kiosk mode control using Android Device Owner privileges

### Setup Requirements

To enable kiosk mode functionality, the tablet must be configured as a Device Owner:

#### Basic Setup Steps

1. **Enable Developer Options** on the Android device
2. **Enable USB Debugging**
3. **Connect device via ADB** and run:
   ```bash
   adb shell dpm set-device-owner band.effective.office.tablet/.AdminReceiver
   ```
4. **Verify Device Owner status**:
   ```bash
   adb shell dpm list-owners
   ```

#### Troubleshooting Device Owner Setup

If the basic setup fails:

1. **Check device compatibility**: Ensure your device supports Device Owner mode
2. **Try factory reset**: Reset the device and try setup immediately after first boot
3. **Remove accounts**: Ensure no Google or other accounts are configured
4. **Check manufacturer documentation**: Some devices have specific setup procedures
5. **Consider enterprise solutions**: For production deployments, consider using Android Enterprise or manufacturer-specific kiosk solutions

## SMS Router Module

### Overview

The SMS Router module is an Android client designed to intercept SMS messages and forward them to configured webhooks. It supports per-SIM settings, delivery logging.

### Technology Stack

- **Framework**: Android with Kotlin
- **UI**: Jetpack Compose (for settings and logs screens)
- **Dependency Injection**: Koin
- **Networking**: Ktor Client
- **Database**: Room (for delivery logs)
- **State Management**: ViewModel and LiveData

### Features

- SMS interception and forwarding to webhooks.
- Per-SIM configuration for webhook URLs and secrets.
- Delivery logging with retry policies.
- Support for Mattermost and Telegram payloads.

### Development Setup

#### Prerequisites

- JDK 17 or higher
- Android Studio Meerkat or newer
- Gradle 8.0 or newer

#### Running the Application

1. Deploy the app to an Android device or emulator.
2. Grant necessary permissions (e.g., RECEIVE_SMS, INTERNET).
3. Configure SIM-specific settings in the app UI.

#### Module Structure

```
smsrouter/
├── app/               # Main application module
│   ├── data/          # Data sources, repositories, and models
│   ├── domain/        # Business logic, use cases, and domain models
│   └── presentation/  # UI components and SMS receiver
```

#### Architectural Patterns

- **Clean Architecture**: Separation of concerns with data, domain, and presentation layers.
- **Dependency Injection**: Koin for managing dependencies.
- **Reactive Programming**: LiveData for reactive state management.
