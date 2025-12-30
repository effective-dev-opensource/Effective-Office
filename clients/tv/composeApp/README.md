# ComposeApp Module (TV)

## Overview

The ComposeApp module is the main entry point for the Effective Office TV application. It integrates all features and core components into a cohesive Compose Multiplatform application configured for TV devices. This module ensures seamless navigation, autoplay functionality, and the ability to add new features efficiently.

## Features

- **Navigation**: Seamless routing between screens with remote-friendly flows.
- **Autoplay Management**: Automatically handles media playback based on focus and priority.
- **Dependency Injection**: Centralized DI setup for managing app-wide dependencies.
- **Theme and Styling**: Optimized for large screens with TV-specific typography and layouts.
- **Modular Design**: Easily extendable architecture for adding new features.
- **TV-Specific Adaptations**: Focusable UI components and support for D-pad navigation.
- **Error Handling**: Graceful recovery from navigation or playback errors.

## Architecture

The ComposeApp module is structured to support a modular and scalable architecture, optimized for TV-specific adaptations. Below is a detailed breakdown of its components:

```
composeApp/
├── src/                # Source code
│   ├── androidMain/    # Android-specific implementations
│   ├── commonMain/     # Shared cross-platform code for Compose Multiplatform
│   └── desktopMain/    # Desktop-specific implementations (if applicable)
├── build.gradle.kts    # Build configuration for the module
└── resources/          # Shared resources (e.g., strings, drawables, layouts)
```

### Design Principles

- **Modularity**: The architecture is designed to allow easy addition of new features without impacting existing functionality.
- **Platform-Specific Adaptations**: Each platform directory contains code tailored to its respective environment, ensuring optimal performance and user experience.
- **Shared Logic**: Common functionality is centralized in `commonMain` to maximize code reuse and maintain consistency across platforms.

### Integration with Features

The ComposeApp module integrates with the following feature modules:

- **Events**: Provides event browsing and QR code registration.
- **Menu**: Acts as the central navigation hub.
- **Photos**: Displays a photo carousel with autoplay functionality.
- **Stories**: Showcases congratulatory messages and highlights.

Each feature module exposes entry composables and registers its resources for seamless integration into the ComposeApp module.

## Key Components

- **App**: Main application composable that sets up navigation, theme, and focus handling
- **Navigation**: Handles routing between screens with remote-friendly flows, ensuring smooth transitions and intuitive user experience
- **AutoplayManager**: Coordinates autoplay for media content based on focus and priority, ensuring a seamless viewing experience
- **DI**: Dependency injection setup for the application
- **Theme**: Application-wide styling and large-screen typography

## Platform-Specific Considerations

- Implement focusable composables and clear navigation order
- Support remote control events and D-pad navigation
- Test on large resolutions and different TV aspect ratios

## Integration (TV-specific)

The ComposeApp module is the TV app entry point and integrates only with TV-targeted implementations and feature providers. Integration expectations for TV:

- **Core modules** (`data`, `domain`, `ui`) — prefer `tvMain` platform implementations and TV-optimized primitives from `core/ui`.
- **Feature modules** (`events`, `menu`, `photos`, `stories`) — only these four TV features are included in this ComposeApp; features must expose TV entry composables, declare TV-specific resources, and register TV providers (e.g., `AutoplayProvider`) when applicable.

### Adding New Features

To add a new feature to the ComposeApp module:

1. **Create the Feature Module**: Develop the feature module with TV-specific implementations and resources.
2. **Expose Entry Composables**: Ensure the feature provides entry composables for integration.
3. **Register Providers**: If the feature involves autoplay or focus management, register the appropriate providers (e.g., `AutoplayProvider`, `FocusManager`).
4. **Integrate with Navigation**: Add the feature to the navigation graph, ensuring smooth transitions and remote-friendly routing.
5. **Test on TV Devices**: Validate the feature on various TV resolutions and aspect ratios to ensure compatibility.

### TV-specific Integration Points

- **AutoplayManager**: Register feature `AutoplayProvider`s at app bootstrap so the app can coordinate autoplay based on focus and priority.
- **Focus & Remote Input**: Integrate with a `FocusManager` and `RemoteInputHandler`; ensure features provide clear focus order and visual focus indicators.
- **Resources & Layouts**: Use TV-friendly layout patterns, large-type typography, and resource qualifiers for different TV resolutions and aspect ratios.
