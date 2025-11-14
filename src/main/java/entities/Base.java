package entities;

/**
 * Clase base abstracta para todas las entidades del sistema.
 *
 * Propósito:
 * - Proporcionar atributos comunes a las entidades Empleado y Legajo
 * - Implementar el patrón de herencia para evitar duplicación de código
 * - Soportar eliminación lógica en lugar de eliminación física
 *
 * Patrón de diseño: Template (clase base abstracta)
 */
public abstract class Base {
    
    // Atributos compartidos por clases hijas
    private long id = 0;                // Hacemos explicito que el tipo de dato long se inicializa en 0
    private boolean eliminado = false;  // Por defecto el valor de eliminado es falso cuando estamos creando una entidad

    
    // Constructor explícito para que las clases hijas hereden los atributos
    public Base() {
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
    
    // Métodos abstractos
    
    public abstract void eliminarRegistro(); // Permitirá la eliminación lógica
    
}
