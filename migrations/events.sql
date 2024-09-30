CREATE TABLE events (
    id UUID PRIMARY KEY,                     -- ID único para cada evento
    aggregate_id UUID NOT NULL,              -- ID del agregado (entidad)
    -- aggregate_type VARCHAR(255) NOT NULL,    -- Tipo de agregado (nombre de la entidad)
    event_type VARCHAR(255) NOT NULL,        -- Tipo del evento (nombre del evento)
    event_data JSONB NOT NULL,               -- Datos del evento en formato JSONB
    version INT NOT NULL,                    -- Versión del agregado después de aplicar el evento
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Fecha de creación del evento
    metadata JSONB,                          -- Metadatos adicionales en formato JSONB
    UNIQUE (aggregate_id, version)           -- Garantiza que cada versión de un agregado es única
);
