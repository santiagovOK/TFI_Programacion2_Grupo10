package service;

import dao.LegajoDAO;
import entities.Legajo;
import java.sql.Connection;
import java.util.List;

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
    /**
     * DAO para acceso a datos de domicilios.
     * Inyectado en el constructor (Dependency Injection).
     * Usa GenericDAO para permitir testing con mocks.
     */
    private final LegajoDAO legajoDAO;

    /**
     * Constructor con inyección de dependencias
     * Valida que el DAO no sea null
     * @param legajoDAO 
     */
    public LegajoServiceImpl(LegajoDAO legajoDAO) {
        if (legajoDAO == null) {
            throw new IllegalArgumentException("LegajoDAO no puede ser null");
        }
        this.legajoDAO = legajoDAO;
    }

    // Métodos

    
    /**
     * MÉTODO OBLIGATORIO DE LA INTERFAZ.
     * Implementamos este método, pero lanzamos un error, porque
     * nuestra regla de negocio prohíbe crear un Legajo por sí solo.
     * Siempre debe crearse DENTRO de la transacción de un Empleado.
     */
    @Override
    public void insertar(Legajo legajo) throws Exception {
        throw new UnsupportedOperationException("Operación no soportada. Un Legajo no puede crearse por sí solo.");
    }
   
    /**
     * Inserta un Legajo DENTRO de una transacción existente.
     * Este método es llamado por EmpleadoServiceImpl.
     * Se validan las reglas de negocio de Legajo (incluyendo unicidad).
     */
    public void insertarTx(Legajo legajo, long empleadoId, Connection conn) throws Exception {
        
        validateLegajo(legajo); // Valida las reglas del negocio

        // Valida la unicidad
        if (legajoDAO.buscarPorNroLegajo(legajo.getNumeroLegajo()) != null) {
            throw new IllegalArgumentException("Error: El número de legajo ya existe.");
        }

        legajoDAO.crearTx(legajo, empleadoId, conn);
    }

    //  Método que valida las reglas de negocio para crear, actualizar un nuevo legajo
    private void validateLegajo(Legajo legajo) {
        
        // Validación de Nulidad (Objeto)
        if (legajo == null) {
            throw new IllegalArgumentException("El legajo no puede ser null");
        }

        // VALIDACIONES DE CAMPOS "NOT NULL"

        if (legajo.getNumeroLegajo() == null || legajo.getNumeroLegajo().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de legajo no puede estar vacío");
        }

        if (legajo.getEstado() == null) {
            throw new IllegalArgumentException("El Estado del Legajo no puede ser null");
        }


        // VALIDACIONES DE LONGITUD (varchar)

        if (legajo.getNumeroLegajo().trim().length() > 20) {
            throw new IllegalArgumentException("El número de legajo no puede exceder los 20 caracteres");
        }

        if (legajo.getCategoria() != null && legajo.getCategoria().length() > 30) {
            throw new IllegalArgumentException("La categoría no puede exceder los 30 caracteres");
        }

        if (legajo.getObservaciones() != null && legajo.getObservaciones().length() > 255) {
            throw new IllegalArgumentException("Las observaciones no pueden exceder los 255 caracteres");
        }
        
    }
    
    @Override
    public void actualizar(Legajo legajo) throws Exception {
       if (legajo == null || legajo.getId() <= 0) {
            throw new IllegalArgumentException("El legajo a actualizar no puede ser null y debe tener un ID válido.");
        }
       validateLegajo(legajo); // Reutilizamos el metodo de validación
       
       // Validamos la unicidad
       Legajo legajoExiste = legajoDAO.buscarPorNroLegajo(legajo.getNumeroLegajo());
       if (legajoExiste != null && legajoExiste.getId() != legajo.getId()) {
            throw new IllegalArgumentException("Error: Número de legajo ya existe");
        }
       
       legajoDAO.actualizar(legajo); 

    }
    
    
    /**
     * Actualiza un Legajo DENTRO de una transacción existente
     * Método propio de esta clase
     */
    public void actualizarTx(Legajo legajo, Connection conn) throws Exception {
        if (legajo == null || legajo.getId() <= 0) {
            throw new IllegalArgumentException("El legajo a actualizar no puede ser null y debe tener un ID válido.");
        }

        validateLegajo(legajo); 

        Legajo legajoExistente = legajoDAO.buscarPorNroLegajo(legajo.getNumeroLegajo());
        if (legajoExistente != null && legajoExistente.getId() != legajo.getId()) {
            throw new IllegalArgumentException("Error: El número de legajo ya existe");
        }

        legajoDAO.actualizarTx(legajo, conn);
    }

        /**
        * Para ser usado en EmpleadoServiceImpl.
        * La operación de borrado (lógico o físico) siempre se inicia desde el Empleado.
        */
    @Override
        public void eliminar(long id) throws Exception {
            if (id <= 0) {
                throw new IllegalArgumentException("El ID para eliminar debe ser mayor a 0.");
            }

            Legajo legajo = legajoDAO.leer(id);
            if (legajo == null) { 
                throw new IllegalArgumentException("El legajo no existe");
            }
            
            legajoDAO.eliminar(id);
        }
    
    /**
     * Realiza la baja lógica DENTRO de una transacción existente.
     * Este método PROPIO es llamado por EmpleadoServiceImpl.
     */
    public void eliminarTx(long id, Connection conn) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID para eliminar debe ser mayor a 0.");
        }
        legajoDAO.eliminarTx(id, conn);
    }    
        
    /**
     * Llama al médoto leer en LegajoDAO
     * @param id
     * @return La fila a un objeto Legao o Null si no se encontró
     * @throws Exception 
     */
    @Override
    public Legajo getById(long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        return legajoDAO.leer(id);
    }
      
    
    /**
     * Llama al método leerTodos en LegajoDAO
     * @return lista de legajos
     * @throws Exception 
     */
    @Override
    public List<Legajo> getAll() throws Exception {
        return legajoDAO.leerTodos();
    }
    
}