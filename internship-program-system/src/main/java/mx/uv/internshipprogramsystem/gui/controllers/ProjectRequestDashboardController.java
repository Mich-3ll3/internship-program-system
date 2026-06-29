package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.internshipprogramsystem.logic.dao.ProjectRequestDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectRequestDTO;

public class ProjectRequestDashboardController {

    @FXML private TableView<ProjectRequestDTO> tblRequests;
    @FXML private TableColumn<ProjectRequestDTO, Integer> colStudentId;
    @FXML private TableColumn<ProjectRequestDTO, Integer> colProjectId;
    @FXML private TableColumn<ProjectRequestDTO, Integer> colPriority;

    private ProjectRequestDAO projectRequestDAO;

    @FXML
    public void initialize() {
        projectRequestDAO = new ProjectRequestDAO();
        setupTableColumns();
        loadRequests();
    }

    private void setupTableColumns() {
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colProjectId.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
    }

    private void loadRequests() {
        try {
            // Right now we just load an empty list or mock data, as we might need a get method in DAO
            ObservableList<ProjectRequestDTO> data = FXCollections.observableArrayList();
            // Optional: load from DB if there is a getAll method
            tblRequests.setItems(data);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron cargar las solicitudes: " + e.getMessage());
        }
    }

    @FXML
    private void handleAssignProject(ActionEvent event) {
        ProjectRequestDTO selected = tblRequests.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Atención", "Debe seleccionar una solicitud de la tabla.");
            return;
        }
        
        // This could open the assignment window
        WindowManagerController.changeView("ProjectAssignment.fxml");
    }

    @FXML
    private void goBack(ActionEvent event) {
        WindowManagerController.changeView("CoordinatorProfessorHomeDashboard.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
