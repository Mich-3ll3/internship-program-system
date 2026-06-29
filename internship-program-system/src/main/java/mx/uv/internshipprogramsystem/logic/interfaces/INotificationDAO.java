package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.List;
import mx.uv.internshipprogramsystem.logic.dto.NotificationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface INotificationDAO {
    boolean insert(NotificationDTO notification) throws BusinessException;
    List<NotificationDTO> findByUserId(int userId) throws BusinessException;
    boolean markAsRead(int notificationId) throws BusinessException;
}
