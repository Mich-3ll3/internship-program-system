package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import mx.uv.internshipprogramsystem.gui.util.NavigationDashboardManager;
import mx.uv.internshipprogramsystem.gui.util.FxmlLoadTask;

public class FxmlLoadSuccessHandler implements EventHandler<WorkerStateEvent> {
    private final Pane centerPane;
    private final String fxmlPath;
    private final FxmlLoadTask task;

    public FxmlLoadSuccessHandler(
        Pane centerPane,
        String fxmlPath,
        FxmlLoadTask task
    ) {
        this.centerPane = centerPane;
        this.fxmlPath = fxmlPath;
        this.task = task;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        Parent loadedView = task.getValue();
        NavigationDashboardManager.cacheView(fxmlPath, loadedView);
        NavigationDashboardManager.displayView(centerPane, loadedView);
    }
}
