-- Add new columns to users table
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(50) NOT NULL DEFAULT 'USER',
    ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(255) DEFAULT '',
    ADD COLUMN IF NOT EXISTS tag VARCHAR(50) DEFAULT '';

-- Add index for common queries
CREATE INDEX IF NOT EXISTS idx_users_role ON users (role);
CREATE INDEX IF NOT EXISTS idx_users_tag ON users (tag);

-- Add comment to columns
COMMENT ON COLUMN users.role IS 'User role (e.g., USER, ADMIN)';
COMMENT ON COLUMN users.avatar_url IS 'URL to user avatar image';
COMMENT ON COLUMN users.tag IS 'User tag for categorization';