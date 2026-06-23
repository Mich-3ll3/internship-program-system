package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import mx.uv.internshipprogramsystem.gui.controllers.LinkedOrganizationManagementController;

public class OrganizationSearchListener implements ChangeListener<String> {
    private final LinkedOrganizationManagementController controller;

    public OrganizationSearchListener(
        LinkedOrganizationManagementController controller
    ) {
        this.controller = controller;
    }

    @Override
    public void changed(
        ObservableValue<? extends String> observable,
        String oldValue,
        String newValue
    ) {
        controller.applyOrganizationFilter(newValue);
    }
}
