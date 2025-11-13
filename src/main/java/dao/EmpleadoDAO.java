package dao;

import config.DatabaseConnection;
import entities.Empleado;
import entities.Legajo; // Importante: necesitamos Legajo para el JOIN

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Empleado.
 * Implementa GenericDao<Empleado> y gestiona toda la persistencia
 * de los objetos Empleado en la base de datos.
 * Características:
 * - Implementa GenericDAO<Empleado> para operaciones CRUD estándar
 * - Usa PreparedStatements en TODAS las consultas (protección contra SQL injection)
 * - Proporciona búsquedas especializadas
 *
 * Patrón: DAO con try-with-resources para manejo automático de recursos JDBC
 *
 * RESPONSABILIDAD CLAVE:
 * Esta clase maneja el LEFT JOIN con la tabla 'legajo' para
 * cargar la relación 1-a-1 (A->B) al leer Empleados.
 */
public class EmpleadoDAO implements GenericDAO<Empleado> {

    // --- QUERIES SQL (EMPLEADO) ---

    private static final String INSERT_SQL =
            "INSERT INTO empleado (nombre, apellido, dni, email, fecha_ingreso, area) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE empleado SET nombre = ?, apellido = ?, dni = ?, email = ?, fecha_ingreso = ?, area = ? WHERE id = ?";

    private static final String DELETE_SQL =
            "UPDATE empleado SET eliminado = TRUE WHERE id = ?";


    // --- QUERIES SQL (EMPLEADO + LEGAJO con JOIN) ---

    private static final String SELECT_BY_ID_SQL =
            "SELECT e.id AS emp_id, e.nombre, e.apellido, e.dni, e.email, e.fecha_ingreso, e.area, " +
                    "l.id AS leg_id, l.nro_legajo, l.categoria, l.estado, l.fecha_alta, l.observaciones " +
                    "FROM empleado e " +
                    "LEFT JOIN legajo l ON e.id = l.empleado_id AND l.eliminado = FALSE " +
                    "WHERE e.id = ? AND e.eliminado = FALSE";

    private static final String SELECT_ALL_SQL =
            "SELECT e.id AS emp_id, e.nombre, e.apellido, e.dni, e.email, e.fecha_ingreso, e.area, " +
                    "l.id AS leg_id, l.nro_legajo, l.categoria, l.estado, l.fecha_alta, l.observaciones " +
                    "FROM empleado e " +
                    "LEFT JOIN legajo l ON e.id = l.empleado_id AND l.eliminado = FALSE " +
                    "WHERE e.eliminado = FALSE";


    // --- QUERIES DE BÚSQUEDA ---

    /**
     * SQL para buscar un Empleado por DNI (búsqueda exacta).
     * El DNI es UNIQUE en la BD, por lo que esto debe devolver 1 o 0 resultados.
     * Incluye el LEFT JOIN para cargar el Legajo.
     */
    private static final String SELECT_BY_DNI_SQL =
            "SELECT e.id AS emp_id, e.nombre, e.apellido, e.dni, e.email, e.fecha_ingreso, e.area, " +
                    "l.id AS leg_id, l.nro_legajo, l.categoria, l.estado, l.fecha_alta, l.observaciones " +
                    "FROM empleado e " +
                    "LEFT JOIN legajo l ON e.id = l.empleado_id AND l.eliminado = FALSE " +
                    "WHERE e.dni = ? AND e.eliminado = FALSE";

    /**
     * SQL para buscar Empleados por Nombre o Apellido (búsqueda flexible).
     * Usamos LIKE '%filtro%' para encontrar coincidencias parciales.
     * Incluye el LEFT JOIN para cargar el Legajo.
     */
    private static final String SELECT_BY_NAME_SQL =
            "SELECT e.id AS emp_id, e.nombre, e.apellido, e.dni, e.email, e.fecha_ingreso, e.area, " +
                    "l.id AS leg_id, l.nro_legajo, l.categoria, l.estado, l.fecha_alta, l.observaciones " +
                    "FROM empleado e " +
                    "LEFT JOIN legajo l ON e.id = l.empleado_id AND l.eliminado = FALSE " +
                    "WHERE (e.nombre LIKE ? OR e.apellido LIKE ?) AND e.eliminado = FALSE";


    // --- MÉTODOS GENÉRICOS ("contrato" con GenericDAO) ---

    @Override
    public void crear(Empleado empleado) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setEmpleadoParameters(stmt, empleado);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    empleado.setId(rs.getLong(1));
                }
            }
        }
    }

    @Override
    public void crearTx(Empleado empleado, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setEmpleadoParameters(stmt, empleado);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    empleado.setId(rs.getLong(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Empleado empleado) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            setEmpleadoParameters(stmt, empleado);
            stmt.setLong(7, empleado.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el Empleado. ID no encontrado: " + empleado.getId());
            }
        }
    }

    @Override
    public void actualizarTx(Empleado empleado, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            setEmpleadoParameters(stmt, empleado);
            stmt.setLong(7, empleado.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el Empleado (Tx). ID no encontrado: " + empleado.getId());
            }
        }
    }

    @Override
    public void eliminar(long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo eliminar (baja lógica) el Empleado. ID no encontrado: " + id);
            }
        }
    }

    @Override
    public void eliminarTx(long id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo eliminar (baja lógica) el Empleado (Tx). ID no encontrado: " + id);
            }
        }
    }

    @Override
    public Empleado leer(long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmpleado(rs);
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
                empleados.add(mapResultSetToEmpleado(rs));
            }
        }
        return empleados;
    }


    // --- MÉTODOS DE BÚSQUEDA ---

    /**
     * Busca un Empleado por su DNI (búsqueda exacta).
     * Dado que el DNI es ÚNICO, devuelve un solo Empleado o null.
     *
     * @param dni El DNI exacto a buscar.
     * @return El Empleado encontrado (con su Legajo) o null si no existe.
     * @throws SQLException Si hay un error de base de datos.
     */
    public Empleado buscarPorDni(String dni) throws SQLException {
        if (dni == null || dni.trim().isEmpty()) {
            return null; // O lanzar new IllegalArgumentException("El DNI no puede ser nulo o vacío");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_DNI_SQL)) {

            stmt.setString(1, dni.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Reutilizamos el mapeador
                    return mapResultSetToEmpleado(rs);
                }
            }
        }
        return null; // No se encontró
    }

    /**
     * Busca Empleados cuyo nombre o apellido coincidan con el filtro (búsqueda flexible).
     *
     * @param filtro El texto a buscar (ej: "gar", "Mar").
     * @return Una lista de Empleados que coinciden (puede estar vacía).
     * @throws SQLException Si hay un error de base de datos.
     */
    public List<Empleado> buscarPorNombreApellido(String filtro) throws SQLException {
        List<Empleado> empleados = new ArrayList<>();
        if (filtro == null || filtro.trim().isEmpty()) {
            return empleados; // Devuelve lista vacía si el filtro es inútil
        }

        // Preparamos el patrón LIKE: '%filtro%'
        String searchPattern = "%" + filtro.trim() + "%";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_NAME_SQL)) {

            stmt.setString(1, searchPattern); // Para e.nombre LIKE ?
            stmt.setString(2, searchPattern); // Para e.apellido LIKE ?

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Reutilizamos el mapeador
                    empleados.add(mapResultSetToEmpleado(rs));
                }
            }
        }
        return empleados;
    }


    // --- MÉTODOS AUXILIARES (Helpers, para cumplir con DRY en los métodos genéricos) ---

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

    private Empleado mapResultSetToEmpleado(ResultSet rs) throws SQLException {
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
            legajo.setNroLegajo(rs.getString("nro_legajo"));
            legajo.setCategoria(rs.getString("categoria"));

            String estado = rs.getString("estado");
            if (estado != null) {
                // Asumiendo que 'estado' es un String en tu entidad Legajo
                legajo.setEstado(estado);
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
