package main;

import java.util.Scanner;

import dao.EmpleadoDAO;
import dao.LegajoDAO;
import service.EmpleadoServiceImpl;
import service.LegajoServiceImpl;

public class AppMenu {

    private static final int EXIT_OPTION = 0;
    private static final String INVALID_INPUT_MESSAGE = "Entrada invalida. Por favor, ingrese un número.";
    private static final String INVALID_OPTION_MESSAGE = "Opción no válida.";
    private static final String EXIT_MESSAGE = "Saliendo...";

    /**
     * Scanner único compartido por toda la aplicación. IMPORTANTE: Solo debe
     * haber UNA instancia de Scanner(System.in). Múltiples instancias causan
     * problemas de buffering de entrada.
     */
    private final Scanner scanner;

    /**
     * Handler que ejecuta las operaciones del menú. Contiene toda la lógica de
     * interacción con el usuario.
     */
    private final MenuHandler menuHandler;

    public AppMenu() {
        this.scanner = new Scanner(System.in);
        EmpleadoServiceImpl empleadoService = createEmpleadoService();
        this.menuHandler = new MenuHandler(scanner, empleadoService);
    }

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
     * Muestra el menú principal, lee la opción del usuario y la convierte a int.
     * Maneja internamente los errores de formato de número.
     *
     * @return La opción ingresada o null si la entrada fue inválida.
     */
    private Integer readMenuOption() {
        MenuDisplay.mostrarMenuPrincipal();
        String input = scanner.nextLine();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println(INVALID_INPUT_MESSAGE);
            return null;
        }
    }

    /**
     * Procesa la opción seleccionada por el usuario y delega a MenuHandler.
     *
     * @param option Número de opción ingresado por el usuario
     * @return true si el menú debe continuar, false si debe finalizar
     */
    private boolean handleMenuOption(int option) {
        switch (option) {
            case 1 -> menuHandler.crearEmpleado();
            case 2 -> menuHandler.listarEmpleados();
            case 3 -> menuHandler.actualizarEmpleado();
            case 4 -> menuHandler.eliminarEmpleado();
            case 5 -> menuHandler.buscarEmpleadoID();
            case 6 -> menuHandler.crearLegajo();
            case 7 -> menuHandler.listarLegajos();
            case 8 -> menuHandler.actualizarLegajo();
            case 9 -> menuHandler.eliminarLegajo();
            case 10 -> menuHandler.listarLegajoPorEstado();
            case EXIT_OPTION -> {
                System.out.println(EXIT_MESSAGE);
                return false;
            }
            default -> System.out.println(INVALID_OPTION_MESSAGE);
        }
        return true;
    }

    /**
     * Método que crea la cadena de dependencias.
     * <p>
     * Flujo de creación:
     * 1- EmpleadoDAO → acceso a datos de empleados
     * 2- LegajoDAO → acceso a datos de legajos
     * 3- LegajoServiceImpl → usa LegajoDAO
     * 4- EmpleadoServiceImpl → usa EmpleadoDAO y LegajoServiceImpl
     *
     * @return EmpleadoServiceImpl completamente inicializado
     */
    private EmpleadoServiceImpl createEmpleadoService() {
        // 1. Creamos los DAOs (Data Access Objects)
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        LegajoDAO legajoDAO = new LegajoDAO();
        // 2. Creamos el servicio de Legajo
        LegajoServiceImpl legajoService = new LegajoServiceImpl(legajoDAO);
        // 3. Creamos y retornamos el servicio de Empleado
        return new EmpleadoServiceImpl(empleadoDAO, legajoService);
    }
}