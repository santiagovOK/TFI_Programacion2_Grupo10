-- -----------------------------------------------------
-- CREACIÓN DE ESTRUCTURA
-- TPI Programación 2 - UTN
-- Dominio: Empleado -> Legajo (A->B)
-- -----------------------------------------------------

-- 1. CREACIÓN DE LA BASE DE DATOS
DROP DATABASE IF EXISTS tpi_prog2_empleados;
CREATE DATABASE IF NOT EXISTS tpi_prog2_empleados
CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE tpi_prog2_empleados;

-- 2. CREACIÓN DE LA TABLA Empleado (Clase A - Padre)
-- Esta tabla se crea primero ya que es la entidad principal
CREATE TABLE IF NOT EXISTS empleado (
    id BIGINT NOT NULL AUTO_INCREMENT,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    dni VARCHAR(15) NOT NULL,
    email VARCHAR(120),
    fecha_ingreso DATE,
    area VARCHAR(50),
    
    PRIMARY KEY (id),
    -- El DNI debe ser único en la organización.
    UNIQUE INDEX uk_dni (dni ASC)
)
ENGINE = InnoDB;


-- 3. CREACIÓN DE LA TABLA Legajo (Clase B - Hijo)
-- Esta tabla se crea en segundo lugar, ya que depende de empleado
CREATE TABLE IF NOT EXISTS legajo (
    id BIGINT NOT NULL AUTO_INCREMENT,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nro_legajo VARCHAR(20) NOT NULL,
    categoria VARCHAR(30),
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL,
    fecha_alta DATE,
    observaciones VARCHAR(255),
    
    -- Clave Foránea que implementa la relación
    empleado_id BIGINT NOT NULL,
    
    PRIMARY KEY (id),
    -- El nro_legajo también debe ser único
    UNIQUE INDEX uk_nro_legajo (nro_legajo ASC),
    
    -- RESTRICCIÓN 1-1 (Requisito TPI):
    -- La Clave Foránea empleado_id se define como ÚNICA
    -- Esto garantiza que un Empleado solo puede tener un Legajo
    UNIQUE INDEX uk_empleado_id (empleado_id ASC),
    
    -- Definición de la Clave Foránea
    CONSTRAINT fk_legajo_empleado
        FOREIGN KEY (empleado_id)
        REFERENCES empleado (id)
        -- ON DELETE CASCADE: Asegura que si se borra un Empleado (A),
        -- su Legajo (B) asociado se borre automáticamente
        ON DELETE CASCADE
        ON UPDATE NO ACTION
)
ENGINE = InnoDB;