-- ============================================================
--  SISTEMA DE GESTIÓN DE FUNCIONARIOS
--  Script 02: Población inicial de tablas
--  Motor: PostgreSQL
-- ============================================================

-- ============================================================
--  TIPOS DE DOCUMENTO
-- ============================================================
INSERT INTO tipo_documento (codigo, nombre) VALUES
    ('CC',   'Cédula de Ciudadanía'),
    ('TI',   'Tarjeta de Identidad'),
    ('CE',   'Cédula de Extranjería'),
    ('PA',   'Pasaporte'),
    ('NIT',  'Número de Identificación Tributaria')
ON CONFLICT (codigo) DO NOTHING;

-- ============================================================
--  DEPARTAMENTOS
-- ============================================================
INSERT INTO departamentos (codigo, nombre) VALUES
    ('ANT', 'Antioquia'),
    ('CUN', 'Cundinamarca'),
    ('VAL', 'Valle del Cauca'),
    ('ATL', 'Atlántico'),
    ('BOL', 'Bolívar'),
    ('SAN', 'Santander'),
    ('NAR', 'Nariño'),
    ('TOL', 'Tolima')
ON CONFLICT (codigo) DO NOTHING;

-- ============================================================
--  MUNICIPIOS
-- ============================================================
INSERT INTO municipios (codigo, nombre, departamento_id) VALUES
    -- Antioquia
    ('05001', 'Medellín',       (SELECT id FROM departamentos WHERE codigo = 'ANT')),
    ('05045', 'Bello',          (SELECT id FROM departamentos WHERE codigo = 'ANT')),
    ('05129', 'Itagüí',         (SELECT id FROM departamentos WHERE codigo = 'ANT')),
    ('05266', 'Envigado',       (SELECT id FROM departamentos WHERE codigo = 'ANT')),
    -- Cundinamarca
    ('11001', 'Bogotá D.C.',    (SELECT id FROM departamentos WHERE codigo = 'CUN')),
    ('25175', 'Chía',           (SELECT id FROM departamentos WHERE codigo = 'CUN')),
    -- Valle del Cauca
    ('76001', 'Cali',           (SELECT id FROM departamentos WHERE codigo = 'VAL')),
    ('76111', 'Buenaventura',   (SELECT id FROM departamentos WHERE codigo = 'VAL')),
    -- Atlántico
    ('08001', 'Barranquilla',   (SELECT id FROM departamentos WHERE codigo = 'ATL')),
    -- Bolívar
    ('13001', 'Cartagena',      (SELECT id FROM departamentos WHERE codigo = 'BOL')),
    -- Santander
    ('68001', 'Bucaramanga',    (SELECT id FROM departamentos WHERE codigo = 'SAN')),
    -- Nariño
    ('52001', 'Pasto',          (SELECT id FROM departamentos WHERE codigo = 'NAR')),
    -- Tolima
    ('73001', 'Ibagué',         (SELECT id FROM departamentos WHERE codigo = 'TOL'))
ON CONFLICT (codigo) DO NOTHING;

-- ============================================================
--  ÁREAS
-- ============================================================
INSERT INTO areas (nombre, descripcion) VALUES
    ('Gerencia General',       'Dirección estratégica y toma de decisiones de alto nivel'),
    ('Recursos Humanos',       'Gestión del talento humano, contratación y bienestar'),
    ('Tecnología e Innovación','Desarrollo de software, infraestructura y transformación digital'),
    ('Finanzas y Contabilidad','Gestión presupuestal, contabilidad y tesorería'),
    ('Jurídica y Cumplimiento','Asesoría legal, contratos y cumplimiento normativo'),
    ('Operaciones',            'Logística, procesos operativos y cadena de valor'),
    ('Comunicaciones',         'Comunicación interna, externa y relaciones públicas')
ON CONFLICT (nombre) DO NOTHING;

-- ============================================================
--  CARGOS
-- ============================================================
INSERT INTO cargos (nombre, nivel_salarial, area_id) VALUES
    -- Gerencia
    ('Gerente General',           'NIVEL_5', (SELECT id FROM areas WHERE nombre = 'Gerencia General')),
    ('Subgerente',                'NIVEL_4', (SELECT id FROM areas WHERE nombre = 'Gerencia General')),
    -- Recursos Humanos
    ('Director de RRHH',          'NIVEL_4', (SELECT id FROM areas WHERE nombre = 'Recursos Humanos')),
    ('Coordinador de RRHH',       'NIVEL_3', (SELECT id FROM areas WHERE nombre = 'Recursos Humanos')),
    ('Analista de Nómina',        'NIVEL_2', (SELECT id FROM areas WHERE nombre = 'Recursos Humanos')),
    ('Auxiliar Administrativo',   'NIVEL_1', (SELECT id FROM areas WHERE nombre = 'Recursos Humanos')),
    -- Tecnología
    ('Director de Tecnología',    'NIVEL_4', (SELECT id FROM areas WHERE nombre = 'Tecnología e Innovación')),
    ('Ingeniero de Software',     'NIVEL_3', (SELECT id FROM areas WHERE nombre = 'Tecnología e Innovación')),
    ('Desarrollador Junior',      'NIVEL_2', (SELECT id FROM areas WHERE nombre = 'Tecnología e Innovación')),
    ('Analista de Datos',         'NIVEL_3', (SELECT id FROM areas WHERE nombre = 'Tecnología e Innovación')),
    -- Finanzas
    ('Director Financiero',       'NIVEL_4', (SELECT id FROM areas WHERE nombre = 'Finanzas y Contabilidad')),
    ('Contador',                  'NIVEL_3', (SELECT id FROM areas WHERE nombre = 'Finanzas y Contabilidad')),
    ('Auxiliar Contable',         'NIVEL_1', (SELECT id FROM areas WHERE nombre = 'Finanzas y Contabilidad')),
    -- Jurídica
    ('Director Jurídico',         'NIVEL_4', (SELECT id FROM areas WHERE nombre = 'Jurídica y Cumplimiento')),
    ('Abogado',                   'NIVEL_3', (SELECT id FROM areas WHERE nombre = 'Jurídica y Cumplimiento')),
    -- Operaciones
    ('Director de Operaciones',   'NIVEL_4', (SELECT id FROM areas WHERE nombre = 'Operaciones')),
    ('Coordinador Logístico',     'NIVEL_3', (SELECT id FROM areas WHERE nombre = 'Operaciones')),
    ('Operador',                  'NIVEL_1', (SELECT id FROM areas WHERE nombre = 'Operaciones'))
ON CONFLICT (nombre, area_id) DO NOTHING;

-- ============================================================
--  FUNCIONARIOS (datos de prueba)
-- ============================================================
INSERT INTO funcionarios (nombres, apellidos, tipo_doc_id, numero_documento,
                          fecha_nacimiento, fecha_ingreso, email, telefono,
                          cargo_id, municipio_id, estado)
VALUES
    ('Carlos Andrés',  'Ramírez Torres',
     (SELECT id FROM tipo_documento WHERE codigo = 'CC'), '1020345678',
     '1985-03-12', '2015-01-10', 'caramirez@entidad.gov.co', '3001234567',
     (SELECT id FROM cargos WHERE nombre = 'Gerente General'),
     (SELECT id FROM municipios WHERE codigo = '05001'), 'ACTIVO'),

    ('Luisa Fernanda', 'Gómez Salcedo',
     (SELECT id FROM tipo_documento WHERE codigo = 'CC'), '1045678901',
     '1990-07-22', '2018-03-15', 'lfgomez@entidad.gov.co', '3109876543',
     (SELECT id FROM cargos WHERE nombre = 'Director de RRHH'),
     (SELECT id FROM municipios WHERE codigo = '05001'), 'ACTIVO'),

    ('Juan David',     'Ospina Ríos',
     (SELECT id FROM tipo_documento WHERE codigo = 'CC'), '1032109876',
     '1993-11-05', '2019-06-01', 'jdospina@entidad.gov.co', '3156789012',
     (SELECT id FROM cargos WHERE nombre = 'Ingeniero de Software'),
     (SELECT id FROM municipios WHERE codigo = '05045'), 'ACTIVO'),

    ('María Camila',   'Herrera Londoño',
     (SELECT id FROM tipo_documento WHERE codigo = 'CC'), '1053210987',
     '1995-04-18', '2020-08-10', 'mcherrera@entidad.gov.co', '3201234567',
     (SELECT id FROM cargos WHERE nombre = 'Analista de Datos'),
     (SELECT id FROM municipios WHERE codigo = '11001'), 'ACTIVO'),

    ('Andrés Felipe',  'Vargas Mejía',
     (SELECT id FROM tipo_documento WHERE codigo = 'CC'), '1067890123',
     '1988-09-30', '2016-02-20', 'afvargas@entidad.gov.co', '3004567890',
     (SELECT id FROM cargos WHERE nombre = 'Contador'),
     (SELECT id FROM municipios WHERE codigo = '76001'), 'ACTIVO'),

    ('Sandra Milena',  'Castillo Ramos',
     (SELECT id FROM tipo_documento WHERE codigo = 'CC'), '1041234567',
     '1992-12-14', '2017-09-05', 'smcastillo@entidad.gov.co', '3123456789',
     (SELECT id FROM cargos WHERE nombre = 'Coordinador de RRHH'),
     (SELECT id FROM municipios WHERE codigo = '08001'), 'ACTIVO'),

    ('Diego Alejandro','Morales Peña',
     (SELECT id FROM tipo_documento WHERE codigo = 'CC'), '1078901234',
     '1987-06-08', '2014-11-15', 'damorales@entidad.gov.co', '3187654321',
     (SELECT id FROM cargos WHERE nombre = 'Director de Tecnología'),
     (SELECT id FROM municipios WHERE codigo = '05001'), 'ACTIVO'),

    ('Laura Cristina', 'Martínez López',
     (SELECT id FROM tipo_documento WHERE codigo = 'CC'), '1015678901',
     '1996-02-25', '2021-03-01', 'lcmartinez@entidad.gov.co', '3212345678',
     (SELECT id FROM cargos WHERE nombre = 'Desarrollador Junior'),
     (SELECT id FROM municipios WHERE codigo = '05129'), 'ACTIVO'),

    ('Roberto Carlos', 'Jiménez Soto',
     (SELECT id FROM tipo_documento WHERE codigo = 'CC'), '1089012345',
     '1980-10-03', '2010-05-20', 'rcjimenez@entidad.gov.co', '3009871234',
     (SELECT id FROM cargos WHERE nombre = 'Director Jurídico'),
     (SELECT id FROM municipios WHERE codigo = '13001'), 'ACTIVO'),

    ('Natalia Andrea',  'Cruz Pardo',
     (SELECT id FROM tipo_documento WHERE codigo = 'CC'), '1026789012',
     '1994-08-17', '2022-01-10', 'nacruz@entidad.gov.co', '3145678901',
     (SELECT id FROM cargos WHERE nombre = 'Auxiliar Administrativo'),
     (SELECT id FROM municipios WHERE codigo = '68001'), 'INACTIVO')
ON CONFLICT (numero_documento) DO NOTHING;

-- ============================================================
--  VERIFICACIÓN
-- ============================================================
SELECT 'tipo_documento' AS tabla, COUNT(*) AS registros FROM tipo_documento
UNION ALL SELECT 'departamentos',  COUNT(*) FROM departamentos
UNION ALL SELECT 'municipios',     COUNT(*) FROM municipios
UNION ALL SELECT 'areas',          COUNT(*) FROM areas
UNION ALL SELECT 'cargos',         COUNT(*) FROM cargos
UNION ALL SELECT 'funcionarios',   COUNT(*) FROM funcionarios;
