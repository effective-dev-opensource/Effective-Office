# Build Logic Module

## Overview
The Build Logic module contains Gradle convention plugins that define common build configurations, dependencies, and tasks used across different modules in the Effective Office project. These plugins help maintain consistency in the build process and reduce duplication in build scripts.

## Features
- Centralized dependency management
- Reusable build configurations
- Standardized plugin application
- Common task definitions
- Consistent project setup

## Structure
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

## Convention Plugins

### Backend Plugins
- **kotlin-common**: Common Kotlin configuration for backend modules
- **spring-boot-application**: Configuration for Spring Boot application modules
- **spring-boot-common**: Common Spring Boot configuration for backend modules
- **spring-data-jpa**: Configuration for modules using Spring Data JPA

### Client Plugins
- **client.kmp.data**: Configuration for KMP data modules
- **client.kmp.domain**: Configuration for KMP domain modules
- **client.kmp.feature**: Configuration for KMP feature modules
- **client.kmp.library**: Configuration for KMP library modules
- **client.kmp.ui**: Configuration for KMP UI modules

## Usage

### Applying Plugins
To use these convention plugins in a module, add the appropriate plugin to the module's build script:

```kotlin
plugins {
    id("band.effective.office.backend.kotlin-common")
}
```

### Example: Setting up a Spring Boot Application Module
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

### Example: Setting up a KMP Feature Module
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

## Development

### Adding a New Convention Plugin
To add a new convention plugin:

1. Create a new Gradle script in the appropriate directory
2. Define the plugin logic
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

## Benefits
- **Consistency**: Ensures consistent configuration across modules
- **Maintainability**: Centralizes build logic for easier updates
- **Reusability**: Allows reuse of common configurations
- **Simplicity**: Simplifies module build scripts