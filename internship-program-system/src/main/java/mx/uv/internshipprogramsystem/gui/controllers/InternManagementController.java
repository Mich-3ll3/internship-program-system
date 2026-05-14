package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.InternValidator;
import java.util.List;
import javafx.event.ActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternManagementController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternManagementController.class);

    @FXML private TextField txtSearchEnrollment;
    @FXML private TableView<InternDTO> tblInterns;
    @FXML private TableColumn<InternDTO, String> colEnrollment;
    @FXML private TableColumn<InternDTO, String> colName;
    @FXML private TableColumn<InternDTO, String> colEmail;
    @FXML private TableColumn<InternDTO, Boolean> colStatus;
    @FXML private TableColumn<InternDTO, String> colNRC;

    private final InternDAO internDAO = new InternDAO();
    private ObservableList<InternDTO> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTable();
        loadInternsData();
    }
    
    @FXML
    private void goUpdateIntern() {
        InternDTO selectedIntern = tblInterns.getSelectionModel().getSelectedItem();

        if (selectedIntern != null) {
            WindowManagerController.changeViewToUpdateIntern("UpdateInternDashboard.fxml", selectedIntern);
        } else {
            showNotification(Alert.AlertType.WARNING, "Selección requerida", 
                "Por favor, selecciona un estudiante de la tabla para poder modificarlo.");
        }
    }

    private void configureTable() {
        colEnrollment.setCellValueFactory(new PropertyValueFactory<>("enrollmentNumber"));
        colName.setCellValueFactory(new PropertyValueFactory<>("nameComplet"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("institucionalEmail"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("active"));
        colNRC.setCellValueFactory(new PropertyValueFactory<>("NRC"));
    }

    private void loadInternsData() {
        try {
            List<InternDTO> list = internDAO.findAll();
            masterData.setAll(list);
            tblInterns.setItems(masterData);
        } catch (BusinessException exception) {
            LOGGER.error("Error al cargar estudiantes", exception);
            showNotification(Alert.AlertType.ERROR, "Error de carga", "No se pudo conectar con la base de datos.");
        }
    }

    @FXML
    private void searchIntern() {
        try {
            String enrollmentNumber = txtSearchEnrollment.getText().trim();
            new InternValidator().validateEnrollmentNumber(enrollmentNumber);

            InternDTO intern = internDAO.findByMatricula(enrollmentNumber);

            if (intern != null) {
                ObservableList<InternDTO> filteredData = FXCollections.observableArrayList(intern);
                tblInterns.setItems(filteredData);
            } else {
                showNotification(Alert.AlertType.WARNING,
                    "Sin resultados",
                    "No se encontró ningún estudiante con matrícula " + enrollmentNumber);
            }
        } catch (BusinessException exception) {
            showNotification(Alert.AlertType.ERROR,
                "Error de validación",
                exception.getMessage());
        }
    }


    private void showNotification(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
     private void goHome(ActionEvent event) {
        WindowManagerController.goBack();
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
    private void goRegisterIntern(ActionEvent event) {
        WindowManagerController.changeView("RegisterInternDashboard.fxml");
    }
    
    @FXML
    private void showPendingFunctionalityMessage() {
        showNotification(Alert.AlertType.INFORMATION, "Próximamente", "Esta funcionalidad estará disponible en la siguiente versión.");
    }
}
