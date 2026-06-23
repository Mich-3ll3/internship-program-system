package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.util.NavigationDashboardManager;
import mx.uv.internshipprogramsystem.gui.util.FxmlLoadTask;

public class FxmlLoadFailureHandler implements EventHandler<WorkerStateEvent> {
    private final String fxmlPath;
    private final FxmlLoadTask task;

    public FxmlLoadFailureHandler(String fxmlPath, FxmlLoadTask task) {
        this.fxmlPath = fxmlPath;
        this.task = task;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        NavigationDashboardManager.handleFailure(task.getException(), fxmlPath);
    }
}
