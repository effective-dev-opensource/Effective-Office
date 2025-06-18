CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    username   VARCHAR(255)  NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Add index for common queries
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_active ON users (active);

-- Add comment to table
COMMENT
ON TABLE users IS 'Table storing user information';


CREATE TABLE workspaces
(
    id      UUID PRIMARY KEY,
    name    VARCHAR(255) NOT NULL UNIQUE,
    tag     VARCHAR(255) NOT NULL UNIQUE,
    zone_id UUID REFERENCES workspace_zones (id)
);

CREATE TABLE utilities
(
    id       UUID PRIMARY KEY,
    name     VARCHAR(255) NOT NULL UNIQUE,
    icon_url VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE workspace_zones
(
    id   UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE workspace_utilities
(
    workspace_id UUID REFERENCES workspaces (id),
    utility_id   UUID REFERENCES utilities (id),
    PRIMARY KEY (workspace_id, utility_id)
);