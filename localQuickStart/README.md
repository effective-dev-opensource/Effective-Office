# localQuickStart — run Effective-Office locally, fully offline

This folder gets the **backend + the Meeting Room tablet app** running on your machine
**without any external accounts** (no Google / Firebase / Synology / Notion / Clockify /
Mattermost). It's meant for "just show me how it works" demos and local development.

> The official README "run locally" path assumes you have **real** Google & Firebase
> service accounts (it even asks you to drop in `google-credentials.json` /
> `firebase-credentials.json`). Without them the server won't even boot. This quick-start
> swaps every provider for the project's built-in **dummy** implementations instead.

---

## What's in this folder

| File | Purpose |
|------|---------|
| `run-backend-local.sh` | Starts the Spring Boot backend with all providers set to dummy and every required env var filled with safe dummy values. |
| `seed-local-db.sh` | Inserts an API key + one zone + two meeting rooms (`Sync`, `Focus`) so the tablet has something to show. Idempotent. |
| `.local-fake-credentials.json` | A **self-generated, fake** Google service-account JSON (valid RSA key, never used to talk to Google). Auto-created by `run-backend-local.sh` if missing. **Git-ignored** (contains a private key → would trip the Gitleaks pre-commit hook). |

## Prerequisites

- Docker (for Postgres)
- JDK 17+ (JDK 21 works — the build targets bytecode 17, no strict toolchain)
- To run the client: **Android SDK + an emulator**, or **Xcode** (iOS simulator)

---

## 1. Start Postgres

```bash
docker run --name postgres-effectiveoffice \
  -e POSTGRES_DB=effectiveoffice -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 -d postgres:15-alpine
```

## 2. Start the backend

```bash
localQuickStart/run-backend-local.sh
```

**Check first whether one is already up** — `lsof -nP -iTCP:8080 -sTCP:LISTEN`. A `bootRun` from an
earlier session has been found still serving two weeks later, Postgres container and seeded data
intact, and rebuilding on top of it only wastes a few minutes.

**Run it in your own terminal.** `bootRun` is a foreground process: started as a background job of
some other tool it dies when that tool exits, and the symptom — a client that suddenly cannot
connect — looks nothing like the cause. If it has to be detached, `nohup … & disown` (macOS has no
`setsid`).

Wait for `Started EffectiveOfficeApplicationKt`. It listens on **http://localhost:8080**,
context path **`/api`**. Quick check (Swagger UI is public and should return 200):

```bash
curl -o /dev/null -w '%{http_code}\n' http://localhost:8080/api/swagger-ui/index.html  # -> 200
open http://localhost:8080/api/swagger-ui/index.html
```

> Note: `/api/actuator/health` returns **403** here — actuator sits behind its own security in
> this project, so don't use it as a liveness check. Use the Swagger URL or the authorized
> call in step 3 instead.

## 3. Seed the database

```bash
localQuickStart/seed-local-db.sh
```

Verify auth + data (the key matches `apiKey` in the repo-root `local.properties`):

```bash
curl -H "Authorization: Bearer effective-office-local-key" \
     "http://localhost:8080/api/v1/workspaces?workspace_tag=meeting"
# -> JSON array with "Sync" and "Focus"
```

Mind the `/v1/`: `/api/workspaces` does not exist and answers "No static resource", which reads as a
broken backend rather than a wrong path.

The seed also inserts 20 organizers for the booking editor's "Choose organizer" list. Three would be
fewer than fills the list's 150.dp cap, and a list with nothing to scroll cannot be told apart from
a list whose scrolling is broken — which cost one bug report. They are inserted with `tag='employer'`
on purpose; see `clients/tablet/core/data/README.md` for why the client asks for one tag and filters
by another.

## 4. Run the tablet app

The client reads its config from the **repo-root `local.properties`**
(`api.url.debug`, `api.url.release`, `apiKey`, `sdk.dir`).

⚠️ **The base URL is baked into the build**, and it differs per platform:

| Target | `api.url.debug` |
|--------|-----------------|
| **iOS simulator** (shares the host network) | `http://localhost:8080` |
| **Android emulator** (host is reachable via a special IP) | `http://10.0.2.2:8080` |

Change that one line and rebuild when you switch platforms.

### Android emulator

```bash
# any tablet-sized AVD; build, install, launch:
./gradlew :clients:tablet:composeApp:assembleDebug
adb install -r -g clients/tablet/composeApp/build/outputs/apk/debug/composeApp-debug.apk
adb shell monkey -p band.effective.office.tablet -c android.intent.category.LAUNCHER 1
```

Four things that cost time here:

- **`adb` and `emulator` are not on `PATH`** — they live under `$HOME/Library/Android/sdk` in
  `platform-tools/` and `emulator/`.
- **Put an `adb` sequence in a bash script, not a zsh line.** `D="adb -s emulator-5554"; $D shell …`
  fails silently in zsh: it does not word-split an unquoted variable, so the whole string is looked
  up as one command name.
- **With more than one device attached, `adb` needs `-s emulator-5554`** or it refuses to choose.
- **The on-screen keyboard does not appear** until
  `adb shell settings put secure show_ime_with_hard_keyboard 1` — the emulator counts the laptop
  keyboard as a hardware one, so anything that has to be tested against a soft keyboard silently
  cannot be.

### iOS simulator

Open `iosApp/iosApp.xcodeproj` in Xcode and hit **Run** (scheme `iosApp`), or:

```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator \
  -destination 'platform=iOS Simulator,name=iPad Pro 11-inch (M4)' build
```

To iterate on Kotlin/Native compile errors fast (without Xcode):
`./gradlew :clients:tablet:composeApp:linkDebugFrameworkIosSimulatorArm64`

Switching to iOS means editing `api.url.debug` to `http://localhost:8080` — **and putting it back
to `10.0.2.2` afterwards**, or the next Android build silently stops reaching the backend.

---

## Files this quick-start needs elsewhere in the repo

These can't live in this folder — the build looks for them at fixed locations. They're all
**git-ignored** and safe to keep locally:

| Path | Why |
|------|-----|
| `local.properties` (repo root) | `api.url.debug/release`, `apiKey`, `sdk.dir`. Any Gradle build fails without it (settings.gradle configures every module). |
| `clients/tablet/composeApp/google-services.json` | The `com.google.gms.google-services` plugin refuses to build without it. A dummy file with package `band.effective.office.tablet` is enough. |
| `clients/tablet/composeApp/src/debug/AndroidManifest.xml` | Debug-only overlay adding `usesCleartextTraffic="true"` so the debug build can reach the local **http** backend. |
| `keystore/debug.keystore` | Debug signing config (alias `androiddebugkey`, store/key pass `android`). Generate with `keytool`. |

---

## Why the workarounds exist (project quirks)

**Backend**
- Provider config classes (Synology, Mattermost, …) load **unconditionally** and read env
  with self-referential YAML (`SYNOLOGY_IP: ${SYNOLOGY_IP:}`) — so every such var must be
  non-empty or Spring dies with a "circular placeholder reference". Hence the long dummy list.
- `photo.saver.provider=dummy` clashes with the photos dummy (both register a bean named
  `dummyPhotoProvider`) → we use `mattermost` with the scheduler disabled instead.
- `FirebaseConfig` reads the credentials file at startup no matter what → the fake JSON.

**iOS** (the `iosMain` source set was clearly never built in CI — it needed 5 fixes to compile):
1. `clients/shared/core/.../DateTimeUtils.ios.kt` — dropped `NSLocale.currentLocale`
   (unresolved on the current Kotlin/Native; `NSDateFormatter` defaults to the current locale).
2. Deleted orphaned `clients/tablet/core/data/src/iosMain/.../HttpClientFactory.ios.kt`
   (`actual` with no `expect`).
3. `clients/tablet/core/ui/.../DateFormatter.ios.kt` — added `import platform.Foundation.languageCode`.
4. `clients/tablet/feature/main/build.gradle.kts` — hardcoded `components-ui-tooling-preview:1.10.0`
   pulled a Kotlin 2.2.x klib (project is 2.1.21) → switched to the managed `compose.components.uiToolingPreview`.
5. Stripped `widthDp/heightDp/locale` params from `@Preview` in feature:main (those params only
   exist in the newer Preview annotation; Compose here is 1.8.1).

## Limitations

- Providers are dummy → bookings live in-memory in the backend and are **not** synced to a real
  Google Calendar. Perfect for demos, not for real scheduling.
