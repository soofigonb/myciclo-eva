CREATE TABLE IF NOT EXISTS inicios_menstruales (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    CONSTRAINT uq_inicio_menstrual_usuario_fecha UNIQUE (usuario_id, fecha)
);

CREATE TABLE IF NOT EXISTS bitacoras (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    patron VARCHAR(100),
    sintomas TEXT,
    estado_animo VARCHAR(100),
    flujo_menstrual VARCHAR(100),
    flujo_vaginal VARCHAR(100),
    notas TEXT,
    CONSTRAINT uq_bitacora_usuario_fecha UNIQUE (usuario_id, fecha)
);

CREATE INDEX IF NOT EXISTS idx_inicios_menstruales_usuario ON inicios_menstruales(usuario_id);
CREATE INDEX IF NOT EXISTS idx_bitacoras_usuario ON bitacoras(usuario_id);
