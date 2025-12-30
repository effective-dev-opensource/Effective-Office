# Data Module (TV)

## Overview

This Data module provides essential utilities and configurations that the TV app depends on for network communication and dependency injection. It ensures safe and efficient data handling across the application.

### Key Utilities

1. **`DataModule`**: Koin DI registration for the shared HTTP client.

   - Centralizes the configuration of the HTTP client used across the app.
   - Ensures consistent setup for interceptors, timeouts, and base URLs.

2. **`get` Helper**: A utility for making safe GET requests.
   - Wraps network calls in a functional approach, returning `Either<ErrorResponse, T>`.
   - Simplifies error handling by encapsulating success and failure cases.

## Features

- **Dependency Injection**: Provides a centralized module for registering data-related dependencies.
- **Safe Network Requests**: Ensures robust error handling for HTTP GET requests.
- **Reusability**: Utilities are designed to be reusable across multiple features.

## Integration

- **Core Modules**: The `DataModule` is integrated into the app's DI graph, ensuring all features can access the shared HTTP client.
- **Feature Modules**: The `get` helper is used by feature modules to make safe network requests.
