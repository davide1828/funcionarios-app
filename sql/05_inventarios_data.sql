-- ============================================================
--  Script 05: Datos iniciales – módulo de inventarios
-- ============================================================

INSERT INTO estados_equipo (nombre, descripcion) VALUES
    ('Disponible',    'Equipo en buen estado, listo para asignar'),
    ('En uso',        'Equipo asignado a un funcionario activo'),
    ('En reparación', 'Equipo fuera de servicio temporalmente'),
    ('Dado de baja',  'Equipo retirado definitivamente del inventario'),
    ('En revisión',   'Equipo en proceso de mantenimiento preventivo')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO marcas (nombre, descripcion) VALUES
    ('Dell',      'Computadores y periféricos Dell Technologies'),
    ('HP',        'Hewlett-Packard – equipos de cómputo y oficina'),
    ('Lenovo',    'Equipos ThinkPad, IdeaPad y servidores Lenovo'),
    ('Apple',     'MacBook, iPad y accesorios Apple'),
    ('Samsung',   'Monitores, tablets y teléfonos Samsung'),
    ('Epson',     'Impresoras y escáneres Epson'),
    ('Logitech',  'Periféricos y accesorios Logitech'),
    ('Cisco',     'Equipos de red y telecomunicaciones Cisco')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO tipos_equipo (nombre, descripcion) VALUES
    ('Computador de escritorio', 'PC de torre o all-in-one'),
    ('Portátil',                 'Laptop o notebook'),
    ('Tablet',                   'Dispositivo táctil portátil'),
    ('Monitor',                  'Pantalla de visualización'),
    ('Impresora',                'Equipo de impresión'),
    ('Teléfono IP',              'Terminal de telefonía sobre IP'),
    ('Switch de red',            'Concentrador de red LAN'),
    ('Escáner',                  'Digitalizador de documentos'),
    ('UPS',                      'Sistema de alimentación ininterrumpida'),
    ('Proyector',                'Equipo de proyección audiovisual')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO inventarios (codigo, nombre, descripcion, estado_id, marca_id, tipo_id, funcionario_id, fecha_registro) VALUES
    ('EQ-001','Dell OptiPlex 7090','PC escritorio Core i7, 16GB RAM, 512GB SSD',
     (SELECT id FROM estados_equipo WHERE nombre='En uso'),
     (SELECT id FROM marcas WHERE nombre='Dell'),
     (SELECT id FROM tipos_equipo WHERE nombre='Computador de escritorio'),
     (SELECT id FROM funcionarios WHERE numero_documento='1032109876'), '2023-01-15'),

    ('EQ-002','HP EliteBook 840 G8','Portátil Core i5, 8GB RAM, 256GB SSD',
     (SELECT id FROM estados_equipo WHERE nombre='En uso'),
     (SELECT id FROM marcas WHERE nombre='HP'),
     (SELECT id FROM tipos_equipo WHERE nombre='Portátil'),
     (SELECT id FROM funcionarios WHERE numero_documento='1053210987'), '2023-02-20'),

    ('EQ-003','Lenovo ThinkPad X1 Carbon','Portátil ultradelgado Core i7, 16GB, 512GB',
     (SELECT id FROM estados_equipo WHERE nombre='Disponible'),
     (SELECT id FROM marcas WHERE nombre='Lenovo'),
     (SELECT id FROM tipos_equipo WHERE nombre='Portátil'),
     NULL, '2023-03-10'),

    ('EQ-004','Samsung Monitor 27"','Monitor Full HD, panel IPS, 75Hz',
     (SELECT id FROM estados_equipo WHERE nombre='En uso'),
     (SELECT id FROM marcas WHERE nombre='Samsung'),
     (SELECT id FROM tipos_equipo WHERE nombre='Monitor'),
     (SELECT id FROM funcionarios WHERE numero_documento='1020345678'), '2022-11-05'),

    ('EQ-005','Epson EcoTank L3210','Impresora multifuncional tinta continua',
     (SELECT id FROM estados_equipo WHERE nombre='En reparación'),
     (SELECT id FROM marcas WHERE nombre='Epson'),
     (SELECT id FROM tipos_equipo WHERE nombre='Impresora'),
     NULL, '2022-08-12'),

    ('EQ-006','Cisco Switch 24 puertos','Switch administrable Catalyst 2960',
     (SELECT id FROM estados_equipo WHERE nombre='En uso'),
     (SELECT id FROM marcas WHERE nombre='Cisco'),
     (SELECT id FROM tipos_equipo WHERE nombre='Switch de red'),
     NULL, '2021-05-30'),

    ('EQ-007','Apple MacBook Pro 14"','M3 Pro, 18GB RAM, 512GB SSD',
     (SELECT id FROM estados_equipo WHERE nombre='En uso'),
     (SELECT id FROM marcas WHERE nombre='Apple'),
     (SELECT id FROM tipos_equipo WHERE nombre='Portátil'),
     (SELECT id FROM funcionarios WHERE numero_documento='1078901234'), '2024-01-08'),

    ('EQ-008','Proyector Epson PowerLite','Full HD, 3600 lúmenes, WiFi',
     (SELECT id FROM estados_equipo WHERE nombre='Disponible'),
     (SELECT id FROM marcas WHERE nombre='Epson'),
     (SELECT id FROM tipos_equipo WHERE nombre='Proyector'),
     NULL, '2023-06-18')
ON CONFLICT (codigo) DO NOTHING;
