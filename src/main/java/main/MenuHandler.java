package main;

import entities.Empleado;
import entities.EstadoLegajo;
import entities.Legajo;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import service.EmpleadoServiceImpl;
import service.LegajoServiceImpl;

/**
 * Controlador de las operaciones del menú (Menu Handler).
 * Gestiona toda la lógica de interacción con el usuario para operaciones CRUD.
 *
 * Responsabilidades:
 * - Capturar entrada del usuario desde consola (Scanner)
 * - Validar entrada básica (conversión de tipos, valores vacíos)
 * - Invocar servicios de negocio (EmpleadoService, LegajoService)
 * - Mostrar resultados y mensajes de error al usuario
 * - Coordinar operaciones complejas (crear empleado con legajo, etc.)
 */
public class MenuHandler {

    /**
     * Scanner compartido para leer entrada del usuario.
     * Inyectado desde AppMenu para evitar múltiples Scanners de System.in.
     */
    private final Scanner scanner;

    /**
     * Servicio de empleados para operaciones CRUD.
     */
    private final EmpleadoServiceImpl empleadoService;

    private static final String EMPLOYEE_LIST_HEADER = "Listando Empleados:";
    private static final String EMPLOYEE_SEPARATOR = "-----------------";

    /**
     * Constructor con inyección de dependencias.
     * Valida que las dependencias no sean null (fail-fast).
     *
     * @param scanner         Scanner compartido para entrada de usuario
     * @param empleadoService Servicio de empleados
     * @throws IllegalArgumentException si alguna dependencia es null
     */
    public MenuHandler(Scanner scanner, EmpleadoServiceImpl empleadoService) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner no puede ser null");
        }
        if (empleadoService == null) {
            throw new IllegalArgumentException("EmpleadoService no puede ser null");
        }
        this.scanner = scanner;
        this.empleadoService = empleadoService;
    }

    /**
     * Opción de menú: crear empleado.
     * (Por ahora sin implementar).
     */
    public void crearEmpleado() {
        try { 
        System.out.println("== Crear empleado ==");
         
         System.out.print("Nombre: ");
         String nombre = scanner.nextLine().trim();
         System.out.print("Apellido: ");
         String apellido = scanner.nextLine().trim();
         System.out.print("DNI: ");
         String dni = scanner.nextLine().trim();
         System.out.print("Email: ");
         String email = scanner.nextLine().trim();
         System.out.print("Area: ");
         String area = scanner.nextLine().trim();
         System.out.print("Ingrese fecha de creación (AAAA-MM-DD): ");
         LocalDate fechaIngreso = LocalDate.parse(scanner.nextLine());
         
                  
         System.out.println("Datos de Legajo (se requiere crear legajo junto al empleado)");
         
         System.out.print("Numero de Legajo: ");
         String numeroLegajo = scanner.nextLine().trim();
         
         EstadoLegajo estado = leerEstadoEmpleado();
         
         System.out.print("Categoria: ");
         String categoria = scanner.nextLine().trim();
         System.out.print("Observaciones: ");
         String observaciones = scanner.nextLine().trim();
         System.out.print("Ingrese fecha de creación (AAAA-MM-DD): ");
         LocalDate fechaAlta = LocalDate.parse(scanner.nextLine());
         
         Legajo legajoNuevo = new Legajo(numeroLegajo, categoria,  estado,
                  fechaAlta, observaciones);
         Empleado empleadoNuevo = new Empleado(nombre, apellido, dni, email,fechaIngreso, area, legajoNuevo);
         
         empleadoService.insertar(empleadoNuevo);
        }
          catch (Exception e) {
            System.err.println("Error al crear persona: " + e.getMessage());
        }
         
    }

    
    private EstadoLegajo leerEstadoEmpleado() {
    while (true) {
        System.out.println("Seleccione el estado del empleado:");
        System.out.println("1 - ACTIVO");
        System.out.println("2 - INACTIVO");
        System.out.print("Opción: ");

        String opcion = scanner.nextLine();

        switch (opcion) {
            case "1":
                return EstadoLegajo.ACTIVO;
            case "2":
                return EstadoLegajo.INACTIVO;
            default:
                System.out.println("Opción inválida. Intente nuevamente.\n");
        }
    }
}

    
    
    /**
     * Muestra los empleados activos.
     */
    public void listarEmpleados() {
        System.out.println(EMPLOYEE_LIST_HEADER);
        try {
            List<Empleado> empleados = empleadoService.getAll();
            if (empleados.isEmpty()) {
                System.out.println("No hay empleados.");
                return;
            }
            printEmpleados(empleados);
        } catch (Exception e) {
            System.err.println("\nERROR al listar empleados: " + e.getMessage());
        }
    }

    private void printEmpleados(List<Empleado> empleados) {
        for (Empleado empleado : empleados) {
            System.out.println(empleado);
            System.out.println(EMPLOYEE_SEPARATOR);
        }
    }

    /**
     * Opción de menú: actualizar empleado.
     * (Por ahora sin implementar).
     */
    public void actualizarEmpleado() {
        throw new UnsupportedOperationException("Actualizar empleado aún no está implementado.");
    }

    /**
     * Opción de menú: eliminar empleado.
     * (Por ahora sin implementar).
     */
    public void eliminarEmpleado() {
        throw new UnsupportedOperationException("Eliminar empleado aún no está implementado.");
    }

    /**
     * Opción de menú: buscar empleado por ID (o DNI según se defina luego).
     * (Por ahora sin implementar).
     */
    public void buscarEmpleadoID() {
        try {
            System.out.println("== Buscar empleado por ID ==");
            System.out.print("ID de la persona a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine());
            Empleado empleado = empleadoService.getById(id);
            if (empleado == null) {
                System.out.println("Empleado no encontrado con ID: " + id);
                return;
            }
            System.out.println("Empleado encontrado:");
            System.out.println(empleado);
        } catch (Exception e) {
            System.err.println("ERROR al buscar empleado: " + e.getMessage());
        }
    }

    /**
     * Opción de menú: crear legajo.
     * (Por ahora sin implementar).
     */
    public void crearLegajo() {
        throw new UnsupportedOperationException("Crear legajo aún no está implementado.");
    }

    /**
     * Opción de menú: listar legajos.
     * (Por ahora sin implementar).
     */
    public void listarLegajos() {
        throw new UnsupportedOperationException("Actualizar legajo aún no está implementado.");

    /**
     * Opción de menú: actualizar legajo.
     * (Por ahora sin implementar).
     */
    public void actualizarLegajo() {
        throw new UnsupportedOperationException("Actualizar legajo aún no está implementado.");
    }

    /**
     * Opción de menú: eliminar legajo.
     * (Por ahora sin implementar).
     */
    public void eliminarLegajo() {
        throw new UnsupportedOperationException("Eliminar legajo aún no está implementado.");
    }

    /**
     * Opción de menú: listar legajos por estado.
     * (Por ahora sin implementar).
     */
    public void listarLegajoPorEstado() {
        throw new UnsupportedOperationException("Listar legajos por estado aún no está implementado.");
    }
}