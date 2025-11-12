-- -----------------------------------------------------
-- Script 1: CREACIÓN DE ESTRUCTURA
-- TPI Programación 2 - UTN
-- Dominio: Empleado -> Legajo
-- -----------------------------------------------------

-- 1. CREACIÓN DE LA BASE DE DATOS
DROP DATABASE IF EXISTS tpi_prog2_empleados;
CREATE DATABASE IF NOT EXISTS tpi_prog2_empleados
CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE tpi_prog2_empleados;

-- 2. CREACIÓN DE LA TABLA 'Legajo' (Clase B)
-- La creamos primero porque 'empleado' es la referencia.
CREATE TABLE IF NOT EXISTS legajo (
    id BIGINT NOT NULL AUTO_INCREMENT,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nro_legajo VARCHAR(20) NOT NULL,
    categoria VARCHAR(30),
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL,
    fecha_alta DATE,
    observaciones VARCHAR(255),
    
    PRIMARY KEY (id),
    UNIQUE INDEX uk_nro_legajo (nro_legajo ASC)
) 
ENGINE = InnoDB;


-- 3. CREACIÓN DE LA TABLA 'Empleado' (Clase A)
CREATE TABLE IF NOT EXISTS empleado (
    id BIGINT NOT NULL AUTO_INCREMENT,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    dni VARCHAR(15) NOT NULL,
    email VARCHAR(120),
    fecha_ingreso DATE,
    area VARCHAR(50),
    
    -- Esta es la clave foránea que implementa la relación 1-1
    legajo_id BIGINT NOT NULL, 
    
    PRIMARY KEY (id),
    UNIQUE INDEX uk_dni (dni ASC),
    
    -- RESTRICCIÓN 1-1:
    -- La FK legajo_id debe ser ÚNICA.
    -- Esto garantiza que un Empleado solo puede tener un Legajo,
    -- y un Legajo solo puede ser asignado a un Empleado.
    UNIQUE INDEX uk_legajo_id (legajo_id ASC),
    
    CONSTRAINT fk_empleado_legajo
        FOREIGN KEY (legajo_id)
        REFERENCES legajo (id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
)
ENGINE = InnoDB;