package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers
        .ProjectResponsibleManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

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
        projectResponsibleManager = new ProjectResponsibleManager();

        loadLinkedOrganizations();

        LOGGER.info(
            "Vista de registro de responsable cargada correctamente."
        );
    }

    @FXML
    private void handleRegisterProjectResponsible(
            ActionEvent event
    ) {
        try {
            ProjectResponsibleDTO responsible =
                buildProjectResponsible();

            boolean wasRegistered =
                projectResponsibleManager
                    .registerProjectResponsible(
                        responsible
                    );

            if (wasRegistered) {
                showInformationAlert(
                    "Responsable registrado correctamente."
                );

                clearForm();
            }
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "No se pudo registrar el responsable de proyecto",
                businessException
            );

            showErrorAlert(
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

    private void loadLinkedOrganizations() {
        try {
            LinkedOrganizationDAO linkedOrganizationDAO =
                new LinkedOrganizationDAO();

            cmbLinkedOrganization.getItems().setAll(
                linkedOrganizationDAO.findAll()
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "No se pudieron cargar las organizaciones vinculadas",
                businessException
            );

            showErrorAlert(
                "No se pudieron cargar las organizaciones vinculadas."
            );
        }
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
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView(
            "CoordinatorProfessorHomeDashboard.fxml"
        );
    }

    @FXML
    private void goResponsibleModule(ActionEvent event) {
        WindowManagerController.changeView(
            "ProjectResponsibleRegisterDashboard.fxml"
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

    private void showInformationAlert(
            String message
    ) {
        Alert informationAlert =
            new Alert(Alert.AlertType.INFORMATION);

        informationAlert.setTitle(
            "Registro exitoso"
        );

        informationAlert.setHeaderText(
            null
        );

        informationAlert.setContentText(
            message
        );

        informationAlert.showAndWait();
    }

    private void showErrorAlert(
            String message
    ) {
        Alert errorAlert =
            new Alert(Alert.AlertType.ERROR);

        errorAlert.setTitle(
            "Error"
        );

        errorAlert.setHeaderText(
            null
        );

        errorAlert.setContentText(
            message
        );

        errorAlert.showAndWait();
    }
}