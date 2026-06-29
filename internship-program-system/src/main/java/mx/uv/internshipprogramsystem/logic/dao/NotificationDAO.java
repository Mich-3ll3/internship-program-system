package mx.uv.internshipprogramsystem.logic.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import mx.uv.internshipprogramsystem.logic.dto.NotificationDTO;

public class NotificationDAO {
    private static final List<NotificationDTO> NOTIFICATIONS = new ArrayList<>();
    private static int nextId = 1;

    public void addNotification(int userId, String message) {
        NotificationDTO notif = new NotificationDTO(userId, message);
        notif.setId(nextId++);
        NOTIFICATIONS.add(notif);
    }

    public List<NotificationDTO> getNotificationsForUser(int userId) {
        return NOTIFICATIONS.stream()
                .filter(n -> n.getUserId() == userId)
                .collect(Collectors.toList());
    }

    public void markAsRead(int notificationId) {
        NOTIFICATIONS.stream()
                .filter(n -> n.getId() == notificationId)
                .findFirst()
                .ifPresent(n -> n.setRead(true));
    }
}
