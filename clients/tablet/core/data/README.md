# Data Module

## Overview
The Data module is part of the core layer of the Effective Office tablet application. It handles all data operations, including API communication, local storage, and data transformations. This module implements the repository pattern to provide a clean API for the domain layer.

## Features
- API client implementation
- Local database management
- Data caching and synchronization
- Repository implementations
- Data source abstractions
- Data mapping between network, database, and domain models

## Architecture
The module follows a layered architecture:

```
data/
├── api/              # API client and service interfaces
├── database/         # Local database implementation
├── di/               # Dependency injection setup
├── mapper/           # Data mappers between different models
├── model/            # Data models (network and database entities)
├── repository/       # Repository implementations
└── source/           # Data source implementations (remote and local)
```

## Key Components

### API
- **ApiClient**: Handles HTTP communication with the backend
- **ApiService**: Defines the API endpoints and operations

### Repository
- **Repository Implementations**: Concrete implementations of domain repository interfaces
- **Data Sources**: Remote and local data source implementations

## Integration
The Data module integrates with:
- Domain module for providing repository implementations
- External libraries for networking (Ktor)

## Organizers are asked for by one tag and filtered by another

`OrganizerRepositoryImpl` requests `user_tag=employee` and then keeps the users whose `tag` is
`employer`. The two do not match, and that is not a typo waiting to bite: the backend ignores the
`user_tag` query parameter and returns every user regardless, so the request tag decides nothing and
the filter does all the work. Staff carry `employer`, which is what the list is meant to show.

Working as intended in every environment it has been run in — the local offline stand and the
team's own environments, on Android, iOS and Aurora alike. Written down only because the mismatch
looks like a bug on the way past, and because it is worth knowing that the two sides are
independent: if the backend ever starts honouring `user_tag`, the request would narrow the response
to `employee` and the `employer` filter would empty the list. Changing either side means changing
both.

Note also that a seeded stand has to use the same tag: `localQuickStart/seed-local-db.sh` inserts
its organizers with `tag='employer'` for exactly this reason.

## Error Handling
The module provides error handling for:
- Network errors and timeouts
- API response errors
- Database errors
- Data mapping exceptions

## Development
### Adding a New Repository
To add a new repository:
1. Define the repository interface in the domain module
2. Create data models in the data/model package
3. Implement the repository in the data/repository package
4. Register the repository in the dependency injection setup

### Testing
The module includes:
- Unit tests for repositories and data sources
- Mock implementations for testing