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
 * Gestiona toda la lógica de interacción con el usuario.
 */
public class MenuHandler {

    private final Scanner scanner;
    private final EmpleadoServiceImpl empleadoService;
    private final LegajoServiceImpl legajoService;

    // Constantes para logging / mensajes
    private static final String EMPLOYEE_LIST_HEADER = "--- Listando Empleados ---";
    private static final String LEGAJO_LIST_HEADER = "--- Listando Legajos ---";
    private static final String SEPARATOR = "------------------------------";
    private static final String NO_EMPLOYEES_FOUND = "No hay empleados para mostrar.";
    private static final String NO_LEGAJOS_FOUND = "No hay legajos para mostrar.";

    // Títulos / prompts reutilizables
    private static final String TITLE_CREAR_EMPLEADO_LEGAJO = "== Crear Empleado y Legajo ==";
    private static final String TITLE_BUSCAR_EMPLEADO = "== Buscar empleado por ID ==";
    private static final String PROMPT_ID_EMPLEADO = "ID del empleado a buscar: ";
    private static final String INVALID_OPTION_STATE_MESSAGE = "Opción inválida. Intente nuevamente.\n";

    // Constantes para operaciones no implementadas / reglas de negocio
    private static final String ERROR_ACTUALIZAR_EMPLEADO_NOT_IMPLEMENTED =
            "Actualizar empleado aún no está implementado.";
    private static final String ERROR_ELIMINAR_EMPLEADO_NOT_IMPLEMENTED =
            "Eliminar empleado aún no está implementado.";
    private static final String ERROR_CREAR_LEGAJO_NOT_SUPPORTED =
            "Crear legajo directamente está deshabilitado; debe crearse junto con un Empleado.";
    private static final String ERROR_ACTUALIZAR_LEGAJO_NOT_IMPLEMENTED =
            "Actualizar legajo aún no está implementado.";
    private static final String ERROR_ELIMINAR_LEGAJO_NOT_SUPPORTED =
            "Eliminar legajo directamente está deshabilitado; debe eliminarse desde el Empleado.";
    private static final String ERROR_LISTAR_LEGAJOS_POR_ESTADO_NOT_IMPLEMENTED =
            "Listar legajos por estado aún no está implementado.";

    /**
     * Constructor con inyección de dependencias (DI).
     * Recibe los servicios que necesita para operar.
     */
    public MenuHandler(Scanner scanner,
                       EmpleadoServiceImpl empleadoService,
                       LegajoServiceImpl legajoService) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner no puede ser null");
        }
        if (empleadoService == null) {
            throw new IllegalArgumentException("EmpleadoService no puede ser null");
        }
        if (legajoService == null) {
            throw new IllegalArgumentException("LegajoService no puede ser null");
        }
        this.scanner = scanner;
        this.empleadoService = empleadoService;
        this.legajoService = legajoService;
    }

    // --- MÉTODOS DE EMPLEADO ---

    /**
     * Pide datos al usuario para crear un Empleado y su Legajo.
     * Lanza excepciones si la validación falla; AppMenu las captura.
     */
    public void crearEmpleado() throws Exception {
        System.out.println(TITLE_CREAR_EMPLEADO_LEGAJO);
        Empleado empleadoNuevo = readEmpleadoWithLegajoFromInput();
        empleadoService.insertar(empleadoNuevo);
        System.out.println("\n¡Empleado creado exitosamente!");
    }

    public void listarEmpleados() throws Exception {
        List<Empleado> empleados = empleadoService.getAll();
        printList(EMPLOYEE_LIST_HEADER, NO_EMPLOYEES_FOUND, empleados);
    }

    public void actualizarEmpleado() {
        throw new UnsupportedOperationException(ERROR_ACTUALIZAR_EMPLEADO_NOT_IMPLEMENTED);
    }

    public void eliminarEmpleado() {
        throw new UnsupportedOperationException(ERROR_ELIMINAR_EMPLEADO_NOT_IMPLEMENTED);
    }

    /**
     * Busca un empleado por ID. Las excepciones (NumberFormatException,
     * IllegalArgumentException, etc.) son propagadas a AppMenu.
     */
    public void buscarEmpleadoID() throws Exception {
        System.out.println(TITLE_BUSCAR_EMPLEADO);
        long id = readEmpleadoIdFromInput();
        Empleado empleado = empleadoService.getById(id);
        System.out.println("Empleado encontrado:");
        System.out.println(empleado);
    }

    // --- MÉTODOS DE LEGAJO ---

    /**
     * Intenta crear un legajo solo.
     * Está deshabilitado por regla de negocio (Legajo se crea junto con Empleado).
     */
    public void crearLegajo() throws Exception {
        throw new UnsupportedOperationException(ERROR_CREAR_LEGAJO_NOT_SUPPORTED);
    }

    /**
     * Muestra todos los legajos activos.
     */
        List<Legajo> legajos = legajoService.getAll();
        printList(LEGAJO_LIST_HEADER, NO_LEGAJOS_FOUND, legajos);

    public void actualizarLegajo() {
        throw new UnsupportedOperationException(ERROR_ACTUALIZAR_LEGAJO_NOT_IMPLEMENTED);
    }

    /**
     * Intenta eliminar un legajo solo.
     * Está deshabilitado por regla de negocio (se elimina desde Empleado).
     */
    public void eliminarLegajo() throws Exception {
        throw new UnsupportedOperationException(ERROR_ELIMINAR_LEGAJO_NOT_SUPPORTED);
    }

    public void listarLegajoPorEstado() {
        throw new UnsupportedOperationException(ERROR_LISTAR_LEGAJOS_POR_ESTADO_NOT_IMPLEMENTED);
    }

    // --- Helpers privados (entrada de datos) ---

    /**
     * Lee desde consola todos los datos necesarios para construir
     * un Empleado con su Legajo asociado.
     */
    private Empleado readEmpleadoWithLegajoFromInput() {
        // Datos de Empleado
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("Apellido: ");
        String apellido = scanner.nextLine().trim();

        System.out.print("DNI: ");
        String dni = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Área: ");
        String area = scanner.nextLine().trim();

        System.out.print("Fecha de Ingreso (AAAA-MM-DD): ");
        LocalDate fechaIngreso = LocalDate.parse(scanner.nextLine());

        System.out.println("\n--- Datos del Legajo (Requerido) ---");

        // Datos de Legajo
        System.out.print("Número de Legajo: ");
        String numeroLegajo = scanner.nextLine().trim();

        EstadoLegajo estado = leerEstadoLegajo();

        System.out.print("Categoría: ");
        String categoria = scanner.nextLine().trim();

        System.out.print("Observaciones (opcional): ");
        String observaciones = scanner.nextLine().trim();

        System.out.print("Fecha de Alta (AAAA-MM-DD): ");
        LocalDate fechaAlta = LocalDate.parse(scanner.nextLine());

        Legajo legajoNuevo = new Legajo(numeroLegajo, categoria, estado, fechaAlta, observaciones);
        return new Empleado(nombre, apellido, dni, email, fechaIngreso, area, legajoNuevo);
    }

    /**
     * Helper para leer y validar el ID de empleado desde consola.
     */
    private long readEmpleadoIdFromInput() {
        System.out.print(PROMPT_ID_EMPLEADO);
        String input = scanner.nextLine();
        // NumberFormatException (IllegalArgumentException) se propaga a AppMenu
        return Long.parseLong(input);
    }

    /**
     * Helper privado para leer y validar el Estado del Legajo.
     */
    private EstadoLegajo leerEstadoLegajo() {
        while (true) {
            System.out.println("Seleccione el estado del legajo:");
            System.out.println("  1 - ACTIVO");
            System.out.println("  2 - INACTIVO");
            System.out.print("Opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    return EstadoLegajo.ACTIVO;
                case "2":
                    return EstadoLegajo.INACTIVO;
                default:
                    System.out.println(INVALID_OPTION_STATE_MESSAGE);
            }
        }
    }

    // --- Helpers privados (presentación) ---

    /**
     * Imprime una lista de elementos con encabezado, mensaje de lista vacía y separador.
     */
    private void printList(String header, String emptyMessage, List<?> items) {
        System.out.println(header);
        if (items == null || items.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        for (Object item : items) {
            System.out.println(item); // Asume que .toString() está bien formateado
            System.out.println(SEPARATOR);
        }
    }
}