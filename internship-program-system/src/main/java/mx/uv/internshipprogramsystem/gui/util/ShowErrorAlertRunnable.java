package mx.uv.internshipprogramsystem.gui.util;

import javafx.scene.control.Alert;

public class ShowErrorAlertRunnable implements Runnable {
    private final String title;
    private final String message;

    public ShowErrorAlertRunnable(String title, String message) {
        this.title = title;
        this.message = message;
    }

    @Override
    public void run() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
