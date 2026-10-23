# Data Module

## Overview
The Data module is part of the core layer of the Effective Office tablet application. It talks to
the backend over Ktor, keeps the last known room state in memory, and implements the repository
interfaces the domain module declares.

There is no local database. What survives a screen change is the in-memory `Buffer`; the only thing
persisted between launches is the configured room name, and that is kept by the domain module.

## Architecture

```
data/
├── api/              # Backend interfaces and their Ktor implementations (api/impl)
├── di/               # Koin module for the data layer
├── dto/              # Network models: booking, user, workspace
├── mapper/           # DTO to domain model mapping
├── repository/       # Repository implementations, network and in-memory
└── utils/            # Buffer, Converter
```

## Key Components

### API
- **BookingApi**, **UserApi**, **WorkspaceApi**: the backend surface the tablet uses, each with a
  Ktor implementation in `api/impl` over the shared `HttpClientProvider`.
- **Collector**: a small helper that turns emitted values into a shared flow.

### Repository
- **BookingRepositoryImpl**, **RoomRepositoryImpl**, **OrganizerRepositoryImpl**: network-backed
  implementations of the domain interfaces.
- **LocalBookingRepositoryImpl**, **LocalRoomRepositoryImpl**: in-memory implementations over
  `Buffer`. The room one also recomputes which event of a room is the current one as time passes.
- **Buffer**: a single `MutableStateFlow` holding the last known room list, shared by both local
  repositories so an update from either is seen by both.

### Mappers
- **EventInfoMapper**, **RoomInfoMapper**: booking and workspace DTOs to domain models.
- **Converter**: the way back, for the few requests that need a domain model as a DTO.

## Error Handling
Every API and repository call returns `Either`, defined in the shared core module. Room lookups
carry `ErrorWithData`, which keeps the last good data alongside the error, so the screen can stay
populated while the tablet is disconnected.

## Integration
The Data module is used by:
- the Domain module, whose repository interfaces it implements;
- the shared core module, for `Either`, `ErrorResponse` and the HTTP client.

## Development
### Adding a New Repository
1. Define the repository interface in the domain module
2. Add the DTOs it needs under `dto/`
3. Implement the repository under `repository/`, mapping DTOs to domain models
4. Register the implementation in `di/DataModule.kt`
