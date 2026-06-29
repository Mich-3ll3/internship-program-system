package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;

public class ProfessorStudentDashboardController {

    @FXML private TextField txtSearch;
    @FXML private TableView<InternDTO> tblStudents;
    @FXML private TableColumn<InternDTO, String> colMatricula;
    @FXML private TableColumn<InternDTO, String> colName;
    @FXML private TableColumn<InternDTO, String> colProject;
    @FXML private TableColumn<InternDTO, String> colStatus;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadStudents();
    }

    private void setupTableColumns() {
        colMatricula.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnrollmentNumber()));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
        colProject.setCellValueFactory(cellData -> new SimpleStringProperty("Proyecto por asignar"));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsActive() != null && cellData.getValue().getIsActive() ? "ACTIVO" : "INACTIVO"));
    }

    private void loadStudents() {
        try {
            // For now, load dummy data or real data if DAO was complete for this specific view
            ObservableList<InternDTO> students = FXCollections.observableArrayList();
            InternDTO mock = new InternDTO();
            mock.setEnrollmentNumber("S20010001");
            mock.setName("Sofía");
            mock.setFirstSurname("Flores");
            mock.setIsActive(true);
            students.add(mock);
            
            tblStudents.setItems(students);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los estudiantes.");
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String filter = txtSearch.getText();
        showAlert(Alert.AlertType.INFORMATION, "Buscar", "Buscando estudiante: " + filter);
    }

    @FXML
    private void handleClearSearch(ActionEvent event) {
        txtSearch.clear();
        loadStudents();
    }

    @FXML
    private void handleViewDocuments(ActionEvent event) {
        InternDTO selected = tblStudents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Seleccione un estudiante primero.");
            return;
        }
        // In a real scenario we pass the student to the next view
        WindowManagerController.changeView("ProfessorDocumentDashboard.fxml");
    }

    @FXML
    private void handleViewReports(ActionEvent event) {
        InternDTO selected = tblStudents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Seleccione un estudiante primero.");
            return;
        }
        // In a real scenario we pass the student to the next view
        WindowManagerController.changeView("ProfessorReportDashboard.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
