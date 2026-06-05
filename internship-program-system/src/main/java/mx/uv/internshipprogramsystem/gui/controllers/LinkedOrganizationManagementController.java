package mx.uv.internshipprogramsystem.gui.controllers;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;
import mx.uv.internshipprogramsystem.logic.validations.LinkedOrganizationValidator;

public class LinkedOrganizationManagementController {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            LinkedOrganizationManagementController.class
        );

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtCity;

    @FXML
    private TextField txtState;

    @FXML
    private TextField txtSector;

    @FXML
    private TextField txtDirectUserCount;

    @FXML
    private TextField txtIndirectUserCount;

    @FXML
    private TextField txtSearchName;

    @FXML
    private TableView<LinkedOrganizationDTO> tblOrganizations;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> colName;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> colEmail;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> colPhoneNumber;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> colCity;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> colSector;

    private final LinkedOrganizationDAO organizationDAO =
        new LinkedOrganizationDAO();

    private ObservableList<LinkedOrganizationDTO> masterData =
        FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            validatePermission(
                Permission.CONSULT_ORGANIZATION
            );

            configureTable();

            loadOrganizations();

            LOGGER.info(
                "Módulo de organizaciones vinculadas cargado correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado al módulo de organizaciones vinculadas.",
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                businessException.getMessage()
            );

            WindowManagerController.changeView(
                "CoordinatorProfessorHomeDashboard.fxml"
            );
        }
    }

    private void configureTable() {
        colName.setCellValueFactory(
            new PropertyValueFactory<LinkedOrganizationDTO, String>(
                "name"
            )
        );

        colEmail.setCellValueFactory(
            new PropertyValueFactory<LinkedOrganizationDTO, String>(
                "email"
            )
        );

        colPhoneNumber.setCellValueFactory(
            new PropertyValueFactory<LinkedOrganizationDTO, String>(
                "phoneNumber"
            )
        );

        colCity.setCellValueFactory(
            new PropertyValueFactory<LinkedOrganizationDTO, String>(
                "city"
            )
        );

        colSector.setCellValueFactory(
            new PropertyValueFactory<LinkedOrganizationDTO, String>(
                "sector"
            )
        );
    }

    private void loadOrganizations() {
        try {
            validatePermission(
                Permission.CONSULT_ORGANIZATION
            );

            List<LinkedOrganizationDTO> organizations =
                organizationDAO.findAll();

            masterData =
                FXCollections.observableArrayList(
                    organizations
                );

            tblOrganizations.setItems(
                masterData
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al cargar organizaciones.",
                businessException
            );

            FormAlertSupport.showError(
                "Error de carga",
                "No se pudo cargar la lista de organizaciones."
            );
        }
    }

    @FXML
    private void validateOrganizationForm() {
        try {
            validatePermission(
                Permission.REGISTER_ORGANIZATION
            );

            LinkedOrganizationDTO organization =
                buildLinkedOrganization();

            LinkedOrganizationValidator validator =
                new LinkedOrganizationValidator();

            validator.validateFullOrganization(
                organization
            );

            boolean wasCreated =
                organizationDAO.createLinkedOrganization(
                    organization
                );

            if (wasCreated) {
                FormAlertSupport.showInformation(
                    "Registro exitoso",
                    "Organización registrada correctamente."
                );

                clearForm();

                loadOrganizations();
            }
        } catch (NumberFormatException numberFormatException) {
            LOGGER.warn(
                "Formato inválido en campos numéricos.",
                numberFormatException
            );

            FormAlertSupport.showError(
                "Error de formato",
                "Los campos de usuarios deben ser números."
            );
        } catch (ValidationException validationException) {
            LOGGER.warn(
                "Error de validación al registrar organización.",
                validationException
            );

            FormAlertSupport.showError(
                "Error de validación",
                validationException.getMessage()
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al registrar organización.",
                businessException
            );

            FormAlertSupport.showError(
                "Error",
                businessException.getMessage()
            );
        }
    }

    private LinkedOrganizationDTO buildLinkedOrganization() {
        LinkedOrganizationDTO organization =
            new LinkedOrganizationDTO(
                txtName.getText().trim(),
                txtAddress.getText().trim(),
                txtCity.getText().trim(),
                txtState.getText().trim(),
                txtEmail.getText().trim(),
                txtPhoneNumber.getText().trim(),
                txtSector.getText().trim(),
                Integer.parseInt(
                    txtIndirectUserCount.getText().trim()
                ),
                Integer.parseInt(
                    txtDirectUserCount.getText().trim()
                )
            );

        return organization;
    }

    @FXML
    private void validateSearchOrganization() {
        try {
            validatePermission(
                Permission.CONSULT_ORGANIZATION
            );

            String query =
                txtSearchName.getText().toLowerCase().trim();

            if (query.isEmpty()) {
                tblOrganizations.setItems(
                    masterData
                );
            } else {
                filterOrganizationsByName(
                    query
                );
            }
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado a la búsqueda de organizaciones.",
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                businessException.getMessage()
            );
        }
    }

    private void filterOrganizationsByName(
            String query
    ) {
        ObservableList<LinkedOrganizationDTO> filteredData =
            FXCollections.observableArrayList();

        for (LinkedOrganizationDTO organization : masterData) {
            if (organization.getName().toLowerCase().contains(query)) {
                filteredData.add(
                    organization
                );
            }
        }

        tblOrganizations.setItems(
            filteredData
        );
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
        WindowManagerController.changeView(
            "CoordinatorProfessorHomeDashboard.fxml"
        );
    }

    @FXML
    private void logOut(ActionEvent event) {
        UserSessionManager.clearSession();

        LOGGER.info(
            "Cierre de sesión realizado correctamente."
        );

        WindowManagerController.changeView(
            "LoginDashboard.fxml"
        );
    }

    @FXML
    private void goLinkedOrganizationModule(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.CONSULT_ORGANIZATION,
            "LinkedOrganizationManagementGUI.fxml",
            "Acceso denegado al módulo de organizaciones vinculadas."
        );
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        openViewWithPermission(
            Permission.CONSULT_INTERN,
            "InternModuleDashboard.fxml",
            "Acceso denegado al módulo de estudiantes."
        );
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        openViewWithPermission(
            Permission.CONSULT_REPORT,
            "ReportHomeDashboard.fxml",
            "Acceso denegado al módulo de reportes."
        );
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        openViewWithPermission(
            Permission.CONSULT_PROJECT,
            "ProjectsModuleDashboard.fxml",
            "Acceso denegado al módulo de proyectos."
        );
    }

    private void openViewWithPermission(
            Permission permission,
            String fxmlName,
            String logMessage
    ) {
        try {
            validatePermission(
                permission
            );

            WindowManagerController.changeView(
                fxmlName
            );

            LOGGER.info(
                "Acceso permitido a la vista {}.",
                fxmlName
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                logMessage,
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                businessException.getMessage()
            );
        }
    }

    private void validatePermission(
            Permission permission
    ) throws BusinessException {
        AccessControlManager accessControlManager =
            new AccessControlManager();

        accessControlManager.validatePermission(
            UserSessionManager.getCurrentUser(),
            permission
        );
    }
}