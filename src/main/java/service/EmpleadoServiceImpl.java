package service;

import dao.EmpleadoDAO;
import entities.Empleado;
import entities.Legajo;
import config.DatabaseConnection;
import config.TransactionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementación del servicio de negocio para la entidad Empleado. Capa
 * intermedia entre la UI y el DAO que aplica validaciones de negocio complejas.
 * <p>
 * Responsabilidades: - Validar datos de persona ANTES de persistir (......) -
 * Garantizar unicidad en el sistema (.....) - COORDINAR operaciones entre
 * Empleado y Legajo (transaccionales) - Proporcionar métodos de búsqueda
 * especializados - Implementar eliminación SEGURA de empleado/legajo (evita FKs
 * huérfanas)
 * <p>
 * Patrón: Service Layer con inyección de dependencias y coordinación de
 * servicios
 */
public class EmpleadoServiceImpl implements GenericService<Empleado> {

    private static final String TRANSACTION_SUCCESS_CREATE = "La transacción de creación fue un éxito.";
    private static final String TRANSACTION_SUCCESS_UPDATE = "La transacción de actualización fue un éxito.";
    private static final String TRANSACTION_SUCCESS_DELETE = "La transacción de eliminación fue un éxito.";
    private static final String TRANSACTION_ERROR_CREATE = "Error en la transacción de creación.";
    private static final String TRANSACTION_ERROR_UPDATE = "Error en la transacción de actualización.";
    private static final String TRANSACTION_ERROR_DELETE = "Error en la transacción de eliminación.";

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

    @Override
    public void insertar(Empleado empleado) throws Exception {
        validateEmpleado(empleado);
        validateDniUnique(empleado.getDni(), null);

        if (empleado.getLegajo() == null) {
            throw new IllegalArgumentException("Un Empleado debe ser creado con un Legajo.");
        }

        try (TransactionManager txManager = new TransactionManager(DatabaseConnection.getConnection())) {
            txManager.startTransaction();
            Connection conn = txManager.getConnection();

            empleadoDAO.crearTx(empleado, conn); // 1ro creamos el empleado con un nuevo ID
            if (empleado.getId() == 0) {
                throw new SQLException("No se pudo crear el empleado, ID es 0.");
            }

            Legajo legajo = empleado.getLegajo(); // 2do creamos el legajo para el empleado
            legajoService.insertarTx(legajo, empleado.getId(), conn);

            txManager.commit();
            System.out.println(TRANSACTION_SUCCESS_CREATE);
        } catch (Exception e) {
            System.err.println(TRANSACTION_ERROR_CREATE + " Rollback automático ejecutado.");
            throw new Exception("Error al crear empleado: ".concat(e.getMessage()), e);
        }
    }

    @Override
    public void actualizar(Empleado empleado) throws Exception {

        // VALIDACIONES de las reglas de negocio
        if (empleado == null || empleado.getId() <= 0) {
            throw new IllegalArgumentException("El empleado a actualizar no puede ser null y debe tener un ID.");
        }
        if (empleado.getLegajo() == null || empleado.getLegajo().getId() <= 0) {
            throw new IllegalArgumentException("El empleado debe tener un legajo asociado con ID para actualizar.");
        }

        validateEmpleado(empleado); // Valida Nombre, Apellido, DNI
        validateDniUnique(empleado.getDni(), empleado.getId()); // Para que la validación de DNI no falle consigo mismo

        try (TransactionManager txManager = new TransactionManager(DatabaseConnection.getConnection())) {
            txManager.startTransaction();
            Connection conn = txManager.getConnection();

            legajoService.actualizarTx(empleado.getLegajo(), conn); // Actualiza legajo
            empleadoDAO.actualizarTx(empleado, conn);               // Actualiza empleado

            txManager.commit();
            System.out.println(TRANSACTION_SUCCESS_UPDATE);
        } catch (Exception e) {
            System.err.println(TRANSACTION_ERROR_UPDATE + " Rollback automático ejecutado.");
            throw new Exception("Error al actualizar empleado: ".concat(e.getMessage()), e);
        }
    }

    @Override
    public void eliminar(long id) throws Exception {
        // VALIDACIONES
        if (id <= 0) {
            throw new IllegalArgumentException("El ID para eliminar debe ser mayor a 0.");
        }

        // Buscamos al empleado para asegurarnos de que existe y obtener el ID de su legajo.
        Empleado empleado = empleadoDAO.leer(id);
        if (empleado == null) {
            throw new IllegalArgumentException("No se encontró un empleado (activo) con el ID: " + id);
        }
        if (empleado.getLegajo() == null) {
            throw new IllegalStateException("Error de datos: El empleado " + id + " no tiene un legajo asociado.");
        }

        long legajoId = empleado.getLegajo().getId();

        try (TransactionManager txManager = new TransactionManager(DatabaseConnection.getConnection())) {
            txManager.startTransaction();
            Connection conn = txManager.getConnection();

            legajoService.eliminarTx(legajoId, conn); // Primero se elimina el Legajo
            empleadoDAO.eliminarTx(id, conn);         // Luego se elimina a Empleado

            txManager.commit();
            System.out.println(TRANSACTION_SUCCESS_DELETE);
            System.out.println("Empleado y su Legajo eliminados");
        } catch (Exception e) {
            System.err.println(TRANSACTION_ERROR_DELETE + " Rollback automático ejecutado.");
            throw new Exception("Error al eliminar empleado: ".concat(e.getMessage()), e);
        }
    }

    @Override
    public Empleado getById(long id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        return empleadoDAO.leer(id);
    }

    @Override
    public List<Empleado> getAll() throws Exception {
        return empleadoDAO.leerTodos();
    }

    private void validateEmpleado(Empleado empleado) {
        if (empleado == null) {
            throw new IllegalArgumentException("La persona no puede ser null");
        }
        if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (empleado.getApellido() == null || empleado.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede estar vacío");
        }
        if (empleado.getDni() == null || empleado.getDni().trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
    }

    private void validateDniUnique(String dni, Long empleadoId) throws Exception {
        Empleado existente = empleadoDAO.buscarPorDni(dni);
        if (existente != null) {
            // Existe una persona con ese DNI
            if (empleadoId == null || existente.getId() != empleadoId) {
                // Es INSERT (personaId == null) o es UPDATE pero el DNI pertenece a otra persona
                throw new IllegalArgumentException("Ya existe una persona con el DNI: " + dni);
            }
            // Si llegamos aquí: es UPDATE y el DNI pertenece a la misma persona → OK
        }
    }
}
