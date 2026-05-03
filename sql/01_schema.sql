-- ============================================================
--  SISTEMA DE GESTIÓN DE FUNCIONARIOS
--  Script 01: Creación del esquema de base de datos
--  Motor: PostgreSQL
--  Autor: Cristian España
-- ============================================================

-- Crear base de datos (ejecutar como superusuario si es necesario)
-- CREATE DATABASE gestion_funcionarios;
-- \c gestion_funcionarios;

-- ============================================================
--  TABLA: tipo_documento
--  Catálogo de tipos de documento de identidad
-- ============================================================
CREATE TABLE IF NOT EXISTS tipo_documento (
    id          SERIAL PRIMARY KEY,
    codigo      VARCHAR(10)  NOT NULL UNIQUE,
    nombre      VARCHAR(80)  NOT NULL,
    activo      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
--  TABLA: departamentos
--  División política del país
-- ============================================================
CREATE TABLE IF NOT EXISTS departamentos (
    id          SERIAL PRIMARY KEY,
    codigo      VARCHAR(5)   NOT NULL UNIQUE,
    nombre      VARCHAR(100) NOT NULL,
    activo      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
--  TABLA: municipios
--  Municipios/ciudades asociados a un departamento
-- ============================================================
CREATE TABLE IF NOT EXISTS municipios (
    id               SERIAL PRIMARY KEY,
    codigo           VARCHAR(10)  NOT NULL UNIQUE,
    nombre           VARCHAR(100) NOT NULL,
    departamento_id  INTEGER      NOT NULL,
    activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_municipio_depto FOREIGN KEY (departamento_id)
        REFERENCES departamentos(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ============================================================
--  TABLA: areas
--  Áreas o dependencias internas de la entidad
-- ============================================================
CREATE TABLE IF NOT EXISTS areas (
    id           SERIAL PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL UNIQUE,
    descripcion  TEXT,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
--  TABLA: cargos
--  Cargos disponibles por área
-- ============================================================
CREATE TABLE IF NOT EXISTS cargos (
    id             SERIAL PRIMARY KEY,
    nombre         VARCHAR(100)     NOT NULL,
    nivel_salarial VARCHAR(20)      NOT NULL,  -- NIVEL_1 … NIVEL_5
    area_id        INTEGER          NOT NULL,
    activo         BOOLEAN          NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cargo_area FOREIGN KEY (area_id)
        REFERENCES areas(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uq_cargo_area UNIQUE (nombre, area_id)
);

-- ============================================================
--  TABLA: funcionarios  (tabla principal del CRUD)
--  Información completa de cada funcionario
-- ============================================================
CREATE TABLE IF NOT EXISTS funcionarios (
    id               SERIAL PRIMARY KEY,
    nombres          VARCHAR(100) NOT NULL,
    apellidos        VARCHAR(100) NOT NULL,
    tipo_doc_id      INTEGER      NOT NULL,
    numero_documento VARCHAR(20)  NOT NULL UNIQUE,
    fecha_nacimiento DATE         NOT NULL,
    fecha_ingreso    DATE         NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    telefono         VARCHAR(20),
    cargo_id         INTEGER      NOT NULL,
    municipio_id     INTEGER      NOT NULL,
    estado           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',  -- ACTIVO / INACTIVO
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_func_tipo_doc   FOREIGN KEY (tipo_doc_id)  REFERENCES tipo_documento(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_func_cargo      FOREIGN KEY (cargo_id)     REFERENCES cargos(id)         ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_func_municipio  FOREIGN KEY (municipio_id) REFERENCES municipios(id)     ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_estado         CHECK (estado IN ('ACTIVO','INACTIVO')),
    CONSTRAINT chk_nivel_salarial CHECK (estado IS NOT NULL)
);

-- Índices de búsqueda frecuente
CREATE INDEX IF NOT EXISTS idx_func_numero_doc   ON funcionarios(numero_documento);
CREATE INDEX IF NOT EXISTS idx_func_email        ON funcionarios(email);
CREATE INDEX IF NOT EXISTS idx_func_cargo        ON funcionarios(cargo_id);
CREATE INDEX IF NOT EXISTS idx_func_estado       ON funcionarios(estado);
CREATE INDEX IF NOT EXISTS idx_municipio_depto   ON municipios(departamento_id);
CREATE INDEX IF NOT EXISTS idx_cargo_area        ON cargos(area_id);

-- ============================================================
--  FUNCIÓN: actualizar updated_at automáticamente
-- ============================================================
CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_func_updated_at
    BEFORE UPDATE ON funcionarios
    FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();
