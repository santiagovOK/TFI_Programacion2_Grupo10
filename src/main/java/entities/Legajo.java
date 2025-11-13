package entities;

import java.util.Date;



/**
 * Entidad que representa un legajo en el sistema.
 * Hereda de Base para obtener id y eliminado.
 *
 * Relación con Empleado:
 * Asociación unidireccional 1 a 1. 
 * Empleado -> Legajo
 *
 * Tabla BD: legajo
 * Campos:
 * - id: int AUTO_INCREMENT PRIMARY KEY UNIQUE NOT NULL (heredado de Base)
 * - eliminado: BOOLEAN DEFAULT FALSE NOT NULL (heredado de Base)
 * - nro_legajo: String NOT NULL 
 * - categoria: String
 * - estado: enum('ACTIVO, INCACTIVO') NOT NULL
 * - feha_alta: date
 * - observaciones: String
 */
public class Legajo extends Base {
    
    // Atributos propios
    private String numeroLegajo;
    private String categoria;
    private EstadoLegajo estado; // Enum (estado contractual activo o inactivo)
    private Date fechaAlta; // Creación administrativa en el sistema
    private String observaciones;
    
    // Constructor
    
    public Legajo(String numeroLegajo, String categoria, EstadoLegajo estado, Date fechaAlta, String observaciones) {
        super(); // Llama al constructor de Base que contiene id y eliminado por defecto
        this.numeroLegajo = numeroLegajo;
        this.categoria = categoria;
        this.estado = estado;
        this.fechaAlta = fechaAlta;
        this.observaciones = observaciones;
    }

    public Legajo() {
        super();
    }
    
    
  
    
    // Getters y Setters

    public String getNumeroLegajo() {
        return numeroLegajo;
    }

    public void setNumeroLegajo(String numeroLegajo) {
        this.numeroLegajo = numeroLegajo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public EstadoLegajo getEstado() {
        return estado;
    }

    public void setEstado(EstadoLegajo estado) {
        this.estado = estado;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    // Métodos

    @Override
    public void eliminarRegistro() {  // Borrado lógico
        this.setEliminado(true);  // Para el atributo eliminado de la superclase
        this.setEstado(EstadoLegajo.INACTIVO); // Es necesario también cambiar el estado de legajo por su situación contractual.
        
    }

    @Override
    public String toString() {
        return "NumeroLegajo= " + getNumeroLegajo() + ", categoria= " + getCategoria() + ", estado= " + getEstado() + ", fechaAlta= " + getFechaAlta() + ", observaciones= " + getObservaciones();
    }
    
}
