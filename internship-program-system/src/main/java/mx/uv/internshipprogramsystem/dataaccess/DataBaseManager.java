package mx.uv.internshipprogramsystem.dataaccess;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DataBaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseManager.class);
    private static final String PROPERTIES_FILE_NAME = "db.properties";
    private static final String URL_PROPERTY_NAME = "db.url";
    private static final String USER_PROPERTY_NAME = "db.user";
    private static final String PASSWORD_PROPERTY_NAME = "db.password";

    private static String url;
    private static String user;
    private static String password;

    static {
        Properties properties = new Properties();
        try (InputStream input = DataBaseManager.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE_NAME)) {
            if (input == null) {
                throw new IllegalStateException("No se encontró db.properties en el classpath");
            }

            properties.load(input);
            url = properties.getProperty(URL_PROPERTY_NAME);
            user = properties.getProperty(USER_PROPERTY_NAME);
            password = properties.getProperty(PASSWORD_PROPERTY_NAME);
        } catch (IOException exception) {
            LOGGER.error("No se pudo cargar la configuración de la base de datos", exception);
            throw new IllegalStateException(
                "No se pudo cargar la configuración de la base de datos.",
                exception
            );
        }
    }

    private DataBaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
