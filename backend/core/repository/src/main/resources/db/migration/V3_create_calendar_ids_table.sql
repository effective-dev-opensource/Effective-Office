CREATE TABLE calendar_ids
(
    id           UUID PRIMARY KEY,
    workspace_id UUID REFERENCES workspaces (id) NOT NULL UNIQUE,
    calendar_id  VARCHAR(255) NOT NULL UNIQUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add index for common queries
CREATE INDEX idx_calendar_ids_workspace_id ON calendar_ids (workspace_id);
CREATE INDEX idx_calendar_ids_calendar_id ON calendar_ids (calendar_id);

-- Add comment to table
COMMENT
ON TABLE calendar_ids IS 'Table storing calendar IDs linked to workspaces';