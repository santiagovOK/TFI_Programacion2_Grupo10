package config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Administra el ciclo de vida de una transacción sobre una Connection.
 */
public class TransactionManager implements AutoCloseable {

    private final Connection connection;
    private boolean transactionActive;

    /**
     * Crea un nuevo administrador de transacciones para la conexión dada.
     *
     * @param connection conexión JDBC no nula.
     * @throws IllegalArgumentException si la conexión es null.
     */
    public TransactionManager(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("La conexión no puede ser null");
        }
        this.connection = connection;
        this.transactionActive = false;
    }

    /**
     * Devuelve la conexión subyacente administrada por este TransactionManager.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Inicia una transacción deshabilitando el auto-commit.
     *
     * @throws SQLException si la conexión no está disponible o está cerrada.
     */
    public void startTransaction() throws SQLException {
        if (connection.isClosed()) {
            throw new SQLException("No se puede iniciar la transacción: conexión cerrada");
        }
        connection.setAutoCommit(false);
        transactionActive = true;
    }

    /**
     * Hace commit de la transacción activa.
     *
     * @throws SQLException si no hay transacción activa o ocurre un error al hacer commit.
     */
    public void commit() throws SQLException {
        if (!transactionActive) {
            throw new SQLException("No hay una transacción activa para hacer commit");
        }
        connection.commit();
        resetTransactionState();
    }

    /**
     * Realiza un rollback de la transacción activa, si existe.
     * <p>
     * Cualquier excepción SQL se registra en stderr y no se propaga.
     */
    public void rollback() {
        if (!hasActiveTransaction()) {
            return;
        }

        try {
            connection.rollback();
            resetTransactionState();
        } catch (SQLException e) {
            System.err.println("Error durante el rollback: " + e.getMessage());
        }
    }

    /**
     * Cierra el TransactionManager, intentando:
     * <ul>
     *     <li>Realizar rollback si hay una transacción activa.</li>
     *     <li>Restaurar el auto-commit.</li>
     *     <li>Cerrar la conexión.</li>
     * </ul>
     * Cualquier excepción SQL se registra en stderr y no se propaga.
     */
    @Override
    public void close() {
        try {
            if (transactionActive) {
                rollback();
            }
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    /**
     * Indica si hay una transacción activa.
     */
    public boolean isTransactionActive() {
        return transactionActive;
    }

    /**
     * Indica si hay una transacción actualmente activa.
     */
    private boolean hasActiveTransaction() {
        return transactionActive;
    }

    /**
     * Resetea el estado interno de la transacción después de commit/rollback.
     */
    private void resetTransactionState() {
        transactionActive = false;
    }
}