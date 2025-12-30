# Stories Feature Module

## Overview

The Stories feature module is designed to showcase congratulatory messages and highlight the most active individuals in the community. It provides a visually engaging experience with a carousel of stories and fullscreen playback, optimized for TV screens and remote control navigation.

## Features

- **Story Carousel and Grid**: Navigate through stories with a carousel optimized for TV screens.
- **Autoplay Integration**: Coordinates with the app-level `AutoplayManager` for seamless transitions.
- **Preload Strategies**: Uses low-resolution thumbnails and metadata preloading for smooth browsing.
- **Localized Content**: Supports multiple languages with localized strings and flag resources.
- **Error Recovery**: Handles playback and network errors gracefully with retry options.

## Architecture

```
stories/
├── di/               # Dependency Injection bindings (providers, services)
├── data/             # Data layer: providers, DTOs, mappers
├── domain/           # Domain layer: models, use cases, repository interfaces
└── presentation/     # Presentation layer: UI components, screens, state management
    ├── autoplay/     # Autoplay hooks and provider implementations
    ├── components/   # Previews, player controls, banners
    ├── rating/       # Rating-specific UIs (Duolingo, Sport, Supernova)
    ├── story/        # Story player and scaffold
    ├── StoriesComponent.kt
    ├── StoriesScreen.kt
    ├── StoriesIntent.kt
    └── StoriesState.kt
```

## Key Components

- **StoriesComponent**: Coordinates loading, autoplay, and navigation.
- **StoriesScreen**: Displays the stories and player entry points.
- **StoryPlayer & PlaybackController**: Manages fullscreen playback with play/pause, next/prev, and seek controls.
- **Autoplay Hooks**: Supports autoplay-driven flows via the `AutoplayManager`.
- **UI Components**: Includes previews, banners, and player controls for consistent design.

## Integration

- **Core Modules**: Utilizes `core/ui` primitives for consistent styling and behavior.
- **Navigation**: Registers routes for story browsing and playback.
- **Data Layer**: Connects to data providers and mappers to fetch and transform story data.

## Development Guidelines

### Platform-Specific Considerations

- Optimize layouts for TV screens with clear focus order and large typography.
- Ensure all UI components are accessible and navigable using a remote control.

## Resources

- Localized strings: `src/commonMain/composeResources/values/strings.xml` (e.g., titles, captions, error messages).
- Feature-specific images: Add to `composeResources/drawable/` and access via `Res.drawable.xxx`.
- Follow `core/ui` guidelines for shared icons and fonts.

## Notes

- Keep preload short and conservative — prefer preloading the next story's metadata and a low-res frame only.
- Ensure graceful fallback when an autoplay or playback error occurs (show retry and fallbacks).
- Use caching to enhance user experience.
