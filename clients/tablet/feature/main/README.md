# Main Feature Module

## Overview
The Main feature module serves as the primary entry point and navigation hub for the Effective Office tablet application. It provides the home screen, main navigation components, and orchestrates the flow between different features of the application.

## Features
- Home screen with dashboard
- Main navigation system
- Resource overview and status
- Quick access to frequently used features
- User profile and authentication
- Notifications center
- Application-wide search
- System status indicators

## Architecture
The module follows a feature-based architecture:

```
main/
├── components/      # Feature-specific UI components
├── di/              # Dependency injection setup
├── domain/          # Domain models and business logic
└── presentation/    # UI screens and components
```

## Key Components

### Components
- **UI Components**: Reusable UI components specific to the main feature
- **Presentation Components**: Screen implementations and UI logic
- **Domain Models**: Business logic and data models

### Architecture Components
- **Dependency Injection**: Module setup and dependency provision
- **Navigation**: Screen navigation and routing

### Features
- **Home Dashboard**: Main entry point with overview and quick actions
- **Navigation System**: System for navigating between different app features
- **User Profile**: User information and settings

## Integration
The Main module integrates with:
- Core domain module for business logic
- Core data module for data operations
- Core UI module for shared UI components
- All feature modules for navigation and coordination

## Known issues

### The error screen never clears once the first load has failed

If the very first workspaces request fails, the screen keeps showing **that** error for as long as
the process lives — even though the once-a-minute refresh starts succeeding straight afterwards and
the data is arriving the whole time. Only killing and relaunching the app clears it.

Seen on 2026-08-06 on the Aurora emulator, though nothing about it is Aurora-specific. The app was
started while the backend was down; its log then read:

```
… Connection failed for request … Reason: Could not connect to server     ← ×4
RESPONSE: 200 OK                                                          ← ×6, to the end of the run
```

and the screen was still showing the error from a request older than all four failures
(`with_bookings_until=1787254488319` against the first logged failure's `…607233`). So it is not the
latest failure being displayed — it is the first one, latched.

**Why it is worth fixing.** These tablets hang on meeting-room walls. A backend blip while one is
rebooting leaves it showing an error until somebody walks up and restarts the app, with correct data
sitting one layer below the whole time.

**Where to look.** Refreshing goes two ways and neither clears the error: `PeriodicRoomRefreshUseCase`
calls `RefreshDataUseCase` on a timer and writes into the local repository's buffer, which this
screen already subscribes to; `UpdateUseCase` only asks the screen to reload, and that reload is
served from cache. The latched state appears to live in a third place — start from this module's
ViewModel and the repository layer in `core/data`.

**Reproducing it** does not need Aurora: start the app on the Android emulator with the backend
stopped, confirm the error screen, then start the backend (`localQuickStart/run-backend-local.sh`)
and watch the screen not recover.

## User Flows

### Application Startup
1. User launches the application
2. System checks authentication status
3. If authenticated, user is directed to the home screen
4. If not authenticated, user is directed to the login screen

### Feature Navigation
1. User selects a feature from the navigation menu
2. System navigates to the selected feature
3. When user completes actions in the feature, they can return to the home screen

## State Management
The Main module manages several application-wide states:
- Authentication state
- Navigation state
- Notification state
- Global search state

## Development
### Adding a New Navigation Destination
To add a new destination to the main navigation:
1. Update the navigation components in the presentation directory
2. Add any necessary UI components to the components directory
3. Update dependency injection in the di directory if needed
4. Ensure proper integration with other features

### Modifying the Dashboard
To modify the home screen dashboard:
1. Update the relevant UI components in the components directory
2. Modify the presentation logic in the presentation directory
3. Update any domain models if needed
4. Ensure the UI is responsive and follows the design guidelines
