# UI Module

## Overview
The UI module is part of the core layer of the Effective Office tablet application. It provides shared UI components, themes, styles, and utilities that are used across different features of the application. This module ensures a consistent look and feel throughout the app and promotes reusability of UI elements.

## Features
- Shared Compose UI components
- Theme definition and styling
- UI utilities
- Date and time components
- Booking-related components
- Common UI elements

## Architecture
The module is organized in the package `band.effective.office.tablet.core.ui` with the following structure:

```
band.effective.office.tablet.core.ui/
├── booking/          # Booking-related components
├── button/           # Button components
├── common/           # Common UI components
├── date/             # Date and time components
├── theme/            # Theme definitions
├── utils/            # UI utility functions
```

## Integration
The UI module is used by:
- Feature modules for building feature-specific screens
- ComposeApp module for app-wide theming

## Usage Examples

### Using a Component
```kotlin
@Composable
fun MyScreen() {
    SuccessButton(
        text = "Submit",
        onClick = { /* handle click */ }
    )
}
```

### Applying Theme
```kotlin
@Composable
fun MyApp() {
    EffectiveOfficeTheme {
        // App content
    }
}
```

## Development
### Adding a New Component
To add a new reusable component:
1. Create a new file in the appropriate component package
2. Implement the component using Compose
3. Add preview functions for different states
4. Document the component's parameters and usage

### Design Guidelines
- Follow Material Design principles
- Ensure components are accessible
- Support both light and dark themes
- Make components responsive to different screen sizes
