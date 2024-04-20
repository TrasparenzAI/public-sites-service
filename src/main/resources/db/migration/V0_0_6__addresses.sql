CREATE TABLE IF NOT EXISTS addresses (
    id BIGSERIAL PRIMARY KEY,
    address_type TEXT,
    category TEXT,
    name TEXT,
    display_name TEXT,
    latitude TEXT,
    longitude TEXT,
    osm_id BIGINT,
    osm_type TEXT,
    osm_address_type TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    version INT DEFAULT 0);

ALTER TABLE companies ADD COLUMN IF NOT EXISTS address_id INT REFERENCES addresses(id);
CREATE INDEX IF NOT EXISTS companies_address_id_idx ON companies(address_id);