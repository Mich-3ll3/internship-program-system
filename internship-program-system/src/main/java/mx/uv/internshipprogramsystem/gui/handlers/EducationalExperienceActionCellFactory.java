package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import mx.uv.internshipprogramsystem.gui.controllers.EducationalExperienceManagementController;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceDTO;

public class EducationalExperienceActionCellFactory
        implements Callback<TableColumn<EducationalExperienceDTO, Void>,
                            TableCell<EducationalExperienceDTO, Void>> {

    private final EducationalExperienceManagementController controller;

    public EducationalExperienceActionCellFactory(
            EducationalExperienceManagementController controller
    ) {
        this.controller = controller;
    }

    @Override
    public TableCell<EducationalExperienceDTO, Void> call(
            TableColumn<EducationalExperienceDTO, Void> param
    ) {
        return new EducationalExperienceActionCell(controller);
    }
}
