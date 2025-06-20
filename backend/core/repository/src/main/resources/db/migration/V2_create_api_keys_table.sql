CREATE TABLE api_keys
(
    id          UUID PRIMARY KEY,
    key_value   VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    user_id     UUID REFERENCES users (id)
);

-- Add index for common queries
CREATE INDEX idx_api_keys_key_value ON api_keys (key_value);

-- Add comment to table
COMMENT
ON TABLE api_keys IS 'Table storing API keys for authentication';