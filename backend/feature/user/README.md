# User Module

## Overview
The User Module provides functionality for managing users within the Effective Office system. It enables creating, retrieving, updating, and deleting user accounts, as well as managing user-related information.

## Features
- User account management (CRUD operations)
- User authentication integration
- Active user filtering
- RESTful API for user operations

## Architecture
The module follows a layered architecture:

```
user/
├── config/        # Configuration classes
├── controller/    # REST controllers
├── dto/           # Data Transfer Objects
├── repository/    # Data access layer
├── service/       # Business logic services
```

## Key Components

### Core Components
- **UserController**: REST API endpoints for user operations
- **UserService**: Business logic for user management
- **UserRepository**: Data access for user entities

### API Endpoints
The module exposes the following REST endpoints:

- `GET /v1/users`: Get all users
- `GET /v1/users/{id}`: Get user by ID
- `GET /v1/users/by-username/{username}`: Get user by username
- `GET /v1/users/active`: Get all active users
- `POST /v1/users`: Create a new user
- `PUT /v1/users/{id}`: Update an existing user
- `DELETE /v1/users/{id}`: Delete a user

## Integration
The User module integrates with:
- Authentication and authorization module
- Notification module for user-related notifications
- Workspace module for user workspace assignments
