# Project Guidelines for Junie

## Project Overview
Effective Office is a multi-module Kotlin application aimed at automating office processes and providing statistics for employees. The project follows a client-server architecture with a Spring Boot backend, multiple client applications including an iOS tablet app, and Docker-based containerization for deployment.

## Project Structure
```
effective-office/
├── backend/           # Server-side Spring Boot application with PostgreSQL
├── clients/           # Client applications
├── iosApp/            # iOS tablet application
├── deploy/            # Deployment configurations (dev and prod environments)
├── scripts/           # Utility scripts and git hooks
├── build-logic/       # Build configuration
├── docs/              # Project documentation
└── media/             # Media assets
```

## Running Tests
Tests can be run using Gradle:
```
./gradlew test
```

For specific modules:
```
./gradlew :backend:app:test
./gradlew :clients:android:test
```

## Build Guidelines
Before submitting changes, Junie should build the project to ensure it compiles correctly:
```
./gradlew build
```

## Code Style Guidelines
1. Follow Kotlin coding conventions for all development
2. Use consistent naming patterns across the codebase
3. Document public APIs and complex logic
4. Ensure no secrets or sensitive information is included in the code
5. Maintain the multi-module structure of the project

## Security Guidelines
1. Never commit sensitive information like API keys or credentials
2. Use environment variables for configuration as shown in the .env.example files
3. Be aware of the pre-commit hooks that scan for potential secrets using Gitleaks

## Feature Development Guidelines
### Modular Architecture
1. Every feature should be implemented as a separate feature module
2. Feature modules should be independent and self-contained
3. Feature modules should only depend on core modules, not on other feature modules
4. New features should be added by creating a new module in the appropriate directory:
   ```
   effective-office/
   ├── backend/
   │   ├── features/
   │   │   ├── feature-a/     # Feature module A
   │   │   ├── feature-b/     # Feature module B
   │   │   └── new-feature/   # New feature module
   ```

### Core Entities
1. Core entities must be placed in appropriate core modules
2. Domain models should be in the `domain` module
3. Data models should be in the `data` module
4. UI components should be in the `ui` module

### Clean Architecture
The project follows Clean Architecture principles:
1. **Domain Layer**: Contains business logic and entities
   - Independent of frameworks and UI
   - Contains use cases, entities, and repository interfaces
2. **Data Layer**: Implements repository interfaces from the domain layer
   - Contains repository implementations, data sources, and mappers
3. **Presentation Layer**: Contains UI components and view models
   - Depends on the domain layer, not on the data layer
4. **Dependencies flow from outer layers to inner layers**:
   ```
   UI/Presentation → Domain ← Data
   ```

### SOLID Principles
1. **Single Responsibility Principle**: Each class should have only one reason to change
2. **Open/Closed Principle**: Software entities should be open for extension but closed for modification
3. **Liskov Substitution Principle**: Objects of a superclass should be replaceable with objects of subclasses without affecting correctness
4. **Interface Segregation Principle**: Many client-specific interfaces are better than one general-purpose interface
5. **Dependency Inversion Principle**: Depend on abstractions, not on concretions

### DRY and KISS Patterns
1. **DRY (Don't Repeat Yourself)**:
   - Avoid code duplication
   - Extract common functionality into reusable components
   - Use shared modules for code that's used across multiple features
2. **KISS (Keep It Simple, Stupid)**:
   - Prefer simple solutions over complex ones
   - Write code that is easy to understand and maintain
   - Avoid premature optimization

## Client Application Development Guidelines
### Navigation Framework
1. **Compose Navigation**:
   - Use Jetpack Navigation Component for Android applications
   - Implement type-safe navigation with sealed classes for destinations
   - Use NavHost for managing navigation between composables

2. **KMP Navigation**:
   - Use Decompose for shared navigation logic across platforms
   - Implement platform-specific navigation adapters
   - Maintain a consistent navigation state model across platforms

### UI Implementation
1. **Compose Multiplatform**:
   - Use Compose Multiplatform for KMP applications for shared UI components across platforms
   - Implement a design system with shared tokens (colors, typography, spacing)
   - Create platform-specific UI adaptations when necessary
   - Use expect/actual declarations for platform-specific UI implementations

2. **Responsive Design**:
   - Implement responsive layouts that adapt to different screen sizes
   - Use adaptive layouts for different form factors (phone, tablet, desktop)
   - Consider accessibility requirements in UI design

### Kotlin Multiplatform (KMP) Considerations
1. **Shared Code Organization**:
   - Place shared business logic in `commonMain` source set
   - Use platform-specific source sets (`androidMain`, `iosMain`) for platform-specific implementations
   - Leverage the `expect/actual` pattern for platform-specific APIs

2. **Dependency Management**:
   - Use multiplatform libraries when available
   - Implement platform-specific adapters for platform-specific libraries
   - Maintain consistent versioning across platforms

3. **Testing Strategy**:
   - Write platform-independent tests in `commonTest`
   - Implement platform-specific tests in platform-specific test source sets
   - Use mocks and test doubles for external dependencies

### Native Android Application Tech Stack
1. **Architecture Components**:
   - Use ViewModel for UI state management
   - Implement Jetpack Compose for declarative UI
   - Use Hilt for dependency injection
   - Implement Room for local data persistence

2. **Networking**:
   - Use Coroutines and Flow for asynchronous operations

3. **UI Components**:
   - Follow Material 3 design guidelines
   - Use Compose Material 3 components
   - Implement custom theming consistent with the design system

### General Tech Stack Considerations
1. **Performance**:
   - Optimize startup time and UI rendering
   - Use background processing for heavy operations

2. **Security**:
   - Implement secure storage for sensitive data
   - Use HTTPS for all network communications
   - Follow platform-specific security best practices

3. **Maintainability**:
   - Document architecture decisions and component interactions
   - Implement comprehensive logging for debugging

## Gradle Convention Plugins Guidelines

### Overview
The `build-logic` module contains Gradle convention plugins that define common build configurations, dependencies, and tasks used across different modules in the project. These plugins help maintain consistency in the build process and reduce duplication in build scripts.

### Benefits
- **Consistency**: Ensures consistent configuration across modules
- **Maintainability**: Centralizes build logic for easier updates
- **Reusability**: Allows reuse of common configurations
- **Simplicity**: Simplifies module build scripts

### Structure
```
build-logic/
├── build.gradle.kts       # Build script for the build-logic module
├── settings.gradle.kts    # Settings for the build-logic module
└── src/
    └── main/
        └── kotlin/
            └── band/
                └── effective/
                    └── office/
                        └── backend/
                            ├── kotlin-common.gradle.kts                  # Common Kotlin configuration
                            ├── spring-boot-application.gradle.kts        # Spring Boot application configuration
                            ├── spring-boot-common.gradle.kts             # Common Spring Boot configuration
                            ├── spring-data-jpa.gradle.kts                # Spring Data JPA configuration
                            ├── band.effective.office.client.kmp.data.gradle.kts        # KMP data module configuration
                            ├── band.effective.office.client.kmp.domain.gradle.kts      # KMP domain module configuration
                            ├── band.effective.office.client.kmp.feature.gradle.kts     # KMP feature module configuration
                            ├── band.effective.office.client.kmp.library.gradle.kts     # KMP library module configuration
                            ├── band.effective.office.client.kmp.ui.gradle.kts          # KMP UI module configuration
                            └── ProjectExtensions.kt                      # Kotlin extensions for project configuration
```

### Available Convention Plugins

#### Backend Plugins
- **kotlin-common**: Common Kotlin configuration for backend modules
- **spring-boot-application**: Configuration for Spring Boot application modules
- **spring-boot-common**: Common Spring Boot configuration for backend modules
- **spring-data-jpa**: Configuration for modules using Spring Data JPA

#### Client Plugins
- **client.kmp.data**: Configuration for KMP data modules
- **client.kmp.domain**: Configuration for KMP domain modules
- **client.kmp.feature**: Configuration for KMP feature modules
- **client.kmp.library**: Configuration for KMP library modules
- **client.kmp.ui**: Configuration for KMP UI modules

### Using Convention Plugins

#### Applying Plugins
To use these convention plugins in a module, add the appropriate plugin to the module's build script:

```kotlin
plugins {
    id("band.effective.office.backend.kotlin-common")
}
```

#### Example: Setting up a Spring Boot Application Module
```kotlin
plugins {
    id("band.effective.office.backend.spring-boot-application")
}

dependencies {
    implementation(project(":backend:core:domain"))
    implementation(project(":backend:feature:user"))
    // Additional dependencies
}
```

#### Example: Setting up a KMP Feature Module
```kotlin
plugins {
    id("band.effective.office.client.kmp.feature")
}

dependencies {
    implementation(project(":clients:shared:domain"))
    implementation(project(":clients:shared:ui"))
    // Additional dependencies
}
```

### Creating New Convention Plugins
To add a new convention plugin:

1. Create a new Gradle script in the appropriate directory under `src/main/kotlin/band/effective/office/`
2. Define the plugin logic using Kotlin DSL
3. Register the plugin in the build-logic build script

Example:
```kotlin
// src/main/kotlin/band/effective/office/backend/new-plugin.gradle.kts
plugins {
    id("kotlin")
    // Other plugins
}

// Configure the plugin
```

### Best Practices
1. **Keep plugins focused**: Each plugin should have a single responsibility
2. **Document plugin behavior**: Add comments explaining what the plugin does
3. **Reuse existing plugins**: Compose new plugins from existing ones when possible
4. **Follow naming conventions**: Use consistent naming for plugins
5. **Test changes**: Verify that changes to plugins don't break existing modules
6. **Version control**: Update all affected modules when changing plugin behavior
