# Photos Feature Module

## Overview

The Photos feature module is designed to display a carousel of photos fetched from the backend. Users can browse through the photos seamlessly using a remote control, with layouts optimized for TV screens.

## Features

- **Carousel Browsing**: The module provides a carousel layout for browsing photos, optimized for TV screens and remote control navigation.
- **Lightweight Caching**: Implements per-page caching to store thumbnails locally, ensuring fast browsing.
- **Prefetching**: Automatically preloads the next set of thumbnails to reduce navigation delays.
- **Error Recovery**: Handles network failures gracefully by displaying placeholders and retry options.

## Architecture

```
photos/
├── di/               # Dependency Injection bindings for the feature
├── data/             # Photo sources, caching, DTOs, mappers, repositories
├── domain/           # Use cases and domain models
└── presentation/
    ├── autoplay/     # Autoplay hooks (preload and provider)
    ├── components/   # Thumbnails, viewer controls
    ├── PhotosComponent.kt
    ├── PhotosScreen.kt
    ├── PhotosIntent.kt
    └── PhotosState.kt
```

## Key Components

- **PhotosComponent**: Manages the slideshow, including loading photos, handling user intents, and managing playback.
- **PhotosScreen**: The main UI composable for displaying the photo carousel.
- **PhotosRepository**: Provides photo data, fetching it from remote sources.
- **PhotosState**: Tracks the current state of the slideshow, including the list of photos, current index, and playback status.
- **Autoplay Hooks**: Preload and provider logic for autoplay functionality.
- **UI Components**: Includes thumbnail cards, viewer controls, and placeholders for a consistent user experience.

## Integration

The Photos feature module integrates seamlessly with the core modules and other feature modules:

- **Core Modules**: Utilizes `core/ui` primitives for consistent styling and behavior.
- **Navigation**: Registers routes for photo browsing and fullscreen viewing.
- **Data Layer**: Connects to the `PhotosRepository` to fetch and cache photo data.

## Development Guidelines

### Platform-Specific Considerations

- Optimize preloads and caching to avoid stutter on TV hardware.
- Ensure all UI components are focusable and navigable using a remote control.

## Resources

- Localized strings: `src/commonMain/composeResources/values/strings.xml` (titles, empty placeholders, content descriptions).

## Notes

- Reuse `core/ui` primitives for a consistent look and feel across the application.
- Keep preloads small and cancelable to ensure smooth performance on TV hardware.

## How It Works

The Photos feature module is built around a `PhotosComponent` that manages the photo slideshow experience. Here's a detailed breakdown of its functionality:

1. **Data Flow**:

   - The `PhotosComponent` fetches photo data from a `PhotosRepository`, which provides the list of photos to display.
   - The data is processed and stored in a `PhotosState`, which tracks the current photo, loading status, and playback state.

2. **Slideshow Management**:

   - The slideshow automatically starts by loading photos when the component is initialized.
   - Users can navigate through photos using intents like `Next`, `Previous`.
   - Auto-advance is enabled when playback is active, transitioning to the next photo after a configurable delay (default: 15 seconds).

3. **Error Handling**:

   - If a photo fails to load, it can be removed from the list using the `RemoveFailedPhoto` intent.
   - Errors during photo loading are logged and displayed to the user, with retry options available.

4. **Performance Optimization**:

   - The component uses lightweight placeholders while loading photos to ensure a responsive UI.
   - Auto-advance jobs are cancelable to prevent unnecessary resource usage.

5. **State Management**:

   - The `PhotosState` tracks the list of photos, the current index, and playback status.
   - State updates trigger UI changes, ensuring a reactive and seamless user experience.

6. **Navigation**:
   - Users can jump to the first or last photo, or navigate sequentially.
   - The component gracefully handles reaching the end of the slideshow by stopping playback and notifying the parent component.
