package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.InternValidator;

public class InternManagementController {

    @FXML
    private TextField txtSearchEnrollment;

    @FXML
    private void validateSearchIntern() {
        try {
            new InternValidator().validateEnrollmentNumber(txtSearchEnrollment.getText());
            FormAlertSupport.showInformation("Búsqueda válida", "La matrícula tiene el formato correcto.");
        } catch (BusinessException exception) {
            FormAlertSupport.showWarning("Error de validación", exception.getMessage());
        }
    }

    @FXML
    private void showPendingFunctionalityMessage() {
        FormAlertSupport.showInformation("Funcionalidad pendiente", "Esta acción se conectará con la lógica de negocio posteriormente.");
    }
}
