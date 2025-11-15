package main;

/**
 * Clase utilitaria para mostrar el menú de la aplicación.
 * Solo contiene métodos estáticos de visualización (no tiene estado).
 *
 * Responsabilidades:
 * - Mostrar el menú principal con todas las opciones disponibles
 * - Formatear la salida de forma consistente
 *
 * Patrón: Utility class (solo métodos estáticos, no instanciable)
 *
 * IMPORTANTE: Esta clase NO lee entrada del usuario.
 * Solo muestra el menú. AppMenu es responsable de leer la opción.
 */
public final class MenuDisplay {

    private static final String MENU_HEADER = "\n========= MENU =========";

    private static final String OPTION_1_CREAR_EMPLEADO = "1. Crear Empleado";
    private static final String OPTION_2_LISTAR_EMPLEADOS = "2. Listar Empleados";
    private static final String OPTION_3_ACTUALIZAR_EMPLEADO = "3. Actualizar empleado";
    private static final String OPTION_4_ELIMINAR_EMPLEADO = "4. Eliminar empleado";
    private static final String OPTION_5_BUSCAR_EMPLEADO_DNI = "5. Buscar empleado por DNI";

    private static final String OPTION_6_CREAR_LEGAJO = "6. Crear Legajo";
    private static final String OPTION_7_LISTAR_LEGAJOS = "7. Listar Legajo";
    private static final String OPTION_8_ACTUALIZAR_LEGAJO = "8. Actualizar Legajo";
    private static final String OPTION_9_ELIMINAR_LEGAJO = "9. Eliminar Legajo ";
    private static final String OPTION_10_LISTAR_LEGAJOS_POR_ESTADO =
            "10. Listar Legajo por Estado - ACTIVO - INACTIVO";

    private static final String OPTION_0_SALIR = "0. Salir";
    private static final String PROMPT_MESSAGE = "Ingrese una opcion: ";

    /**
     * Constructor privado para evitar la instanciación.
     * Esta clase es puramente utilitaria.
     */
    private MenuDisplay() {
        // Evita la instanciación
    }

    /**
     * Muestra el menú principal escribiendo en {@link System#out}.
     * Se mantiene por compatibilidad con el código existente.
     */
    public static void mostrarMenuPrincipal() {
        mostrarMenuPrincipal(System.out);
    }

    /**
     * Muestra el menú principal usando el {@link java.io.PrintStream} indicado.
     * Permite redirigir la salida (por ejemplo, a tests o logs).
     *
     * @param out flujo de salida donde se imprimirá el menú
     */
    public static void mostrarMenuPrincipal(java.io.PrintStream out) {
        printMenuHeader(out);
        printEmpleadoOptions(out);
        printLegajoOptions(out);
        printExitOption(out);
        printPrompt(out);
    }

    private static void printMenuHeader(java.io.PrintStream out) {
        out.println(MENU_HEADER);
    }

    private static void printEmpleadoOptions(java.io.PrintStream out) {
        out.println(OPTION_1_CREAR_EMPLEADO);
        out.println(OPTION_2_LISTAR_EMPLEADOS);
        out.println(OPTION_3_ACTUALIZAR_EMPLEADO);
        out.println(OPTION_4_ELIMINAR_EMPLEADO);
        out.println(OPTION_5_BUSCAR_EMPLEADO_DNI);
    }

    private static void printLegajoOptions(java.io.PrintStream out) {
        out.println(OPTION_6_CREAR_LEGAJO);
        out.println(OPTION_7_LISTAR_LEGAJOS);
        out.println(OPTION_8_ACTUALIZAR_LEGAJO);
        out.println(OPTION_9_ELIMINAR_LEGAJO);
        out.println(OPTION_10_LISTAR_LEGAJOS_POR_ESTADO);
    }

    private static void printExitOption(java.io.PrintStream out) {
        out.println(OPTION_0_SALIR);
    }

    private static void printPrompt(java.io.PrintStream out) {
        out.print(PROMPT_MESSAGE);
    }
}