package mx.uv.internshipprogramsystem.gui.util;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mx.uv.internshipprogramsystem.gui.handlers.FxmlLoadSuccessHandler;
import mx.uv.internshipprogramsystem.gui.handlers.FxmlLoadFailureHandler;

public final class NavigationDashboardManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(
        NavigationDashboardManager.class
    );
    private static final Map<String, Parent> viewsCache = new HashMap<>();

    private NavigationDashboardManager() {
    }

    public static void clearCache() {
        viewsCache.clear();
        LOGGER.info("Se ha limpiado la caché de vistas.");
    }

    public static void cacheView(String fxmlPath, Parent view) {
        viewsCache.put(fxmlPath, view);
    }

    public static void loadCenterView(Pane contentParent, String fxmlPath)
            throws ViewLoadException {
        loadCenterViewAsynchronously(contentParent, fxmlPath);
    }

    public static void loadCenterViewAsynchronously(
        Pane centerPane,
        String fxmlPath
    ) {
        if (centerPane == null || fxmlPath == null || fxmlPath.trim().isEmpty()) {
            LOGGER.error("El contenedor o la ruta FXML es nula o vacía.");
            return;
        }

        // Se comenta la recuperación desde caché para asegurar que todas las pantallas
        // que contienen tablas o listados de datos dinámicos se inicialicen de cero
        // y carguen la información actualizada de la base de datos.
        /*
        Parent cachedView = viewsCache.get(fxmlPath);
        if (cachedView != null) {
            LOGGER.info("Vista desde caché: {}", fxmlPath);
            displayView(centerPane, cachedView);
            return;
        }
        */

        LOGGER.info("Cargando FXML de forma asíncrona: {}", fxmlPath);
        FxmlLoadTask task = new FxmlLoadTask(fxmlPath);

        task.setOnSucceeded(
            new FxmlLoadSuccessHandler(centerPane, fxmlPath, task)
        );
        task.setOnFailed(
            new FxmlLoadFailureHandler(fxmlPath, task)
        );

        new Thread(task).start();
    }

    public static void handleFailure(Throwable ex, String fxmlPath) {
        LOGGER.error("Fallo técnico al cargar la vista: " + fxmlPath, ex);
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle("Error de Carga");
        alert.setHeaderText("No se pudo cargar el módulo");
        alert.setContentText("Ocurrió un error al cargar la vista solicitada. "
            + "Por favor, intente de nuevo.");
        alert.showAndWait();
    }

    public static void displayView(Pane contentParent, Parent view) {
        if (view instanceof BorderPane) {
            ((BorderPane) view).setLeft(null);
        }
        if (view instanceof javafx.scene.layout.Region region) {
            region.setPrefWidth(javafx.scene.layout.Region.USE_COMPUTED_SIZE);
            region.setPrefHeight(javafx.scene.layout.Region.USE_COMPUTED_SIZE);
            region.setMaxWidth(Double.MAX_VALUE);
            region.setMaxHeight(Double.MAX_VALUE);
            region.setMinWidth(0);
            region.setMinHeight(0);
        }
        if (contentParent instanceof BorderPane) {
            ((BorderPane) contentParent).setCenter(view);
        } else {
            contentParent.getChildren().setAll(view);
        }
    }
}
