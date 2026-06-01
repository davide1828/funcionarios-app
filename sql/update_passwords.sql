-- Script para actualizar contraseñas de funcionarios de prueba
-- Contraseña: Admin123*
-- Hash BCrypt (cost=12): $2a$12$ZVhHfQ4YyznHjohYA14qNe8fHXpCvwJIIlT3A6gHe.Np9fBD2ldPC

-- Actualizar todos los funcionarios con la contraseña de prueba
UPDATE funcionarios SET
    password_hash = '$2a$12$ZVhHfQ4YyznHjohYA14qNe8fHXpCvwJIIlT3A6gHe.Np9fBD2ldPC'
WHERE password_hash IS NULL;

-- Asignar rol ADMINISTRADOR a Carlos Ramírez (Gerente General)
UPDATE funcionarios SET
    rol = 'ADMINISTRADOR'
WHERE email = 'caramirez@entidad.gov.co';

-- Asignar rol ADMINISTRADOR a otros líderes
UPDATE funcionarios SET
    rol = 'ADMINISTRADOR'
WHERE email IN ('lfgomez@entidad.gov.co', 'damorales@entidad.gov.co', 'afvargas@entidad.gov.co');

-- Verificar que se actualizaron correctamente
SELECT email, password_hash IS NOT NULL as tiene_contraseña, rol FROM funcionarios;
