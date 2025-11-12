-- -----------------------------------------------------
-- Script 2: DATOS DE PRUEBA
-- TPI Programación 2 - UTN
-- -----------------------------------------------------

USE tpi_prog2_empleados;

-- Dado que Empleado(A) referencia a Legajo(B),
-- primero se crean los Legajos y después los Empleados

-- Insertamos Legajos (B)
INSERT INTO legajo (nro_legajo, categoria, estado, fecha_alta) 
VALUES 
('L-1001', 'Senior', 'ACTIVO', '2020-05-10'),
('L-1002', 'Junior', 'ACTIVO', '2023-11-01'),
('L-1003', 'Semi-Senior', 'INACTIVO', '2021-02-15');

-- Insertamos Empleados (A) y los asociamos a los legajos con id 1, 2, 3 etc..
INSERT INTO empleado (nombre, apellido, dni, email, fecha_ingreso, area, legajo_id) 
VALUES 
('Juan', 'Pérez', '30123456', 'juan.perez@email.com', '2020-05-10', 'Desarrollo', 1),
('Maria', 'Gomez', '35789012', 'maria.gomez@email.com', '2023-11-01', 'Desarrollo', 2),
('Carlos', 'Lopez', '32456789', 'carlos.lopez@email.com', '2021-02-15', 'Ventas', 3);

-- Ejemplo de un empleado eliminado lógicamente
INSERT INTO legajo (nro_legajo, categoria, estado, fecha_alta) 
VALUES ('L-1004', 'Senior', 'INACTIVO', '2019-01-01');

INSERT INTO empleado (nombre, apellido, dni, email, fecha_ingreso, area, legajo_id, eliminado) 
VALUES ('Laura', 'Martinez', '28001002', 'laura.martinez@email.com', '2019-01-01', 'RRHH', 4, TRUE);