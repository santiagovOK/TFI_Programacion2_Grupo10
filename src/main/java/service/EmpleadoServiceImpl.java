package service;

import dao.EmpleadoDAO;
import entities.Empleado;
import java.util.List;

/**
 * Implementación del servicio de negocio para la entidad Empleado. Capa
 * intermedia entre la UI y el DAO que aplica validaciones de negocio complejas.
 *
 * Responsabilidades: - Validar datos de persona ANTES de persistir (......) -
 * Garantizar unicidad en el sistema (.....) - COORDINAR operaciones entre
 * Empleado y Legajo (transaccionales) - Proporcionar métodos de búsqueda
 * especializados - Implementar eliminación SEGURA de empleado/legajo (evita FKs
 * huérfanas)
 *
 * Patrón: Service Layer con inyección de dependencias y coordinación de
 * servicios
 */
public class EmpleadoServiceImpl implements EmpleadoService<Empleado> {

    /**
     * DAO para acceso a datos de empleados. Inyectado en el constructor
     * (Dependency Injection).
     */
    private final EmpleadoDAO empleadoDAO;
    private final LegajoServiceImpl legajoService;

    /**
     * constructor con inyección de dependencias
     */
    public EmpleadoServiceImpl(EmpleadoDAO empleadoDAO, LegajoServiceImpl legajoService) {
        if (empleadoDAO == null) {
            throw new IllegalArgumentException("EmpleadoDAO no puede ser null");
        }
        if (legajoService == null) {
            throw new IllegalArgumentException("LegajoService no puede ser null");
        }
        this.empleadoDAO = empleadoDAO;
        this.legajoService = legajoService;
    }

        /**
     * faltaría agregar los metodos crud - las validaciones - busquedas
     */
   
}
