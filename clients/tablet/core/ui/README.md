# UI Module

## Overview
The UI module is part of the core layer of the Effective Office tablet application. It holds the
theme, the shared composables the feature modules build their screens out of, and the inactivity
tracking that returns the tablet to its resting state.

## Architecture
The module is organized in the package `band.effective.office.tablet.core.ui`:

```
band.effective.office.tablet.core.ui/
├── booking/          # Booking alert
├── button/           # SuccessButton
├── common/           # Shared views: buttons, loader, error and disconnect states
├── date/             # Date and time strip with arrows
├── inactivity/       # Inactivity countdown and the tracker that feeds it
├── theme/            # Color scheme, custom palette, typography
├── utils/            # Date formatting and display mapping
└── LoadMainScreen.kt # Full-screen loading state
```

Strings and drawables live in `composeResources`, with a Russian translation in `values-ru`.

## Key Components
- **AppTheme**: applies the Material color scheme and typography, and provides
  `LocalCustomColorsPalette` — the colors Material 3 has no slot for, such as busy and free room
  status.
- **InactivityTracking**: the countdown itself, held once for the whole app and started at the
  composition root. Its `timeouts` flow is what resets the selected date and closes an open modal.
- **InactivityTracker**: the composable that feeds the countdown with any input inside it. One
  instance per scene layer — a dialog is a window of its own and never reaches the instance
  installed at the root.
- **DateTimeView**, **EventDurationView**, **EventOrganizerView**: the field rows the booking editor
  is assembled from.
- **Loader**, **LoadMainScreen**, **ErrorMainScreen**, **Disconnect**: the loading, error and
  offline states shared across screens.

## Integration
The UI module is used by:
- the feature modules, for shared views, the palette and the typography;
- the ComposeApp module, for `AppTheme` and the inactivity tracking installed at the root.

## Usage Examples

### Applying the theme
```kotlin
@Composable
fun Root() {
    AppTheme {
        // App content
    }
}
```

### Using a shared button
```kotlin
SuccessButton(onClick = { /* handle click */ }) {
    Text(text = stringResource(Res.string.apply_date_time_for_booking))
}
```

## Fork defects
Corrections to the Aurora fork's own bugs. Each is expected to die when the fork is fixed, and each
is marked `// Fork defect:` at its site.

### The fork's resource loader renders SVG only
`vectorResource` from `compose-resources` hands the drawable bytes straight to Skia's `SVGDOM`. Every
icon this project ships is Android vector XML, so the call dies with `Can't wrap nullptr` and takes
the process with it — the first icon on the first screen is enough. `painterResource` returns
nothing instead of dying, which is worse to diagnose.

`res/PainterResource.linux.kt` resolves the bytes itself and picks the decoder by their **signature,
not their extension**: raster magic bytes go to `decodeToImageBitmap`, an `<svg` tag to `SVGDOM`,
anything else to the vendored AOSP parser in `res/vectorxml/` (`javax.xml` has no Kotlin/Native
target, hence the hand-rolled DOM beside it). The vector-XML path yields an `ImageVector`, so
`Icon(tint = …)`, the intrinsic dp size and the dark theme keep working; an SVG has its colors baked
in and gets none of that.

## Development
### Adding a New Shared View
1. Put it in the package that matches its kind, or in `common/` if it fits nowhere else
2. Take `Modifier` as the first parameter, defaulting to `Modifier`
3. Take colors from `MaterialTheme` or `LocalCustomColorsPalette`, never as literals
4. Put any user-visible text in `composeResources`, in both `values` and `values-ru`
