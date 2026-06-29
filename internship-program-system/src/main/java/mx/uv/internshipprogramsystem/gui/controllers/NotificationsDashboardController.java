package mx.uv.internshipprogramsystem.gui.controllers;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import mx.uv.internshipprogramsystem.logic.dao.NotificationDAO;
import mx.uv.internshipprogramsystem.logic.dto.NotificationDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class NotificationsDashboardController {

    @FXML private ListView<String> listNotifications;
    
    private NotificationDAO notificationDAO;
    private UserDTO currentUser;
    private List<NotificationDTO> userNotifications;

    @FXML
    public void initialize() {
        notificationDAO = new NotificationDAO();
        currentUser = UserSessionManager.getCurrentUser();
        
        loadNotifications();
    }

    private void loadNotifications() {
        if (currentUser == null) return;
        
        userNotifications = notificationDAO.getNotificationsForUser(currentUser.getId());
        ObservableList<String> items = FXCollections.observableArrayList();
        
        if (userNotifications.isEmpty()) {
            items.add("No tienes notificaciones nuevas.");
        } else {
            for (NotificationDTO n : userNotifications) {
                String prefix = n.isRead() ? "[Leída] " : "[NUEVA] ";
                items.add(prefix + n.getMessage() + " - " + n.getDate().toString());
            }
        }
        
        listNotifications.setItems(items);
    }

    @FXML
    private void handleMarkAllRead(ActionEvent event) {
        if (userNotifications != null) {
            for (NotificationDTO n : userNotifications) {
                notificationDAO.markAsRead(n.getId());
            }
            loadNotifications();
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView("InternHomeDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        WindowManagerController.changeView("LoginDashboard.fxml");
    }
}
