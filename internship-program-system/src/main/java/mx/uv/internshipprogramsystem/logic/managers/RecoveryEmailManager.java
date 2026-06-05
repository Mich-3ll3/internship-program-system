package mx.uv.internshipprogramsystem.logic.managers;

import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;
import mx.uv.internshipprogramsystem.logic.security.EmailConfiguration;

public class RecoveryEmailManager {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(RecoveryEmailManager.class);

    public void sendRecoveryEmail(
            String recipientEmail,
            String recoveryToken
    ) throws BusinessException {
        InputValidator.validateNotEmpty(
            recipientEmail,
            "El correo destinatario no puede estar vacío."
        );

        InputValidator.validateNotEmpty(
            recoveryToken,
            "El token de recuperación no puede estar vacío."
        );

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
            message.setSubject("Recuperación de contraseña");
            message.setText(buildRecoveryMessage(recoveryToken));

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
                "Correo de recuperación enviado a {}",
                recipientEmail
            );
        } catch (MessagingException messagingException) {
            throw new BusinessException(
                "No se pudo enviar el correo de recuperación.",
                messagingException
            );
        } finally {
            closeTransport(transport);
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

    private String buildRecoveryMessage(String recoveryToken) {
        String recoveryMessage =
            "Solicitud de recuperación de contraseña.\n\n"
            + "Utiliza el siguiente token para cambiar tu contraseña:\n\n"
            + recoveryToken
            + "\n\n"
            + "Este token expirará en 1 hora.";

        return recoveryMessage;
    }

    private void closeTransport(
            Transport transport
    ) {
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
