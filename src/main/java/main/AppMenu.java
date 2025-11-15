package main;

import java.util.Scanner;

import dao.EmpleadoDAO;
import dao.LegajoDAO;
import service.EmpleadoServiceImpl;
import service.LegajoServiceImpl;

public class AppMenu {

    // --- Constantes ---
    private static final int EXIT_OPTION = 0;
    private static final String INVALID_INPUT_MESSAGE = "Entrada invalida. Por favor, ingrese un número.";
    private static final String INVALID_OPTION_MESSAGE = "Opción no válida.";
    private static final String EXIT_MESSAGE = "Saliendo...";
    private static final String BUSINESS_RULE_ERROR_PREFIX = "\n[ERROR DE REGLA DE NEGOCIO]: ";
    private static final String DATA_ERROR_PREFIX = "\n[ERROR DE DATOS]: ";
    private static final String UNEXPECTED_ERROR_PREFIX = "\n[ERROR INESPERADO]: ";
    private static final String PRESS_ENTER_TO_CONTINUE_MESSAGE = "\nPresione Enter para continuar...";

    // --- Dependencias ---
    private final Scanner scanner;
    private final MenuHandler menuHandler;

    /**
     * Constructor de AppMenu.
     * Aquí se crean e inyectan todas las dependencias de la aplicación.
     * Creamos cada DAO y Servicio UNA SOLA VEZ y los compartimos.
     */
    public AppMenu() {
        this.scanner = new Scanner(System.in);
        this.menuHandler = initializeMenuHandler();
    }

    /**
     * Inicia el bucle principal de la aplicación.
     */
    public void run() {
        boolean keepRunning = true;
        while (keepRunning) {
            Integer option = readMenuOption();
            if (option != null) {
                keepRunning = handleMenuOption(option);
            }
        }
        scanner.close();
    }

    /**
     * Muestra el menú principal y lee la opción del usuario.
     */
    private Integer readMenuOption() {
        MenuDisplay.mostrarMenuPrincipal(); // Asumimos que esta clase existe
        String input = scanner.nextLine();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println(INVALID_INPUT_MESSAGE);
            return null;
        }
    }

    /**
     * Procesa la opción del usuario y delega al MenuHandler.
     */
    private boolean handleMenuOption(int option) {
        if (option == EXIT_OPTION) {
            System.out.println(EXIT_MESSAGE);
            return false;
        }

        MenuAction action = switch (option) {
            // Opciones de Empleado
            case 1 -> () -> menuHandler.crearEmpleado();
            case 2 -> () -> menuHandler.listarEmpleados();
            case 3 -> () -> menuHandler.actualizarEmpleado();
            case 4 -> () -> menuHandler.eliminarEmpleado();
            case 5 -> () -> menuHandler.buscarEmpleadoID();

            // Opciones de Legajo
            case 6 -> () -> menuHandler.crearLegajo();
            case 7 -> () -> menuHandler.listarLegajos();
            case 8 -> () -> menuHandler.actualizarLegajo();
            case 9 -> () -> menuHandler.eliminarLegajo();
            case 10 -> () -> menuHandler.listarLegajoPorEstado();

            default -> null;
        };

        if (action == null) {
            System.out.println(INVALID_OPTION_MESSAGE);
            return true;
        }

        executeMenuAction(action);
        return true;
    }

    /**
     * Ejecuta una acción de menú y maneja de forma centralizada
     * todos los tipos de errores y la pausa para el usuario.
     */
    private void executeMenuAction(MenuAction action) {
        try {
            action.run();
        } catch (UnsupportedOperationException e) {
            System.err.println(BUSINESS_RULE_ERROR_PREFIX + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println(DATA_ERROR_PREFIX + e.getMessage());
        } catch (Exception e) {
            System.err.println(UNEXPECTED_ERROR_PREFIX + e.getMessage());
            e.printStackTrace();
        }

        System.out.println(PRESS_ENTER_TO_CONTINUE_MESSAGE);
        scanner.nextLine();
    }

    /**
     * Inicializa la cadena de dependencias (DAOs, Services, MenuHandler).
     */
    private MenuHandler initializeMenuHandler() {
        // 1. DAOs (capa de datos)
        LegajoDAO legajoDAO = new LegajoDAO();
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();

        // 2. Servicios (capa de negocio)
        LegajoServiceImpl legajoService = new LegajoServiceImpl(legajoDAO);
        EmpleadoServiceImpl empleadoService = new EmpleadoServiceImpl(empleadoDAO, legajoService);

        // 3. MenuHandler
        return new MenuHandler(this.scanner, empleadoService, legajoService);
    }

    /**
     * Interfaz funcional para representar cualquier acción del menú
     * que pueda lanzar una excepción.
     */
    @FunctionalInterface
    private interface MenuAction {
        void run() throws Exception;
    }
}