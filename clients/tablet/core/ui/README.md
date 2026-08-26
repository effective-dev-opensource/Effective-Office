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

The order inside the frame is fixed, and each of these rules cost a round trip to the device:
- the status-bar inset goes **inside** the rotation, or the padding lands in the window's portrait
  coordinate space and draws as a stripe down the edge instead of a band along the top;
- the inset goes **inside** the scale, not the other way round. `ScaledUiDensity` measures its own
  constraints, so under the padding it would normalise 1157 px instead of the window's 1200, the
  parity above would be gone and everything would come out some 3.6% larger;
- the theme goes **outside** the frame, because `AppTheme`'s own `Surface` is what paints the strip
  the inset leaves bare; with the theme inside, that strip came out white.

## The on-screen keyboard
`softKeyboardOverlapPx()` answers how many pixels of the app's own content the keyboard covers, and
the three platforms mean three different things by it. Android draws edge to edge and never resizes
its window, so the answer is the ime inset less the navigation bar the content is already padded
away from. iOS shortens the Compose scene to the area above the keyboard before any of this runs, so
the answer is zero — subtracting the height a second time collapses the card, which it did. Aurora
covers the content the way Android does and tells nobody, so the height comes out of the maliit
session, and everything below is about making that number usable.

**It has to be asked for, not listened to.** `Keyboard.listenState` fires when the keyboard opens
but carries `height = 0`; maliit sends the size in a follow-up event that never reaches the app.
Worse, a second `listenState` subscription beside the fork's own kills maliit outright — after the
first session closes the keyboard does not come back without a restart. So `Keyboard.height()` is
polled every 100 ms while the modal that reads it is on screen. This looks like a stopgap and is
not one: **do not turn it back into a subscription.** Polling also comes out ahead on its own
merits — there is nothing to lose on `onWindowPause()`, the answer grows with the keyboard as it
slides in, and a keyboard swiped away behind the app's back shows up as a zero on the next tick.

**The number cannot be taken at face value.** The dev phone answers 535 against a 720x1600 screen —
keyboard-shaped. The Quadro T answers 2000 against 1200x2000, the screen's whole long side: maliit's
surface spans the screen, and the key strip's own thickness is nowhere in the binding, down to the
libac struct `{ height, is_open }`. The SDK emulator answers an honest 520 out of 1200; the
TrustPhone T1 answers nothing at all, `reported=0`. Hence the rule: anything reaching the screen's
short side is a surface rather than a keyboard, and 0.43 of that side is substituted. The fraction
is measured twice, not guessed — a 520 px key strip across the Quadro T's 1200 px short side in a
screenshot, and the emulator's own 520 out of the same 1200.

`Keyboard.isOpen()` is never a gate on the poll, only a line in the log: it has read `false` with
the keyboard up, and on the tablet it kept the poll silent for a whole session.

**A press is taken as a promise of a keyboard.** The fork starts the maliit session synchronously
and grants focus at the end of it — six seconds on the Quadro T, with focus given and taken away in
between. Focus, `isOpen()` and the state event all become true after the keyboard has already
covered the field, so `noteSoftKeyboardExpected()` on the press is the only signal that comes
first. The promise lives ten seconds and is dropped the moment a keyboard really appears or editing
ends. Three seconds was tried and expired mid-handshake: the modal dropped back, the host read that
as a keyboard going away, cleared the focus and closed the session — the app fighting the keyboard
it had summoned itself. A short settle window keeps a stale `isOpen() = true` from the previous
session from spending the promise before the keyboard has moved.

**The promise has to publish its own estimate, and this is what makes it work at all.** Reading it
inside the poll is not enough, because the poll asks maliit first, and the first call into that
binding blocks until the fork has finished opening the session — 2205 ms measured on a TrustPhone
T1, on the composition's own thread. Read there, the advance queues behind the exact wait it exists
to cover: the card moves only after the keyboard is already up. So the press writes an estimate into
snapshot state itself, and the poll only refines it once maliit answers; the larger of the two wins,
which hands the decision to the real height as soon as there is one. The short side is cached from
the last poll so the press never has to ask the window for it.

The symptom, if this is ever undone: only the first press of a session is slow, the rest are
instant, and it comes back after the session is closed — which reads as "sometimes it works,
sometimes it doesn't". The emulator cannot show it, opening a session there takes 59 ms. **Check
this one on real hardware.**

The poll's answers go to the log under the `SoftKeyboard` tag on change only, with the raw height
and the screen beside the number actually used, so a run off a new device says which branch it took.
Sizes are printed with spaces around the separator for the reason the `OrganizerList` line gives.

## Fork defects
Corrections to the Aurora fork's own bugs. Each is expected to die when the fork is fixed, and each
is marked `// Fork defect:` at its site.

### Fling direction
A drag scrolls a list the right way and the release throws the inertia back the other way. A slow
drag is fine throughout, and a slow drag is the one gesture that ends with no velocity and never
flings at all, which puts the fault in the velocity rather than in the drag.

`listFlingBehavior()` wraps the platform default and flips the sign of the velocity going in and of
the remainder coming out; Android and iOS hand the default back untouched.
`snapListFlingBehavior(state)` is the same correction around a snapping fling, for a `LazyColumn`
whose resting item is the selected value. It has no callers since the Aurora time picker went back
to the Material3 clock, and is kept so the next hand-rolled scroller need not put the seam back.

The linux actual logs `fling v=… flipped, unconsumed=…` on every fling — the only place the number
is visible. If a future fork build starts handing back the sign the drag had, both shapes have to
go, together.

### The maliit session is never closed
The fork opens a maliit session when a field takes focus and then parks in `awaitCancellation()`
with no `finally`, so nothing ever stops it: the field is done being edited and maliit still
believes it is feeding one. A leftover session is the likeliest reason input goes to the app no
more, which on the tablet reads as a freeze. `closeSoftKeyboard()` does what the missing `finally`
would have done.

Where it is closed matters as much as that it is. `Keyboard.close()` called from inside maliit's own
key dispatch deadlocks the process: `send_state` waits on the channel the `send_input` still on the
stack is holding. Deferring with a `launch` does not help either — both of the fork's dispatchers
run tasks inline on the main thread. So the request goes into a conflated channel and
`AuroraKeyboardSessionCloser`, mounted once at the root, closes the session after a
`withFrameNanos {}`: on this fork the frame clock is the only scheduler that genuinely defers.

Every way out of editing is funnelled into one door, the field losing focus, and the field closes
the session from there. That is why the Done action does not call `defaultKeyboardAction(Done)` —
it hides the keyboard synchronously, inside that same dispatch — and why it clears the focus a frame
later rather than at once. `freeFocus()` is not a way out at all: it releases focus that was
*captured*, and nothing here captures any, so the field stayed focused with the keyboard up.

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
