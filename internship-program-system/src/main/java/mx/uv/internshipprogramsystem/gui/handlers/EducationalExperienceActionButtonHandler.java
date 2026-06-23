package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class EducationalExperienceActionButtonHandler
        implements EventHandler<ActionEvent> {

    private final EducationalExperienceActionCell cell;

    public EducationalExperienceActionButtonHandler(
            EducationalExperienceActionCell cell
    ) {
        this.cell = cell;
    }

    @Override
    public void handle(ActionEvent event) {
        cell.handleAction();
    }
}
