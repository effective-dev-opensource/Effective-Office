-- Creates the table for storing device information
CREATE TABLE devices
(
    -- Primary key
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    -- Unique Android device ID
    device_id  VARCHAR(255) NOT NULL UNIQUE,
    -- Tag for device identification (e.g., meeting room name)
    tag        VARCHAR(255) NOT NULL,
    -- Creation timestamp of the record
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster lookup by device_id
CREATE INDEX idx_devices_device_id ON devices (device_id);

-- Index for faster lookup by tag
CREATE INDEX idx_devices_tag ON devices (tag);

-- Comments for table and columns
COMMENT ON TABLE devices IS 'Table for storing information about devices';
COMMENT ON COLUMN devices.device_id IS 'Unique Android device ID';
COMMENT ON COLUMN devices.tag IS 'Tag for device identification';