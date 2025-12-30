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
- **Navigation**: Decompose
- **Dependency Injection**: Koin
- **State Management**: Decompose with MVI pattern
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
