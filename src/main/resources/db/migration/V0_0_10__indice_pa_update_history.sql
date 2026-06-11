CREATE TABLE indice_pa_update_history (
    id BIGSERIAL PRIMARY KEY,
    version INT NOT NULL DEFAULT 0,
    update_date TIMESTAMP NOT NULL,
    updated_from DATE,
    total_indice_pa_companies INT NOT NULL,
    processed_companies INT NOT NULL,
    total_active_companies INT NOT NULL,
    total_visible_companies INT NOT NULL,
    inserted_companies INT NOT NULL,
    modified_companies INT NOT NULL,
    deleted_companies INT NOT NULL
);

CREATE INDEX indice_pa_update_history_update_date_idx
    ON indice_pa_update_history(update_date DESC);
