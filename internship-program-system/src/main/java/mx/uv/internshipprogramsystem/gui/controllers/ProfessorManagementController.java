package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfessorManagementController {

    private static final Logger LOGGER = Logger.getLogger(ProfessorManagementController.class.getName());

    @FXML private TextField txtSearchStaffNumber;
    @FXML private TextArea txtProfessorDetails;
    @FXML private TableView<ProfessorDTO> tblProfessors;
    @FXML private TableColumn<ProfessorDTO, Integer> colStaffNumber;
    @FXML private TableColumn<ProfessorDTO, String> colName;
    @FXML private TableColumn<ProfessorDTO, String> colEmail;
    @FXML private TableColumn<ProfessorDTO, Boolean> colCoordinator;
    @FXML private TableColumn<ProfessorDTO, Boolean> colStatus;
    private ObservableList<ProfessorDTO> professorsList;

    @FXML
    public void initialize() {
        configureTableColumns();
        loadProfessorsData();
        setupTableSelectionListener();
    }

    private void configureTableColumns() {
        colStaffNumber.setCellValueFactory(new PropertyValueFactory<>("staffNumber"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("institucionalEmail"));
        colCoordinator.setCellValueFactory(new PropertyValueFactory<>("isCoordinator"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("isActive"));
        colCoordinator.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item ? "Sí" : "No"));
            }
        });
    }

    private void loadProfessorsData() {
        try {
            ProfessorDAO professorDAO = new ProfessorDAO();
            List<ProfessorDTO> list = professorDAO.findAll(); 
            professorsList = FXCollections.observableArrayList(list);
            tblProfessors.setItems(professorsList);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not load professors data", e);
            showNotification(Alert.AlertType.ERROR, "Error de carga", "No se pudo conectar con la base de datos.");
        }
    }

    private void setupTableSelectionListener() {
        tblProfessors.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayProfessorDetails(newSelection);
            }
        });
    }

    private void displayProfessorDetails(ProfessorDTO professor) {
        String details = String.format(
            "Nombre completo: %s %s %s\nNúmero de personal: %d\nCorreo: %s\nRol: %s",
            professor.getName(), 
            professor.getFirstSurname(), 
            professor.getSecondSurname(),
            professor.getStaffNumber(),
            professor.getInstitucionalEmail(),
            professor.getIsCoordinator() ? "Coordinador" : "Profesor de tiempo completo"
        );
        txtProfessorDetails.setText(details);
    }

    @FXML
    private void validateSearchProfessor() {
        String searchCriteria = txtSearchStaffNumber.getText().trim();
        if (searchCriteria.isEmpty()) {
            tblProfessors.setItems(professorsList);
            return;
        }
        ObservableList<ProfessorDTO> filteredList = professorsList.filtered(p -> 
            String.valueOf(p.getStaffNumber()).contains(searchCriteria)
        );
        tblProfessors.setItems(filteredList);
    }

    @FXML
    private void showPendingFunctionalityMessage() {
        showNotification(Alert.AlertType.INFORMATION, "Próximamente", "Esta funcionalidad estará disponible en la siguiente versión.");
    }

    private void showNotification(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}