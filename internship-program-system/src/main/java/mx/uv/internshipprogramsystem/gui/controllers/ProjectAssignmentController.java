package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import mx.uv.internshipprogramsystem.logic.dao.ProjectAssignmentDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectAssignmentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

import java.time.LocalDate;

public class ProjectAssignmentController {

    @FXML private TextField txtEstudianteId;
    @FXML private TextField txtProyectoId;
    @FXML private TextField txtProfesorId;
    @FXML private DatePicker dpFechaAsignacion;

    private ProjectAssignmentDAO assignmentDAO = new ProjectAssignmentDAO();

    @FXML
    private void handleSave() {
        try {
            int studentId = Integer.parseInt(txtEstudianteId.getText().trim());
            int projectId = Integer.parseInt(txtProyectoId.getText().trim());
            int professorId = Integer.parseInt(txtProfesorId.getText().trim());
            LocalDate fecha = dpFechaAsignacion.getValue() != null ? dpFechaAsignacion.getValue() : LocalDate.now();

            // Construir el DTO con los cuatro campos
            ProjectAssignmentDTO assignment = new ProjectAssignmentDTO(studentId, projectId, professorId, fecha);

            boolean result = assignmentDAO.insert(assignment);

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Registro de Asignación");
            alert.setHeaderText(null);
            alert.setContentText(result 
                ? "Asignación guardada correctamente." 
                : "No se pudo guardar la asignación.");
            alert.showAndWait();

        } catch (BusinessException be) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de negocio");
            alert.setHeaderText("No se pudo registrar la asignación");
            alert.setContentText(be.getMessage());
            alert.showAndWait();
        } catch (NumberFormatException nfe) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de formato");
            alert.setHeaderText("Datos inválidos");
            alert.setContentText("Verifica que los IDs sean números enteros.");
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
        txtEstudianteId.clear();
        txtProyectoId.clear();
        txtProfesorId.clear();
        dpFechaAsignacion.setValue(null);
    }
}
