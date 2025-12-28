# UI Module (TV)

## Overview

The UI module contains shared UI primitives, reusable composables, and TV-focused UI infrastructure used across the TV client. It centralizes theme tokens, focus utilities, layout primitives, and common screens (placeholders, empty/error states). This module ensures a consistent and visually appealing user interface tailored for TV devices.

## Key Responsibilities

- **Theme Tokens**: Centralized definitions for colors, typography, spacings, and shapes optimized for TVs.
- **Focus Management**: Focusable primitives, focus order helpers, and visual focus states for remote navigation.
- **Reusable Components**: Common screens and UI elements such as placeholders, skeletons, empty states, error views, toasts, and dialogs.
- **Image Handling**: Efficient image loading, caching, and graceful placeholders for media-heavy interfaces.

## Architecture

```
ui/
├── composeResources/   # Drawables, fonts, values (strings) used by Compose resources
├── kotlin/
│   └── band/effective/office/tv/core/ui/
│       ├── autoplay/   # Autoplay-related helpers and providers
│       ├── components/ # Reusable composables and UI building blocks
│       ├── di/         # DI bindings for UI (theme, managers)
│       ├── image/      # Image loader and helpers
│       ├── model/      # Small UI models and view data classes
│       ├── screen/     # Common screens and scaffolds (placeholder, error, autoplay screens)
│       └── theme/      # Colors, typography, shapes, and theme setup
```

## Key Components & Patterns

### Theme & Tokens

- **Colors**: High-contrast color pairs for readability on large screens.
- **Typography**: Large, legible fonts optimized for TV viewing distances.
- **Spacing & Shapes**: Consistent spacing and rounded corners for a polished look.
- **Dark/Light Mode**: Support for both themes with seamless transitions.

### Focus & Remote Navigation

- **Focusable Wrappers**: Simplify focus handling with reusable wrappers.
- **FocusManager**: Coordinate focus order and manage remote navigation.
- **Visual Indicators**: Provide clear focus states with outlines, scaling, and shadows.

### Reusable Components

- **Placeholders**: `LoadingPlaceholder` for loading states.
- **Empty States**: `EmptyState` composables for no-content scenarios.
- **Error Views**: `ErrorState` with retry actions for error handling.
- **Dialogs & Toasts**: Standardized components for transient messages.

### Images & Media

- **ImageLoader**: Shared helper for efficient image loading and caching.
- **Graceful Placeholders**: Preload low-res thumbnails and fetch hi-res assets on demand.
- **Background Decoding**: Avoid UI thread blocking with background image processing.

## Design & Development Guidelines

### Theme & Usage

- Apply the app TV theme wrapper (e.g., `EffectiveOfficeTheme`) at the app root so all features inherit tokens.
- Use theme tokens for spacing, typography, and colors to ensure consistency.
- Support light/dark themes and adapt to different TV resolutions.

### Design Guidelines

- **Typography**: Use large, legible fonts for readability from a distance.
- **Colors**: Ensure high contrast for visibility on TV screens.
- **Focus States**: Make focus and selection states visually distinct and stable.
- **Responsiveness**: Adapt components to various TV resolutions and aspect ratios.

## Notes

- Reuse `core/ui` components rather than creating new duplicates.
- Use the theme tokens for spacing and typography; avoid hardcoding sizes.
- Test components on multiple TV devices to ensure compatibility and performance.
