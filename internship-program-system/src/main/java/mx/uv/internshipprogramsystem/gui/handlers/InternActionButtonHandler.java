package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class InternActionButtonHandler implements EventHandler<ActionEvent> {
    private final InternActionCell cell;

    public InternActionButtonHandler(InternActionCell cell) {
        this.cell = cell;
    }

    @Override
    public void handle(ActionEvent event) {
        cell.handleAction();
    }
}
