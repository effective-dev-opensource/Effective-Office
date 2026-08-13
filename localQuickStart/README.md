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

## 4. Run the tablet app

The client reads its config from the **repo-root `local.properties`**
(`api.url.debug`, `api.url.release`, `apiKey`, `sdk.dir`).

⚠️ **The base URL is baked into the build**, and it differs per platform:

| Target | Property | Value |
|--------|----------|-------|
| **iOS simulator** (shares the host network) | `api.url.debug` | `http://localhost:8080` |
| **Android emulator** (host is reachable via a special IP) | `api.url.debug` | `http://10.0.2.2:8080` |
| **Aurora emulator** (same IP, but the build is release) | `api.url.release` | `http://10.0.2.2:8080` |

Switching platforms means editing that line and rebuilding. The script does the editing:

```bash
localQuickStart/point-client-at-local.sh ios|android|aurora
```

### Android emulator

```bash
# any tablet-sized AVD; build, install, launch:
./gradlew :clients:tablet:composeApp:assembleDebug
adb install -r -g clients/tablet/composeApp/build/outputs/apk/debug/composeApp-debug.apk
adb shell monkey -p band.effective.office.tablet -c android.intent.category.LAUNCHER 1
```

### iOS simulator

Open `iosApp/iosApp.xcodeproj` in Xcode and hit **Run** (scheme `iosApp`), or:

```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator \
  -destination 'platform=iOS Simulator,name=iPad Pro 11-inch (M4)' build
```

To iterate on Kotlin/Native compile errors fast (without Xcode):
`./gradlew :clients:tablet:composeApp:linkDebugFrameworkIosSimulatorArm64`

⚠️ **Typing into the simulator is ASCII-only and goes through the active macOS layout.**
With a Russian layout `an` arrives as `фт`, and filtering the organizer list then looks broken
when it is not. Switch to an English layout before typing.

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
