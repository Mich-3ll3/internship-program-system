package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.SelfAssessmentManager;

public class RegisterSelfAssessmentController implements Initializable {

    @FXML private ComboBox<Integer> cmbEstudiante;
    @FXML private ComboBox<Integer> cmbProyecto;
    @FXML private ComboBox<Integer> cmbOrganizacion;
    @FXML private ComboBox<Integer> cmbResponsable;

    @FXML private TextField txtDepartamento;
    @FXML private TextField txtLugar;

    @FXML private DatePicker dpFecha;

    @FXML private ToggleGroup tgAfirmacion1;
    @FXML private ToggleGroup tgAfirmacion2;
    @FXML private ToggleGroup tgAfirmacion3;
    @FXML private ToggleGroup tgAfirmacion4;
    @FXML private ToggleGroup tgAfirmacion5;
    @FXML private ToggleGroup tgAfirmacion6;
    @FXML private ToggleGroup tgAfirmacion7;
    @FXML private ToggleGroup tgAfirmacion8;
    @FXML private ToggleGroup tgAfirmacion9;
    @FXML private ToggleGroup tgAfirmacion10;

    @FXML private TextArea txtObservaciones;

    private final SelfAssessmentManager selfAssessmentManager = new SelfAssessmentManager();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @FXML
    private void handleSave(javafx.event.ActionEvent event) {
        try {
            selfAssessmentManager.registerSelfAssessment(
                cmbEstudiante.getValue(),
                cmbProyecto.getValue(),
                cmbOrganizacion.getValue(),
                cmbResponsable.getValue(),
                txtDepartamento.getText(),
                txtLugar.getText(),
                dpFecha.getValue(),
                getSelectedValue(tgAfirmacion1),
                getSelectedValue(tgAfirmacion2),
                getSelectedValue(tgAfirmacion3),
                getSelectedValue(tgAfirmacion4),
                getSelectedValue(tgAfirmacion5),
                getSelectedValue(tgAfirmacion6),
                getSelectedValue(tgAfirmacion7),
                getSelectedValue(tgAfirmacion8),
                getSelectedValue(tgAfirmacion9),
                getSelectedValue(tgAfirmacion10),
                txtObservaciones.getText()
            );

            showSuccess("Autoevaluación registrada correctamente.");
            goBack(event);

        } catch (BusinessException exception) {
            showError("Error al guardar la autoevaluación: " + exception.getMessage());
        }
    }

    @FXML
    private void handleCancel(javafx.event.ActionEvent event) {
        goBack(event);
    }

    @FXML
    private void goBack(javafx.event.ActionEvent event) {
        WindowManagerController.changeView("SelfAssessmentHomeDashboard.fxml");
    }

    private int getSelectedValue(ToggleGroup toggleGroup) {
        if (toggleGroup.getSelectedToggle() != null) {
            return Integer.parseInt(toggleGroup.getSelectedToggle().getUserData().toString());
        }
        return 0;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
