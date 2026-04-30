package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import mx.uv.internshipprogramsystem.logic.dao.ReportDAO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

import java.time.LocalDate;

public class ReportController {

    @FXML private TextField txtNumero;
    @FXML private TextField txtObservaciones;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtRutaArchivo;
    @FXML private TextField txtEstudianteId;
    @FXML private TextField txtProfesorId;
    @FXML private TextField txtProyectoId;

    private ReportDAO reportDAO = new ReportDAO();

    @FXML
    public void initialize() {
        cmbTipo.getItems().addAll("Parcial", "Final");
    }

    @FXML
    private void handleSave() {
        try {
            ReportDTO report = new ReportDTO();
            report.setNumber(Integer.parseInt(txtNumero.getText()));
            report.setDate(LocalDate.now());
            report.setGeneralObservations(txtObservaciones.getText());
            report.setType(cmbTipo.getValue());
            report.setStatus("Pendiente");
            report.setFilePath(txtRutaArchivo.getText());
            report.setStudentId(Integer.parseInt(txtEstudianteId.getText()));
            report.setProfessorId(Integer.parseInt(txtProfesorId.getText()));
            report.setProjectId(Integer.parseInt(txtProyectoId.getText()));

            boolean result = reportDAO.registerReport(report);

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Registro de Reporte");
            alert.setHeaderText(null);
            alert.setContentText(result 
                ? "Reporte guardado correctamente." 
                : "No se pudo guardar el reporte.");
            alert.showAndWait();

        } catch (BusinessException be) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de negocio");
            alert.setHeaderText("No se pudo registrar el reporte");
            alert.setContentText(be.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error inesperado");
            alert.setHeaderText("Ocurrió un problema");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        txtNumero.clear();
        txtObservaciones.clear();
        txtRutaArchivo.clear();
        txtEstudianteId.clear();
        txtProfesorId.clear();
        txtProyectoId.clear();
        cmbTipo.getSelectionModel().clearSelection();
    }
}
