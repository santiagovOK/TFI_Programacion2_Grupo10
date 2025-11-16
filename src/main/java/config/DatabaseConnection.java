package config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    // --- Rutas de Búsqueda (Classpath) ---
    private static final String CONFIG_PATH_IDEAL = "resources/config.properties";
    private static final String CONFIG_PATH_LEGACY = "config.properties";

    // --- Ruta de Búsqueda (File System - Raíz del proyecto) ---
    private static final String CONFIG_PATH_ROOT = "config.properties";

    private static final String PROPERTY_DB_URL = "db.url";
    private static final String PROPERTY_DB_NAME = "db.name";
    private static final String PROPERTY_DB_USER = "db.user";
    private static final String PROPERTY_DB_PASSWORD = "db.pass";
    private static final String JDBC_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    private static final Properties CONFIG_PROPERTIES = new Properties();
    private static final String DB_URL;

    // Bloque estático: se ejecuta una sola vez cuando arranca la app
    static {
        try (InputStream input = findConfigFile()) { // No cambia

            loadProperties(input);
            loadJdbcDriver();
            DB_URL = buildDatabaseUrl();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Error al cargar la configuración de la base de datos o el driver JDBC.",
                    e);
        }
    }

    /**
     * MÉTODO MODIFICADO: Intenta cargar el config desde 3 ubicaciones.
     *
     * @return Un InputStream del archivo encontrado.
     * @throws IOException si no se encuentra el archivo en NINGUNA ubicación.
     */
    private static InputStream findConfigFile() throws IOException {
        ClassLoader classLoader = DatabaseConnection.class.getClassLoader();

        // 1. Intenta la ruta IDEAL ("src/resources/config.properties")
        InputStream input = classLoader.getResourceAsStream(CONFIG_PATH_IDEAL);
        if (input != null) {
            System.out.println("Configuración encontrada en (classpath): " + CONFIG_PATH_IDEAL);
            return input;
        }

        // 2. Si falla, intenta la ruta LEGACY ("src/config.properties")
        System.out.println("ADVERTENCIA: No se encontró '" + CONFIG_PATH_IDEAL + "'. Buscando en (classpath): "
                + CONFIG_PATH_LEGACY);
        input = classLoader.getResourceAsStream(CONFIG_PATH_LEGACY);
        if (input != null) {
            System.out.println("Configuración encontrada en (classpath): " + CONFIG_PATH_LEGACY);
            return input;
        }

        // 3. Si AMBAS fallan, intenta la RAÍZ DEL PROYECTO (File System)
        System.out.println(
                "ADVERTENCIA: No se encontró en el classpath. Buscando en la raíz del proyecto: " + CONFIG_PATH_ROOT);
        try {
            // *** ¡AQUÍ ESTÁ LA NUEVA LÓGICA! ***
            input = new FileInputStream(CONFIG_PATH_ROOT);
            System.out.println("Configuración encontrada en (file system): " + CONFIG_PATH_ROOT);
            return input;

        } catch (FileNotFoundException e) {
            // Es normal que falle si el archivo no está, así que solo informamos y
            // continuamos.
            System.out.println("ADVERTENCIA: No se encontró en la raíz del proyecto.");
        }

        // 4. Si Todo falla, ahora sí lanzamos el error.
        throw new IOException(
                "¡ERROR CRÍTICO! No se pudo encontrar 'config.properties' en NINGUNA de las ubicaciones:\n" +
                        "1. " + CONFIG_PATH_IDEAL + " (dentro de src/resources/)\n" +
                        "2. " + CONFIG_PATH_LEGACY + " (dentro de src/)\n" +
                        "3. " + CONFIG_PATH_ROOT + " (en la raíz del proyecto)");
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

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DB_URL,
                CONFIG_PROPERTIES.getProperty(PROPERTY_DB_USER),
                CONFIG_PROPERTIES.getProperty(PROPERTY_DB_PASSWORD));
    }
}