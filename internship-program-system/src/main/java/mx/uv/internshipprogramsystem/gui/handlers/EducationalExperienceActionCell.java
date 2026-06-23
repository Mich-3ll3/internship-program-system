package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import mx.uv.internshipprogramsystem.gui.controllers.EducationalExperienceManagementController;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceDTO;

public class EducationalExperienceActionCell
        extends TableCell<EducationalExperienceDTO, Void> {

    private final Button viewButton;
    private final EducationalExperienceManagementController controller;

    public EducationalExperienceActionCell(
            EducationalExperienceManagementController controller
    ) {
        this.controller = controller;
        this.viewButton = new Button("Ver");
        this.viewButton.setStyle(
            "-fx-background-color: #e0f2fe; "
            + "-fx-background-radius: 10; "
            + "-fx-text-fill: #075985; "
            + "-fx-font-weight: bold; "
            + "-fx-cursor: hand; "
            + "-fx-padding: 6 14 6 14;"
        );
        this.viewButton.setOnAction(
            new EducationalExperienceActionButtonHandler(this)
        );
    }

    public void handleAction() {
        EducationalExperienceDTO selectedExperience =
            getTableView().getItems().get(getIndex());
        controller.showEducationalExperienceDetails(selectedExperience);
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || getTableRow() == null
                || getTableRow().getItem() == null) {
            setGraphic(null);
        } else {
            setGraphic(viewButton);
        }
    }
}
