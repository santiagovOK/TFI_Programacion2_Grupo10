package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase responsable de gestionar la conexión a la base de datos.
 * Lee el archivo `config.properties` y expone un método estático
 * para obtener nuevas conexiones.
 */
public class DatabaseConnection {

    private static final String CONFIG_FILE_NAME = "config.properties";

    private static final String PROPERTY_DB_URL = "db.url";
    private static final String PROPERTY_DB_NAME = "db.name";
    private static final String PROPERTY_DB_USER = "db.user";
    private static final String PROPERTY_DB_PASSWORD = "db.pass";
    private static final String JDBC_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    private static final Properties CONFIG_PROPERTIES = new Properties();
    private static final String DB_URL;

    // esto se ejecuta una sola vez cuando la app arranca
    // así no leemos el archivo a cada rato
    static {
        try (InputStream input = new FileInputStream(CONFIG_FILE_NAME)) {
            loadProperties(input);
            loadJdbcDriver();
            DB_URL = buildDatabaseUrl();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // si esto falla, que explote todo. no podemos seguir sin bd.
            throw new RuntimeException(
                    "Error al cargar la configuración de la base de datos o el driver JDBC.",
                    e
            );
        }
    }

    private static void loadProperties(InputStream input) throws IOException {
        CONFIG_PROPERTIES.load(input);
    }

    private static void loadJdbcDriver() throws ClassNotFoundException {
        Class.forName(JDBC_DRIVER_CLASS);
    }

    private static String buildDatabaseUrl() {
        return CONFIG_PROPERTIES.getProperty(PROPERTY_DB_URL)
                + CONFIG_PROPERTIES.getProperty(PROPERTY_DB_NAME);
    }

    /**
     * Devuelve una conexión nueva a la base de datos.
     *
     * Importante: quien pide la conexión es responsable de cerrarla.
     *
     * @return una {@link java.sql.Connection}
     * @throws SQLException si algo sale mal con la base de datos
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DB_URL,
                CONFIG_PROPERTIES.getProperty(PROPERTY_DB_USER),
                CONFIG_PROPERTIES.getProperty(PROPERTY_DB_PASSWORD)
        );
    }
}