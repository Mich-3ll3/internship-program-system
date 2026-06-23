package mx.uv.internshipprogramsystem.dataaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

public final class DatabaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String PROPERTIES_FILE_NAME = "db.properties";
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final String URL_PROPERTY_NAME = "db.url";
    private static final String USER_PROPERTY_NAME = "db.user";
    private static final String PASSWORD_PROPERTY_NAME = "db.password";

    private static String url;
    private static String user;
    private static String password;

    static {
        // Priority 1: Environment Variables
        url = System.getenv("DB_URL");
        user = System.getenv("DB_USER");
        password = System.getenv("DB_PASSWORD");

        if (url != null && user != null && password != null) {
            LOGGER.info("Configuracion de base de datos cargada desde variables de entorno.");
        } else {
            // Priority 2: External config.properties
            Properties properties = new Properties();
            File externalFile = new File(System.getProperty("user.dir") + File.separator + CONFIG_FILE_NAME);
            boolean loaded = false;
            if (externalFile.exists() && externalFile.isFile()) {
                try (InputStream input = new FileInputStream(externalFile)) {
                    properties.load(input);
                    url = properties.getProperty(URL_PROPERTY_NAME);
                    user = properties.getProperty(USER_PROPERTY_NAME);
                    password = properties.getProperty(PASSWORD_PROPERTY_NAME);
                    if (url != null && user != null && password != null) {
                        LOGGER.info("Configuracion de base de datos cargada desde archivo externo: {}", externalFile.getAbsolutePath());
                        loaded = true;
                    }
                } catch (IOException exception) {
                    LOGGER.warn("No se pudo leer el archivo externo config.properties, intentando fallback a db.properties interno", exception);
                }
            }

            // Priority 3: Internal db.properties
            if (!loaded) {
                try (InputStream input = DatabaseManager.class.getClassLoader()
                        .getResourceAsStream(PROPERTIES_FILE_NAME)) {
                    if (input == null) {
                        LOGGER.error("No se encontro db.properties en el classpath ni config.properties externo ni variables de entorno.");
                        throw new IllegalStateException("No se encontro db.properties en el classpath");
                    }

                    properties.clear();
                    properties.load(input);
                    url = properties.getProperty(URL_PROPERTY_NAME);
                    user = properties.getProperty(USER_PROPERTY_NAME);
                    password = properties.getProperty(PASSWORD_PROPERTY_NAME);
                    LOGGER.info("Configuracion de base de datos cargada desde db.properties interno.");
                } catch (IOException exception) {
                    LOGGER.error("No se pudo cargar la configuracion de la base de datos", exception);
                    throw new IllegalStateException(
                        "No se pudo cargar la configuracion de la base de datos.",
                        exception
                    );
                }
            }
        }
    }

    private DatabaseManager() {
    }

    public static Connection getConnection() throws DataAccessException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException exception) {
            LOGGER.error("Error de conexion a la base de datos", exception);
            throw new DataAccessException("No se pudo establecer la conexion con la base de datos", exception);
        }
    }
}
