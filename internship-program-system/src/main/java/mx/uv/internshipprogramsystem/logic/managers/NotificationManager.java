package mx.uv.internshipprogramsystem.logic.managers;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mx.uv.internshipprogramsystem.logic.dao.NotificationDAO;
import mx.uv.internshipprogramsystem.logic.dto.NotificationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.INotificationDAO;

public class NotificationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationManager.class);
    private final INotificationDAO notificationDAO;

    public NotificationManager() {
        this.notificationDAO = new NotificationDAO();
    }

    public boolean createNotification(int userId, String message) throws BusinessException {
        if (message == null || message.trim().isEmpty()) {
            throw new BusinessException("El mensaje de notificación no puede estar vacío.");
        }
        NotificationDTO notification = new NotificationDTO(userId, message.trim());
        boolean success = notificationDAO.insert(notification);
        if (success) {
            LOGGER.info("Notificación creada exitosamente para el usuario {}", userId);
        }
        return success;
    }

    public List<NotificationDTO> getUserNotifications(int userId) throws BusinessException {
        return notificationDAO.findByUserId(userId);
    }

    public boolean markNotificationAsRead(int notificationId) throws BusinessException {
        boolean success = notificationDAO.markAsRead(notificationId);
        if (success) {
            LOGGER.info("Notificación {} marcada como leída.", notificationId);
        }
        return success;
    }
}
