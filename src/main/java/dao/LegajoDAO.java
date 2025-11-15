package dao;

import config.DatabaseConnection;
import entities.EstadoLegajo; // Importar el Enum
import entities.Legajo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Legajo. Implementa
 * GenericDao<Legajo> y gestiona toda la persistencia de los objetos Legajo en
 * la base de datos.
 *
 * Características: - Implementa GenericDAO<Legajo> para operaciones CRUD
 * estándar. - Usa PreparedStatements en TODAS las consultas. - Nn realiza JOINs
 * (es la entidad 'B' o 'hija'). - Maneja la inserción/actualización de la FK
 * 'empleado_id'. - Implementa baja lógica (eliminado = TRUE).
 *
 * Esta clase es una "entidad hija" depende de Empleado sus métodos crear deben
 * ser llamados dentro de una transacción manejada por el Service, que provea el
 * 'empleado_id'
 *
 */
public class LegajoDAO implements GenericDAO<Legajo> {

    // --- QUERIES SQL (LEGAJO) ---
    /**
     * SQL para insertar un Legajo (B). Requiere el 'empleado_id' (FK de A).
     */
    private static final String INSERT_SQL
            = "INSERT INTO legajo (nro_legajo, categoria, estado, fecha_alta, observaciones, empleado_id) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    /**
     * SQL para actualizar un Legajo. Permite cambiar todos los campos excepto
     * el 'empleado_id', ya que la relación 1-a-1 es fija.
     */
    private static final String UPDATE_SQL
            = "UPDATE legajo SET nro_legajo = ?, categoria = ?, estado = ?, fecha_alta = ?, observaciones = ? "
            + "WHERE id = ?";

    /**
     * SQL para realizar una BAJA LÓGICA (soft delete).
     */
    private static final String DELETE_SQL
            = "UPDATE legajo SET eliminado = TRUE WHERE id = ?";

    /**
     * SQL para leer un Legajo por ID. Filtra por 'eliminado = FALSE' sin
     * necesidad de JOINs.
     */
    private static final String SELECT_BY_ID_SQL
            = "SELECT * FROM legajo WHERE id = ? AND eliminado = FALSE";

    /**
     * SQL para leer TODOS los Legajos. Filtra por 'eliminado = FALSE' sin
     * necesidad de JOINs.
     */
    private static final String SELECT_ALL_SQL
            = "SELECT * FROM legajo WHERE eliminado = FALSE";

    // --- QUERIES DE BÚSQUEDA ---
    /**
     * SQL para buscar un Legajo por 'nro_legajo' (búsqueda exacta). El
     * 'nro_legajo' es UNIQUE en la BD.
     */
    private static final String SELECT_BY_NRO_LEGAJO_SQL
            = "SELECT * FROM legajo WHERE nro_legajo = ? AND eliminado = FALSE";

//    // --- IMPLEMENTACIÓN DE MÉTODOS GENÉRICOS (Relación con GenericDAO) ---
//
//    @Override
//    public void crear(Legajo legajo) throws SQLException {
//        // Este método crea su propia conexión y la cierra
//        try (Connection conn = DatabaseConnection.getConnection()) {
//            ejecutarCreacion(legajo, conn); // Llama al ayudante
//        }
//    }
//
//    @Override
//    public void crearTx(Legajo legajo, Connection conn) throws SQLException {
//        // Este método usa la conexión recibida y NO la cierra
//        ejecutarCreacion(legajo, conn); // Llama al ayudante
//    }
//
//    @Override
//    public void actualizar(Legajo legajo) throws SQLException {
//        // Este método crea su propia conexión y la cierra
//        try (Connection conn = DatabaseConnection.getConnection()) {
//            ejecutarActualizacion(legajo, conn); // Llama al ayudante
//        }
//    }
//
//    @Override
//    public void actualizarTx(Legajo legajo, Connection conn) throws SQLException {
//        // Este método usa la conexión recibida y NO la cierra
//        ejecutarActualizacion(legajo, conn); // Llama al ayudante
//    }
//
//    @Override
//    public void eliminar(long id) throws SQLException {
//        // Este método crea su propia conexión y la cierra
//        try (Connection conn = DatabaseConnection.getConnection()) {
//            ejecutarEliminacion(id, conn); // Llama al ayudante
//        }
//    }
//
//    @Override
//    public void eliminarTx(long id, Connection conn) throws SQLException {
//        // Este método usa la conexión recibida y NO la cierra
//        ejecutarEliminacion(id, conn); // Llama al ayudante
//    }
//
//    @Override
//    public Legajo leer(long id) throws SQLException {
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
//
//            stmt.setLong(1, id);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    // Mapea la fila a un objeto Legajo
//                    return mapResultSetToLegajo(rs);
//                }
//            }
//        }
//        return null; // No se encontró
//    }
//
//    @Override
//    public List<Legajo> leerTodos() throws SQLException {
//        List<Legajo> legajos = new ArrayList<>();
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
//             ResultSet rs = stmt.executeQuery()) {
//
//            while (rs.next()) {
//                legajos.add(mapResultSetToLegajo(rs));
//            }
//        }
//        return legajos;
//    }
    /**
     * NO USAR. Un Legajo no puede crearse solo. Debe crearse usando el método
     * transaccional especializado: crearTx(Legajo legajo, long empleadoId,
     * Connection conn)
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public void crearTx(Legajo legajo, Connection conn) throws SQLException {
        throw new UnsupportedOperationException("Operación no soportada. Use crearTx(Legajo legajo, long empleadoId, Connection conn)");
    }

    /**
     * MÉTODO ESPECIALIZADO PARA EL SERVICE Este es el método correcto para
     * crear un Legajo (B). Es llamado por el EmpleadoService dentro de una
     * transacción.
     *
     * @param legajo El objeto Legajo a insertar.
     * @param empleadoId El ID del Empleado (A) al que pertenece.
     * @param conn La conexión transaccional.
     * @throws SQLException Si hay un error de SQL.
     */
    public void crearTx(Legajo legajo, long empleadoId, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            // Usamos el helper, pasando el empleadoId
            setLegajoParameters(stmt, legajo, empleadoId);

            stmt.executeUpdate();

            // Obtenemos el ID autogenerado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    legajo.setId(rs.getLong(1));
                }
            }
        }
    }

    // --- Métodos de Actualización (Genéricos) ---
    @Override
    public void actualizar(Legajo legajo) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ejecutarActualizacion(legajo, conn);
        }
    }

    @Override
    public void actualizarTx(Legajo legajo, Connection conn) throws SQLException {
        ejecutarActualizacion(legajo, conn);
    }

    // --- Métodos de Eliminación (Genéricos) ---
    @Override
    public void eliminar(long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ejecutarEliminacion(id, conn);
        }
    }

    @Override
    public void eliminarTx(long id, Connection conn) throws SQLException {
        ejecutarEliminacion(id, conn);
    }

    // --- Métodos de Lectura (Genéricos) ---
    @Override
    public Legajo leer(long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLegajo(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Legajo> leerTodos() throws SQLException {
        List<Legajo> legajos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                legajos.add(mapResultSetToLegajo(rs));
            }
        }
        return legajos;
    }

    // --- MÉTODOS DE BÚSQUEDA ---
    /**
     * Busca un Legajo por su 'nro_legajo' (búsqueda exacta).
     *
     * @param nroLegajo El número de legajo exacto a buscar.
     * @return El Legajo encontrado o null si no existe.
     * @throws SQLException Si hay un error de base de datos.
     */
    public Legajo buscarPorNroLegajo(String nroLegajo) throws SQLException {
        if (nroLegajo == null || nroLegajo.trim().isEmpty()) {
            return null;
        }

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_BY_NRO_LEGAJO_SQL)) {

            stmt.setString(1, nroLegajo.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLegajo(rs);
                }
            }
        }
        return null;
    }

    // --- MÉTODOS AUXILIARES (Helpers, para cumplir con DRY en los métodos genéricos) ---
    /**
     * Método auxiliar para setear los parámetros de un Legajo en un
     * PreparedStatement (usado por crear y crearTx).
     */
//    private void setLegajoParameters(PreparedStatement stmt, Legajo legajo) throws SQLException {
//        stmt.setString(1, legajo.getNroLegajo());
//        stmt.setString(2, legajo.getCategoria());
//        stmt.setString(3, legajo.getEstado()); // Asumiendo ENUM como String
//
//        if (legajo.getFechaAlta() != null) {
//            stmt.setDate(4, Date.valueOf(legajo.getFechaAlta()));
//        } else {
//            stmt.setNull(4, Types.DATE);
//        }
//
//        stmt.setString(5, legajo.getObservaciones());
//
//        // Seteo de la Clave Foránea
//        // Asumimos que la entidad Legajo tiene 'getEmpleadoId()'
//        stmt.setLong(6, legajo.getEmpleadoId());
//    }
    private void setLegajoParameters(PreparedStatement stmt, Legajo legajo, long empleadoId) throws SQLException {
        stmt.setString(1, legajo.getNumeroLegajo());
        stmt.setString(2, legajo.getCategoria());

        // CORRECCIÓN (Conflicto 3): Convertir Enum a String para la BD
        if (legajo.getEstado() != null) {
            stmt.setString(3, legajo.getEstado().name()); // .name() -> "ACTIVO" o "INACTIVO"
        } else {
            stmt.setNull(3, Types.VARCHAR); // O manejar según regla de negocio
        }

        // CORRECCIÓN (Conflicto 2): Usar LocalDate
        if (legajo.getFechaAlta() != null) {
            // Convertir java.time.LocalDate a java.sql.Date
            stmt.setDate(4, java.sql.Date.valueOf(legajo.getFechaAlta()));
        } else {
            stmt.setNull(4, Types.DATE);
        }

        stmt.setString(5, legajo.getObservaciones());

        // CORRECCIÓN (Conflicto 1): Usar el ID del parámetro, no del objeto
        stmt.setLong(6, empleadoId);
    }

    /**
     * Método auxiliar para ejecutar la lógica de actualización. Es llamado
     * tanto por actualizar() como por actualizarTx(). NO cierra la conexión.
     */
//    private void ejecutarActualizacion(Legajo legajo, Connection conn) throws SQLException {
//        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
//
//            stmt.setString(1, legajo.getNroLegajo());
//            stmt.setString(2, legajo.getCategoria());
//            stmt.setString(3, legajo.getEstado());
//
//            if (legajo.getFechaAlta() != null) {
//                stmt.setDate(4, Date.valueOf(legajo.getFechaAlta()));
//            } else {
//                stmt.setNull(4, Types.DATE);
//            }
//
//            stmt.setString(5, legajo.getObservaciones());
//            stmt.setLong(6, legajo.getId()); // ID para el WHERE
//
//            int rowsAffected = stmt.executeUpdate();
//            if (rowsAffected == 0) {
//                throw new SQLException("No se pudo actualizar el Legajo. ID no encontrado: " + legajo.getId());
//            }
//        }
//    }
    /**
     * Método auxiliar para ejecutar la lógica de actualización. NO cierra la
     * conexión.
     */
    private void ejecutarActualizacion(Legajo legajo, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, legajo.getNumeroLegajo());
            stmt.setString(2, legajo.getCategoria());

            // Convertir Enum a String
            if (legajo.getEstado() != null) {
                stmt.setString(3, legajo.getEstado().name());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }

            // Usar LocalDate
            if (legajo.getFechaAlta() != null) {
                stmt.setDate(4, java.sql.Date.valueOf(legajo.getFechaAlta()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setString(5, legajo.getObservaciones());
            stmt.setLong(6, legajo.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el Legajo. ID no encontrado: " + legajo.getId());
            }
        }
    }

    /**
     * GENERA ERROR DE COMPILACIÓN setLegajoParameters por la firma. 
     * No se usa.
     */
    
    /**
     * Método auxiliar para ejecutar la lógica de creación. Es llamado tanto por
     * crear() como por crearTx(). NO cierra la conexión.
     
        private void ejecutarCreacion(Legajo legajo, Connection conn) throws SQLException {
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

                setLegajoParameters(stmt, legajo);
                stmt.executeUpdate();

                // Obtenemos el ID autogenerado
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        legajo.setId(rs.getLong(1));
                    }
                }
            }
        }
    */
    
    
    /**
     * Método auxiliar para ejecutar la lógica de eliminación (baja lógica). Es
     * llamado tanto por eliminar() como por eliminarTx(). NO cierra la
     * conexión.
     */
    private void ejecutarEliminacion(long id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo eliminar (baja lógica) el Legajo. ID no encontrado: " + id);
            }
        }
    }

    /**
     * Método auxiliar para mapear un ResultSet a un objeto Legajo. Este
     * mapeador es simple, no involucra JOINs.
     */
//    private Legajo mapResultSetToLegajo(ResultSet rs) throws SQLException {
//        Legajo legajo = new Legajo();
//
//        legajo.setId(rs.getLong("id"));
//        legajo.setNroLegajo(rs.getString("nro_legajo"));
//        legajo.setCategoria(rs.getString("categoria"));
//        legajo.setEstado(rs.getString("estado"));
//
//        Date fechaAlta = rs.getDate("fecha_alta");
//        if (fechaAlta != null) {
//            legajo.setFechaAlta(fechaAlta.toLocalDate());
//        }
//
//        legajo.setObservaciones(rs.getString("observaciones"));
//
//        // Mapeamos la FK por si es necesaria
//        legajo.setEmpleadoId(rs.getLong("empleado_id"));
//
//        // El campo 'eliminado' no se mapea porque la query ya filtra
//        return legajo;
    private Legajo mapResultSetToLegajo(ResultSet rs) throws SQLException {
        Legajo legajo = new Legajo();

        legajo.setId(rs.getLong("id"));
        legajo.setNumeroLegajo(rs.getString("nro_legajo"));
        legajo.setCategoria(rs.getString("categoria"));

        // CORRECCIÓN (Conflicto 3): Convertir String de BD a Enum
        String estadoStr = rs.getString("estado");
        if (estadoStr != null) {
            legajo.setEstado(entities.EstadoLegajo.valueOf(estadoStr));
        }

        // CORRECCIÓN (Conflicto 2): Usar LocalDate
        java.sql.Date fechaAlta = rs.getDate("fecha_alta");
        if (fechaAlta != null) {
            legajo.setFechaAlta(fechaAlta.toLocalDate());
        }

        legajo.setObservaciones(rs.getString("observaciones"));

        // No necesitamos setear el empleado_id
        // en la entidad Legajo, respetando la unidireccionalidad
        return legajo;
    }
    
    /**
     * Método abstracto
     * Lanzamos un error porque nuestra regla de negocio prohíbe crear un Legajo por sí solo. 
     * Se debe usar el método crearTx.
     */
    @Override
    public void crear(Legajo legajo) throws SQLException {
        throw new UnsupportedOperationException("No puede crear Legajo sin Empleado");
    }
    
    
}
