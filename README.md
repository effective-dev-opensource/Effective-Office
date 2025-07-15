# Effective Office
## Overview
Effective Office is a comprehensive office management system designed to streamline workplace operations, resource management, and employee interactions. The project consists of a backend server and tablet clients that work together to create an efficient office environment.

## Features
- Resource booking and management
- Employee scheduling and coordination
- Office space optimization
- Cross-platform support (android, iOS)
- Secure authentication and authorization
- Real-time updates and notifications

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
   git clone https://github.com/your-organization/effective-office.git
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
├── iosApp/            # iOS mobile application
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
- **CI/CD**: Automated build and deployment pipelines
- **Version Control**: Git with pre-commit hooks

## Code Style & Conventions
- Follow Kotlin coding conventions for backend development
- Use consistent naming patterns across the codebase
- Document public APIs and complex logic
- Run the pre-commit hook to ensure no secrets are committed

## Contributing
1. Ensure you've run the installation script (`./scripts/install.sh`)
2. Follow our [Git Flow](docs/GIT_FLOW.md) for branching and commit conventions
3. Create a feature branch (`git checkout -b feature/amazing-feature`)
4. Commit your changes (`git commit -m 'Add some amazing feature'`)
5. Push to the branch (`git push origin feature/amazing-feature`)
6. Open a Pull Request

## Roadmap
- TODO

## Authors
- TODO

## License
- TODO
