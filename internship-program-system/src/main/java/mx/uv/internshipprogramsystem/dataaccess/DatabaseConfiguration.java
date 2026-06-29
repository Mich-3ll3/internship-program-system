package mx.uv.internshipprogramsystem.dataaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DatabaseConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private static final String PROPERTIES_FILE_NAME = "db.properties";
    private static final String CONFIGURATION_FILE_NAME = "config.properties";
    private static final String URL_PROPERTY_NAME = "db.url";
    private static final String USER_PROPERTY_NAME = "db.user";
    private static final String PASSWORD_PROPERTY_NAME = "db.password";
    private static final String DRIVER_PROPERTY_NAME = "db.driver";
    private static final String ENVIRONMENT_URL_NAME = "DB_URL";
    private static final String ENVIRONMENT_USER_NAME = "DB_USER";
    private static final String ENVIRONMENT_PASSWORD_NAME = "DB_PASSWORD";
    private static final String ENVIRONMENT_DRIVER_NAME = "DB_DRIVER";

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConfiguration(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfiguration load() {

        Properties configurationProperties = resolveConfiguration();

        validateConfiguration(configurationProperties);
        initializeDatabaseDriver(configurationProperties.getProperty(DRIVER_PROPERTY_NAME));
        
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration (
                configurationProperties.getProperty(
                        URL_PROPERTY_NAME
                ),
                configurationProperties.getProperty(
                        USER_PROPERTY_NAME
                ),
                configurationProperties.getProperty(
                        PASSWORD_PROPERTY_NAME
                )
        );
        return databaseConfiguration;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    private static Properties resolveConfiguration() throws DataAccessException {
        Properties configurationProperties = null;

        Optional<Properties> environmentConfiguration = loadEnvironmentConfiguration();

        if (environmentConfiguration.isPresent()) {
            configurationProperties = environmentConfiguration.get();
        } else {
            Optional<Properties> externalConfiguration = loadExternalConfiguration();
            if (externalConfiguration.isPresent()) {
                configurationProperties = externalConfiguration.get();
            } else {
                configurationProperties = loadInternalConfiguration();
            }
        }

        return configurationProperties;
    }
        
    private static Optional<Properties> loadEnvironmentConfiguration() {
        
        Optional<Properties> configuration = Optional.empty();
        
        String url = System.getenv(ENVIRONMENT_URL_NAME);
        String user = System.getenv(ENVIRONMENT_USER_NAME);
        String password = System.getenv(ENVIRONMENT_PASSWORD_NAME);
        String driver = System.getenv(ENVIRONMENT_DRIVER_NAME);

        if (!isMissingProperty(url)
                && !isMissingProperty(user)
                && !isMissingProperty(password)
                && !isMissingProperty(driver)) {

            Properties configurationProperties = new Properties();

            configurationProperties.setProperty(URL_PROPERTY_NAME,url);
            configurationProperties.setProperty(USER_PROPERTY_NAME,user);
            configurationProperties.setProperty(PASSWORD_PROPERTY_NAME,password);
            configurationProperties.setProperty(DRIVER_PROPERTY_NAME,driver);

            configuration = Optional.of(configurationProperties);

            LOGGER.info("Configuración de la base de datos cargada desde variables de entorno.");
        }

        return configuration;
    }

    private static Optional<Properties> loadExternalConfiguration() {
        
        Optional<Properties> configuration = Optional.empty();
        File configurationFile =
                new File(
                        System.getProperty("user.dir")
                                + File.separator
                                + CONFIGURATION_FILE_NAME
                );

        if (configurationFile.exists() && configurationFile.isFile()) {

            Properties configurationProperties = new Properties();

            try (InputStream inputStream = new FileInputStream(configurationFile)) {
                configurationProperties.load(inputStream);
                configuration = Optional.of(configurationProperties);
                LOGGER.info("Configuración de la base de datos cargada desde archivo externo.");
            } catch (IOException exception) {
                LOGGER.warn("No fue posible leer el archivo de configuración externo.", exception);
                throw new DataAccessException(
                        "No fue posible leer la configuración de la base de datos.",
                        exception
                );
            }
        }
        return configuration;
    }

    private static Properties loadInternalConfiguration() throws DataAccessException {

        Properties configurationProperties = new Properties();

        try (InputStream inputStream =
                     DatabaseConfiguration.class
                             .getClassLoader()
                             .getResourceAsStream(PROPERTIES_FILE_NAME)
                    ) {

            if (inputStream == null) {
                LOGGER.error("No se encontró el archivo interno de configuración.");
                throw new IllegalStateException("No se encontró el archivo de configuración interno.");
            }
            
            configurationProperties.load(inputStream);
            LOGGER.info("Configuración de la base de datos cargada desde archivo interno.");
            
        } catch (IOException exception) {
            LOGGER.error("No fue posible cargar el archivo de configuración interno.", exception);
            throw new IllegalStateException(
                    "No fue posible cargar la configuración de la base de datos.",
                    exception
            );
        }
        return configurationProperties;
    }

    private static void validateConfiguration(Properties configurationProperties) {

        validateProperty(configurationProperties.getProperty(URL_PROPERTY_NAME), URL_PROPERTY_NAME);
        validateProperty(configurationProperties.getProperty( USER_PROPERTY_NAME),USER_PROPERTY_NAME);
        validateProperty(configurationProperties.getProperty( PASSWORD_PROPERTY_NAME), PASSWORD_PROPERTY_NAME);
        validateProperty(configurationProperties.getProperty( DRIVER_PROPERTY_NAME), DRIVER_PROPERTY_NAME);
    }

    private static void validateProperty(String propertyValue, String propertyName) throws DataAccessException {

        if (isMissingProperty(propertyValue)) {

            LOGGER.error("La propiedad {} no está configurada.",propertyName);

            throw new DataAccessException(
                    String.format(
                            "La propiedad %s no está configurada.",
                            propertyName
                    )
            );
        }
    }

    private static void initializeDatabaseDriver(String driverName) {

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException exception) {
            LOGGER.error("No se encontró el driver JDBC {}.",driverName,exception);
            throw new IllegalStateException(
                    "No se encontró el driver JDBC configurado.",
                    exception
            );
        }
    }
    
    private static boolean isMissingProperty(String propertyValue) {

        boolean missingProperty = propertyValue == null || propertyValue.isBlank();
        return missingProperty;
    }
}