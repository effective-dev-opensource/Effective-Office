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

Logs leave the device through journald: `ssh defaultuser@<ip> journalctl -f`.

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
| Current time | `TimeReceiver` | coroutine ticking once a minute | time/zone changes noticed late |
| Settings | `SettingsStore` | in-memory map | does not survive a restart |
| Date/time pickers | `DatePickerView` / `TimePickerView` | Material3 | — |
| Logging | `Napier` | package squat + `fflush` | — |
| `@Preview` | `org.jetbrains.compose.ui.tooling.preview.Preview` | package squat | annotation is inert |
| Push notifications | — | none | no room updates by push |

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

`TimeReceiver` on Android is a `BroadcastReceiver` for system time changes; on iOS it is an
`NSTimer` on the main run loop. On linux there is neither, so the actual simply launches a
coroutine on `Dispatchers.Default` that sleeps 60 seconds and pushes
`Clock.System.now()` into `CurrentTimeHolder`, forever.

The practical difference: a manual clock change or a timezone switch is not observed, it is only
picked up at the next tick. For a wall-mounted room tablet that is acceptable; it is still a
polyfill and not an equivalent.

### Settings

multiplatform-settings has no linux target, so `settingsStoreModule()` provides a `SettingsStore`
backed by a plain `mutableMapOf<String, String>`. Everything works — the room picker writes and
reads it — but nothing is persisted, so the selected meeting room is forgotten on restart. The
fork ships `ru.auroraos.kmp:ak-shared-preferences`, which is the intended replacement.

### Date and time pickers

calf publishes no linux artifacts, so `DatePickerView` and `TimePickerView` are implemented
directly on Material3 `DatePicker` / `rememberDatePickerState` and `TimePicker` /
`rememberTimePickerState`. That is not a downgrade — calf draws the same Material3 widgets under
the hood on Android. The colours are mapped onto `LocalCustomColorsPalette` so they match the
rest of the app, and the time picker uses `TimePickerLayoutType.Vertical` with `is24Hour` taken
from `DateDisplayMapper`.

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

### Push notifications

There is no FCM on Aurora and no substitute wired up, so the room list is never updated by push —
only by the app's own polling.

## Layout: orientation, insets and scale

Three declarations in `core:ui/platform` and `composeApp/platform` are switched by a flag and are
a no-op on Android and iOS.

| API | What it does | Where it is applied |
|---|---|---|
| `ForcedLandscape` | rotates content to landscape when the window arrives portrait | root, `DialogBackgroundDim`, organizer popup |
| `ScaledUiDensity` | normalises the dp space to `uiScaleBaseline` by the short side | same three |
| `statusBarInset` | padding for Aurora's status bar | root, **inside** the rotated content |

**Why three layers and not just the root.** The fork renders `Popup` and `dialog<>` as separate
scenes, in the untouched window and with the system density. Nothing applied at the root reaches
them, so both wrappers are re-applied in every layer.

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

Three candidates, with what each costs:

- **`800.dp` — what is in the code.** Aurora lays out in 1333x800 dp, ~15% more room than the
  Android reference; the wrapping is gone, verified on the device. Price: everything is ~15%
  smaller than drawn.
- **`686.dp`** — exact parity with the reference tablet. The UI is the size it was designed at,
  but the wrapping comes back and has to be fixed in the texts and layout.
- **`~740.dp`** — the middle, ~8% smaller than the reference. Untested.

The metrics overlay and `ScaledUiDensity` are scaffolding: once the baseline is settled, the
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

## Not finished yet

- **Keyboard input.** The fork delivers maliit input as ordinary key events and fills `codePoint`
  only for `Char` events; whether the field actually types is unverified. The key events are
  logged under the `OrganizerPicker` tag.
- **Settings live in memory** — the selected room does not survive a restart. See the settings
  section; `ru.auroraos.kmp:ak-shared-preferences` is the intended fix.
- **The locale is hardcoded** to `ru`, and with it the month names. Aurora exposes the system
  locale through Qt; wiring it up and localising the dates belong together.
- **The time ticker is naive** — one coroutine tick a minute, so clock and timezone changes are
  noticed late.
- **No FCM,** so room updates never arrive by push.
- **No kiosk mode** — there is no Aurora equivalent of the Android device-admin / lock-task path.
- **The icons are wrong.** `clients/tablet/composeApp/icons` holds four PNGs of the right sizes,
  but they are not the application's icons and need replacing.
- **The dropdown position differs between devices** — next to the field on the tablet, off to the
  side on the dev phone. Unexplained.
- **The scale baseline has not been re-checked** on the target tablet since the number was
  chosen — see the baseline section.
