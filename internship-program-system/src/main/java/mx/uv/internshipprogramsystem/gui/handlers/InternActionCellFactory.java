package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import mx.uv.internshipprogramsystem.gui.controllers.InternManagementController;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;

public class InternActionCellFactory
    implements Callback<TableColumn<InternDTO, Void>, TableCell<InternDTO, Void>> {

    private final InternManagementController controller;

    public InternActionCellFactory(InternManagementController controller) {
        this.controller = controller;
    }

    @Override
    public TableCell<InternDTO, Void> call(TableColumn<InternDTO, Void> param) {
        return new InternActionCell(controller);
    }
}
