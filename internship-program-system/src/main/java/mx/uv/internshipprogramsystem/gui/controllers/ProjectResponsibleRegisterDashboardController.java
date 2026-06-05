package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.ProjectResponsibleManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;

public class ProjectResponsibleRegisterDashboardController
        implements Initializable {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ProjectResponsibleRegisterDashboardController.class
        );

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastNameFather;

    @FXML
    private TextField txtLastNameMother;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPosition;

    @FXML
    private ComboBox<LinkedOrganizationDTO> cmbLinkedOrganization;

    private ProjectResponsibleManager projectResponsibleManager;

    @Override
    public void initialize(
            URL url,
            ResourceBundle resourceBundle
    ) {
        try {
            validatePermission(
                Permission.REGISTER_PROJECT_RESPONSIBLE
            );

            projectResponsibleManager =
                new ProjectResponsibleManager();

            loadLinkedOrganizations();

            LOGGER.info(
                "Vista de registro de responsable cargada correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado al registro de responsable de proyecto.",
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

    @FXML
    private void handleRegisterProjectResponsible(
            ActionEvent event
    ) {
        try {
            validatePermission(
                Permission.REGISTER_PROJECT_RESPONSIBLE
            );

            ProjectResponsibleDTO responsible =
                buildProjectResponsible();

            boolean wasRegistered =
                projectResponsibleManager.registerProjectResponsible(
                    responsible
                );

            if (wasRegistered) {
                FormAlertSupport.showInformation(
                    "Registro exitoso",
                    "Responsable registrado correctamente."
                );

                clearForm();
            }
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "No se pudo registrar el responsable de proyecto.",
                businessException
            );

            FormAlertSupport.showError(
                "Error",
                businessException.getMessage()
            );
        }
    }

    private ProjectResponsibleDTO buildProjectResponsible()
            throws BusinessException {
        LinkedOrganizationDTO selectedOrganization =
            cmbLinkedOrganization
                .getSelectionModel()
                .getSelectedItem();

        validateSelectedOrganization(
            selectedOrganization
        );

        ProjectResponsibleDTO responsible =
            new ProjectResponsibleDTO(
                txtFirstName.getText().trim(),
                txtLastNameFather.getText().trim(),
                txtLastNameMother.getText().trim(),
                txtEmail.getText().trim(),
                txtPosition.getText().trim(),
                selectedOrganization.getId()
            );

        return responsible;
    }

    private void validateSelectedOrganization(
            LinkedOrganizationDTO selectedOrganization
    ) throws BusinessException {
        if (selectedOrganization == null) {
            throw new BusinessException(
                "Debe seleccionar una organización vinculada."
            );
        }
    }

    private void loadLinkedOrganizations()
            throws BusinessException {
        LinkedOrganizationDAO linkedOrganizationDAO =
            new LinkedOrganizationDAO();

        cmbLinkedOrganization.getItems().setAll(
            linkedOrganizationDAO.findAll()
        );
    }

    @FXML
    private void clearForm() {
        txtFirstName.clear();

        txtLastNameFather.clear();

        txtLastNameMother.clear();

        txtEmail.clear();

        txtPosition.clear();

        cmbLinkedOrganization
            .getSelectionModel()
            .clearSelection();
    }

    @FXML
    private void goHome(
            ActionEvent event
    ) {
        WindowManagerController.changeView(
            "CoordinatorProfessorHomeDashboard.fxml"
        );
    }

    @FXML
    private void goResponsibleModule(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.CONSULT_PROJECT_RESPONSIBLE,
            "ProjectResponsibleModuleDashboard.fxml",
            "Acceso denegado al módulo de responsables de proyecto."
        );
    }

    @FXML
    private void logOut(
            ActionEvent event
    ) {
        UserSessionManager.clearSession();

        LOGGER.info(
            "Cierre de sesión realizado correctamente."
        );

        WindowManagerController.changeView(
            "LoginDashboard.fxml"
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