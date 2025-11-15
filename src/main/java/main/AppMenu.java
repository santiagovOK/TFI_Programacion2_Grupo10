package main;
import java.util.Scanner;
import dao.EmpleadoDAO;
import dao.LegajoDAO;
import service.EmpleadoServiceImpl;
import service.LegajoServiceImpl;

// ... existing code ...

public class AppMenu {

    private static final int EXIT_OPTION = 0;
    private static final String INVALID_INPUT_MESSAGE = "Entrada invalida. Por favor, ingrese un número.";

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
    /**
     * Flag que controla el loop principal del menú. Se setea a false cuando el
     * usuario selecciona "0 - Salir".
     */
    private boolean running;

    // ... existing code ...

    public AppMenu() {
        this.scanner = new Scanner(System.in);
        EmpleadoServiceImpl empleadoService = createEmpleadoService();
        this.menuHandler = new MenuHandler(scanner, empleadoService);
        this.running = true;
    }

    /**
     * Punto de entrada de la aplicación Java. Crea instancia de AppMenu y
     * ejecuta el menú principal.
     *
     * @param args Argumentos de línea de comandos (no usados)
     */
    public static void main(String[] args) {
        AppMenu app = new AppMenu();
        app.run();
    }

    /**
     * Loop principal del menú.
     *
     * Flujo: 1. Mientras running==true: a. Muestra menú con
     * MenuDisplay.mostrarMenuPrincipal() b. Lee opción del usuario
     * (scanner.nextLine()) c. Convierte a int (puede lanzar
     * NumberFormatException) d. Procesa opción con processOption() 2. Si el
     * usuario ingresa texto no numérico: Muestra mensaje de error y continúa 3.
     * Cuando running==false (opción 0): Sale del loop y cierra Scanner
     *
     * Manejo de errores: - NumberFormatException: Captura entrada no numérica
     * (ej: "abc") - Muestra mensaje amigable y NO termina la aplicación - El
     * usuario puede volver a intentar
     *
     * IMPORTANTE: El Scanner se cierra al salir del loop. Cerrar
     * Scanner(System.in) cierra System.in para toda la aplicación.
     */
    public void run() {
        while (running) {
            try {
                MenuDisplay.mostrarMenuPrincipal();
                int opcion = Integer.parseInt(scanner.nextLine());
                processOption(opcion);
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Por favor, ingrese un número.");
            }
        }
        scanner.close();
    }

    /**
     * Procesa la opción seleccionada por el usuario y delega a MenuHandler.
     *
     * Mapeo de opciones (corresponde a MenuDisplay): 1 → Crear persona (con
     * domicilio opcional) 2 → Listar personas (todas o filtradas) 3 →
     * Actualizar persona 4 → Eliminar persona (soft delete) 5 → Crear domicilio
     * independiente 6 → Listar domicilios 7 → Actualizar domicilio por ID
     * (afecta a todas las personas que lo comparten) 8 → Eliminar domicilio por
     * ID (PELIGROSO - puede dejar FKs huérfanas) 9 → Actualizar domicilio de
     * una persona (afecta a todas las personas que lo comparten) 10 → Eliminar
     * domicilio de una persona (SEGURO - actualiza FK primero) 0 → Salir (setea
     * running=false para terminar el loop)
     *
     * Opción inválida: Muestra mensaje y continúa el loop.
     *
     * IMPORTANTE: Todas las excepciones de MenuHandler se capturan dentro de
     * los métodos. processOption() NO propaga excepciones al caller (run()).
     *
     * @param opcion Número de opción ingresado por el usuario
     */
    private void processOption(int opcion) {
        switch (opcion) {
            case 1 ->
                menuHandler.crearEmpleado();
            case 2 ->
                menuHandler.listarEmpleados();
            case 3 ->
                menuHandler.actualizarEmpleado();
            case 4 ->
                menuHandler.eliminarEmpleado();
            case 5 ->
                menuHandler.buscarEmpleadoID();
            case 6 ->
                menuHandler.crearLegajo();
            case 7 ->
                menuHandler.listarLegajos();
            case 8 ->
                menuHandler.actualizarLegajo();
            case 9 ->
                menuHandler.eliminarLegajo();
            case 10 ->
                menuHandler.listarLegajoPorEstado();
            case 0 -> {
                System.out.println("Saliendo...");
                running = false;
            }
            default ->
                System.out.println("Opción no valida.");
        }
    }

    /**
     * método que crea la cadena de dependencias
     *
     * Flujo de creación: 1- EmpleadoDAO → ccceso a datos de empleados 2-
     * LegajoDAO → ccceso a datos de legajos 3- LegajoServiceImpl → usa
     * LegajoDAO 4- EmpleadoServiceImpl → usa EmpleadoDAO y LegajoServiceImpl
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
