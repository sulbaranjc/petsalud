-- =============================================================================
-- PetSalud - Script de datos de ejemplo (seed data)
-- Propósito: Poblar la base de datos con registros de muestra para desarrollo
-- Prerequisito: haber ejecutado ini_db.sql
-- =============================================================================

USE petsalud;

-- Desactivar verificación de FK durante la carga masiva
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================================================
-- CATÁLOGOS (10 registros por tabla)
-- =============================================================================

-- especie
INSERT INTO especie (id, nombre, descripcion) VALUES
  (1,  'Perro',   'Canis lupus familiaris'),
  (2,  'Gato',    'Felis catus'),
  (3,  'Ave',     'Aves domésticas (loros, canarios, etc.)'),
  (4,  'Reptil',  'Reptiles domésticos (tortugas, iguanas, etc.)'),
  (5,  'Roedor',  'Hamsters, conejos, cobayas, etc.'),
  (6,  'Pez',     'Peces de acuario y estanque'),
  (7,  'Conejo',  'Oryctolagus cuniculus - conejo doméstico'),
  (8,  'Hurón',   'Mustela putorius furo - hurón doméstico'),
  (9,  'Cobaya',  'Cavia porcellus - también conocido como cuy o conejillo de indias'),
  (10, 'Tortuga', 'Quelónios domésticos de tierra y agua');

-- especialidad
INSERT INTO especialidad (id, nombre) VALUES
  (1,  'Medicina Interna'),
  (2,  'Cirugía General'),
  (3,  'Dermatología'),
  (4,  'Oftalmología'),
  (5,  'Odontología'),
  (6,  'Cardiología'),
  (7,  'Neurología'),
  (8,  'Medicina General'),
  (9,  'Oncología'),
  (10, 'Nutrición y Dietética');

-- estado_cita
INSERT INTO estado_cita (id, nombre) VALUES
  (1,  'Pendiente'),
  (2,  'Confirmada'),
  (3,  'En curso'),
  (4,  'Completada'),
  (5,  'Cancelada'),
  (6,  'No asistió'),
  (7,  'En sala de espera'),
  (8,  'Reprogramada'),
  (9,  'En consulta'),
  (10, 'Pendiente de pago');

-- medicamento (10 registros)
INSERT INTO medicamento (id, nombre, presentacion, descripcion) VALUES
  (1,  'Amoxicilina',    'Cápsula 500 mg',                'Antibiótico betalactámico de amplio espectro'),
  (2,  'Metronidazol',   'Tableta 250 mg',                'Antiprotozoario y antibacteriano anaeróbico'),
  (3,  'Ivermectina',    'Solución inyectable 1 %',       'Antiparasitario endectocida de amplio espectro'),
  (4,  'Prednisona',     'Tableta 5 mg',                  'Corticoesteroide antiinflamatorio e inmunosupresor'),
  (5,  'Enrofloxacina',  'Tableta 50 mg',                 'Antibiótico fluoroquinolona bactericida'),
  (6,  'Cetirizina',     'Tableta 10 mg',                 'Antihistamínico H1 para reacciones alérgicas'),
  (7,  'Omeprazol',      'Cápsula 20 mg',                 'Inhibidor de la bomba de protones'),
  (8,  'Furosemida',     'Tableta 40 mg',                 'Diurético de asa para edemas y ascitis'),
  (9,  'Tramadol',       'Tableta 50 mg',                 'Analgésico opioide para dolor moderado a severo'),
  (10, 'Vitamina B12',   'Solución inyectable 1000 mcg/ml', 'Suplemento vitamínico para estados carenciales');

-- vacuna (10 registros nuevos)
INSERT INTO vacuna (id, nombre, laboratorio, descripcion) VALUES
  (1,  'Antirrábica Canina',    'Zoetis',               'Vacuna antirrábica para perros, protección 1-3 años'),
  (2,  'Nobivac DHPPi',         'MSD Animal Health',    'Cuádruple: Moquillo, Hepatitis, Parvovirus, Parainfluenza'),
  (3,  'Leptospira L4',         'MSD Animal Health',    'Vacuna contra Leptospirosis, cubre 4 serogrupos'),
  (4,  'Nobivac Bordetella',    'MSD Animal Health',    'Vacuna intranasal contra Tos de las perreras'),
  (5,  'Influenza Canina H3N2', 'Merck Animal Health',  'Vacuna bivalente contra Influenza Canina H3N2 y H3N8'),
  (6,  'Felocell 4',            'Zoetis',               'Cuádruple felina: Rinotraqueítis, Calicivirus, Panleucopenia, Clamidia'),
  (7,  'Leucofeligen',          'Virbac',               'Bivalente felina: Leucemia felina (FeLV) y Panleucopenia'),
  (8,  'Antirrábica Felina',    'Boehringer Ingelheim', 'Vacuna antirrábica específica para gatos'),
  (9,  'Hexadog',               'Boehringer Ingelheim', 'Vacuna séxtuple canina de amplia cobertura'),
  (10, 'Purevax RCP',           'Merial',               'Triple felina sin adyuvante: Rinotraqueítis, Calicivirus, Panleucopenia');

-- raza (23 registros)
INSERT INTO raza (id, nombre, id_especie) VALUES
  (1,  'Labrador Retriever',  1),
  (2,  'Golden Retriever',    1),
  (3,  'Pastor Alemán',       1),
  (4,  'Bulldog Francés',     1),
  (5,  'Poodle',              1),
  (6,  'Chihuahua',           1),
  (7,  'Rottweiler',          1),
  (8,  'Mestizo',             1),
  (9,  'Persa',               2),
  (10, 'Siamés',              2),
  (11, 'Maine Coon',          2),
  (12, 'Bengalí',             2),
  (13, 'Mestizo',             2),
  (14, 'Yorkshire Terrier',   1),
  (15, 'Beagle',              1),
  (16, 'Dálmata',             1),
  (17, 'Shih Tzu',            1),
  (18, 'Husky Siberiano',     1),
  (19, 'Abisinio',            2),
  (20, 'Angora',              2),
  (21, 'Ragdoll',             2),
  (22, 'Canario',             3),
  (23, 'Loro Gris Africano',  3);

-- =============================================================================
-- ENTIDADES PRINCIPALES
-- =============================================================================

-- propietario (10 registros)
INSERT INTO propietario (id, nombre, apellido, documento, telefono, email, direccion) VALUES
  (1,  'Carlos',   'Ramírez Soto',     '12345678', '3001234567', 'carlos.ramirez@email.com',  'Calle 45 #12-30, Medellín'),
  (2,  'María',    'González Pérez',   '23456789', '3019876543', 'maria.gonzalez@email.com',  'Carrera 7 #80-15, Bogotá'),
  (3,  'Juan',     'Pérez Díaz',       '34567890', '3024567890', 'juan.perez@email.com',      'Av. Los Álamos 123, Cali'),
  (4,  'Ana',      'Martínez López',   '45678901', '3033456789', 'ana.martinez@email.com',    'Calle 10 #5-40, Barranquilla'),
  (5,  'Luis',     'Rodríguez García', '56789012', '3042345678', 'luis.rodriguez@email.com',  'Transversal 30 #60-20, Bucaramanga'),
  (6,  'Carmen',   'López Torres',     '67890123', '3051234567', 'carmen.lopez@email.com',    'Calle 80 #25-10, Cartagena'),
  (7,  'Pedro',    'Hernández Ruiz',   '78901234', '3060123456', 'pedro.hernandez@email.com', 'Av. 68 #45-50, Bogotá'),
  (8,  'Rosa',     'García Vargas',    '89012345', '3079012345', 'rosa.garcia@email.com',     'Calle 15 #30-05, Pereira'),
  (9,  'Antonio',  'Sánchez Mora',     '90123456', '3088901234', 'antonio.sanchez@email.com', 'Carrera 50 #10-75, Manizales'),
  (10, 'Isabel',   'Torres Jiménez',   '01234567', '3097890123', 'isabel.torres@email.com',   'Calle 100 #20-30, Medellín');

-- veterinario (10 registros)
INSERT INTO veterinario (id, nombre, apellido, matricula, telefono, email, id_especialidad) VALUES
  (1,  'Alejandro', 'Vargas Ríos',    'VET-001', '3110001111', 'a.vargas@petsalud.com',    1),
  (2,  'Patricia',  'Morales Cano',   'VET-002', '3120002222', 'p.morales@petsalud.com',   2),
  (3,  'Gustavo',   'Ríos Ospina',    'VET-003', '3130003333', 'g.rios@petsalud.com',      3),
  (4,  'Valentina', 'Cruz Salazar',   'VET-004', '3140004444', 'v.cruz@petsalud.com',      4),
  (5,  'Sebastián', 'Díaz Herrera',   'VET-005', '3150005555', 's.diaz@petsalud.com',      5),
  (6,  'Laura',     'Jiménez Pardo',  'VET-006', '3160006666', 'l.jimenez@petsalud.com',   6),
  (7,  'Ricardo',   'Flores Acosta',  'VET-007', '3170007777', 'r.flores@petsalud.com',    7),
  (8,  'Natalia',   'Castillo Mejía', 'VET-008', '3180008888', 'n.castillo@petsalud.com',  8),
  (9,  'Andrés',    'Rojas Castaño',  'VET-009', '3190009999', 'a.rojas@petsalud.com',     1),
  (10, 'Gabriela',  'Mendoza Patiño', 'VET-010', '3200010000', 'g.mendoza@petsalud.com',   2);

-- mascota (10 registros)
-- razas referenciadas: 1=Labrador, 2=Golden Retriever, 3=Pastor Alemán,
--   9=Persa, 10=Siamés, 11=Maine Coon, 14=Yorkshire Terrier
INSERT INTO mascota (id, nombre, fecha_nacimiento, sexo, color, id_especie, id_raza, id_propietario) VALUES
  (1,  'Max',    '2020-03-15', 'M', 'Dorado',            1, 2,    1),
  (2,  'Luna',   '2019-07-22', 'F', 'Negro y café',      1, 3,    2),
  (3,  'Milo',   '2021-01-10', 'M', 'Crema y café',      2, 10,   3),
  (4,  'Pelusa', '2018-11-30', 'F', 'Blanco',            2, 9,    4),
  (5,  'Rocky',  '2022-05-18', 'M', 'Negro',             1, 1,    5),
  (6,  'Coco',   '2020-08-03', 'F', 'Dorado',            1, NULL, 6),
  (7,  'Nala',   '2021-12-25', 'F', 'Gris oscuro',       2, NULL, 7),
  (8,  'Toby',   '2019-04-17', 'M', 'Azul y dorado',     1, 14,   8),
  (9,  'Lola',   '2023-02-08', 'F', 'Naranja y blanco',  2, 11,   9),
  (10, 'Paco',   '2020-09-14', 'M', 'Verde y rojo',      3, NULL, 10);

-- =============================================================================
-- CITAS (10 registros — todas completadas para habilitar historial clínico)
-- =============================================================================
INSERT INTO cita (id, id_mascota, id_veterinario, id_estado_cita, fecha_hora, motivo) VALUES
  (1,  1,  1, 4, '2026-01-10 09:00:00', 'Control anual y vacunación'),
  (2,  2,  2, 4, '2026-01-15 10:30:00', 'Revisión post-operatoria'),
  (3,  3,  3, 4, '2026-01-20 11:00:00', 'Dermatitis en zona dorsal'),
  (4,  4,  8, 4, '2026-02-03 09:30:00', 'Pérdida de apetito y letargia'),
  (5,  5,  1, 4, '2026-02-10 14:00:00', 'Vacunación anual'),
  (6,  6,  8, 4, '2026-02-17 10:00:00', 'Vómitos frecuentes'),
  (7,  7,  3, 4, '2026-02-20 16:00:00', 'Alopecia y prurito intenso'),
  (8,  8,  5, 4, '2026-03-05 08:30:00', 'Limpieza dental programada'),
  (9,  9,  8, 4, '2026-03-12 11:00:00', 'Chequeo general — primera visita'),
  (10, 10, 8, 4, '2026-03-18 15:30:00', 'Control de peso y evaluación nutricional');

-- =============================================================================
-- HISTORIAL CLÍNICO
-- =============================================================================

-- consulta (10 registros — una por cita, restricción UNIQUE en id_cita)
INSERT INTO consulta (id, id_cita, fecha_hora, peso_kg, temperatura_c, frecuencia_cardiaca, frecuencia_resp, anamnesis, examen_fisico, diagnostico, observaciones) VALUES
  (1,  1,  '2026-01-10 09:10:00', 28.5, 38.5,  88, 20,
   'Propietario refiere buen apetito y actividad normal. Sin vacunas desde hace un año.',
   'Mucosas rosadas, hidratado, BCS 5/9. Sin hallazgos anormales.',
   'Paciente sano. Control preventivo.',
   'Se aplica Antirrábica y Nobivac DHPPi. Próximo control en 12 meses.'),

  (2,  2,  '2026-01-15 10:40:00', 32.0, 38.8,  92, 22,
   'Operada de piómetra hace 10 días. Herida con buen aspecto según propietario.',
   'Cicatriz limpia, sin edema ni eritema. Sutura íntegra. Sin dolor abdominal.',
   'Recuperación post-operatoria satisfactoria.',
   'Retirar puntos en 5 días. Continuar amoxicilina 3 días más.'),

  (3,  3,  '2026-01-20 11:15:00',  4.2, 38.9, 140, 28,
   'Rascado excesivo y caída de pelo en zona dorsal desde hace 2 semanas.',
   'Alopecia difusa dorsolumbar, eritema moderado, pápulas y costras. Signo de pulgas positivo.',
   'Dermatitis alérgica por pulgas (DAPP).',
   'Pipeta antiparasitaria. Prednisona 5 días. Revisar en 2 semanas.'),

  (4,  4,  '2026-02-03 09:45:00',  3.8, 38.6, 144, 26,
   'Sin comer bien desde hace 3 días, letárgica, un episodio de vómito el día anterior.',
   'Deshidratación leve (5%), abdomen sensible en zona media. Sin masas palpables.',
   'Gastroenteritis leve.',
   'Dieta blanda 5 días. Metronidazol y omeprazol. Control si no mejora en 72 h.'),

  (5,  5,  '2026-02-10 14:10:00', 30.0, 38.4,  80, 18,
   'Propietario refiere excelente estado general. Activo y con buen apetito.',
   'BCS 5/9. Mucosas rosadas. Auscultación cardiorrespiratoria sin hallazgos.',
   'Paciente sano. Control preventivo.',
   'Se aplica refuerzo de Leptospira L4. Siguiente control en 12 meses.'),

  (6,  6,  '2026-02-17 10:15:00', 12.3, 38.7,  96, 24,
   'Tres episodios de vómito en las últimas 24 h. Come poco. Pudo ingerir algo del suelo.',
   'Mucosas levemente pálidas. Dolor leve a palpación craneoabdominal.',
   'Gastritis aguda. Probable ingestión de cuerpo extraño menor.',
   'Ayuno 12 h, luego dieta blanda. Omeprazol y metronidazol 5 días. Rx si no mejora.'),

  (7,  7,  '2026-02-20 16:15:00',  5.1, 38.7, 138, 27,
   'Prurito intenso y pérdida de pelo en cabeza y cuello desde hace 1 mes. Dieta de pollo y arroz.',
   'Eritema periocular y perioral, costras y excoriaciones cervicales. Sin parásitos visibles.',
   'Hipersensibilidad alimentaria. Posible atopia.',
   'Dieta hipoalergénica 8 semanas. Cetirizina 1 mg/kg. Control en 3 semanas.'),

  (8,  8,  '2026-03-05 08:45:00',  3.5, 38.3, 148, 30,
   'Propietario refiere mal aliento y que el paciente come con dificultad.',
   'Sarro dental grado III, gingivitis moderada en molares superiores. Pieza 108 con movilidad grado II.',
   'Enfermedad periodontal grado II.',
   'Destartaje ultrasónico bajo anestesia. Extracción pieza 108. Amoxicilina 7 días.'),

  (9,  9,  '2026-03-12 11:10:00',  4.8, 38.6, 136, 26,
   'Primera visita. Sin historial médico previo. Buena alimentación según propietario.',
   'BCS 4/9, algo delgada. Mucosas rosadas. Pelaje opaco. Sin hallazgos internos relevantes.',
   'Paciente sana. Bajo peso leve.',
   'Iniciar suplemento vitamínico. Aumentar ración. Vacunas al día. Control en 1 mes.'),

  (10, 10, '2026-03-18 15:40:00',  0.3, 41.0, 280, 50,
   'Ave con plumas erizadas, letárgica y sin comer desde ayer.',
   'Plumas erizadas, ojos semicerrados, mucosas levemente cianóticas, pérdida de peso evidente.',
   'Síndrome de enfermedad general en psitácido. Descartar infección bacteriana.',
   'Antibioterapia empírica. Calor suplementario. Análisis de heces. Reevaluar en 48 h.');

-- tratamiento (10 registros distribuidos entre consultas)
INSERT INTO tratamiento (id, id_consulta, descripcion, fecha_inicio, fecha_fin, observaciones) VALUES
  (1,  1,  'Vacunación anual: Antirrábica + Nobivac DHPPi',                           '2026-01-10', NULL,         'Próxima vacunación: enero 2027'),
  (2,  2,  'Limpieza de herida quirúrgica con solución salina, 2 veces al día',       '2026-01-15', '2026-01-25', 'Retirar puntos en 5 días'),
  (3,  3,  'Aplicación de pipeta antiparasitaria (Frontline Plus)',                   '2026-01-20', NULL,         'Repetir mensualmente durante 3 meses'),
  (4,  3,  'Corticoterapia con prednisona para control del prurito',                  '2026-01-20', '2026-01-25', 'Reducir dosis progresivamente; no suspender de forma brusca'),
  (5,  4,  'Dieta blanda: pollo hervido con arroz blanco a partes iguales',           '2026-02-03', '2026-02-08', 'Reintroducir dieta normal de forma gradual al finalizar'),
  (6,  5,  'Vacunación: Leptospira L4 — refuerzo anual',                              '2026-02-10', NULL,         'Siguiente dosis en 12 meses'),
  (7,  6,  'Ayuno 12 horas; luego dieta blanda por 5 días',                           '2026-02-17', '2026-02-22', 'Si reaparecen vómitos, acudir de urgencias'),
  (8,  7,  'Cambio a dieta hipoalergénica (hidrolizado proteico) por 8 semanas',      '2026-02-20', '2026-04-20', 'No ofrecer premios ni alimentos distintos a la dieta indicada'),
  (9,  8,  'Cuidados post-exodoncia: evitar croquetas duras por 10 días',             '2026-03-05', '2026-03-15', 'Revisar cicatrización en próximo control'),
  (10, 9,  'Vitamina B12 inyectable semanal durante 4 semanas',                       '2026-03-12', '2026-04-09', 'Administrar en clínica; no en casa');

-- consulta_medicamento (medicamentos administrados en clínica durante la consulta)
INSERT INTO consulta_medicamento (id, id_consulta, id_medicamento, dosis, frecuencia, observaciones) VALUES
  (1,  2,  1, '1 cápsula (500 mg)',  'Cada 12 horas', '5 días. Administrar con alimento.'),
  (2,  3,  4, '1 mg/kg',             'Cada 24 horas', '5 días. Reducir dosis progresivamente; no suspender bruscamente.'),
  (3,  4,  2, '15 mg/kg',            'Cada 12 horas', '5 días. Mezclar con la comida.'),
  (4,  4,  7, '0.5 mg/kg',           'Cada 24 horas', '7 días. En ayunas, 30 min antes del alimento.'),
  (5,  6,  7, '0.5 mg/kg',           'Cada 24 horas', '5 días. En ayunas, 30 min antes de ofrecer alimento.'),
  (6,  6,  2, '15 mg/kg',            'Cada 12 horas', '5 días. Con comida para minimizar irritación gástrica.'),
  (7,  7,  6, '1 mg/kg',             'Cada 24 horas', '21 días. Administrar preferiblemente en la noche.'),
  (8,  8,  1, '1 cápsula (500 mg)',  'Cada 8 horas',  '7 días. Iniciar después de la cirugía.'),
  (9,  9,  10,'0.5 ml',              'Cada 7 días',   '4 semanas. Vía subcutánea. Administrar en clínica.'),
  (10, 10, 5, '10 mg/kg',            'Cada 12 horas', '7 días. Mezclar en alimento blando. Mantener al ave en ambiente cálido.');

-- consulta_vacuna (vacunas aplicadas en clínica durante la consulta)
INSERT INTO consulta_vacuna (id, id_consulta, id_vacuna, proxima_dosis, lote, observaciones) VALUES
  (1, 1, 1, '2027-01-10', 'ZOE-2025-A1', 'Sin reacción adversa.'),
  (2, 1, 2, '2027-01-10', 'MSD-2025-B3', 'DHPPi aplicada junto con antirrábica en misma visita.'),
  (3, 5, 3, '2027-02-10', 'MSD-2025-L2', 'Refuerzo anual de Leptospira L4. Sin reacción.');

-- Reactivar verificación de FK
SET FOREIGN_KEY_CHECKS = 1;
