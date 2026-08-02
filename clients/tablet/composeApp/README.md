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