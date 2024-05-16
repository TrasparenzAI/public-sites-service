ALTER TABLE addresses ALTER COLUMN osm_id TYPE TEXT;
ALTER TABLE addresses RENAME COLUMN osm_id TO external_id;
ALTER TABLE addresses RENAME COLUMN osm_type TO external_type;

ALTER TABLE addresses ADD COLUMN geolocalized_by TEXT;
UPDATE addresses SET geolocalized_by = 'Nominatim';