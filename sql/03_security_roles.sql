-- ============================================================
--  Script 03: Seguridad – contraseña y rol en funcionarios
--  + corrección de constraint mal nombrada (Bug #1)
-- ============================================================

-- Bug #1: eliminar la constraint con nombre y condición incorrectos
ALTER TABLE funcionarios
    DROP CONSTRAINT IF EXISTS chk_nivel_salarial;

-- Enum de roles
DO $$ BEGIN
    CREATE TYPE rol_usuario AS ENUM ('ADMINISTRADOR', 'DOCENTE');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

-- Nuevos campos: contraseña encriptada y rol
ALTER TABLE funcionarios
    ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255),
    ADD COLUMN IF NOT EXISTS rol           rol_usuario NOT NULL DEFAULT 'DOCENTE';

-- Constraint correcta para estado
ALTER TABLE funcionarios
    ADD CONSTRAINT chk_estado_valor
        CHECK (estado IN ('ACTIVO', 'INACTIVO'));

-- Índice para búsqueda de login por email (ya existe UNIQUE, pero lo nombramos)
CREATE INDEX IF NOT EXISTS idx_func_email_login ON funcionarios(email);

-- Actualizar los 10 funcionarios de prueba con contraseña BCrypt de 'Admin123*'
-- Hash BCrypt de 'Admin123*' con cost=12
UPDATE funcionarios SET
    password_hash = '$2a$12$ZVhHfQ4YyznHjohYA14qNe8fHXpCvwJIIlT3A6gHe.Np9fBD2ldPC',
    rol = 'ADMINISTRADOR'
WHERE numero_documento = '1020345678';  -- Gerente General

UPDATE funcionarios SET
    password_hash = '$2a$12$ZVhHfQ4YyznHjohYA14qNe8fHXpCvwJIIlT3A6gHe.Np9fBD2ldPC',
    rol = 'ADMINISTRADOR'
WHERE numero_documento IN ('1045678901','1089012345','1067890123','1078901234');

UPDATE funcionarios SET
    password_hash = '$2a$12$ZVhHfQ4YyznHjohYA14qNe8fHXpCvwJIIlT3A6gHe.Np9fBD2ldPC',
    rol = 'DOCENTE'
WHERE numero_documento IN ('1032109876','1053210987','1041234567','1015678901',
                           '1026789012');
