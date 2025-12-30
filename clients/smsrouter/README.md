# SMS Router Feature Module

## Overview

The SMS Router module provides a small Android client that receives incoming SMS messages and forwards them to configured webhooks (Mattermost, Telegram). It collects SIM metadata, persists per-SIM settings, and stores delivery logs.

## Features

- **SMS interception** via `SmsReceiver` (BroadcastReceiver for `Telephony.Sms.Intents.SMS_RECEIVED_ACTION`).
- **Forwarding** to external webhooks (Mattermost, Telegram) with payload mapping.
- **Per-SIM settings**: webhook URL, secret key, webhook type, Telegram `chatId` (stored in SharedPreferences).
- **Delivery logging** using Room DB (`smsrouter-db`, table `sms_logs`) and a Messages UI.
- **Retry policy**: up to 3 attempts with a 60s overall timeout; retry callbacks for monitoring.
- **DI & networking**: Koin for DI, Ktor-based HTTP client for requests.

## Architecture

```
smsrouter/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── data/          # SmsApiService, repositories, DB (Room), mappers, DI
│       │   ├── database/   # Room entities and Dao (SmsLogDao)
│       │   ├── mapper/     # SmsDataDtoMapper
│       │   └── repository/ # SettingsRepositoryImpl, SmsLogsRepositoryImpl, SmsForwardingRepositoryImpl
│       ├── domain/        # models, use-cases (ForwardSmsUseCase), interfaces
│       └── presentation/  # SmsReceiver, MainActivity, Compose screens (settings, messages), ViewModels
```

## Key Components

- **SmsReceiver** — receives SMS broadcasts, builds `SmsData` and triggers forwarding.
- **ForwardSmsUseCase** — coordinates log lifecycle and forwarding, handles updating logs on success/failure.
- **SmsApiServiceImpl** — performs HTTP POST to webhooks, implements retry & timeout logic and notifies retries via `onRetry`.
- **SmsDataDtoMapper** — maps `SmsData` to Mattermost/Telegram DTOs.
- **SettingsRepositoryImpl** — persists per-SIM settings in `SharedPreferences`.
- **SmsLogsRepositoryImpl** / **SmsLogDao** — persists delivery logs in Room DB and exposes state to UI.

## Integration

- **Permissions**: Requires `RECEIVE_SMS`, `READ_SMS`, `READ_PHONE_STATE`, `READ_PHONE_NUMBERS` (where applicable), and `INTERNET`.
- **App wiring**: Koin modules register `dataModule`, `domainModule`, `presentationModule` during application startup (`SmsRouterApplication`).
- **UI**: Settings screen allows configuring per-SIM webhook URL, secret, webhook type, and Telegram `chatId`.
- **Webhook contract**: Requests include header `Authorization: Bearer <secretKey>` and JSON payloads:
  - Mattermost: `{ "text": "<message>" }`
  - Telegram: `{ "chat_id": "<chatId>", "text": "<message>" }`

## Development Guidelines

- Keep forwarding and retry logic inside `ForwardSmsUseCase` / `SmsApiServiceImpl` for testability.
- Add unit tests for `ForwardSmsUseCase` (mock repos/service) and `SmsApiService` (mock Ktor client).
- To add a webhook type: add value to `WebhookType`, implement the DTO in `data/models`, update `SmsDataDtoMapper` and tests.
- To change retry policy: adjust attempts, backoff and timeout in `SmsApiServiceImpl`.

### Platform-Specific Considerations

- Requesting `READ_PHONE_STATE` at runtime is necessary to enumerate SIMs; the Settings screen requests permission when needed.
- Retry counts are stored in memory keyed by `smsId`; if `smsId` is absent or process restarts, retry counters are not persisted.

## Notes

- On failure, check per-SIM `webhookUrl` and `secretKey` and ensure Telegram `chatId` is set when `WebhookType=TELEGRAM`.
- Use Logcat (`ForwardSmsUseCase` tag) and the Messages UI to inspect delivery status and errors.
- Ensure the app is excluded from battery optimizations where SMS reception may be affected.
