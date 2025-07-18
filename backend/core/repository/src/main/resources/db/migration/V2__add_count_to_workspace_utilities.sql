-- Add count column to workspace_utilities table
ALTER TABLE workspace_utilities
ADD COLUMN count INTEGER NOT NULL DEFAULT 1;

-- Add comment to count column
COMMENT ON COLUMN workspace_utilities.count IS 'Number of this utility in the workspace';