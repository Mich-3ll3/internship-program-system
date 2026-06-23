package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import mx.uv.internshipprogramsystem.gui.controllers.RegisterLinkedOrganizationDashboardController;

public class StateTextChangedListener implements ChangeListener<String> {
    private final RegisterLinkedOrganizationDashboardController controller;

    public StateTextChangedListener(RegisterLinkedOrganizationDashboardController controller) {
        this.controller = controller;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        controller.handleStateTextChangedExternal(newValue);
    }
}
