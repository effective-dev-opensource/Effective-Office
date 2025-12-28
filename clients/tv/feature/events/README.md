# Events Feature Module

## Overview

The Events feature module provides a TV-optimized interface for displaying upcoming events and enabling user registration via QR codes.

## Features

- **Event Browsing**: Display a list of upcoming events with intuitive navigation.
- **Event Details**: Show detailed information about each event, including time, location, and description.
- **QR Code Registration**: Generate and display QR codes for easy event registration.
- **Offline Support**: Cache event data for fast loading.
- **Error Recovery**: Handle network failures gracefully with cached data and retry options.

## Architecture

```
events/
├── di/               # Dependency Injection bindings for the feature
├── data/             # Data layer: API clients, DTOs, mappers, repositories
├── domain/           # Domain layer: models, use cases, repository interfaces
└── presentation/     # Presentation layer: UI components, screens, state management
    ├── autoplay/     # Autoplay hooks for event flows
    ├── components/   # Reusable UI components
    ├── EventsComponent.kt
    ├── EventsScreen.kt
    ├── EventsIntent.kt
    └── EventsState.kt
```

## Key Components

- **EventsComponent**: Coordinates data, navigation, and QR code generation.
- **EventsScreen**: Displays event lists, details, and QR codes for registration.
- **EventsRepository**: Fetches event data from remote sources.
- **EventsState**: Tracks the current state of the event list and user interactions.
- **Autoplay Hooks**: Supports autoplay-driven event flows.
- **UI Components**: Includes cards, list items, and headers for consistent design.

## Integration

- **Core Modules**: Utilizes `core/ui` primitives for consistent styling and behavior.
- **Navigation**: Registers routes for event browsing and detailed views.
- **Data Layer**: Connects to the `EventsRepository` to fetch and cache event data.

## Development Guidelines

### Platform-Specific Considerations

- Optimize layouts for TV screens with clear focus order and large typography.
- Ensure all UI components are accessible and navigable using a remote control.

## Resources

- Localized strings: `src/commonMain/composeResources/values/strings.xml` (e.g., titles, captions, error messages).
- Feature-specific images: Add to `composeResources/drawable/` and access via `Res.drawable.xxx`.
- Follow `core/ui` guidelines for shared icons and fonts.

## Notes

- Prioritize clear focus order and D-pad navigation.
- Ensure accessibility labels and large typography for TV viewing.
- Use caching and offline support to enhance user experience.
