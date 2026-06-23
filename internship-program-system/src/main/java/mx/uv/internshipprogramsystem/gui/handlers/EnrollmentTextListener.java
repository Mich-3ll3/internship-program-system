package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import mx.uv.internshipprogramsystem.gui.controllers.RegisterInternFormController;

public class EnrollmentTextListener implements ChangeListener<String> {
    private final RegisterInternFormController controller;

    public EnrollmentTextListener(RegisterInternFormController controller) {
        this.controller = controller;
    }

    @Override
    public void changed(
            ObservableValue<? extends String> observable,
            String oldValue,
            String newValue
    ) {
        controller.handleEnrollmentChanged(newValue);
    }
}
