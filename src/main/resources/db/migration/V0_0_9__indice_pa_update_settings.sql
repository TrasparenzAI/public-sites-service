CREATE TABLE indice_pa_update_settings (
    id BIGSERIAL PRIMARY KEY,
    version INT NOT NULL DEFAULT 0,
    locked BOOLEAN NOT NULL DEFAULT FALSE,
    last_update TIMESTAMP
);

INSERT INTO indice_pa_update_settings (locked) VALUES (FALSE);
