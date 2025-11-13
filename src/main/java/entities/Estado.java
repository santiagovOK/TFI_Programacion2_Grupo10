package entities;

/**
 * Define los estados contractuales de un legajo
 * Se utiliza un Enum para garantizar la integridad de los datos
 * y evitar valores arbitrarios.
 */
public enum Estado {
    /**
     * El empleado está contractualmente activo en la empresa.
     */
    ACTIVO,

    /**
     * El empleado no está activo (ej. licencia, suspensión, egreso).
     */
    INACTIVO
}