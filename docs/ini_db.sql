-- =============================================================================
-- PetSalud - Script de creación de base de datos
-- Motor: MySQL / MariaDB
-- Descripción: Gestión de citas médicas para mascotas
-- =============================================================================

DROP DATABASE IF EXISTS petsalud;

CREATE DATABASE petsalud
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_spanish_ci
  DEFAULT ENCRYPTION = 'N';

USE petsalud;

-- =============================================================================
-- TABLAS CATÁLOGO (sin dependencias externas)
-- =============================================================================

CREATE TABLE especie (
  id          INT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador único de la especie',
  nombre      VARCHAR(60)  NOT NULL                COMMENT 'Nombre de la especie (ej: Perro, Gato, Ave)',
  descripcion VARCHAR(255)     NULL                COMMENT 'Descripción opcional de la especie',
  foto_url    VARCHAR(500)     NULL                COMMENT 'URL o ruta de la imagen representativa de la especie',
  activo      TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '1 = activo, 0 = inactivo',
  PRIMARY KEY (id),
  UNIQUE INDEX uq_especie_nombre (nombre)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Catálogo de especies animales atendidas en la clínica';


CREATE TABLE especialidad (
  id          INT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador único de la especialidad',
  nombre      VARCHAR(100) NOT NULL                COMMENT 'Nombre de la especialidad (ej: Medicina Interna, Cirugía)',
  descripcion VARCHAR(255)     NULL                COMMENT 'Descripción de la especialidad',
  activo      TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '1 = activo, 0 = inactivo',
  PRIMARY KEY (id),
  UNIQUE INDEX uq_especialidad_nombre (nombre)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Catálogo de especialidades veterinarias';


CREATE TABLE estado_cita (
  id     INT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del estado',
  nombre VARCHAR(50)  NOT NULL                COMMENT 'Nombre del estado (ej: Pendiente, Confirmada, Cancelada)',
  PRIMARY KEY (id),
  UNIQUE INDEX uq_estado_cita_nombre (nombre)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Catálogo de estados posibles para una cita médica';


CREATE TABLE medicamento (
  id           INT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del medicamento',
  nombre       VARCHAR(150) NOT NULL                COMMENT 'Nombre comercial o genérico del medicamento',
  presentacion VARCHAR(100)     NULL                COMMENT 'Presentación (ej: Tableta 500mg, Jarabe 250ml)',
  descripcion  VARCHAR(255)     NULL                COMMENT 'Descripción o indicaciones generales',
  activo       TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '1 = activo, 0 = inactivo',
  PRIMARY KEY (id),
  INDEX idx_medicamento_nombre (nombre)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Catálogo de medicamentos disponibles para prescripción';


CREATE TABLE vacuna (
  id          INT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador único de la vacuna',
  nombre      VARCHAR(150) NOT NULL                COMMENT 'Nombre de la vacuna',
  laboratorio VARCHAR(100)     NULL                COMMENT 'Laboratorio fabricante',
  descripcion VARCHAR(255)     NULL                COMMENT 'Descripción de la vacuna y enfermedades que previene',
  activo      TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '1 = activo, 0 = inactivo',
  PRIMARY KEY (id),
  INDEX idx_vacuna_nombre (nombre)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Catálogo de vacunas aplicables a las mascotas';


-- =============================================================================
-- TABLAS CATÁLOGO CON DEPENDENCIAS
-- =============================================================================

CREATE TABLE raza (
  id         INT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador único de la raza',
  nombre     VARCHAR(100) NOT NULL                COMMENT 'Nombre de la raza (ej: Labrador, Siamés)',
  id_especie INT          NOT NULL                COMMENT 'Especie a la que pertenece esta raza',
  foto_url   VARCHAR(500)     NULL                COMMENT 'URL o ruta de la imagen representativa de la raza',
  activo     TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '1 = activo, 0 = inactivo',
  PRIMARY KEY (id),
  UNIQUE INDEX uq_raza_nombre_especie (nombre, id_especie),
  INDEX idx_raza_especie (id_especie),
  CONSTRAINT fk_raza_especie
    FOREIGN KEY (id_especie) REFERENCES especie (id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Catálogo de razas por especie';


-- =============================================================================
-- ENTIDADES PRINCIPALES
-- =============================================================================

CREATE TABLE propietario (
  id              INT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del propietario',
  nombre          VARCHAR(100) NOT NULL                COMMENT 'Nombre(s) del propietario',
  apellido        VARCHAR(100) NOT NULL                COMMENT 'Apellido(s) del propietario',
  documento       VARCHAR(20)  NOT NULL                COMMENT 'Número de documento de identidad (DNI, cédula, pasaporte)',
  telefono        VARCHAR(20)      NULL                COMMENT 'Número de teléfono de contacto',
  email           VARCHAR(150)     NULL                COMMENT 'Correo electrónico del propietario',
  direccion       VARCHAR(255)     NULL                COMMENT 'Dirección de residencia',
  activo          TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '1 = activo, 0 = dado de baja',
  created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de registro',
  PRIMARY KEY (id),
  UNIQUE INDEX uq_propietario_documento (documento),
  INDEX idx_propietario_email (email),
  INDEX idx_propietario_apellido (apellido)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Propietarios o tutores de las mascotas';


CREATE TABLE veterinario (
  id              INT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del veterinario',
  nombre          VARCHAR(100) NOT NULL                COMMENT 'Nombre(s) del veterinario',
  apellido        VARCHAR(100) NOT NULL                COMMENT 'Apellido(s) del veterinario',
  matricula       VARCHAR(50)  NOT NULL                COMMENT 'Número de matrícula profesional',
  telefono        VARCHAR(20)      NULL                COMMENT 'Teléfono de contacto',
  email           VARCHAR(150)     NULL                COMMENT 'Correo electrónico profesional',
  id_especialidad INT              NULL                COMMENT 'Especialidad principal del veterinario',
  foto_url        VARCHAR(500)     NULL                COMMENT 'Ruta de la foto de perfil del veterinario',
  activo          TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '1 = activo, 0 = dado de baja',
  created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de alta en el sistema',
  PRIMARY KEY (id),
  UNIQUE INDEX uq_veterinario_matricula (matricula),
  INDEX idx_veterinario_email (email),
  INDEX idx_veterinario_especialidad (id_especialidad),
  CONSTRAINT fk_veterinario_especialidad
    FOREIGN KEY (id_especialidad) REFERENCES especialidad (id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Personal veterinario de la clínica';


CREATE TABLE mascota (
  id                INT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador único de la mascota',
  nombre            VARCHAR(100) NOT NULL                COMMENT 'Nombre de la mascota',
  fecha_nacimiento  DATE             NULL                COMMENT 'Fecha de nacimiento (aproximada si se desconoce)',
  sexo              ENUM('M','F','Desconocido') NOT NULL DEFAULT 'Desconocido' COMMENT 'Sexo biológico: M=Macho, F=Hembra',
  color             VARCHAR(80)      NULL                COMMENT 'Color o descripción del pelaje/plumaje',
  id_especie        INT          NOT NULL                COMMENT 'Especie de la mascota',
  id_raza           INT              NULL                COMMENT 'Raza de la mascota (NULL si es mestiza o desconocida)',
  id_propietario    INT          NOT NULL                COMMENT 'Propietario o tutor responsable',
  foto_url          VARCHAR(500)     NULL                COMMENT 'URL o ruta de la foto de la mascota',
  activo            TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '1 = activo, 0 = fallecido o dado de baja',
  created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de registro en el sistema',
  PRIMARY KEY (id),
  INDEX idx_mascota_propietario (id_propietario),
  INDEX idx_mascota_especie (id_especie),
  INDEX idx_mascota_raza (id_raza),
  CONSTRAINT fk_mascota_propietario
    FOREIGN KEY (id_propietario) REFERENCES propietario (id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_mascota_especie
    FOREIGN KEY (id_especie) REFERENCES especie (id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_mascota_raza
    FOREIGN KEY (id_raza) REFERENCES raza (id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Mascotas registradas en la clínica';


-- =============================================================================
-- ENTIDADES DE GESTIÓN DE CITAS
-- =============================================================================

CREATE TABLE cita (
  id              INT           NOT NULL AUTO_INCREMENT COMMENT 'Identificador único de la cita',
  id_mascota      INT           NOT NULL                COMMENT 'Mascota para la que se agenda la cita',
  id_veterinario  INT           NOT NULL                COMMENT 'Veterinario asignado a la cita',
  id_estado_cita  INT           NOT NULL                COMMENT 'Estado actual de la cita',
  fecha_hora      DATETIME      NOT NULL                COMMENT 'Fecha y hora programada de la cita',
  motivo          VARCHAR(255)  NOT NULL                COMMENT 'Motivo o razón principal de la consulta',
  observaciones   TEXT              NULL                COMMENT 'Observaciones adicionales del agendamiento',
  created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora en que se creó la cita',
  PRIMARY KEY (id),
  INDEX idx_cita_mascota (id_mascota),
  INDEX idx_cita_veterinario (id_veterinario),
  INDEX idx_cita_estado (id_estado_cita),
  INDEX idx_cita_fecha_hora (fecha_hora),
  CONSTRAINT fk_cita_mascota
    FOREIGN KEY (id_mascota) REFERENCES mascota (id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_cita_veterinario
    FOREIGN KEY (id_veterinario) REFERENCES veterinario (id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_cita_estado
    FOREIGN KEY (id_estado_cita) REFERENCES estado_cita (id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Citas médicas agendadas para las mascotas';


-- =============================================================================
-- ENTIDADES DE HISTORIAL CLÍNICO
-- =============================================================================

CREATE TABLE consulta (
  id                   INT            NOT NULL AUTO_INCREMENT COMMENT 'Identificador único de la consulta',
  id_cita              INT            NOT NULL                COMMENT 'Cita a la que pertenece esta consulta',
  fecha_hora           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora efectiva de la consulta',
  peso_kg              DECIMAL(5,2)       NULL                COMMENT 'Peso de la mascota en kilogramos',
  temperatura_c        DECIMAL(4,1)       NULL                COMMENT 'Temperatura corporal en grados Celsius',
  frecuencia_cardiaca  INT                NULL                COMMENT 'Frecuencia cardíaca (latidos por minuto)',
  frecuencia_resp      INT                NULL                COMMENT 'Frecuencia respiratoria (respiraciones por minuto)',
  anamnesis            TEXT               NULL                COMMENT 'Historia clínica relatada por el propietario',
  examen_fisico        TEXT               NULL                COMMENT 'Hallazgos del examen físico',
  diagnostico          TEXT               NULL                COMMENT 'Diagnóstico o impresión diagnóstica',
  observaciones        TEXT               NULL                COMMENT 'Observaciones y recomendaciones generales',
  PRIMARY KEY (id),
  UNIQUE INDEX uq_consulta_cita (id_cita),
  CONSTRAINT fk_consulta_cita
    FOREIGN KEY (id_cita) REFERENCES cita (id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Registro clínico de cada consulta médica realizada';


CREATE TABLE tratamiento (
  id           INT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del tratamiento',
  id_consulta  INT          NOT NULL                COMMENT 'Consulta en la que se indicó el tratamiento',
  descripcion  TEXT         NOT NULL                COMMENT 'Descripción detallada del tratamiento indicado',
  fecha_inicio DATE             NULL                COMMENT 'Fecha de inicio del tratamiento',
  fecha_fin    DATE             NULL                COMMENT 'Fecha de finalización estimada del tratamiento',
  observaciones VARCHAR(255)    NULL                COMMENT 'Instrucciones adicionales para el propietario',
  PRIMARY KEY (id),
  INDEX idx_tratamiento_consulta (id_consulta),
  CONSTRAINT fk_tratamiento_consulta
    FOREIGN KEY (id_consulta) REFERENCES consulta (id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Tratamientos médicos indicados en cada consulta';


CREATE TABLE receta (
  id            INT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador único de la línea de receta',
  id_consulta   INT          NOT NULL                COMMENT 'Consulta a la que pertenece esta receta',
  id_medicamento INT         NOT NULL                COMMENT 'Medicamento recetado',
  dosis         VARCHAR(100) NOT NULL                COMMENT 'Dosis a administrar (ej: 1 tableta, 5ml)',
  frecuencia    VARCHAR(100) NOT NULL                COMMENT 'Frecuencia de administración (ej: cada 8 horas)',
  duracion      VARCHAR(100)     NULL                COMMENT 'Duración del tratamiento (ej: 7 días)',
  indicaciones  TEXT             NULL                COMMENT 'Indicaciones especiales de administración',
  PRIMARY KEY (id),
  INDEX idx_receta_consulta (id_consulta),
  INDEX idx_receta_medicamento (id_medicamento),
  CONSTRAINT fk_receta_consulta
    FOREIGN KEY (id_consulta) REFERENCES consulta (id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_receta_medicamento
    FOREIGN KEY (id_medicamento) REFERENCES medicamento (id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Medicamentos recetados en cada consulta (una fila por medicamento)';


CREATE TABLE historial_vacunacion (
  id               INT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del registro de vacunación',
  id_mascota       INT          NOT NULL                COMMENT 'Mascota vacunada',
  id_vacuna        INT          NOT NULL                COMMENT 'Vacuna aplicada',
  id_veterinario   INT          NOT NULL                COMMENT 'Veterinario que aplicó la vacuna',
  fecha_aplicacion DATE         NOT NULL                COMMENT 'Fecha en que se aplicó la vacuna',
  proxima_dosis    DATE             NULL                COMMENT 'Fecha estimada para la próxima dosis o refuerzo',
  lote             VARCHAR(50)      NULL                COMMENT 'Número de lote del vial de vacuna',
  observaciones    VARCHAR(255)     NULL                COMMENT 'Observaciones adicionales de la vacunación',
  created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de registro en el sistema',
  PRIMARY KEY (id),
  INDEX idx_vacunacion_mascota (id_mascota),
  INDEX idx_vacunacion_vacuna (id_vacuna),
  INDEX idx_vacunacion_veterinario (id_veterinario),
  INDEX idx_vacunacion_fecha (fecha_aplicacion),
  CONSTRAINT fk_vacunacion_mascota
    FOREIGN KEY (id_mascota) REFERENCES mascota (id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_vacunacion_vacuna
    FOREIGN KEY (id_vacuna) REFERENCES vacuna (id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_vacunacion_veterinario
    FOREIGN KEY (id_veterinario) REFERENCES veterinario (id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Historial de vacunación de cada mascota';


-- =============================================================================
-- TABLAS DE RELACIÓN CONSULTA ↔ INSUMOS APLICADOS EN CLÍNICA
-- =============================================================================

CREATE TABLE consulta_medicamento (
  id              INT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador único',
  id_consulta     INT          NOT NULL                COMMENT 'Consulta en la que se administró el medicamento',
  id_medicamento  INT          NOT NULL                COMMENT 'Medicamento administrado',
  dosis           VARCHAR(100)     NULL                COMMENT 'Dosis administrada (ej: 5ml, 1 tableta)',
  frecuencia      VARCHAR(100)     NULL                COMMENT 'Frecuencia si aplica (ej: cada 8h por 3 días)',
  observaciones   VARCHAR(255)     NULL                COMMENT 'Observaciones adicionales',
  PRIMARY KEY (id),
  INDEX idx_cm_consulta    (id_consulta),
  INDEX idx_cm_medicamento (id_medicamento),
  CONSTRAINT fk_cm_consulta
    FOREIGN KEY (id_consulta)    REFERENCES consulta    (id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_cm_medicamento
    FOREIGN KEY (id_medicamento) REFERENCES medicamento (id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Medicamentos administrados durante una consulta en clínica';


CREATE TABLE consulta_vacuna (
  id            INT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador único',
  id_consulta   INT          NOT NULL                COMMENT 'Consulta en la que se aplicó la vacuna',
  id_vacuna     INT          NOT NULL                COMMENT 'Vacuna aplicada',
  proxima_dosis DATE             NULL                COMMENT 'Fecha sugerida para la próxima dosis o refuerzo',
  lote          VARCHAR(50)      NULL                COMMENT 'Número de lote del vial',
  observaciones VARCHAR(255)     NULL                COMMENT 'Observaciones adicionales',
  PRIMARY KEY (id),
  INDEX idx_cv_consulta (id_consulta),
  INDEX idx_cv_vacuna   (id_vacuna),
  CONSTRAINT fk_cv_consulta
    FOREIGN KEY (id_consulta) REFERENCES consulta (id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_cv_vacuna
    FOREIGN KEY (id_vacuna)   REFERENCES vacuna   (id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_spanish_ci
  COMMENT='Vacunas aplicadas durante una consulta en clínica';


-- =============================================================================
-- USUARIO DE BASE DE DATOS
-- =============================================================================

-- Eliminar el usuario si ya existe (para re-ejecución del script)
DROP USER IF EXISTS 'petsalud'@'localhost';

-- Crear el usuario con contraseña
CREATE USER 'petsalud'@'localhost'
  IDENTIFIED BY 'petsalud'
  COMMENT 'Usuario de la aplicación PetSalud';

-- Otorgar permisos completos sobre la base de datos petsalud
GRANT ALL PRIVILEGES ON petsalud.* TO 'petsalud'@'localhost';

-- Aplicar los cambios de privilegios
FLUSH PRIVILEGES;

-- =============================================================================
-- FIN DEL SCRIPT
-- =============================================================================
