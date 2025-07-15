# Workspace Module

## Overview
The Workspace Module provides functionality for managing workspaces within the Effective Office system. It enables retrieving information about different types of workspaces, their zones, utilities, and availability for booking.

## Features
- Workspace management and retrieval
- Workspace zone organization
- Workspace utilities tracking
- Integration with booking system for availability checking
- Filtering workspaces by tags
- RESTful API for workspace operations

## Architecture
The module follows a layered architecture:

```
workspace/
└── core/
    ├── config/        # Configuration classes
    ├── controller/    # REST controllers
    ├── dto/           # Data Transfer Objects
    ├── repository/    # Data access layer
    ├── service/       # Business logic services
```

## Key Components

### Core Components
- **WorkspaceController**: REST API endpoints for workspace operations
- **WorkspaceService**: Business logic for workspace management
- **WorkspaceRepository**: Data access for workspace entities

### API Endpoints
The module exposes the following REST endpoints:

- `GET /v1/workspaces/{id}`: Get workspace by ID
- `GET /v1/workspaces?workspace_tag={tag}`: Get workspaces by tag
- `GET /v1/workspaces?workspace_tag={tag}&with_bookings_from={timestamp}&with_bookings_until={timestamp}`: Get workspaces by tag with booking information
- `GET /v1/workspaces/zones`: Get all workspace zones

## Data Models

### Workspace
Represents a physical workspace in the office:
- ID
- Name
- Utilities (equipment and amenities)
- Zone (location within the office)
- Tag (type of workspace, e.g., "meeting", "desk")

### Workspace Zone
Represents a physical area or section within the office:
- ID
- Name

### Utility
Represents equipment or amenities available in a workspace:
- ID
- Name
- Icon URL
- Count

## Integration
The Workspace module integrates with:
- Booking module for checking workspace availability
- User module for workspace assignments
- Notification module for workspace-related notifications

## Examples

### Retrieving Available Workspaces
1. A client application requests workspaces with a specific tag (e.g., "meeting")
2. The client specifies a time range for availability checking
3. The system returns workspaces matching the tag, including their booking information
4. The client can determine which workspaces are available during the specified time period