# Tablet on Aurora OS

The tablet application also builds for Aurora OS. It is the same modules, not a fork of the
app: `-PbuildVariant=aurora` switches `settings.gradle.kts` over to the Aurora fork of Compose,
includes only the tablet modules, and points each of them at its own `build.aurora.gradle.kts`.
The upstream build files are never touched, so Android and iOS builds are unaffected.

## Setup

On top of the usual `api.url.debug` / `api.url.release` / `apiKey`, `local.properties` needs:

- `auroraMavenPath` — path (relative to the repo root) to the local maven fork that holds the
  Aurora Compose plugin and libraries. The fork lives outside git.
- `AURORA_DEVICE_IP` — the device to deploy to over SSH. Can also be passed as
  `-PAURORA_DEVICE_IP=…`, which wins.

Packaging and deploy additionally need Docker (the Aurora build tools image) and an SSH key at
`~/.ssh/qtc_id`.

## Commands

```
# fastest loop — no Docker needed
./gradlew -PbuildVariant=aurora :clients:tablet:composeApp:compileKotlinLinuxX64

# build and sign the rpm
./gradlew -PbuildVariant=aurora :clients:tablet:composeApp:buildReleasePipeline

# build, install and launch on the device
./gradlew -PbuildVariant=aurora :clients:tablet:composeApp:runReleaseOnDevice
```

Logs leave the device through journald, but **`ssh defaultuser@<ip> journalctl -f` does not work** —
`defaultuser` is not in the `systemd-journal` group, so it answers "No journal files were opened due
to insufficient permissions". There is no `sudo` on the device either. Three ways that do work:

- **the output of the deploy task itself.** `runReleaseOnDevice` streams the app's stdout back, and
  that is usually all you want: one log per run, which lines up with one test scenario per run.
- `ssh -t defaultuser@<ip> "devel-su journalctl -f"` — a continuous log across runs. The `-t` is
  required, or `devel-su` cannot prompt for the developer password.
- one-time `ssh -t defaultuser@<ip> "devel-su usermod -a -G systemd-journal defaultuser"`, after
  which the plain command above works.

## How the build variant is wired

### The switch

`settings.gradle.kts` changes three things at once when the flag is set:

1. **Plugin versions.** The binding lives in `pluginManagement { plugins { … } }`, and the build
   files write `id("org.jetbrains.compose")` with no version. It has to be `pluginManagement`
   and not the top-level `plugins {}` block: the latter applies to the `Settings` object, and
   declaring compose there actually applies it and breaks configuration.
2. **Repositories.** The local maven fork goes first. Settings scripts cannot read
   `local.properties` on their own, so the file is loaded by hand; and because the
   `pluginManagement` and `plugins` blocks are compiled separately from the script body and
   cannot see its declarations, that loading has to be duplicated. The path is duplicated once
   more in the root `build.gradle.kts` — project repositories override the ones declared in
   settings (`PREFER_PROJECT` mode).
3. **The module list.** Only `clients:shared:core` and the nine tablet modules are included.
   `backend`, `clients:tv` and `clients:smsrouter` pull AGP and upstream Compose through
   `build-logic`, and those cannot coexist with the fork in one invocation. The fast-booking
   module's directory on disk is `fastbooking` while its project path is `fastBooking`, so
   `projectDir` is set explicitly rather than relying on a case-insensitive filesystem.

### Why the modules have their own build files

The convention plugins from `build-logic` are deliberately not used in `build.aurora.gradle.kts`
— they are exactly what drags AGP and upstream Compose in. Each Aurora build file therefore
declares its targets (`linuxArm64`, `linuxX64`) and dependencies itself.

Per-module notes:

- `clients/shared/core` — the ktor `curl` engine is added in `linuxMain` only.
- `clients/tablet/core/ui` — additionally depends on `compose.ui`, needed for `Painter` and
  `ImageVector` in the linux drawable implementation. Coil is not wired in: the tablet does not
  load images over the network.
- `clients/tablet/core/domain` — multiplatform-settings has no linux target, so `SettingsStore`
  gets its own linux actual.
- `clients/tablet/feature/bookingEditor` — calf publishes no linux target, so the date and time
  pickers get Material3 actuals (on Android and iOS calf draws the same widgets underneath).
- `clients/tablet/feature/settings` — no `compose.resources` block here, same as in the upstream
  file, so its `Res` package stays the default one.
- `clients/tablet/composeApp` — the fork's navigation arrives through the `compose.navigation`
  DSL accessor, it has no catalog alias. AGP is not applied, so `gradleLocalProperties()` is
  unavailable and `local.properties` is read by hand for buildkonfig; `VERSION_NAME` has to be
  kept in sync with the upstream file manually, since there is no `android.defaultConfig` to
  take it from.

### Linking

Linking a Kotlin/Native linux binary does not fit in the default Kotlin daemon heap, hence the
raised `kotlin.daemon.jvmargs` in `gradle.properties`.

The executable needs the fork's own compiler flags (`-Xoverride-konan-properties`, without which
linking against the sysroot fails) and `Qt5Network` on top of what `cmpLinkerOpts` already adds,
because the http client is built on ktor-curl.

### Packaging and deploy

`auroraBuild` describes the package itself: app id, the `Internet` permission, `maliit-glib` for
the on-screen keyboard, and the four icon sizes. `auroraDevices` describes where it goes.

`runReleaseOnDevice` and `buildReleasePipeline` both come from the fork's plugins and are both
registered in `afterEvaluate`, so chaining them is only possible through
`tasks.matching { }.configureEach` — `tasks.named()` throws at that point. That wiring lives in
`aurora-tasks.gradle.kts` to keep the build file about the build.

## Resource packaging

Aurora packages compose resources **flat and without a namespace**: `<qualifier>/<file>` becomes
`<qualifier>_<file>`. Any module's `Res` therefore finds a file by name alone, and several
modules shipping an identically named `values/strings.xml` collapse into a single
`values_strings.commonMain.cvr` — whichever module's `Res` gets there first reads someone else's
bytes at its own offsets and dies in Base64 decoding.

Two consequences:

- string file names are split per module (`strings_app.xml`, `strings_main.xml`,
  `strings_settings.xml`, `strings_booking_editor.xml`). This does not affect key names — the
  generator takes them from `<string name="…">` — so no call site changes;
- the `stageAuroraResources` task collects the `composeResources` of every tablet module into a
  single directory, because `aurora-build` only packages `preparedResources` of its own module
  and does not follow project dependencies. The task runs with `duplicatesStrategy = FAIL` so
  that a future name clash is a build error rather than a wrong `.cvr` read at runtime.

## Platform implementations and polyfills

Everything the fork does not provide is filled in below. Nothing here is Aurora-only in the
sense of living in a separate app — these are `actual` implementations behind seams that Android
and iOS already had, plus two package squats.

| Area | Seam | Linux implementation | Caveat |
|---|---|---|---|
| HTTP engine | `HttpClientFactory` | ktor `Curl` | needs `Qt5Network` at link time |
| Drawables | `core.ui.res.painterResource` / `vectorResource` | own loader + vendored vector XML parser | SVG ignores `tint` |
| Date formatting | `LocalDateTime.toLocalisedString` | hand-rolled pattern expansion | month names hardcoded |
| Locale | `getCurrentLanguageCode` | hardcoded `"ru"` | not read from the system |
| Current time | `TimeReceiver` | coroutine ticking once a minute, aligned to `:00` | time/zone changes noticed late |
| Settings | `SettingsStore` | in-memory map | does not survive a restart |
| Date picker | `DatePickerView` | own month grid | Material3's is unusable, see below |
| Time picker | `TimePickerView` | Material3 | — |
| Logging | `Napier` | package squat + `fflush` | — |
| `@Preview` | `org.jetbrains.compose.ui.tooling.preview.Preview` | package squat | annotation is inert |
| Push notifications | — | none — polling instead | see below |

### Drawables: the resource facade and the vector XML parser

The fork's `components-resources` renders SVG only. Feeding it an Android Vector Drawable makes
`vectorResource` die with "Can't wrap nullptr", and `painterResource` return an empty picture.
Since the project's drawables are all Android vector XML, the loader is replaced rather than the
assets.

`core:ui` declares a facade in `band.effective.office.tablet.core.ui.res` whose signatures match
the compose-resources ones exactly, so switching a call site is a change of import and nothing
else. Android and iOS delegate straight back to compose-resources; only linux does real work:

- the drawable's raw bytes are resolved directly through `getDrawableResourceBytes`, which picks
  the right resource item for the current environment;
- the decoder is chosen **by byte signature**, not by file extension — PNG / JPEG / BMP / WebP
  magic bytes go to a `BitmapPainter`, a `<svg` substring goes to Skia's `SVGDOM`, and anything
  else is treated as Android vector XML;
- reading is a suspend call, so a transparent 24dp `ImageVector` holds the slot for the frame or
  so before decoding finishes, and any failure falls back to that same placeholder rather than
  throwing — which matters, because an exception thrown from a composable is invisible here (see
  Gotchas).

The vector XML path is a vendored parser under `core/ui/src/linuxMain/.../res/vectorxml`:

- `VectorXmlDom.kt` — a minimal XML DOM written in plain Kotlin. `javax.xml` does not exist on
  Kotlin/Native and Skia will not parse this dialect, so there was nothing to reuse.
- `XmlVectorParser.kt` — AOSP-derived (Apache 2.0 header retained), turns the parsed document
  into an `ImageVector`. Handles `<path>`, `<clip-path>`, `<group>`, linear/radial/sweep
  gradients and `aapt:attr`.
- `ValueParser.kt` — also AOSP-derived: colour, fill type, stroke cap/join, tile mode and dp
  value parsing.

Going through `ImageVector` rather than rasterising is what keeps `Icon(tint = …)` and intrinsic
dp sizes working. SVG is the exception: its colours are baked into the document, so a tint
applied to an SVG has no effect.

### Date formatting and locale

kotlinx-datetime's `byUnicodePattern` refuses locale-dependent directives on Kotlin/Native —
`MMMM` fails with "The directive 'MMMM' is locale-dependent, but locales are not supported in
Kotlin" — and there is no `java.time` or `NSDateFormatter` to fall back on. Delegating to the
common `toFormattedString` therefore throws, which on Aurora presents as a blank screen rather
than a crash.

`DateTimeUtils.linux.kt` expands the pattern itself: it walks the string, matches the longest
directive at each position (order matters — `MMMM` has to be tried before `MMM` before `MM`) and
substitutes. Supported: `MMMM`, `MMM`, `MM`, `M`, `yyyy`, `yy`, `dd`, `d`, `HH`, `H`, `hh`, `h`,
`mm`, `m`, `ss`, `s`, `a` — which covers everything `DateDisplayMapper` actually produces.
Russian month names are in the genitive case, so "25 ноября" reads correctly.

Month names being hardcoded is the direct consequence of the second gap: `getCurrentLanguageCode()`
returns `"ru"` unconditionally on linux. There is no `Locale.getDefault()` and no
`NSLocale.currentLocale`; Aurora does expose the system locale through Qt, but that is not wired
up. A meeting-room tablet is always Russian in practice, so this has not bitten yet — but it
means an English build would still print Russian months.

### Current time

`TimeReceiver` is an `expect class` with one implementation per platform, because each system has
its own way of waking an app once a minute and using it is what keeps a wall-mounted tablet off the
battery. Android registers a `BroadcastReceiver` for `ACTION_TIME_TICK` (the system's own minute
cadence, so no timer at all) plus `ACTION_TIME_CHANGED` and `ACTION_TIMEZONE_CHANGED`; iOS puts an
`NSTimer` on the main run loop and observes `NSSystemClockDidChange`. On linux there is neither, so
the actual launches a coroutine on `Dispatchers.Default` — `CurrentTimeTicker`, which sleeps to the
next whole minute and pushes `Clock.System.now()` into `CurrentTimeHolder`, forever.

The instance comes from Koin: `timeReceiverModule()` is an expect module in the shape of
`settingsStoreModule()`, because only Android's implementation needs a `Context` and only Android's
graph has one. `AppRoot` starts and stops it, which is the one root all three platforms share — an
earlier version constructed it in `AppActivity` alone, and the clock silently never moved on iOS or
Aurora.

The practical difference on linux: a manual clock change or a timezone switch is not observed, it is
only picked up at the next tick. For a wall-mounted room tablet that is acceptable; it is still a
polyfill and not an equivalent.

### Settings

multiplatform-settings has no linux target, so `settingsStoreModule()` provides a `SettingsStore`
backed by a plain `mutableMapOf<String, String>`. Everything works — the room picker writes and
reads it — but nothing is persisted, so the selected meeting room is forgotten on restart. The
fork ships `ru.auroraos.kmp:ak-shared-preferences`, which is the intended replacement.

### Date and time pickers

calf publishes no linux artifacts, so both pickers are implemented here. The two halves ended up
in very different places.

**Time** is Material3 `TimePicker` / `rememberTimePickerState`, and that is not a downgrade — calf
draws the same Material3 widget under the hood on Android. The colours are mapped onto
`LocalCustomColorsPalette`, and it uses `TimePickerLayoutType.Vertical` with `is24Hour` taken from
`DateDisplayMapper` — passing `is24Hour` explicitly matters, because the default would fall back
to `PlatformDateFormat.is24HourFormat()`.

**Date** cannot use Material3 at all. The fork ships
`androidx.compose.material3.internal.PlatformDateFormat` as a stub carrying `// @todo feature linux`:
`firstDayOfWeek = 0`, `weekdayNames = emptyList()`, `formatWithSkeleton` returning `""`. Material3's
`WeekDays` then walks `firstDayOfWeek - 1 until weekdayNames.size`, which is `-1 until 0` and
indexes an empty list — an exception on the very first frame, swallowed by the fork, presenting as
the dialog hanging and then dying. Fixing just the index would not help either: the month headline
would still be empty and the day grid still shifted by one.

So `DatePickerView.linux.kt` is a hand-written 6×7 month grid. The layout maths is
`CalendarGrid.kt` in `shared:core/linuxMain` — always six rows padded with nulls, so the dialog
does not change height when you page months — and the Russian month and weekday names are in
`RuCalendarNames.kt` next to it. Note there are two month lists there and they are not
interchangeable: formatting a date needs the genitive ("25 ноября"), a calendar header needs the
nominative ("Ноябрь 2026").

Deliberately no `LazyVerticalGrid` and no `FlowRow` — both are `SubcomposeLayout`, which this
dialog avoids on purpose. No year picker either (the backend serves a 14-day window, month arrows
are plenty) and no restriction on past days, which Material3's picker did not impose either.

### Logging

Napier publishes no linux targets, and calls to it sit in common code. Rather than guarding every
call site, its package is squatted: a source directory carrying `io.github.aakira.napier`, wired
in with `kotlin.srcDir` from the Aurora build file only. Callers and their imports are untouched
and the upstream builds never see the file.

The stub must live in exactly one module — two modules declaring the same package produce
duplicate symbols at klib link time. That module is `shared:core`; `core:domain` and `core:ui`
re-export it with `api(project(…))`, so the tablet modules get it transitively.

Two more details make it actually usable on a device:

- the stub prints to stdout, and under journald stdout is fully buffered, so lines printed just
  before a crash are lost. The stub therefore flushes after every line, through an `expect`/`actual`
  pair whose linux side calls `fflush(null)`.
- `LoggerInitializer` only installs an Antilog when `isDebug`, and on linux `isDebug` is
  `Platform.isDebugBinary` — false, because the Aurora variant links a *release* executable. So
  `Main.kt` installs it directly instead. The same flag also selects `API_URL_RELEASE`, which is
  worth remembering when a build talks to the wrong backend.

### `@Preview`

`compose-ui-tooling-preview` has no linux target either, and `@Preview` is used in
`feature/main`'s common code. Same trick: a squatted
`org.jetbrains.compose.ui.tooling.preview.Preview` annotation, wired in from the Aurora build
file only. It carries every parameter the call sites use (`widthDp`, `heightDp`, `locale`, …) or
the module would not compile; the annotation itself does nothing.

### Push notifications, and the polling that replaces them

There is no FCM on Aurora and no substitute wired up, so nothing is ever pushed here.

Polling covers it instead: `roomRefreshInterval` in `core:domain/platform` is a minute on linux and
drives `PeriodicRoomRefreshUseCase`, which calls `RefreshDataUseCase` on a timer. That is enough on
its own — the refresh writes into the local repository's buffer, and the main screen is already
subscribed to it, so the existing chain carries the update the rest of the way.

Note that `UpdateUseCase`, which also ticks, is **not** this: it only asks the screen to reload, and
the reload is served from the cache. Before the polling was added, Aurora never re-read the server
after startup at all.

iOS turned out to need the same thing for the same reason — there is no Firebase in `iosMain`
either, so `Collector.emit` is never called there.

Android stays on push alone. Worth knowing what that costs, because it was seen during testing: the
emulator showed a slot as free for an hour after another client had booked it, and the backend had
the booking the whole time. A push that does not arrive is indistinguishable from nothing having
changed — the screen stays wrong until the app is restarted, which on a wall-mounted tablet can be
weeks. A deliberately low-frequency backstop here would close that off.

## Layout: orientation, insets and scale

The declarations in `core:ui/platform` and `composeApp/platform` are switched by a flag and are
a no-op on Android and iOS.

| API | What it does | Where it is applied |
|---|---|---|
| `ForcedLandscape` | rotates content to landscape when the window arrives portrait | root, date/time picker `Dialog`, organizer popup (fallback path) |
| `ScaledUiDensity` | normalises the dp space to `uiScaleBaseline` by the short side | same three |
| `statusBarInset` | padding for Aurora's status bar | root, **inside** the rotated content |
| `softKeyboardOverlapPx` | how much of the content the keyboard covers | `ModalHost`, to keep the focused field visible |

**The keyboard is the one Aurora has to work out for itself.** Android reads the ime inset, iOS
answers zero because the system has already shortened the scene, and the fork reports no keyboard
insets at all — so `Keyboard.isOpen()` is polled every 100 ms while a modal is on screen, and the
covered height is *estimated* as half the window's short side. Estimated because the real number
is not exposed anywhere: maliit's surface spans the whole screen in the panel's native portrait
frame, so `Keyboard.height()` returns the full long side (2000 on the tablet), and the key strip's
thickness exists in no layer of the binding, down to the libac struct (`{ height, is_open }`).
Overshooting the estimate only lifts the field a bit higher; undershooting hides it.

Two hard-won rules around that poll:

- **Do not subscribe.** `Keyboard.listenState` next to the fork's own listener breaks maliit
  outright: the first session closes normally and every later focus gets no keyboard until the app
  restarts. The event is also useless for the size (`height = 0`; maliit sends the size in a
  follow-up event which never reaches the app) and buys no time — it fires at the end of the
  session-start handshake, milliseconds before `isOpen()` turns truthful anyway.
- **The tap is the only early signal.** The fork starts the maliit session synchronously *before*
  granting focus; that costs a visible second or two during which the keyboard is already rising
  while focus, `isOpen()` and the state event all still say closed. So the organizer field opens
  its list from the raw press (`awaitFirstDown`, not focus) and calls
  `noteSoftKeyboardExpected()`, which makes the overlap poll report optimistically for a 3 s
  grace — the card lifts with the keyboard instead of trailing it. The notice is never cancelled
  by a truthful-looking `isOpen()` (that can be a stale `true` from the previous session, and
  acting on it made the lift a coin flip); it just expires.

**The session is also closed by hand,** through `closeSoftKeyboard()` — a no-op on Android and iOS.
The fork opens a maliit session when a field takes focus and then parks in `awaitCancellation()`
with no `finally`, so it never stops one: the field is done being edited and maliit still believes
it is feeding it. A session outliving its field is the likeliest trigger for the freeze where the
app keeps drawing and polling but never receives another tap.

Everything that ends editing therefore leaves through one door — the field losing focus — and that
is where the session is closed. `Done` and picking a name from the list clear the focus to get
there; so does a tap on the dim; and so does `ModalHost` when the poll says the keyboard went away
while the field still held focus, which is the gesture testing reports the freeze on and the one
case where nothing else would have noticed. `freeFocus()` used to stand in for this and could not
work: it releases *captured* focus, and nothing here captures any.

**How to read a run off the device.** The keyboard says what it is doing under the `SoftKeyboard`
tag, the field under `OrganizerPicker`, the modal under `ModalHost`:

```sh
journalctl --since '<HH:MM>' --no-pager | grep -E 'SoftKeyboard|OrganizerPicker|ModalHost|Uncaught'
```

Picking a name from the list reads `field focus: true` → `overlap 0px -> Npx` → the key events →
`field focus: false` → `closed the session` → `overlap Npx -> 0px`. Swiping the keyboard away
instead leaves the field focused, so `overlap Npx -> 0px` → `keyboard gone on its own, field still
focused: true` → `field focus: false` → `closed the session`; verified on the dev phone, where the
whole sequence comes out in that order and the app stays alive.

`keyboard was already down` on that line is normal and not a sign the close was pointless:
`isOpen()` says whether the keyboard is on screen, maliit takes it down as soon as the Qt focus
goes, and the session being closed outlives both.

Every call into the fork's keyboard binding is wrapped, so a Kotlin exception from it is logged
rather than fatal; a native crash inside the binding is not catchable that way, and the last line
before silence is then the thing to look at.

**This is aimed at the tablet, and only the tablet.** On the Quadro T the keyboard comes up along
the bottom of the content, exactly where the shared `ModalHost` geometry expects it. On the dev
phone it comes up along the right-hand side instead, where a vertical shift does nothing — the same
device split as the dropdown that lands next to the field on the tablet and off to the side on the
phone, and probably the same cause: the phone's window really is portrait and rotated by
`ForcedLandscape`, while the tablet's is not.

**Why three layers and not just the root.** The fork renders `Popup` and `Dialog` as separate
scenes, in the untouched window and with the system density. Nothing applied at the root reaches
them, so both wrappers are re-applied in every layer. The modals themselves no longer need a layer:
they are state-driven overlays in the main scene (the date/time picker's own `Dialog` is the only
dialog window left — see AppNavHost for why).

The organizer list inside a modal is not a `Popup` on Aurora at all any more: a popup scene takes
a visible pause to come up and has to be aimed across two scenes that disagree about rotation and
density. It renders into the modal's overlay slot instead (`ModalHostState.overlay`), composed
inside the card's own box — same scene, same transforms, anchored with one `localPositionOf`
between field and card. The `Popup` path survives only as the fallback for a host without the
slot.

**Why the popup is positioned by hand.** Its position provider returns `0,0`, the layer is
stretched to fill the window, and the list itself is moved with `offset`. The stretching is not
optional: a popup window is sized to its content by default, so an offset list would fall outside
its own window and be clipped — which is exactly what happened on Android. The gap between the
field and the list is expressed in px so a substituted density cannot shift it. Anchor
coordinates are used as-is: `positionInWindow()` reports coordinates in the unrotated content
layout, and the rotation is a drawing effect that does not touch them.

**Why the inset goes inside the rotated content.** Applied outside, the padding would land in the
window's portrait coordinate space and show up as a stripe down the side after rotation. The
background is painted before the padding so the strip under the status bar stays dark.

## UI scale baseline

The scene density cannot be set on Aurora: the fork creates the scene as
`ComposeScene(density = Density(ru.auroraos.kmp.window.contentScale.toFloat()))`, and
`contentScale` comes from the system with the window. So the dp space is fixed by the app
instead — `ScaledUiDensity` substitutes `LocalDensity` with `short_side_px / uiScaleBaseline` and
pins `fontScale` to 1 so the system font scale does not multiply on top of ours.

The measurements were taken with the version overlay, which prints `win` (window size in px), `d`
and `fs` (density and font scale from the system) and `ui` (what `ScaledUiDensity` computed). The
overlay deliberately sits outside `ScaledUiDensity` — inside it, it would report the substituted
values instead of the system ones it exists to show.

| device | window px | system density | dp space |
|---|---|---|---|
| Quadro T (Aurora) | 1200x2000, rotated to 2000x1200 | 1.80 | 1111x667 |
| reference Android tablet | 1920x1200 | 1.75 | 1097x686 |
| Android emulator (tablet profile) | 2560x1600 | 2.00 | 1280x800 |

The two real devices differ by about 3%, not the 20–30% the first screenshots suggested — those
compared different builds, and the fork draws text wider than Android does (most likely a
different fallback font for Cyrillic). So the baseline is not correcting a density mismatch; it
buys room for wider text.

**The baseline is `686.dp`, and the parity it gives is exact rather than approximate.** Both real
devices are **1200 px on the short side** — the Quadro's window is 1200x2000, the Android reference
1920x1200 — so 1200/686 = 1.7493 against Android's own 1.75. The two lay out identically, in dp and
in pixels. The ~3% in the table above is the gap between the devices' *system* densities, which is
not what the app ends up using.

The alternative was **`800.dp`**, which is what the code carried first: it lays Aurora out in
1333x800 dp, ~15% more room than the reference, which hides the fact that the fork draws Cyrillic
wider — at the price of everything being ~15% smaller than it was drawn. Parity is the better
default; the wrapping that 800 was hiding has to be fixed in the texts and layout instead.

Two caveats on the current number. It has not been looked at on the Quadro since the change — the
arithmetic is solid, what it does to the wrapping is not verified. And the metrics overlay and
`ScaledUiDensity` are still scaffolding: once someone has confirmed the layout on the device, the
debug line should come out.

## Freezes: the maliit re-entrancy deadlock and the input teardown

A "frozen" app on this fork is almost never hung for the reason the UI suggests. Two distinct
mechanisms were caught on the device, and they need different tools to tell apart — start with
`cat /proc/<pid>/wchan` of the main thread.

**1. `futex_wait_queue_me` — the maliit deadlock (the whole process stops, polling included).**
The `ak-keyboard-maliit` binding is not re-entrant: key events arrive through
`MaliitEvents::send_input`, and `Keyboard.close()` sends `send_state` through the same channel —
so a close reached *synchronously from inside a key event handler* waits on the dispatch that is
still on the stack, forever. Kotlin/Native GC then parks every other thread, which is why even
the IO polling goes silent. Three separate call chains hit it, all starting at the organizer
field's `onDone`:

- `defaultKeyboardAction(Done)` → `SoftwareKeyboardController.hide()` → `Keyboard.close()`;
- `focusManager.clearFocus()` → CoreTextField ends its input session → the cancel handler of
  `startInputMethod` → `MaliitTextInputService.stopInput()` → `Keyboard.close()`;
- the app's own `closeSoftKeyboard()`.

The trap on the way out: **a `launch` does not defer anything here.** Both of the fork's
dispatchers — `FlushCoroutineDispatcher` (composition) and `ComposeUiMainDispatcher`
(`Dispatchers.Main`) — execute tasks inline when already on the main thread, so a "deferred"
block still runs inside the key dispatch. The only thing that genuinely leaves the dispatch is a
real suspension point against the frame clock. Hence the shape of the fix:

- `onDone` drops the focus behind `withFrameNanos {}` (composition scope carries the frame clock);
- `closeSoftKeyboard()` only `trySend`s into a conflated channel; `AuroraKeyboardSessionCloser`
  (mounted once in `Main.kt`) receives, awaits a frame, and closes outside any dispatch;
- `defaultKeyboardAction(Done)` is gone — Android and iOS take the keyboard down with the focus
  anyway.

The rule that falls out, for any future code: **never call `Keyboard.close()`, clear focus, or
end a text session synchronously from a key event handler.** Touch handlers are fine — touch
comes through `ac_window`, a different channel.

The proper fix belongs to the fork: `send_state` should not need the lock the in-flight
`send_input` holds, and `stopInput` should not close the session inline.

**2. `do_epoll_wait` — input torn down on window Pause (screen keeps repainting, input dead).**
`ComposeWindow.onWindowPause()` disposes the keyboard and unlistens both input and touch; a Pause
with no matching Resume would leave the app deaf while it keeps rendering. `onWindowResume()` also
re-listens without unlistening first, so an extra Resume duplicates every key.

This mechanism is **hypothetical so far** — it has never been confirmed on the device. An
`AuroraFreezeGuard` briefly shipped against it (`WindowEvents.unlistenLifecycleAll()` after the
first Resume) and was removed: the same call strips the fork's *own* lifecycle listener, freezing
the Compose `Lifecycle` in RESUMED forever and skipping `Keyboard.dispose()` and the destroy
cleanup — a bigger hazard than the one it guarded against. The
`MAttributeExtensionManager ... Invalid focus state` journal line once read as its signature turns
out to fire on *every* field focus, freeze or no freeze — it is maliit-server noise, not evidence.
If a deaf-but-rendering freeze ever shows up, log the lifecycle events first
(`WindowEvents.listenLifecycle`) and look for a Pause with no Resume; the fix then belongs in the
fork (input re-listen on the next frame), not in blanket unlistening.

**Getting a stack when it happens again:** the device has `gdbserver` (no gdb). As root:
`gdbserver --attach :2345 <pid>`, tunnel the port (`ssh -L 2345:localhost:2345`), then from the
host `lldb` against the local unstripped `composeApp.kexe`: `platform select remote-linux`,
`gdb-remote 2345`, `thread backtrace all`. Symbol names come out intact; the three chains above
were read straight off it.

## Gotchas

Each of these cost at least one round of on-device debugging.

- **An exception thrown from a composable is swallowed by the fork.** The frame is rolled back,
  the screen does not change and nothing appears in the log — it reads as "navigation did not
  work". Two unrelated bugs presented this way: date formatting and a `-1` room index.
- **The Aurora binary is a release binary,** so `Platform.isDebugBinary` is false. See the
  logging section — this affects both the log and which API url is used.
- **stdout is fully buffered under journald,** hence the flush after every log line.
- **The window arrives portrait** on every device seen so far, so `ForcedLandscape` rotates
  everywhere; it is not a phone-only path.
- **The system's own gestures stay in the portrait window,** because `ForcedLandscape` is a
  `rotate(90f)` — a drawing effect that never touches the window's geometry. Confirmed on a
  TrustPhone T1: holding the phone sideways so the content reads horizontally, the close gesture
  fires from the physical *side* edge and swiping up from the bottom does nothing. Nothing in the
  app can move those zones; it needs real window orientation from the fork
  (`ru.auroraos.kmp.window`).
- **Keyboard input works** — verified on a TrustPhone T1: the organizer field types from the maliit
  keyboard and the list filters as you go. The fork delivers the input as ordinary key events and
  fills `codePoint` only for `Char` events; they are logged under the `OrganizerPicker` tag if this
  ever needs looking at again.

## Not finished yet

- **Settings live in memory** — the selected room does not survive a restart. See the settings
  section; `ru.auroraos.kmp:ak-shared-preferences` is the intended fix.
- **The locale is hardcoded** to `ru`, and with it the month names. Aurora exposes the system
  locale through Qt; wiring it up and localising the dates belong together.
- **The time ticker is naive on Aurora** — one coroutine tick a minute (aligned to `:00`, so the
  displayed minute flips with the wall clock), but clock and timezone changes are still noticed
  only at the next tick. Android and iOS are woken by the system and see them at once.
- **No FCM,** so room updates arrive by polling once a minute rather than by push.
- **No kiosk mode** — there is no Aurora equivalent of the Android device-admin / lock-task path.
- **The dropdown position differs between devices** — next to the field on the tablet, off to the
  side on the dev phone. Unexplained.
- **The scale baseline has not been looked at on the target tablet** since it was moved to 686 dp
  for parity. The arithmetic is exact; whether the text wrapping that 800 dp was hiding is
  acceptable is unverified. See the baseline section.
