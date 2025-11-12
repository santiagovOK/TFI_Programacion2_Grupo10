package config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * esta clase se encarga de todo el bardo de la conexión a la bd.
 * lee el archivo `config.properties`
 * y te da un método estático para pedirle una conexión.
 */
public class DatabaseConnection {

    private static final String PROPERTIES_FILE = "config.properties";
    private static final Properties properties = new Properties();
    private static final String DB_URL;

    // esto se ejecuta una sola vez cuando la app arranca.
    // así no leemos el archivo a cada rato.
    static {
        try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            
            // cargamos el archivo de propiedades
            properties.load(input);

            // le decimos a java que use el driver de mysql (el .jar que bajamos)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // armamos la url completa para la conexión
            DB_URL = properties.getProperty("db.url") + properties.getProperty("db.name");
            
        } catch (Exception e) {
            e.printStackTrace();
            // si esto falla, que explote todo. no podemos seguir sin bd.
            throw new RuntimeException("error al cargar la config de la bd o el driver.", e);
        }
    }

    /**
     * te da una conexión nueva a la base de datos.
     * el tpi pide que devuelva un `java.sql.Connection`.
     *
     * importante: el que pide la conexión (el service), se encarga de cerrarla.
     *
     * @return una `java.sql.Connection`
     * @throws SQLException si algo sale mal con la bd
     */
    public static Connection getConnection() throws SQLException {
        // devolvemos la conexión fresquita, usando los datos que ya cargamos
        return DriverManager.getConnection(
                DB_URL,
                properties.getProperty("db.user"),
                properties.getProperty("db.pass")
        );
    }
}