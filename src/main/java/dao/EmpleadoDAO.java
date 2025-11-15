package dao;

import config.DatabaseConnection;
import entities.Empleado;
import entities.Legajo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Empleado.
 * Implementa GenericDAO<Empleado> y gestiona toda la persistencia de Empleado
 * en la base de datos.
 *
 * Características:
 * - Implementa GenericDAO<Empleado> para operaciones CRUD estándar
 * - Usa PreparedStatements en TODAS las consultas (protección contra SQL injection)
 * - Proporciona búsquedas especializadas
 *
 * RESPONSABILIDAD CLAVE: Esta clase maneja el LEFT JOIN con la tabla 'legajo'
 * para cargar la relación 1-a-1 (A->B) al leer Empleados.
 */
public class EmpleadoDAO implements GenericDAO<Empleado> {

    // --- QUERIES SQL (EMPLEADO) ---

    private static final String INSERT_SQL =
            "INSERT INTO empleado (nombre, apellido, dni, email, fecha_ingreso, area) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE empleado SET nombre = ?, apellido = ?, dni = ?, email = ?, fecha_ingreso = ?, area = ? " +
                    "WHERE id = ?";

    private static final String DELETE_SQL =
            "UPDATE empleado SET eliminado = TRUE WHERE id = ?";

    // --- QUERIES SQL (EMPLEADO + LEGAJO con JOIN) ---

    private static final String SELECT_BASE_JOIN =
            "SELECT e.id AS emp_id, e.nombre, e.apellido, e.dni, e.email, e.fecha_ingreso, e.area, " +
                    "       l.id AS leg_id, l.nro_legajo, l.categoria, l.estado, l.fecha_alta, l.observaciones " +
                    "FROM empleado e " +
                    "LEFT JOIN legajo l ON e.id = l.empleado_id AND l.eliminado = FALSE ";

    private static final String SELECT_BY_ID_SQL =
            SELECT_BASE_JOIN +
                    "WHERE e.id = ? AND e.eliminado = FALSE";

    private static final String SELECT_ALL_SQL =
            SELECT_BASE_JOIN +
                    "WHERE e.eliminado = FALSE";

    // --- QUERIES DE BÚSQUEDA ---

    /**
     * SQL para buscar un Empleado por DNI (búsqueda exacta).
     * El DNI es UNIQUE en la BD. Incluye LEFT JOIN para cargar el Legajo.
     */
    private static final String SELECT_BY_DNI_SQL =
            SELECT_BASE_JOIN +
                    "WHERE e.dni = ? AND e.eliminado = FALSE";

    /**
     * SQL para buscar Empleados por Nombre o Apellido (búsqueda flexible).
     * Usa LIKE '%filtro%'. Incluye LEFT JOIN para cargar el Legajo.
     */
    private static final String SELECT_BY_NAME_SQL =
            SELECT_BASE_JOIN +
                    "WHERE (e.nombre LIKE ? OR e.apellido LIKE ?) AND e.eliminado = FALSE";

    // --- MÉTODOS GENÉRICOS (GenericDAO) ---

    @Override
    public void crear(Empleado empleado) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setEmpleadoParameters(stmt, empleado);
            stmt.executeUpdate();
            assignGeneratedId(stmt, empleado);
        }
    }

    @Override
    public void crearTx(Empleado empleado, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setEmpleadoParameters(stmt, empleado);
            stmt.executeUpdate();
            assignGeneratedId(stmt, empleado);
        }
    }

    @Override
    public void actualizar(Empleado empleado) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            executeUpdate(empleado, conn);
        }
    }

    @Override
    public void actualizarTx(Empleado empleado, Connection conn) throws SQLException {
        executeUpdate(empleado, conn);
    }

    @Override
    public void eliminar(long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            executeSoftDelete(id, conn);
        }
    }

    @Override
    public void eliminarTx(long id, Connection conn) throws SQLException {
        executeSoftDelete(id, conn);
    }

    @Override
    public Empleado leer(long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEmpleado(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Empleado> leerTodos() throws SQLException {
        List<Empleado> empleados = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                empleados.add(mapRowToEmpleado(rs));
            }
        }
        return empleados;
    }

    // --- MÉTODOS DE BÚSQUEDA ESPECIALIZADOS ---

    /**
     * Busca un Empleado por su DNI (búsqueda exacta).
     * Dado que el DNI es ÚNICO, devuelve un solo Empleado o null.
     */
    public Empleado buscarPorDni(String dni) throws SQLException {
        if (isNullOrBlank(dni)) {
            return null;
        }

        String trimmedDni = dni.trim();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_DNI_SQL)) {

            stmt.setString(1, trimmedDni);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEmpleado(rs);
                }
            }
        }
        return null;
    }

    /**
     * Busca Empleados cuyo nombre o apellido coincidan con el filtro (búsqueda flexible).
     *
     * @param filtro El texto a buscar (ej: "gar", "Mar").
     * @return Una lista de Empleados que coinciden (puede estar vacía).
     */
    public List<Empleado> buscarPorNombreApellido(String filtro) throws SQLException {
        List<Empleado> empleados = new ArrayList<>();
        if (isNullOrBlank(filtro)) {
            return empleados;
        }

        String searchPattern = "%" + filtro.trim() + "%";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_NAME_SQL)) {

            stmt.setString(1, searchPattern); // e.nombre LIKE ?
            stmt.setString(2, searchPattern); // e.apellido LIKE ?

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    empleados.add(mapRowToEmpleado(rs));
                }
            }
        }
        return empleados;
    }

    // --- HELPERS (parámetros, mapping, validación, reutilización de lógica) ---

    private void setEmpleadoParameters(PreparedStatement stmt, Empleado empleado) throws SQLException {
        stmt.setString(1, empleado.getNombre());
        stmt.setString(2, empleado.getApellido());
        stmt.setString(3, empleado.getDni());
        stmt.setString(4, empleado.getEmail());

        if (empleado.getFechaIngreso() != null) {
            stmt.setDate(5, Date.valueOf(empleado.getFechaIngreso()));
        } else {
            stmt.setNull(5, Types.DATE);
        }

        stmt.setString(6, empleado.getArea());
    }

    /**
     * Extrae el ID autogenerado y lo asigna al Empleado.
     */
    private void assignGeneratedId(PreparedStatement stmt, Empleado empleado) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                empleado.setId(rs.getLong(1));
            }
        }
    }

    /**
     * Ejecuta la lógica de actualización usando una conexión existente.
     */
    private void executeUpdate(Empleado empleado, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            setEmpleadoParameters(stmt, empleado);
            stmt.setLong(7, empleado.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el Empleado. ID no encontrado: " + empleado.getId());
            }
        }
    }

    /**
     * Ejecuta la baja lógica (soft delete) usando una conexión existente.
     */
    private void executeSoftDelete(long id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo eliminar (baja lógica) el Empleado. ID no encontrado: " + id);
            }
        }
    }

    /**
     * Verifica si un String es nulo o está vacío tras hacer trim().
     */
    private boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Mapea una fila del ResultSet a un Empleado (incluyendo su Legajo si existe).
     */
    private Empleado mapRowToEmpleado(ResultSet rs) throws SQLException {
        Empleado empleado = new Empleado();
        empleado.setId(rs.getLong("emp_id"));
        empleado.setNombre(rs.getString("nombre"));
        empleado.setApellido(rs.getString("apellido"));
        empleado.setDni(rs.getString("dni"));
        empleado.setEmail(rs.getString("email"));

        Date fechaIngreso = rs.getDate("fecha_ingreso");
        if (fechaIngreso != null) {
            empleado.setFechaIngreso(fechaIngreso.toLocalDate());
        }
        empleado.setArea(rs.getString("area"));

        long legajoId = rs.getLong("leg_id");
        if (legajoId > 0 && !rs.wasNull()) {
            Legajo legajo = new Legajo();
            legajo.setId(legajoId);
            legajo.setNumeroLegajo(rs.getString("nro_legajo"));
            legajo.setCategoria(rs.getString("categoria"));

            String estadoStr = rs.getString("estado");
            if (estadoStr != null) {
                legajo.setEstado(entities.EstadoLegajo.valueOf(estadoStr));
            }

            Date fechaAlta = rs.getDate("fecha_alta");
            if (fechaAlta != null) {
                legajo.setFechaAlta(fechaAlta.toLocalDate());
            }
            legajo.setObservaciones(rs.getString("observaciones"));

            empleado.setLegajo(legajo);
        } else {
            empleado.setLegajo(null);
        }

        return empleado;
    }
}