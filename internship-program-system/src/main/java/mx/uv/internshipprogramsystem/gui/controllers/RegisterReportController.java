package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import mx.uv.internshipprogramsystem.logic.dao.ReportDAO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public class RegisterReportController implements Initializable {

    @FXML private TextField txtNumber;
    @FXML private TextField txtCoveredHours;
    @FXML private DatePicker dpDate;
    @FXML private DatePicker dpPeriodStart;
    @FXML private DatePicker dpPeriodEnd;
    @FXML private ComboBox<String> cmbProject;
    @FXML private TextArea txtMethodology;
    @FXML private TextArea txtResults;

    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private Hyperlink lnkVolver;

    private final ReportDAO reportDAO = new ReportDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            cmbProject.setItems(javafx.collections.FXCollections.observableArrayList(reportDAO.getAllProjects()));
        } catch (BusinessException ex) {
            showError("Error cargando proyectos: " + ex.getMessage());
        }
    }

    @FXML
    private void handleSaveReport() {

    }

    @FXML
    private void handleCancel() {
        WindowManagerController.changeView("ReportHomeDashboard.fxml");
    }

    @FXML
    private void goHome(javafx.event.ActionEvent actionEvent) {
        WindowManagerController.changeView("InternHomeDashboard.fxml");
    }

    @FXML
    private void goProjectsModule(javafx.event.ActionEvent actionEvent) {
        WindowManagerController.changeView("ProjectsDashboard.fxml");
    }

    @FXML
    private void goDocumentsModule(javafx.event.ActionEvent actionEvent) {
        WindowManagerController.changeView("DocumentsDashboard.fxml");
    }

    @FXML
    private void goReportsModule(javafx.event.ActionEvent actionEvent) {
        WindowManagerController.changeView("ReportHomeDashboard.fxml");
    }

    @FXML
    private void goSelfAssessmentsModule(javafx.event.ActionEvent actionEvent) {
        WindowManagerController.changeView("SelfAssessmentHomeDashboard.fxml");
    }

    @FXML
    private void logOut(javafx.event.ActionEvent actionEvent) {
        WindowManagerController.changeView("LoginDashboard.fxml");
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Éxito");
        alert.showAndWait();
    }
}
