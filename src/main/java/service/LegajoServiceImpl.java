package service;

import dao.LegajoDAO;
import entities.Legajo;

import java.sql.Connection;
import java.util.List;

/**
 * Implementación del servicio de negocio para la entidad Legajo.
 * Capa intermedia entre la UI y el DAO que aplica validaciones de negocio.
 * <p>
 * Responsabilidades:
 * - Validar que los datos del legajo sean correctos ANTES de persistir
 * - Aplicar reglas de negocio
 * - Delegar operaciones de BD al DAO
 * - Transformar excepciones técnicas en errores de negocio comprensibles
 * <p>
 * Patrón: Service Layer con inyección de dependencias
 */
public class LegajoServiceImpl implements GenericService<Legajo> {

    private static final int NUMERO_LEGAJO_MAX_LENGTH = 20;
    private static final int CATEGORIA_MAX_LENGTH = 30;
    private static final int OBSERVACIONES_MAX_LENGTH = 255;

    private static final String ERROR_ID_INVALIDO = "El ID debe ser mayor a 0.";
    private static final String ERROR_LEGAJO_NULL = "El legajo no puede ser null";
    private static final String ERROR_LEGAJO_NO_EXISTE = "El legajo no existe";
    private static final String ERROR_NUMERO_LEGAJO_VACIO = "El número de legajo no puede estar vacío";
    private static final String ERROR_ESTADO_NULL = "El Estado del Legajo no puede ser null";
    private static final String ERROR_NUMERO_LEGAJO_LARGO =
            "El número de legajo no puede exceder los " + NUMERO_LEGAJO_MAX_LENGTH + " caracteres";
    private static final String ERROR_CATEGORIA_LARGA =
            "La categoría no puede exceder los " + CATEGORIA_MAX_LENGTH + " caracteres";
    private static final String ERROR_OBSERVACIONES_LARGAS =
            "Las observaciones no pueden exceder los " + OBSERVACIONES_MAX_LENGTH + " caracteres";
    private static final String ERROR_NUMERO_LEGAJO_DUPLICADO = "Error: El número de legajo ya existe.";
    private static final String ERROR_NUMERO_LEGAJO_DUPLICADO_OTRO =
            "Error: El número de legajo ya existe y pertenece a otra persona.";

    /**
     * DAO para acceso a datos de legajos.
     * Inyectado en el constructor (Dependency Injection).
     */
    private final LegajoDAO legajoDAO;

    /**
     * Constructor con inyección de dependencias.
     * Valida que el DAO no sea null.
     */
    public LegajoServiceImpl(LegajoDAO legajoDAO) {
        if (legajoDAO == null) {
            throw new IllegalArgumentException("LegajoDAO no puede ser null");
        }
        this.legajoDAO = legajoDAO;
    }

    // --- Métodos de Negocio Transaccionales (Llamados por EmpleadoService) ---

    /**
     * MÉTODO OBLIGATORIO DE LA INTERFAZ (GenericService).
     * <p>
     * Un Legajo no puede crearse por sí solo. Siempre debe crearse dentro de la
     * transacción de un Empleado.
     */
    @Override
    public void insertar(Legajo legajo) throws Exception {
        throw new UnsupportedOperationException(
                "Operación no soportada. Un Legajo no puede crearse por sí solo."
                        + " Debe crear un Empleado para que este método asigne el legajo."
        );
    }

    /**
     * Inserta un Legajo DENTRO de una transacción existente.
     * Este método es llamado por EmpleadoServiceImpl.
     * Valida las reglas de negocio propias de Legajo (incluyendo unicidad).
     */
    public void insertarTx(Legajo legajo, long empleadoId, Connection conn) throws Exception {
        validateLegajoData(legajo);
        validateNumeroLegajoUniqueForInsert(legajo);
        legajoDAO.crearTx(legajo, empleadoId, conn);
    }

    /**
     * Actualiza un Legajo DENTRO de una transacción existente.
     * Este método es llamado por EmpleadoServiceImpl.
     */
    public void actualizarTx(Legajo legajo, Connection conn) throws Exception {
        validateLegajoForUpdate(legajo);
        validateLegajoData(legajo);
        validateNumeroLegajoUniqueForUpdate(legajo);
        legajoDAO.actualizarTx(legajo, conn);
    }

    /**
     * Realiza la baja lógica de un Legajo DENTRO de una transacción existente.
     * Este método es llamado por EmpleadoServiceImpl.
     */
    public void eliminarTx(long id, Connection conn) throws Exception {
        validateLegajoId(id);
        legajoDAO.eliminarTx(id, conn);
    }

    // --- Métodos Públicos (Usados por el Menú) ---

    @Override
    public void actualizar(Legajo legajo) throws Exception {
        validateLegajoForUpdate(legajo);
        validateLegajoData(legajo);
        validateNumeroLegajoUniqueForUpdate(legajo);
        legajoDAO.actualizar(legajo);
    }

    /**
     * MÉTODO OBLIGATORIO DE LA INTERFAZ (GenericService).
     * <p>
     * Prohibimos que un legajo se borre por sí solo, ya que eso
     * dejaría a un Empleado "huérfano" (sin legajo).
     */
    @Override
    public void eliminar(long id) throws Exception {
        throw new UnsupportedOperationException(
                "Operación no soportada. Un Legajo no puede eliminarse por sí solo. "
                        + "Debe eliminar el Empleado asociado (ID: " + id + ")"
        );
    }

    /**
     * Llama al método leer en LegajoDAO.
     * Usado para buscar un legajo individualmente.
     */
    @Override
    public Legajo getById(long id) throws Exception {
        validateLegajoId(id);
        Legajo legajo = legajoDAO.leer(id);
        if (legajo == null) {
            throw new IllegalArgumentException(ERROR_LEGAJO_NO_EXISTE);
        }
        return legajo;
    }

    /**
     * Llama al método leerTodos en LegajoDAO.
     * Usado para listar todos los legajos.
     */
    @Override
    public List<Legajo> getAll() throws Exception {
        return legajoDAO.leerTodos();
    }

    // --- Métodos Privados de Validación ---

    /**
     * Valida todas las reglas de negocio de un Legajo relacionadas con sus datos
     * (campos obligatorios, longitudes, etc.).
     */
    private void validateLegajoData(Legajo legajo) {
        if (legajo == null) {
            throw new IllegalArgumentException(ERROR_LEGAJO_NULL);
        }

        if (legajo.getNumeroLegajo() == null || legajo.getNumeroLegajo().trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_NUMERO_LEGAJO_VACIO);
        }

        if (legajo.getEstado() == null) {
            throw new IllegalArgumentException(ERROR_ESTADO_NULL);
        }

        String nroLegajoTrim = legajo.getNumeroLegajo().trim();
        if (nroLegajoTrim.length() > NUMERO_LEGAJO_MAX_LENGTH) {
            throw new IllegalArgumentException(ERROR_NUMERO_LEGAJO_LARGO);
        }

        if (legajo.getCategoria() != null && legajo.getCategoria().length() > CATEGORIA_MAX_LENGTH) {
            throw new IllegalArgumentException(ERROR_CATEGORIA_LARGA);
        }

        if (legajo.getObservaciones() != null && legajo.getObservaciones().length() > OBSERVACIONES_MAX_LENGTH) {
            throw new IllegalArgumentException(ERROR_OBSERVACIONES_LARGAS);
        }
    }

    /**
     * Valida que el ID de un legajo sea válido (> 0).
     */
    private void validateLegajoId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException(ERROR_ID_INVALIDO);
        }
    }

    /**
     * Valida que un legajo sea apto para actualización (objeto no nulo y con ID válido).
     */
    private void validateLegajoForUpdate(Legajo legajo) {
        if (legajo == null || legajo.getId() <= 0) {
            throw new IllegalArgumentException("El legajo a actualizar no puede ser null y debe tener un ID válido.");
        }
    }

    /**
     * Valida la unicidad del número de legajo para operaciones de inserción.
     */
    private void validateNumeroLegajoUniqueForInsert(Legajo legajo) throws Exception {
        Legajo existente = legajoDAO.buscarPorNroLegajo(legajo.getNumeroLegajo());
        if (existente != null) {
            throw new IllegalArgumentException(ERROR_NUMERO_LEGAJO_DUPLICADO);
        }
    }

    /**
     * Valida la unicidad del número de legajo para operaciones de actualización,
     * ignorando el propio legajo.
     */
    private void validateNumeroLegajoUniqueForUpdate(Legajo legajo) throws Exception {
        Legajo legajoExistente = legajoDAO.buscarPorNroLegajo(legajo.getNumeroLegajo());
        if (legajoExistente != null && legajoExistente.getId() != legajo.getId()) {
            throw new IllegalArgumentException(ERROR_NUMERO_LEGAJO_DUPLICADO_OTRO);
        }
    }
}