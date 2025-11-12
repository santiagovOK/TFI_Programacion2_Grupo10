package Service;

/**
 * Implementación del servicio de negocio para la entidad Legajo.
 * Capa intermedia entre la UI y el DAO que aplica validaciones de negocio.
 *
 * Responsabilidades:
 * - Validar que los datos del legajo sean correctos ANTES de persistir
 * - Aplicar reglas de negocio
 * - Delegar operaciones de BD al DAO
 * - Transformar excepciones técnicas en errores de negocio comprensibles
 *
 * Patrón: Service Layer con inyección de dependencias
 */
public class LegajoServiceImpl implements GenericService<Legajo> {

    
    private final GenericDAO<Legajo> legajoDAO;



}