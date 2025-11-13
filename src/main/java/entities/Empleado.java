package entities;

import java.util.Date;

/**
 * Entidad que representa un empleado en el sistema.
 * Hereda de Base para obtener id y eliminado.
 *
 * Relación con Empleado:
 * Asociación unidireccional 1 a 1. 
 * Empleado -> Legajo
 *
 * Tabla BD: empleado
 * Campos:
 * - id: int AUTO_INCREMENT PRIMARY KEY UNIQUE NOT NULL (heredado de Base)
 * - eliminado: BOOLEAN DEFAULT FALSE NOT NULL (heredado de Base)
 * - nombre: String NOT NULL
 * - apellido: String NOT NULL
 * - dni: String UNIQUE NOT NULL
 * - email: String
 * - fecha_ingreso: Date
 * - area: String
 */
public class Empleado extends Base {
    
    // Atributos propios
    private String nombre; // obligatorio
    private String apellido; // obligatorio
    private String dni; // obligatorio, único e irrepetible
    private String email; // opcional, único
    private Date fechaIngreso; // Inicio de la relación laboral
    private String area;
    
    private Legajo legajo; // Asociación 1:1 unidireccional con Legajo
    
    // Constructores
    
    public Empleado(String nombre, String apellido, String dni, String email, Date fechaIngreso, String area, Legajo legajo) { // Completo para el DAO y trabajar desde la BD
        super(); // Llama al constructor de Base que contiene id y eliminado por defecto
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.fechaIngreso = fechaIngreso;
        this.area = area;
        this.legajo = legajo;
    }

    public Empleado() { // Explícito, para el Menú y su construcción a través de setters
        super(); 
    }
    
    
    // Getters y Setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFecha_ingreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Legajo getLegajo() {
        return legajo;
    }

    public void setLegajo(Legajo legajo) {
        this.legajo = legajo;
    }

    @Override
    public void eliminarRegistro() { // Borrado lógico
        super.setEliminado(true); 
    }

    @Override
    public String toString() {
        return "Empleado (ID: " + getId() + ")" +
               "\nNombre: " + getNombre() + 
               "\nApellido: " + getApellido() + 
               "\nDNI: " + getDni() + 
               "\nEmail: " + getEmail() + 
               "\nFecha de Ingreso: " + getFechaIngreso() + 
               "\nArea: " + getArea() +
               "\nEliminado: " + isEliminado() + 
               "\nLegajo: (" + getLegajo() + ").\n";
    }
    
    
    
    
}
