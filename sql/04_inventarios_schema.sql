-- ============================================================
--  Script 04: Módulo de Inventarios
--  Tablas: estados_equipo, marcas, tipos_equipo, inventarios
-- ============================================================

CREATE TABLE IF NOT EXISTS estados_equipo (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL UNIQUE,
    descripcion TEXT,
    activo      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS marcas (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(80)  NOT NULL UNIQUE,
    descripcion TEXT,
    activo      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tipos_equipo (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(80)  NOT NULL UNIQUE,
    descripcion TEXT,
    activo      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inventarios (
    id              SERIAL PRIMARY KEY,
    codigo          VARCHAR(30)  NOT NULL UNIQUE,
    nombre          VARCHAR(150) NOT NULL,
    descripcion     TEXT,
    estado_id       INTEGER      NOT NULL,
    marca_id        INTEGER      NOT NULL,
    tipo_id         INTEGER      NOT NULL,
    funcionario_id  INTEGER,
    fecha_registro  DATE         NOT NULL DEFAULT CURRENT_DATE,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_estado      FOREIGN KEY (estado_id)      REFERENCES estados_equipo(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_inv_marca       FOREIGN KEY (marca_id)       REFERENCES marcas(id)         ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_inv_tipo        FOREIGN KEY (tipo_id)        REFERENCES tipos_equipo(id)   ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_inv_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id)   ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_inv_estado     ON inventarios(estado_id);
CREATE INDEX IF NOT EXISTS idx_inv_marca      ON inventarios(marca_id);
CREATE INDEX IF NOT EXISTS idx_inv_tipo       ON inventarios(tipo_id);
CREATE INDEX IF NOT EXISTS idx_inv_funcionario ON inventarios(funcionario_id);
CREATE INDEX IF NOT EXISTS idx_inv_activo     ON inventarios(activo);

-- Trigger updated_at para inventarios
CREATE TRIGGER trg_inv_updated_at
    BEFORE UPDATE ON inventarios
    FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();
