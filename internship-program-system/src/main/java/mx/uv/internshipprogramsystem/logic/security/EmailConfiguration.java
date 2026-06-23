package mx.uv.internshipprogramsystem.logic.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EmailConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailConfiguration.class);
    private static final String CONFIGURATION_FILE = "email.properties";
    private static final String EXTERNAL_CONFIG_FILE_NAME = "config.properties";

    private static final Properties PROPERTIES = loadProperties();

    private EmailConfiguration() {
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        // Priority 1: Environment Variables
        String envHost = System.getenv("SMTP_HOST");
        String envPort = System.getenv("SMTP_PORT");
        String envEmail = System.getenv("SMTP_EMAIL");
        String envPassword = System.getenv("SMTP_PASSWORD");

        if (envHost != null && envPort != null && envEmail != null && envPassword != null) {
            properties.setProperty("smtp.host", envHost);
            properties.setProperty("smtp.port", envPort);
            properties.setProperty("smtp.email", envEmail);
            properties.setProperty("smtp.password", envPassword);
            LOGGER.info("Configuración de correo SMTP cargada desde variables de entorno");
        } else {
            boolean loaded = false;

            // Priority 2: External config.properties file
            File externalFile = new File(EXTERNAL_CONFIG_FILE_NAME);
            if (externalFile.exists() && externalFile.isFile()) {
                try (InputStream input = new FileInputStream(externalFile)) {
                    Properties tempProps = new Properties();
                    tempProps.load(input);
                    String extHost = tempProps.getProperty("smtp.host");
                    String extPort = tempProps.getProperty("smtp.port");
                    String extEmail = tempProps.getProperty("smtp.email");
                    String extPassword = tempProps.getProperty("smtp.password");
                    if (extHost != null && extPort != null && extEmail != null && extPassword != null) {
                        properties.setProperty("smtp.host", extHost);
                        properties.setProperty("smtp.port", extPort);
                        properties.setProperty("smtp.email", extEmail);
                        properties.setProperty("smtp.password", extPassword);
                        loaded = true;
                        LOGGER.info("Configuración de correo SMTP cargada desde archivo externo: {}", EXTERNAL_CONFIG_FILE_NAME);
                    }
                } catch (IOException exception) {
                    LOGGER.warn("No se pudo cargar el correo SMTP desde config.properties externo, intentando cargar email.properties interno", exception);
                }
            }

            // Priority 3: Internal email.properties fallback
            if (!loaded) {
                try (InputStream inputStream = Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(CONFIGURATION_FILE)) {
                    if (inputStream != null) {
                        properties.load(inputStream);
                        LOGGER.info("Configuración de correo SMTP cargada desde email.properties interno");
                    } else {
                        LOGGER.warn("No se encontró el archivo de propiedades de correo interno: {}", CONFIGURATION_FILE);
                    }
                } catch (IOException exception) {
                    LOGGER.error("No se pudo cargar la configuración de correo interna: {}", CONFIGURATION_FILE, exception);
                    throw new IllegalStateException(
                        "No se pudo cargar email.properties",
                        exception
                    );
                }
            }
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