# Effective Office

## Goal :dart:

The main goal of the project is the automation of various processes in the office and providing
interesting statistics for employees.

## Technical goal: :wrench:

The main technical task of the project is to create a multi-module application on Kotlin,
trying to focus on the most modern and relevant solutions in this language. Throughout the project,
we tried to use other languages and
technologies as little as possible.

## 📺 Features: (Meeting Room Tablet App)


<img src="media/tablet/demo-tablet.gif" style="height: 50%;" />

### 🔧 Features Overview

| Feature                     | Description                                                  |
|----------------------------|--------------------------------------------------------------|
| Real-time Availability     | Displays up-to-date status of meeting rooms                  |
| Quick Booking              | Instantly reserve an available room with a single tap        |
| Time-Specific Reservations| Book rooms for specific time slots                           |
| Booking Cancellation       | Cancel existing reservations with ease                       |
| Early Room Release         | Free up the room before the end of the reservation           |
| Google Calendar Integration| Syncs all bookings with Google Calendar                      |

## Architecture

The project follows a client-server architecture:

- **Backend**: Spring Boot application with PostgreSQL database
- **Clients**: Client applications and iOS tablet app
- **Deployment**: Docker-based containerization for easy deployment
- **Security**: Git hooks for leak detection to prevent sensitive information exposure

## Getting Started

### Prerequisites

- Git
- Docker and Docker Compose
- JDK 17 or higher
- Gitleaks (for development)

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/effective-dev-opensource/Effective-Office
   cd effective-office
   ```

2. Run the installation script to set up git hooks:
   ```
   ./scripts/install.sh
   ```
   This script installs a pre-commit hook that scans for potential secrets using Gitleaks.

3. Set up environment variables:
   ```
   cp deploy/dev/.env.example deploy/dev/.env
   ```
   Edit the `.env` file with your configuration.

4. Build and run the application:
   ```
   cd deploy/dev
   docker-compose up -d
   ```

## Project Structure

```
effective-office/
├── backend/           # Server-side application
│   └── README.md      # Detailed backend documentation
├── clients/           # Client applications
│   └── README.md      # Detailed client documentation
├── iosApp/            # iOS tablet application
├── deploy/            # Deployment configurations
│   ├── dev/           # Development environment
│   └── prod/          # Production environment
├── scripts/           # Utility scripts
│   ├── git-hooks/     # Git hooks for development
│   └── install.sh     # Installation script
└── build-logic/       # Build configuration
```

For detailed documentation:

- [Backend Documentation](./backend/README.md)
- [Client Documentation](./clients/README.md)
- [Build Logic Documentation](./build-logic/README.md)
- [Calendar Integration Documentation](docs/CALENDAR_INTEGRATION.md)

## Development Tools

- **Build System**: Gradle with Kotlin DSL
- **Containerization**: Docker and Docker Compose
- **Security Scanning**: Gitleaks for secret detection
- **Version Control**: Git with pre-commit hooks

## Code Style & Conventions

- Follow Kotlin coding conventions for backend development
- Use consistent naming patterns across the codebase
- Document public APIs and complex logic
- Run the pre-commit hook to ensure no secrets are committed

## Contributing :raised_hands:

Our project is open-source, so we welcome quality contributions! To make your contribution to the
project efficient and easy to check out, you can familiarize yourself with the project's [git flow
and commit rules](docs/GIT_FLOW.md). If you want to solve an existing issue in the project, you can read the list in
the issues tab in the repository.

## Roadmap
- 📺 A TV application is in development, featuring a corporate news and photo feed, event announcements with registration
  from an external service, Duolingo and sports leaderboards, and a tracker for the internal currency.

## Authors :writing_hand:

- [Stanislav Radchenko](https://github.com/Radch-enko)
- [Vitaly Smirnov](https://github.com/KrugarValdes)

