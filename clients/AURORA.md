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
- `AURORA_DEVICE_PORT` and `AURORA_DEVICE_SSH_KEY` — optional, and only the SDK emulator needs
  them. Defaults are 22 and `~/.ssh/qtc_id`, which is a real device; the emulator has no address of
  its own (qemu forwards its ssh onto a host port) and authorises the SDK's own key instead. The
  key path is resolved against `$HOME`. Both take `-P…` overrides like the address does.

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

# the same, onto the SDK emulator
./gradlew -PbuildVariant=aurora :clients:tablet:composeApp:runReleaseOnDevice \
  -PAURORA_DEVICE_IP=127.0.0.1 -PAURORA_DEVICE_PORT=2223 \
  -PAURORA_DEVICE_SSH_KEY=AuroraOS/vmshare/ssh/private_keys/sdk
```

The emulator's model has to be **landscape** for the keyboard and the organizer list to sit where
the tablet puts them: the window then arrives 2000x1200 and `ForcedLandscape` passes the content
through untouched, so maliit draws along the content's own bottom edge. A portrait emulator window
reproduces the dev phone instead — the content is rotated and the keyboard comes up along its right
edge, where a vertical shift does nothing. The real Quadro T is the portrait one, so geometry fixes
have to be confirmed there; everything else is easier to watch in landscape. Deploying onto a
freshly booted emulator usually fails once with `Sync output timed out after 60 seconds` — that is
the install timing out under emulation, not a build error; the second run, on a warm system, goes
through.

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
| `ForcedLandscape` | rotates content to landscape when the window arrives portrait | root, date/time picker `Dialog` |
| `ScaledUiDensity` | normalises the dp space to `uiScaleBaseline` by the short side | same two |
| `statusBarInset` | padding for Aurora's status bar | root, **inside** the rotated content |
| `softKeyboardOverlapPx` | how much of the content the keyboard covers | `ModalHost`, to keep the focused field visible |

**The keyboard is the one Aurora has to work out for itself.** Android reads the ime inset, iOS
answers zero because the system has already shortened the scene, and the fork reports no keyboard
insets at all — so the height comes from the maliit session directly, `Keyboard.height()` on a
100 ms poll while a modal is on screen.

It is polled rather than subscribed to because the events are unusable: `Keyboard.listenState`
fires when the keyboard opens, but that event carries `height = 0` — maliit sends the size in a
follow-up event which never reaches the app. Polling is the sturdier half anyway. There is no
subscription to lose (the fork drops its listeners in `onWindowPause()` and never restores them —
the same defect behind the organizer-input freeze); the answer grows as the keyboard slides in, so
the modal follows it instead of jumping; and a keyboard swiped away behind the app's back, which
the fork does not report either, reads as closed on the next tick.

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
the bottom of the content, where the shared `ModalHost` geometry expects it. On the dev phone it
comes up along the right-hand side instead, where a vertical shift does nothing.

It is not the split it once looked like. Both windows arrive portrait and both are rotated — the
probe measures 720x1600 on the phone and 1200x2000 on the tablet, with the scene equal to the
window on each — so an earlier guess here, that the tablet's window was landscape and the phone's
was not, was wrong. What differs is where maliit puts its keys: along the window's bottom on the
phone, which the rotation turns into the content's right-hand side, and along the content's bottom
on the tablet.

**Why two layers and not just the root.** The fork renders `Popup` and `Dialog` as separate scenes,
in the untouched window and with the system density. Nothing applied at the root reaches them, so
both wrappers are re-applied in every layer that is one. Only the date/time picker's `Dialog` still
is: the modals are state-driven overlays in the main scene (see AppNavHost for why), and the
organizer list is content inside the modal's card rather than a popup at all.

**Why the organizer list is not a popup here.** A `Popup` on this fork is a scene of its own — a
second window created on demand. It takes a visible pause to come up, arrives without the rotation,
density and inactivity tracking applied around everything else, and has to be aimed by carrying the
field's coordinates into it. That last part is what killed the approach: the anchor came from
`positionInWindow()`, which maps a node's position up through every ancestor, `ForcedLandscape`
included, so the Y it reports for a node inside the rotated content is that node's content-X. The
list duly landed off to the side. This document and the code both claimed the opposite for a while,
on the strength of the list appearing roughly beside the field; the same confusion cost the keyboard
shift a fortnight before `ModalHostState` settled it.

So the list is content now, not a window: `ModalHost` exposes an overlay slot, the linux
`OrganizerListPopup` writes into it, and the slot is composed inside the card's own box. It shows up
on the frame it opens, inherits everything already applied around the card, and anchors against the
field's row through their nearest shared ancestor — where the keyboard shift and the rotation are on
both sides of the measurement and cancel. There is no popup fallback: the only user is the booking
editor, and it is only ever composed inside a modal.

**The row, not the field inside it.** The list takes its width from the row (`mTextFieldSize`), so
that is what it has to be positioned by; anchored to the `TextField` instead, it started 20.dp — the
row's horizontal padding — to the right of where it was drawn from, and hung the same 20.dp past the
card's right edge. Measured on the emulator: card 1259 wide, row at x=61 and 1137 wide, field at
x=96 and 854 wide, list 1137 wide drawn from x=96 — so 96..1233 against the row's 61..1198. This is
the second time this list has been diagnosed off a screenshot and the second time the screenshot was
not enough to tell "offset" from "wrong width"; the `OrganizerList` log line exists so the third
time is arithmetic. Its sizes are written `1137 x 262` rather than `1137x262` on purpose — the
deploy plugin scans the app's output for a native backtrace and reads a bare `0x0`, which is what an
unmeasured list logs, as an address, failing the run with "Application crashed with critical
errors".

**The list opens on the press, not on the focus.** Aurora grants focus at the end of the maliit
handshake — seconds, and up to six of them on the Quadro T — so a list opened from the focus
callback arrived after the keyboard: press, card jumps, pause, keyboard, pause, list. Nothing about
the list needs focus, and it now opens where the keyboard shift is already triggered, in the press
handler. Android and iOS grant focus on the same gesture, so the two moments were never apart there.

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
- ~~**The dropdown landing off to the side**~~ — fixed and verified on the Quadro T emulator: the
  list is no longer a popup, no longer anchored through window space, and now anchored on the same
  node it is sized to. It draws exactly over the row, 61..1198 in a 1259-wide card.
- **The scale baseline has not been looked at on the target tablet** since it was moved to 686 dp
  for parity. The arithmetic is exact; whether the text wrapping that 800 dp was hiding is
  acceptable is unverified. See the baseline section.
