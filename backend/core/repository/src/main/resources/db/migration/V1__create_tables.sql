-- Create workspace_zones table first to fix reference issue
CREATE TABLE workspace_zones
(
    id   UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Create users table with all fields from migrations
CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    username   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    role       VARCHAR(50)  NOT NULL DEFAULT '',
    avatar_url VARCHAR(255) DEFAULT '',
    tag        VARCHAR(50)  DEFAULT ''
);

-- Add index for common queries on users
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_active ON users (active);
CREATE INDEX idx_users_role ON users (role);
CREATE INDEX idx_users_tag ON users (tag);

-- Add comment to users table and columns
COMMENT ON TABLE users IS 'Table storing user information';
COMMENT ON COLUMN users.role IS 'User role with empty string default';
COMMENT ON COLUMN users.avatar_url IS 'URL to user avatar image with empty string default';
COMMENT ON COLUMN users.tag IS 'User tag for categorization with empty string default';

-- Create workspaces table
CREATE TABLE workspaces
(
    id      UUID PRIMARY KEY,
    name    VARCHAR(255) NOT NULL UNIQUE,
    tag     VARCHAR(255) NOT NULL,
    zone_id UUID REFERENCES workspace_zones (id)
);

-- Create utilities table
CREATE TABLE utilities
(
    id       UUID PRIMARY KEY,
    name     VARCHAR(255) NOT NULL UNIQUE,
    icon_url VARCHAR(255) NOT NULL UNIQUE
);

-- Create workspace_utilities table with count column
CREATE TABLE workspace_utilities
(
    workspace_id UUID REFERENCES workspaces (id),
    utility_id   UUID REFERENCES utilities (id),
    count        INTEGER NOT NULL DEFAULT 1,
    PRIMARY KEY (workspace_id, utility_id)
);

COMMENT ON TABLE workspace_utilities IS 'Table linking workspaces to utilities with their quantities';
COMMENT ON COLUMN workspace_utilities.count IS 'Number of utility items associated with the workspace';

-- Create api_keys table from V2
CREATE TABLE api_keys
(
    id          UUID PRIMARY KEY,
    key_value   VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    user_id     UUID REFERENCES users (id)
);

-- Add index for common queries on api_keys
CREATE INDEX idx_api_keys_key_value ON api_keys (key_value);

-- Add comment to api_keys table
COMMENT ON TABLE api_keys IS 'Table storing API keys for authentication';

-- Create calendar_ids table from V3
CREATE TABLE calendar_ids
(
    id           UUID PRIMARY KEY,
    workspace_id UUID REFERENCES workspaces (id) NOT NULL UNIQUE,
    calendar_id  VARCHAR(255) NOT NULL UNIQUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add index for common queries on calendar_ids
CREATE INDEX idx_calendar_ids_workspace_id ON calendar_ids (workspace_id);
CREATE INDEX idx_calendar_ids_calendar_id ON calendar_ids (calendar_id);

-- Add comment to calendar_ids table
COMMENT ON TABLE calendar_ids IS 'Table storing calendar IDs linked to workspaces';

-- Create calendar_channels table from ChannelEntity
CREATE TABLE calendar_channels
(
    calendar_id  VARCHAR(255) PRIMARY KEY,
    channel_id   VARCHAR(255) NOT NULL,
    resource_id  VARCHAR(255) NOT NULL,
    type         VARCHAR(255),
    address      VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add index for common queries on calendar_channels
CREATE INDEX idx_calendar_channels_channel_id ON calendar_channels (channel_id);
CREATE INDEX idx_calendar_channels_resource_id ON calendar_channels (resource_id);

-- Add comment to calendar_channels table
COMMENT ON TABLE calendar_channels IS 'Table storing Google Calendar notification channels';
