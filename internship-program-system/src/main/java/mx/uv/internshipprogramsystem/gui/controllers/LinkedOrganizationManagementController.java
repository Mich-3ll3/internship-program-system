package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import mx.uv.internshipprogramsystem.logic.validations.LinkedOrganizationValidator;

public class LinkedOrganizationManagementController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkedOrganizationManagementController.class);

    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhoneNumber;
    @FXML private TextField txtAddress;
    @FXML private TextField txtCity;
    @FXML private TextField txtState;
    @FXML private TextField txtSector;
    @FXML private TextField txtDirectUserCount;
    @FXML private TextField txtIndirectUserCount;
    @FXML private TextField txtSearchName;
    @FXML private TableView<LinkedOrganizationDTO> tblOrganizations;
    @FXML private TableColumn<LinkedOrganizationDTO, String> colName;
    @FXML private TableColumn<LinkedOrganizationDTO, String> colEmail;
    @FXML private TableColumn<LinkedOrganizationDTO, String> colPhoneNumber;
    @FXML private TableColumn<LinkedOrganizationDTO, String> colCity;
    @FXML private TableColumn<LinkedOrganizationDTO, String> colSector;

    private final LinkedOrganizationDAO organizationDAO = new LinkedOrganizationDAO();
    private ObservableList<LinkedOrganizationDTO> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTable();
        loadOrganizations();
    }

    private void configureTable() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colSector.setCellValueFactory(new PropertyValueFactory<>("sector"));
    }

    private void loadOrganizations() {
        try {
            List<LinkedOrganizationDTO> list = organizationDAO.findAll();
            masterData = FXCollections.observableArrayList(list);
            tblOrganizations.setItems(masterData);
        } catch (BusinessException e) {
            LOGGER.error("Error al cargar organizaciones", e);
        }
    }

    @FXML
    private void validateOrganizationForm() {
        try {
            LinkedOrganizationDTO organization = new LinkedOrganizationDTO(
                txtName.getText().trim(),
                txtAddress.getText().trim(),
                txtCity.getText().trim(),
                txtState.getText().trim(),
                txtEmail.getText().trim(),
                txtPhoneNumber.getText().trim(),
                txtSector.getText().trim(),
                Integer.parseInt(txtIndirectUserCount.getText().trim()),
                Integer.parseInt(txtDirectUserCount.getText().trim())
            );

            LinkedOrganizationValidator validator = new LinkedOrganizationValidator();
            validator.validateFullOrganization(organization);

            if (organizationDAO.createLinkedOrganization(organization)) {
                showNotification(Alert.AlertType.INFORMATION, "Registro exitoso", "Organización registrada correctamente.");
                clearForm();
                loadOrganizations();
            }
        } catch (NumberFormatException e) {
            showNotification(Alert.AlertType.ERROR, "Error de formato", "Los campos de usuarios deben ser números.");
        } catch (ValidationException e) {
            showNotification(Alert.AlertType.ERROR, "Error de validación", e.getMessage());
        } catch (BusinessException e) {
            showNotification(Alert.AlertType.ERROR, "Error de base de datos", e.getMessage());
        }
    }

    @FXML
    private void validateSearchOrganization() {
        String query = txtSearchName.getText().toLowerCase().trim();
        if (query.isEmpty()) {
            tblOrganizations.setItems(masterData);
        } else {
            ObservableList<LinkedOrganizationDTO> filteredData = masterData.stream()
                .filter(org -> org.getName().toLowerCase().contains(query))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
            tblOrganizations.setItems(filteredData);
        }
    }

    @FXML
    private void clearForm() {
        txtName.clear();
        txtEmail.clear();
        txtPhoneNumber.clear();
        txtAddress.clear();
        txtCity.clear();
        txtState.clear();
        txtSector.clear();
        txtDirectUserCount.clear();
        txtIndirectUserCount.clear();
    }
    
    @FXML
     private void goHome(ActionEvent event) {
        WindowManagerController.changeView("CoordinatorProfessorHomeDashboard.fxml");
    }
     
    @FXML
    private void logOut(ActionEvent event) {
        WindowManagerController.changeView("LoginDashboard.fxml");
    }
    
    @FXML
    private void goLinkedOrganizationModule(ActionEvent event) {
        WindowManagerController.changeView("LinkedOrganizationManagementGUI.fxml");
    }
    
    @FXML
    private void goInternModule(ActionEvent event) {
        WindowManagerController.changeView("InternModuleDashboard.fxml");
    }
    
    @FXML
    private void goReportsModule(ActionEvent event) {
        // TODO: lógica para abrir el módulo de reportes
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        // TODO: lógica para abrir el módulo de proyectos
    }

    private void showNotification(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
