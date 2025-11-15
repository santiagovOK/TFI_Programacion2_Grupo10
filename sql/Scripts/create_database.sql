-- -----------------------------------------------------
-- CREACIÓN DE ESTRUCTURA
-- TFI Programación 2 - UTN
-- Dominio: Empleado -> Legajo (A->B) (1→1 unidireccional)
-- -----------------------------------------------------

DROP DATABASE IF EXISTS tpi_prog2_empleados;

CREATE DATABASE IF NOT EXISTS tpi_prog2_empleados
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE tpi_prog2_empleados;

-- -----------------------------------------------------
-- Tabla Empleado (Clase A - Padre)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS empleado
(
    -- Identidad
    id                  BIGINT      NOT NULL AUTO_INCREMENT,

    -- Estado lógico
    eliminado           BOOLEAN     NOT NULL DEFAULT FALSE,

    -- Datos de negocio
    nombre              VARCHAR(80) NOT NULL,
    apellido            VARCHAR(80) NOT NULL,
    dni                 VARCHAR(15) NOT NULL,
    email               VARCHAR(120),
    fecha_ingreso       DATE,
    area                VARCHAR(50),

    -- Auditoría: rastreo de creación y actualización
    fecha_creacion      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    -- Clave primaria
    PRIMARY KEY (id),

    -- Restricciones de unicidad (reglas de negocio)
    CONSTRAINT uq_empleado_dni
        UNIQUE (dni),
    CONSTRAINT uq_empleado_email
        UNIQUE (email),

    -- Índices para optimización de consultas
    INDEX idx_empleado_nombre_apellido (nombre, apellido),
    INDEX idx_empleado_area (area),
    INDEX idx_empleado_eliminado (eliminado),
    INDEX idx_empleado_fecha_ingreso (fecha_ingreso)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Tabla Legajo (Clase B - Hijo) - Depende de Empleado
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS legajo
(
    -- Identidad
    id                  BIGINT                      NOT NULL AUTO_INCREMENT,

    -- Estado lógico
    eliminado           BOOLEAN                     NOT NULL DEFAULT FALSE,

    -- Datos de negocio
    nro_legajo          VARCHAR(20)                 NOT NULL,
    categoria           VARCHAR(30),
    estado              ENUM ('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    -- El legajo no puede existir si no tiene fecha de alta
    fecha_alta          DATE                        NOT NULL,
    observaciones       VARCHAR(255),

    -- Auditoría
    fecha_creacion      TIMESTAMP                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP                   NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    -- Relación con Empleado (1-1)
    empleado_id         BIGINT                      NOT NULL,

    -- Clave primaria
    PRIMARY KEY (id),

    -- Unicidad de datos de negocio
    CONSTRAINT uq_legajo_nro_legajo
        UNIQUE (nro_legajo),

    -- Un empleado solo puede tener un legajo (relación 1-1)
    CONSTRAINT uq_legajo_empleado_id
        UNIQUE (empleado_id),

    -- Índices para consultas frecuentes
    INDEX idx_legajo_estado (estado),
    INDEX idx_legajo_categoria (categoria),
    INDEX idx_legajo_eliminado (eliminado),
    INDEX idx_legajo_fecha_alta (fecha_alta),
    INDEX idx_legajo_estado_categoria (estado, categoria),

    -- Clave foránea (relación con empleado)
    CONSTRAINT fk_legajo_empleado_id
        FOREIGN KEY (empleado_id)
            REFERENCES empleado (id)
            -- Si se borra un Empleado (A), su Legajo (B) se borra automáticamente
            ON DELETE CASCADE
            -- La PK de empleado no debería modificarse
            ON UPDATE NO ACTION
) ENGINE = InnoDB;