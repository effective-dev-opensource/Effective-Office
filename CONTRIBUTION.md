# Contributing to Effective Office

Thank you for your interest in contributing to Effective Office! This document provides guidelines for contributing to the project.

## Table of Contents
- [Code of Conduct](#code-of-conduct)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Pull Request Process](#pull-request-process)
- [Testing Guidelines](#testing-guidelines)
- [Documentation](#documentation)
- [Feature Development Guidelines](#feature-development-guidelines)
- [Security Guidelines](#security-guidelines)
- [Build Guidelines](#build-guidelines)

## Code of Conduct

We expect all contributors to follow these basic principles:
- Be respectful and inclusive
- Provide constructive feedback
- Focus on what is best for the community
- Show empathy towards other community members

## Development Workflow

We follow a modified Git Flow branching model. For detailed information, please refer to our [Git Flow](docs/GIT_FLOW.md).

### Branching Strategy
- **main**: Production-ready code
- **develop**: Integration branch for features
- **feature/**: For developing new features
- **bugfix/**: For fixing non-critical bugs
- **hotfix/**: For critical fixes
- **release/**: For preparing releases

### Feature Development Process
1. Create a feature branch from `develop`:
   ```bash
   git checkout develop
   git pull
   git checkout -b feature/your-feature-name
   ```

2. Work on your feature, committing changes regularly
3. Push your branch to the remote repository
4. Create a Pull Request to merge into `develop`

## Coding Standards

### General Guidelines
1. Follow Kotlin coding conventions for all development
2. Use consistent naming patterns across the codebase
3. Document public APIs and complex logic
4. Ensure no secrets or sensitive information is included in the code
5. Maintain the multi-module structure of the project

### Commit Message Guidelines
We follow these conventions for commit messages:
- Start with a type: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`
- Use the imperative mood ("Add feature" not "Added feature")
- Keep the first line under 50 characters
- Provide more detailed explanations in subsequent lines if necessary

Example:
```
feat: Add user authentication system

- Implement JWT token generation
- Add login and registration endpoints
- Create user repository and service
```

## Pull Request Process

1. Update documentation with details of changes if appropriate
2. Update version numbers in any examples files and documentation
3. Ensure all CI checks pass before requesting a review
4. You may merge the Pull Request once you have the sign-off of at least one other developer

## Testing Guidelines

Tests can be run using Gradle:
```bash
./gradlew test
```

For specific modules:
```bash
./gradlew :backend:app:test
./gradlew :clients:android:test
```

### Testing Requirements
- Write unit tests for all new features
- Ensure existing tests pass before submitting a PR
- Include integration tests for API endpoints
- For KMP modules, write platform-independent tests in `commonTest`

For more detailed testing guidelines, see our [Wiki: Testing Guidelines](https://github.com/effective-dev-opensource/Effective-Office/wiki/Testing-Guidelines).

## Documentation

- Update README files when adding new features or making significant changes
- Document public APIs with KDoc comments
- Keep documentation up-to-date with code changes
- Add comments for complex logic

## Feature Development Guidelines

For comprehensive feature development guidelines, please refer to our [Wiki: Feature Development Guidelines](https://github.com/effective-dev-opensource/Effective-Office/wiki/Feature-Development-Guidelines).

### Modular Architecture
1. Every feature should be implemented as a separate feature module
2. Feature modules should be independent and self-contained
3. Feature modules should only depend on core modules, not on other feature modules
4. New features should be added by creating a new module in the appropriate directory

### Clean Architecture
The project follows Clean Architecture principles:
1. **Domain Layer**: Contains business logic and entities
2. **Data Layer**: Implements repository interfaces from the domain layer
3. **Presentation Layer**: Contains UI components and view models
4. **Dependencies flow from outer layers to inner layers**

### SOLID Principles
1. **Single Responsibility Principle**: Each class should have only one reason to change
2. **Open/Closed Principle**: Software entities should be open for extension but closed for modification
3. **Liskov Substitution Principle**: Objects of a superclass should be replaceable with objects of subclasses without affecting correctness
4. **Interface Segregation Principle**: Many client-specific interfaces are better than one general-purpose interface
5. **Dependency Inversion Principle**: Depend on abstractions, not on concretions

## Security Guidelines

1. Never commit sensitive information like API keys or credentials
2. Use environment variables for configuration as shown in the .env.example files
3. Be aware of the pre-commit hooks that scan for potential secrets using Gitleaks
4. Follow secure coding practices to prevent common vulnerabilities

For more information, see our [Wiki: Security Guidelines](https://github.com/effective-dev-opensource/Effective-Office/wiki/Security-Guidelines).

## Build Guidelines

Before submitting changes, build the project to ensure it compiles correctly:
```bash
./gradlew build
```

For specific components, use the appropriate Gradle tasks.

For detailed build instructions, see our [Wiki: Build Guidelines](https://github.com/effective-dev-opensource/Effective-Office/wiki/Build-Guidelines).

## Additional Resources

For more detailed information on any aspect of contributing to Effective Office, please refer to our comprehensive [Wiki documentation](https://github.com/effective-dev-opensource/Effective-Office/wiki).

## Questions or Need Help?

If you have questions or need help with the contribution process, please reach out to the project maintainers:
- [Alex Korovyansky](https://t.me/alexkorovyansky)
- [Matvey Avgul](https://t.me/matthewavgul)
- [Tatyana Terleeva](https://t.me/tatyana_terleeva)
- [Stanislav Radchenko](https://github.com/Radch-enko)
- [Vitaly Smirnov](https://github.com/KrugarValdes)
- [Victoria Maksimovna](https://t.me/the_koheskine)

Thank you for contributing to Effective Office!
