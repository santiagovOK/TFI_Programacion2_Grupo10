-- -----------------------------------------------------
-- Script 2: DATOS DE PRUEBA
-- TPI Programación 2 - UTN
-- -----------------------------------------------------

USE tpi_prog2_empleados;

-- Insertamos Empleados (A)
INSERT INTO empleado (nombre, apellido, dni, email, fecha_ingreso, area) 
VALUES 
('Juan', 'Pérez', '30123456', 'juan.perez@email.com', '2020-05-10', 'Desarrollo'),
('Maria', 'Gomez', '35789012', 'maria.gomez@email.com', '2023-11-01', 'Desarrollo'),
('Carlos', 'Lopez', '32456789', 'carlos.lopez@email.com', '2021-02-15', 'Ventas');

-- Insertamos Legajos (B) y los asociamos a los empleados creados
INSERT INTO legajo (nro_legajo, categoria, estado, fecha_alta, empleado_id) 
VALUES 
('L-1001', 'Senior', 'ACTIVO', '2020-05-10', 1),
('L-1002', 'Junior', 'ACTIVO', '2023-11-01', 2),
('L-1003', 'Semi-Senior', 'INACTIVO', '2021-02-15', 3);

-- Ejemplo de un empleado eliminado lógicamente
INSERT INTO empleado (nombre, apellido, dni, email, fecha_ingreso, area, eliminado) 
VALUES ('Laura', 'Martinez', '28001002', 'laura.martinez@email.com', '2019-01-01', 'RRHH', TRUE);

-- Su legajo asociado (ID de empleado 4) también está marcado como eliminado
INSERT INTO legajo (nro_legajo, categoria, estado, fecha_alta, empleado_id, eliminado) 
VALUES ('L-1004', 'Senior', 'INACTIVO', '2019-01-01', 4, TRUE);