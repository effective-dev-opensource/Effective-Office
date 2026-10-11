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
`AuroraWindowFrame` is the one place the Aurora window is prepared for the app, and it is applied
once, at the root. A `Dialog` is a scene of its own, but the fork's 0.0.5 carries the composition
locals into it, the substituted density among them, so the date picker lays out like the rest
without the frame being applied a second time. On Android and iOS the flags behind the frame are off
and every wrapper passes its content through.

The window arrives portrait on every live device — 1200x2000 on the Quadro T, 720x1600 on the dev
phone — while the app is a landscape kiosk. The fork turns the canvas itself, to the orientation the
compositor reports, and hands Compose a container that is landscape already: 1600x720 on the dev
phone, with the on-screen keyboard turned along with it and the system gestures where a native app
has them. What the compositor reports follows the device unless the package pins it, so
`auroraBuild` declares `orientation { default.set(listOf(Orientation.Landscape)) }`, which reaches
the `.desktop` file as `Orientation=LandscapeMask`; without it the dev phone goes portrait as soon
as it is held upright. All of this is the fork's 0.0.5 — on 0.0.4 the window stays portrait and
nothing here turns it.

One trap for the stand: the fork takes every panel for portrait. Told `Landscape` on a panel that is
landscape to begin with, which is the SDK emulator's stock tablet model, it swaps the sides and
turns the canvas all the same, and the content is laid out 1200x2000 and drawn on its side. The
emulator has to be given a portrait panel, as the devices have.

The organizer list goes into `ModalHostState.overlay`, a slot inside the card's own box, and not
into a `Popup`. The card is moved clear of the keyboard by a layer translation, and a popup on
Aurora does not follow it: placed once against the field, it stays where it was when the keyboard
leaves and the card comes back down, and hangs in the air above the field — seen on the TrustPhone
with the fork's 0.0.5. In the slot the list appears on the frame it opens and everything that moves
the card moves it identically. It anchors on the row around the field and not on the field itself,
because the row is also what it takes its width from: with the card 1259 wide, the row at x=61
spanning 1137 and the field at x=96 spanning 854, a list sized to the row but placed at the field
ran 96..1233 against the row's 61..1198 and hung off the card's
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
pass really matters. Everything below turns on one fact: consumption is what marks a gesture
cancelled, and *who has already run* decides whether we can see it.

`waitForUpOrCancellation()` treats a consumed change as cancelled, and `changedToUp()` is itself
`!isConsumed && previousPressed && !pressed` — an up that somebody has taken is not an up as far as
that function is concerned. Its default `Main` pass wakes on the **down** event the field has just
consumed in `detectTapAndPress`, so on `Main` every ordinary tap comes back cancelled. That was the
bug.

On `Initial` two things are true instead. The down is behind us — our own `awaitFirstDown` spent
that node's `Initial` dispatch, so the loop begins at the next event. And the up is still unconsumed
when we see it: `Initial` runs root to leaf, the field is our descendant and takes the up on `Main`,
which comes after. So `changedToUp()` is true for us and the tap is recognised — not because the up
branch is checked before consumption is, but because at that moment nothing has consumed it yet.

What is left to judge is the moves in between, and those separate the two gestures cleanly: the
field consumes none of them during a tap, while a scroll above us consumes them as soon as it passes
touch slop. Its consumption happens on `Main`, where the parent runs after the child, so it is not
visible to us on that pass — the `Final` re-read inside the same loop is what sees it. `Final` also
runs root to leaf, so the ordering that makes it work is not sibling order but the fact that the
whole `Final` sweep is dispatched after the whole `Main` sweep, as a second traversal
(`HitPathTracker.dispatchChanges`).

None of this survives being moved: a `pointerInput` placed under an ancestor that consumes on
`Initial` would read cancelled again, and the reasoning would have to be redone rather than assumed.

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

The fork reports no insets — `WindowInsets.statusBars`, `systemBars` and `safeDrawing` are all zero,
on 0.0.5 as before — so the room for Aurora's status bar is a constant, `statusBarInset`, a band
along the top of the content. On the dev phone the system does not draw the status bar over a
landscape app at all and the band is spare there; the tablet has not been looked at on 0.0.5, which
is why the band stays. Nothing is set aside for a display cutout: the dev phone's camera sits at the
left edge of the landscape content, and the fork does not report that either.

The order inside the frame is fixed, and each of these rules cost a round trip to the device:
- the status-bar inset goes **inside** the scale, not the other way round. `ScaledUiDensity`
  measures its own constraints, so under the padding it would normalise 1157 px instead of the
  window's 1200, the parity above would be gone and everything would come out some 3.6% larger;
- the theme goes **outside** the frame, because `AppTheme`'s own `Surface` is what paints the strip
  the inset leaves bare; with the theme inside, that strip came out white.

## The on-screen keyboard
`softKeyboardOverlapPx()` answers how many pixels of the app's own content the keyboard covers, and
the three platforms mean three different things by it. Android draws edge to edge and never resizes
its window, so the answer is the ime inset less the navigation bar the content is already padded
away from. iOS shortens the Compose scene to the area above the keyboard before any of this runs, so
the answer is zero — subtracting the height a second time collapses the card, which it did. Aurora
covers the content the way Android does, and since the fork's 0.0.5 says so through
`WindowInsets.ime` — though not in a number that can be used as it comes, and everything below is
about that.

**The inset is the surface, not the keys.** With the canvas turned landscape the inset comes back as
the whole long side of the panel: 1600 on a 960x1600 emulator, 1520 against the TrustPhone's
720x1600. That is the extent of maliit's turned surface in the panel's own coordinates, while the
key strip on a screenshot of the emulator is 520 px. `Keyboard.height()` and the `listenState` event
give the same number or none at all, so nothing the fork hands over carries the strip's thickness.
Hence the rule: an inset reaching the window's short side is a surface rather than a keyboard, and
0.43 of that side is substituted; an inset below the short side is believed as it is. The fork keeps
the inset in the scene's own density, so under `ScaledUiDensity` it is taken back through
`Window.scale()` before it is compared with anything in pixels.

Up to 0.0.4 there was no inset and the height was polled out of the maliit binding every 100 ms,
with a first call that blocked the composition's thread for 2205 ms on the TrustPhone while the fork
opened the session. Nothing on the app's side calls into that binding now.

**A press is taken as a promise of a keyboard.** Focus and the inset both arrive after the keyboard
has started to cover the field: 120 to 300 ms after the press on the TrustPhone on 0.0.5, six
seconds on the Quadro T on 0.0.4, which has not been looked at since. So
`noteSoftKeyboardExpected()` on the press publishes the estimate at once, in snapshot state, and the
card moves with the press. The promise lives ten seconds and is taken back once an inset really
appears. It lifts nothing by itself: the host moves the card for a focused field, and once editing
ends there is none. Three seconds was tried and expired mid-handshake: the modal dropped back, the
host read that as a keyboard going away and cleared the focus — the app fighting the keyboard it had
summoned itself. A short settle window keeps an inset the previous edit left behind from spending
the promise before the keyboard has moved.

The answers go to the log under the `SoftKeyboard` tag whenever the inset changes, with the raw
inset and the short side beside the number actually used, so a run off a new device says which
branch it took.

**The session is the fork's to close.** Up to 0.0.4 the fork opened a maliit session on focus and
never closed it, and the app had to. In 0.0.5 the session ends in a `finally` that closes the
keyboard, and a keyboard hidden from its own side releases the focus, so nothing on the app's side
closes a session any more — seen on the emulator and on the TrustPhone alike.

Every way out of editing is funnelled into one door, the field losing focus. The Done action does
not call `defaultKeyboardAction(Done)`, which hides the keyboard synchronously inside maliit's own
key dispatch, and it clears the focus a frame later rather than at once: both of the fork's
dispatchers run a bare `launch` inline, so the frame clock is the only scheduler that genuinely
defers. Both precautions date from 0.0.4, where tearing the session down inside that dispatch
deadlocked the process, and have not been tried without on 0.0.5. `freeFocus()` is not a way out at
all: it releases focus that was *captured*, and nothing here captures any, so the field stayed
focused with the keyboard up.

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
