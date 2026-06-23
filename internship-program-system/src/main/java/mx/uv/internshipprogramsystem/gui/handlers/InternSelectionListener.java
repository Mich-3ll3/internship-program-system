package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import mx.uv.internshipprogramsystem.gui.controllers.InternManagementController;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;

public class InternSelectionListener implements ChangeListener<InternDTO> {
    private final InternManagementController controller;

    public InternSelectionListener(InternManagementController controller) {
        this.controller = controller;
    }

    @Override
    public void changed(
        ObservableValue<? extends InternDTO> observable,
        InternDTO oldValue,
        InternDTO newValue
    ) {
        controller.updateInternSelection(newValue);
    }
}
