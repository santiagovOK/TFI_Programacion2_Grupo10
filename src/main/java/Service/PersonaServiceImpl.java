package Service;

/**
 * Implementación del servicio de negocio para la entidad Persona.
 * Capa intermedia entre la UI y el DAO que aplica validaciones de negocio complejas.
 *
 * Responsabilidades:
 * - Validar datos de persona ANTES de persistir (......)
 * - Garantizar unicidad en el sistema (.....)
 * - COORDINAR operaciones entre Persona y Legajo (transaccionales)
 * - Proporcionar métodos de búsqueda especializados
 * - Implementar eliminación SEGURA de persona/legajo (evita FKs huérfanas)
 *
 * Patrón: Service Layer con inyección de dependencias y coordinación de servicios
 */
public class PersonaServiceImpl implements GenericService<Persona> {
    /**
     * DAO para acceso a datos de personas.
     * Inyectado en el constructor (Dependency Injection).
     */
    private final PersonaDAO personaDAO;

    

}