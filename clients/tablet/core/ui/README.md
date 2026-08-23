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

## Aurora window model
`AuroraWindowFrame` is the one place the Aurora window is prepared for the app, and every scene
layer applies it: the fork draws `Popup` and `Dialog` as separate scenes in the untouched window, so
nothing installed at the root reaches them. `DialogSceneFrame` is that re-application for a dialog.
On Android and iOS the flags behind the frame are off and every wrapper passes its content through.

The window arrives portrait on every live device — 1200x2000 on the Quadro T, 720x1600 on the dev
phone — while the app is a landscape kiosk, so `ForcedLandscape` rotates the content by 90°. That is
a drawing effect and the window's own geometry never changes, which has two consequences. System
gestures stay in the portrait window: on the TrustPhone the close gesture fires from the physical
side edge and the swipe from the bottom does nothing, and this cannot be fixed from inside the app —
the fork would have to report the content orientation to the compositor. And `positionInWindow()`
goes through the rotation, so the window-Y of a node inside the rotated content is its content-X;
anything positional has to be measured between two nodes on the same side of the rotation, where it
cancels out.

The organizer list is what that rule cost. A `Popup` on Aurora is a scene of its own: it comes up
after a visible pause and shares no ancestor with the field, so it cannot be aimed — carrying the
field's window coordinates across carries its content-X as a Y. The list goes into
`ModalHostState.overlay` instead, a slot inside the card's own box, so it appears on the frame it
opens and everything that moves the card moves it identically. It anchors on the row around the
field and not on the field itself, because the row is also what it takes its width from: with the
card 1259 wide, the row at x=61 spanning 1137 and the field at x=96 spanning 854, a list sized to
the row but placed at the field ran 96..1233 against the row's 61..1198 and hung off the card's
right edge — the row's own 20.dp horizontal padding, in full. The numbers go to the log under the
`OrganizerList` tag: `anchor 61,612 in card 1259 x 947; row 1137 x 125; list 1137 x 262` on the
tablet, `anchor 37,369 in card 756 x 570; row 682 x 75; list 682 x 157` on a small screen. Sizes are
printed with spaces around the separator on purpose — the deploy plugin scans the app's output for a
native backtrace, takes a bare `0x0` for an address and ends the run over a line that is only saying
the list has not been measured yet.

The list also opens on the raw press rather than on the focus callback. Aurora grants focus at the
end of the maliit handshake, so waiting for it gave press, pause, keyboard, pause, list. Android and
iOS grant focus on the same gesture, so there the two moments were never apart. A `TextField` asks
for focus on the up, though, so the press alone is not yet an edit: a gesture that ends up scrolling
the editor's column has to toggle the list back, or a drag started on the field leaves it open.

Which gesture that is has to be read on the **Initial** pass, and this is the one place where the
pass really matters. `waitForUpOrCancellation()` treats a consumed change as a cancelled gesture,
and its default `Main` pass wakes on the down event the field has already consumed — the field takes
the down in `detectTapAndPress` — so on `Main` every ordinary tap comes back cancelled. On `Initial`
the down is behind us, an up short-circuits before consumption is looked at, and the only changes
left to be judged are the moves in between: the field consumes none of those during a tap, and a
scroll above us consumes them as soon as it passes touch slop. That is the whole difference between
the two gestures, and it is why the answer is read a pass earlier rather than by a slop measurement
of our own. The scroll's own consumption happens on `Main`, after us, which is why the check that
sees it is the `Final`-pass re-read inside the same loop.

The list is gated on `expanded` and not on the focus. Long-pressing into text selection opens the
list and then cancels the gesture, which closes it again — with the focus granted. Gated on focus,
the field would sit focused with the list shut and no further tap could reopen it.

The scene density cannot be set on Aurora — the fork builds the scene with the `contentScale` the
system handed over — so the app fixes its own dp space instead: `ScaledUiDensity` substitutes
`short_side_px / uiScaleBaseline` and pins `fontScale` to 1 so the system font scale cannot multiply
on top of it. The baseline of 686 dp is parity with the reference Android tablet rather than a
familiar screen size: both live devices are 1200 px on the short side — the Quadro T's window is
1200x2000, the reference tablet is 1920x1200 — so 1200/686 = 1.7493 against that tablet's own 1.75.
The two lay out the same, not merely close. The answer goes to the log on every change under the
`UiScale` tag, e.g. `content 2000x1200, density 1.7492712`.

The order inside the frame is fixed, and each of these rules cost a round trip to the device:
- the status-bar inset goes **inside** the rotation, or the padding lands in the window's portrait
  coordinate space and draws as a stripe down the edge instead of a band along the top;
- the inset goes **inside** the scale, not the other way round. `ScaledUiDensity` measures its own
  constraints, so under the padding it would normalise 1157 px instead of the window's 1200, the
  parity above would be gone and everything would come out some 3.6% larger;
- the theme goes **outside** the frame, because `AppTheme`'s own `Surface` is what paints the strip
  the inset leaves bare; with the theme inside, that strip came out white.

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
