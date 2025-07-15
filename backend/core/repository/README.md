# Repository Module

## Overview
The Repository Module provides database access and persistence functionality for the Effective Office backend. It manages database connections, migrations, and implements data access patterns for storing and retrieving domain entities.

## Features
- Database configuration and connection management
- Database schema definition and migrations
- Data access layer for domain entities

## Architecture
The module is organized into configuration and database migration resources:

```
repository/
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── band/
│       │       └── effective/
│       │           └── office/
│       │               └── backend/
│       │                   └── core/
│       │                       └── repository/
│       │                           └── config/
│       │                               └── DatabaseConfig.kt  # Database configuration
│       └── resources/
│           ├── application-repository.yml  # Repository-specific configuration
│           └── db/
│               └── migration/
│                   └── V1__create_tables.sql  # Database schema migration
```

## Key Components

### Configuration
- **DatabaseConfig**: Spring configuration class that enables JPA repositories and transaction management

### Database Schema
The module defines the following database tables:
- **workspace_zones**: Stores workspace zone information
- **users**: Stores user information
- **workspaces**: Stores workspace information
- **utilities**: Stores utilities information
- **workspace_utilities**: Links workspaces and utilities
- **api_keys**: Stores API keys for authentication
- **calendar_ids**: Stores calendar IDs linked to workspaces
- **calendar_channels**: Stores Google Calendar notification channels

## Database Migrations
The module uses Flyway for database migrations:
- **V1__create_tables.sql**: Creates the initial database schema with all necessary tables, indexes, and constraints

### How to Implement Migrations

#### Migration Naming Convention
Flyway uses a specific naming convention for migration files:
```
V{version}__{description}.sql
```
- `{version}`: A version number (e.g., 1, 2, 3.1, 4.5.7)
- `{description}`: A description with words separated by underscores

Examples:
- `V1__create_tables.sql`
- `V2__add_user_preferences.sql`
- `V3__alter_workspace_table.sql`

#### Creating a New Migration

1. **Determine the next version number**: Look at the existing migrations in `src/main/resources/db/migration/` and increment the highest version number.

2. **Create a new SQL file** in the `src/main/resources/db/migration/` directory following the naming convention.

3. **Write your SQL statements** in the file. Include:
   - Clear comments explaining the purpose of each change
   - Table creation statements with appropriate constraints
   - Index creation for frequently queried columns
   - Comments on tables and columns for documentation
   - Any necessary data migrations

4. **Test your migration** by running the application with the new migration file in place.

#### Best Practices

1. **Make migrations idempotent** when possible (can be run multiple times without causing errors)
2. **Use explicit naming** for constraints, indexes, and foreign keys
3. **Include comments** in your SQL to explain complex changes
4. **Keep migrations focused** on a specific change or related set of changes
5. **Never modify existing migration files** that have been committed to version control
6. **Use transactions** for complex migrations to ensure atomicity

#### Example Migration

```sql
-- Add user preferences table
CREATE TABLE user_preferences
(
    user_id       UUID PRIMARY KEY REFERENCES users (id),
    theme         VARCHAR(50) NOT NULL DEFAULT 'light',
    notifications BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add index for common queries
CREATE INDEX idx_user_preferences_theme ON user_preferences (theme);

-- Add comment to table
COMMENT ON TABLE user_preferences IS 'Table storing user preference settings';
```

#### Flyway Configuration

The Flyway configuration is defined in `application-repository.yml`:
```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    table: flyway_schema_history
```

## Integration
The Repository Module is used by:
- Service implementations that need to persist or retrieve data
- Application configuration for database setup
- Other modules that need data access capabilities

## Configuration
The module includes repository-specific configuration in `application-repository.yml`, which can be customized for different environments.

## Usage Examples

### Implementing a Repository
```kotlin
@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByUsername(username: String): UserEntity?
    fun findByEmail(email: String): UserEntity?
}

// Using the repository in a service implementation
@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)?.toDomain()
    }
}
```

## Development
When extending this module:
1. Use Flyway migrations for database schema changes
2. Follow naming conventions for database objects
3. Add appropriate indexes for frequently queried columns
4. Document table structures and relationships
5. Ensure proper transaction management
