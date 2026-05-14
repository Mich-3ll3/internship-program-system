package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.event.ActionEvent;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.ProfessorValidator;

public class ProfessorManagementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorManagementController.class.getName());
    

    @FXML private TextField txtSearchStaffNumber;
    @FXML private TableView<ProfessorDTO> tblProfessors;
    @FXML private TableColumn<ProfessorDTO, Integer> colStaffNumber;
    @FXML private TableColumn<ProfessorDTO, String> colName;
    @FXML private TableColumn<ProfessorDTO, String> colEmail;
    @FXML private TableColumn<ProfessorDTO, Boolean> colCoordinator;
    @FXML private TableColumn<ProfessorDTO, Boolean> colStatus;
    
    private final ProfessorDAO professorDAO = new ProfessorDAO();
    private ObservableList<ProfessorDTO> masterData = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        configureTableColumns();
        loadProfessorsData();
    }
    
    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView("AdminHomeDashboard.fxml");
    }

    @FXML
    private void goProfessorModule(ActionEvent event) {
        WindowManagerController.changeView("ProfessorModuleDashboard.fxml");
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        WindowManagerController.changeView("InternModuleDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        WindowManagerController.changeView("LoginDashboard.fxml");
    }
    
    @FXML
    private void goRegisterProfessor(ActionEvent event) {
        WindowManagerController.changeView("RegisterProfessorDashboard.fxml");
    }
    
    @FXML
    private void goUpdateProfessor() {
        ProfessorDTO selectedProfessor = tblProfessors.getSelectionModel().getSelectedItem();

        if (selectedProfessor != null) {
            WindowManagerController.changeViewToUpdateProfessor("UpdateProfessorDashboard.fxml", selectedProfessor);
        } else {
            showNotification(Alert.AlertType.WARNING, "Selección requerida", 
                "Por favor, selecciona un profesor de la tabla para poder modificarlo.");
        }
    }

    private void configureTableColumns() {
        colStaffNumber.setCellValueFactory(new PropertyValueFactory<>("staffNumber"));
        colName.setCellValueFactory(new PropertyValueFactory<>("nameComplet"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("institucionalEmail"));
        colCoordinator.setCellValueFactory(new PropertyValueFactory<>("coordinator"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("isActive"));
    }

    private void loadProfessorsData() {
        try {
            List<ProfessorDTO> list = professorDAO.findAll();
            masterData.setAll(list);
            tblProfessors.setItems(masterData);
        } catch (BusinessException exception) {
            LOGGER.error("Error al cargar profesores", exception);
            showNotification(Alert.AlertType.ERROR, "Error de carga", "No se pudo conectar con la base de datos.");
        }
    }

    @FXML
    private void searchProfessor() {
        try {
            String staffNumber = txtSearchStaffNumber.getText().trim();
            new ProfessorValidator().validateStaffNumber(staffNumber);

            ProfessorDTO professor = professorDAO.findByStaffNumber(staffNumber);

            if (professor != null) {
                ObservableList<ProfessorDTO> filteredData = FXCollections.observableArrayList(professor);
                tblProfessors.setItems(filteredData);
            } else {
                showNotification(Alert.AlertType.WARNING,
                    "Sin resultados",
                    "No se encontró ningún profesor con número de personal " + staffNumber);
            }
        } catch (BusinessException exception) {
            showNotification(Alert.AlertType.ERROR,
                "Error de validación",
                exception.getMessage());
        }
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