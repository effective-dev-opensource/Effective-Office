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

## Responsive Design
The applications are designed to work on:
- Tablets
