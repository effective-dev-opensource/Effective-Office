-- Update default values for user fields to ensure they are empty strings
ALTER TABLE users
    ALTER COLUMN role SET DEFAULT '',
    ALTER COLUMN avatar_url SET DEFAULT '',
    ALTER COLUMN tag SET DEFAULT '';

-- Update any existing NULL values to empty strings
UPDATE users
SET role = ''
WHERE role IS NULL;

UPDATE users
SET avatar_url = ''
WHERE avatar_url IS NULL;

UPDATE users
SET tag = ''
WHERE tag IS NULL;

-- Add comment to explain the change
COMMENT ON COLUMN users.role IS 'User role with empty string default';
COMMENT ON COLUMN users.avatar_url IS 'URL to user avatar image with empty string default';
COMMENT ON COLUMN users.tag IS 'User tag for categorization with empty string default';