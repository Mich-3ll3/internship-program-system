package mx.uv.internshipprogramsystem.gui.util;

import java.io.IOException;
import java.net.URL;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.nio.charset.StandardCharsets;

public class FxmlLoadTask extends Task<Parent> {
    private final String fxmlPath;

    public FxmlLoadTask(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    @Override
    protected Parent call() throws Exception {
        URL url = NavigationDashboardManager.class.getResource(fxmlPath);
        if (url == null) {
            throw new IOException("Archivo FXML no encontrado: " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(url);
        loader.setCharset(StandardCharsets.UTF_8);
        return loader.load();
    }
}
