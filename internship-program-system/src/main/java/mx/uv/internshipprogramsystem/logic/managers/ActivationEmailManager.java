package mx.uv.internshipprogramsystem.logic.managers;

import java.sql.Connection;
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
import mx.uv.internshipprogramsystem.logic.dto.ActivationTokenDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.security.SecurityManager;
import mx.uv.internshipprogramsystem.logic.security.EmailConfiguration;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ActivationEmailManager {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ActivationEmailManager.class);

    private static final int ACTIVATION_TOKEN_EXPIRATION_HOURS = 24;

    private final ActivationTokenDAO activationTokenDAO;
    private final SecurityManager securityManager;

    public ActivationEmailManager() {
        activationTokenDAO = new ActivationTokenDAO();
        securityManager = new SecurityManager();
    }

    public String createActivationToken(
            int userId,
            Connection connection
    ) throws BusinessException {
        String activationToken = securityManager.generateActivationToken();
        String tokenHash = securityManager.hashToken(activationToken);

        ActivationTokenDTO activationTokenDTO =
            buildActivationToken(userId, tokenHash);

        saveActivationToken(activationTokenDTO, connection);

        return activationToken;
    }

    public void resendActivationToken(
            int userId,
            String institutionalEmail
    ) throws BusinessException {
        InputValidator.validatePositive(
            userId,
            "El identificador del usuario no es válido."
        );

        InputValidator.validateNotEmpty(
            institutionalEmail,
            "El correo institucional no puede estar vacío."
        );

        invalidatePreviousTokens(userId);

        String activationToken = securityManager.generateActivationToken();
        String tokenHash = securityManager.hashToken(activationToken);

        ActivationTokenDTO activationTokenDTO =
            buildActivationToken(userId, tokenHash);

        saveActivationToken(activationTokenDTO);

        sendActivationEmail(
            institutionalEmail,
            activationToken
        );

        LOGGER.info(
            "Nuevo token de activación enviado a {}",
            institutionalEmail
        );
    }

    public void sendActivationEmail(
            String recipientEmail,
            String activationToken
    ) throws BusinessException {
        Properties properties = buildEmailProperties();
        Session session = Session.getInstance(properties);
        Transport transport = null;

        try {
            Message message = new MimeMessage(session);

            message.setFrom(
                new InternetAddress(
                    EmailConfiguration.getEmail()
                )
            );
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(recipientEmail)
            );
            message.setSubject("Activación de cuenta");
            message.setText(buildActivationMessage(activationToken));

            transport = session.getTransport("smtp");
            transport.connect(
                EmailConfiguration.getHost(),
                EmailConfiguration.getEmail(),
                EmailConfiguration.getPassword()
            );
            transport.sendMessage(
                message,
                message.getAllRecipients()
            );

            LOGGER.info(
                "Correo de activación enviado a {}",
                recipientEmail
            );
        } catch (MessagingException messagingException) {
            throw new BusinessException(
                "No se pudo enviar el correo de activación.",
                messagingException
            );
        } finally {
            closeTransport(transport);
        }
    }

    private void invalidatePreviousTokens(int userId)
            throws BusinessException {
        boolean wereTokensInvalidated =
            activationTokenDAO.invalidateTokensByUserId(userId);

        if (!wereTokensInvalidated) {
            LOGGER.warn(
                "No se encontraron tokens activos para invalidar "
                + "del usuario {}",
                userId
            );
        }
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

    private void saveActivationToken(
            ActivationTokenDTO activationToken,
            Connection connection
    ) throws BusinessException {
        boolean wasCreated = activationTokenDAO.create(
            activationToken,
            connection
        );

        validateActivationTokenWasCreated(wasCreated);
    }

    private void saveActivationToken(
            ActivationTokenDTO activationToken
    ) throws BusinessException {
        boolean wasCreated = activationTokenDAO.create(activationToken);

        validateActivationTokenWasCreated(wasCreated);
    }

    private void validateActivationTokenWasCreated(boolean wasCreated)
            throws BusinessException {
        if (!wasCreated) {
            throw new BusinessException(
                "No se pudo generar el token de activación."
            );
        }
    }

    private Properties buildEmailProperties() {
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put(
            "mail.smtp.host",
            EmailConfiguration.getHost()
        );

        properties.put(
            "mail.smtp.port",
            EmailConfiguration.getPort()
        );

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

    private void closeTransport(Transport transport) {
        if (transport != null && transport.isConnected()) {
            try {
                transport.close();
            } catch (MessagingException messagingException) {
                LOGGER.error(
                    "Error al cerrar la conexión SMTP",
                    messagingException
                );
            }
        }
    }
}