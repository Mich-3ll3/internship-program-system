package mx.uv.internshipprogramsystem.logic.security;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ActivationTokenDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.ActivationTokenDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public class UserRegistrationManager {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(UserRegistrationManager.class);

    private static final int ACTIVATION_TOKEN_EXPIRATION_HOURS = 24;
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SYSTEM_EMAIL = "internship.system.uv@gmail.com";
    private static final String SYSTEM_EMAIL_PASSWORD = "ugxmnvljsieuxhpu";

    private final UserDAO userDAO;
    private final ActivationTokenDAO activationTokenDAO;
    private final SecurityManager securityManager;

    public UserRegistrationManager() {
        userDAO = new UserDAO();
        activationTokenDAO = new ActivationTokenDAO();
        securityManager = new SecurityManager();
    }

    public int registerUser(UserDTO user) throws BusinessException {
        int userId = userDAO.create(user);
        String activationToken = securityManager.generateActivationToken();
        String tokenHash = securityManager.hashToken(activationToken);
        ActivationTokenDTO activationTokenDTO =
            buildActivationToken(userId, tokenHash);

        saveActivationToken(activationTokenDTO);
        sendActivationEmail(user.getInstitutionalEmail(), activationToken);

        LOGGER.info(
            "Usuario registrado y correo de activación enviado: {}",
            user.getInstitutionalEmail()
        );

        return userId;
    }

    private ActivationTokenDTO buildActivationToken(
            int userId,
            String tokenHash
    ) {
        ActivationTokenDTO activationToken = new ActivationTokenDTO();

        activationToken.setUserId(userId);
        activationToken.setTokenHash(tokenHash);
        activationToken.setExpirationDate(getExpirationDate());
        activationToken.setUsed(false);

        return activationToken;
    }

    private Timestamp getExpirationDate() {
        Timestamp expirationDate = Timestamp.from(
            Instant.now().plus(
                ACTIVATION_TOKEN_EXPIRATION_HOURS,
                ChronoUnit.HOURS
            )
        );

        return expirationDate;
    }

    private void saveActivationToken(ActivationTokenDTO activationToken)
            throws BusinessException {
        boolean wasCreated = activationTokenDAO.create(activationToken);

        if (!wasCreated) {
            LOGGER.error(
                "No se pudo guardar el token de activación para usuario {}",
                activationToken.getUserId()
            );

            throw new BusinessException(
                "No se pudo generar el token de activación."
            );
        }

        LOGGER.info(
            "Token de activación guardado para usuario {}",
            activationToken.getUserId()
        );
    }

    private void sendActivationEmail(
            String recipientEmail,
            String activationToken
    ) throws BusinessException {
        Properties properties = buildEmailProperties();
        Session session = Session.getInstance(properties);
        Transport transport = null;

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(SYSTEM_EMAIL));
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(recipientEmail)
            );
            message.setSubject("Activación de cuenta");
            message.setText(buildActivationMessage(activationToken));

            transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, SYSTEM_EMAIL, SYSTEM_EMAIL_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());

            LOGGER.info(
                "Correo de activación enviado a {}",
                recipientEmail
            );
        } catch (MessagingException messagingException) {
            LOGGER.error(
                "Error al enviar correo de activación a {}",
                recipientEmail,
                messagingException
            );

            throw new BusinessException(
                "No se pudo enviar el correo de activación.",
                messagingException
            );
        } finally {
            closeTransport(transport);
        }
    }

    private void closeTransport(Transport transport)
            throws BusinessException {
        if (transport != null && transport.isConnected()) {
            try {
                transport.close();
            } catch (MessagingException messagingException) {
                LOGGER.error(
                    "Error al cerrar la conexión SMTP",
                    messagingException
                );

                throw new BusinessException(
                    "No se pudo cerrar la conexión de correo.",
                    messagingException
                );
            }
        }
    }

    private Properties buildEmailProperties() {
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);

        return properties;
    }

    private String buildActivationMessage(String activationToken) {
        String activationMessage =
            "Bienvenido al Sistema de Prácticas Profesionales.\n\n"
            + "Utiliza el siguiente token para activar tu cuenta:\n\n"
            + activationToken
            + "\n\n"
            + "Este token expirará en 24 horas.";

        return activationMessage;
    }
}