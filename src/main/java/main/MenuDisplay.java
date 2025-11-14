/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
public class MenuDisplay {
    /**
     * Muestra el menú principal con todas las opciones CRUD.
     *
     * Opciones de Empleado (1-5):
     * 1. Crear Empleado: Permite crear persona con domicilio opcional
     * 2. Listar Empleado: Lista todas o busca por nombre/apellido
     * 3. Actualizar Empleado: Actualiza datos de Empleado
     * 4. Eliminar Empleado: Soft delete de empleado
     * 5. Buscar Empleado por Dni: Busca empleado por DNI
     * 
     * Opciones de Domicilios (6-10):
     * 6. Crear Legajo: Crea Lejado
     * 7. Listar Legajos: Lista todos los Legajos 
     * 8. Actualizar Legajo por ID: Actualiza Legajo directamente 
     * 9. Eliminar Legajo : Eliminacion Logica de Legajo
     * 10. Listar Legajos por Estado : ACTIVO - INACTIVO
     * 
     *
     * Opción de salida:
     * 0. Salir: Termina la aplicación
     *
     * Formato:
     * - Separador visual "========= MENU ========="
     * - Lista numerada clara
     * - Prompt "Ingrese una opcion: " sin salto de línea (espera input)
     *
     * Nota: Los números de opción corresponden al switch en AppMenu.processOption().
     */
    public static void mostrarMenuPrincipal() {
        System.out.println("\n========= MENU =========");
        System.out.println("1. Crear Empleado");
        System.out.println("2. Listar Empleados");
        System.out.println("3. Actualizar empleado");
        System.out.println("4. Eliminar empleado");
        System.out.println("5. Buscar empleado por DNI");
        System.out.println("6. Crear Legajo");
        System.out.println("7. Listar Legajo");
        System.out.println("8. Actualizar Legajo");
        System.out.println("9. Eliminar Legajo ");
        System.out.println("10. Listar Legajo por Estado - ACTIVO - INACTIVO");
        System.out.println("0. Salir");
        System.out.print("Ingrese una opcion: ");
    }
}