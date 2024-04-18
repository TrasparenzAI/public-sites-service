CREATE TABLE IF NOT EXISTS municipalities (
    id BIGSERIAL PRIMARY KEY,
    codice_regione TEXT,
    denominazione TEXT NOT NULL,
    denominazione_altra_lingua TEXT,
    codice_ripartizione_geografica TEXT,
    ripartizione_geografica TEXT,
    denominazione_regione TEXT,
    denominazione_unita_sovracomunale TEXT,
    capoluogo BOOLEAN,
    sigla_automobilistica TEXT,
    codice_comune TEXT,
    codice_catastale TEXT NOT NULL,
    data_cancellazione DATE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    version INT DEFAULT 0);

ALTER TABLE companies ADD COLUMN municipality_id INT REFERENCES municipalities(id);
CREATE INDEX companies_municipality_id_idx ON companies(municipality_id);