package mx.uv.internshipprogramsystem.logic.security;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class EmailConfiguration {

    private static final String CONFIGURATION_FILE =
        "email.properties";

    private static final Properties PROPERTIES =
        loadProperties();

    private EmailConfiguration() {
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (
            InputStream inputStream =
                Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(CONFIGURATION_FILE)
        ) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException exception) {
            throw new IllegalStateException(
                "No se pudo cargar email.properties",
                exception
            );
        }

        return properties;
    }

    public static String getHost() {
        return PROPERTIES.getProperty("smtp.host");
    }

    public static String getPort() {
        return PROPERTIES.getProperty("smtp.port");
    }

    public static String getEmail() {
        return PROPERTIES.getProperty("smtp.email");
    }

    public static String getPassword() {
        return PROPERTIES.getProperty("smtp.password");
    }
}