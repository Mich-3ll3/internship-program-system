package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.EducationalExperienceRegistrationManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;

public class EducationalExperienceRegisterDashboardController
        implements Initializable {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            EducationalExperienceRegisterDashboardController.class
        );

    @FXML
    private TextField txtNrc;

    @FXML
    private TextField txtSchoolPeriod;

    @FXML
    private TextField txtSection;

    @FXML
    private ComboBox<ProfessorDTO> cmbProfessor;

    @FXML
    private CheckBox chkActive;

    private EducationalExperienceRegistrationManager
            educationalExperienceRegistrationManager;

    @Override
    public void initialize(
            URL url,
            ResourceBundle resourceBundle
    ) {
        try {
            validatePermission(
                Permission.REGISTER_EDUCATIONAL_EXPERIENCE
            );

            educationalExperienceRegistrationManager =
                new EducationalExperienceRegistrationManager();

            loadProfessors();

            chkActive.setSelected(true);
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado al registro de experiencia educativa",
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                "No tienes permisos para acceder a esta vista."
            );

            WindowManagerController.changeView(
                "CoordinatorProfessorHomeDashboard.fxml"
            );
        }
    }

    @FXML
    private void validateRegisterEducationalExperienceForm(
            ActionEvent event
    ) {
        try {
            validatePermission(
                Permission.REGISTER_EDUCATIONAL_EXPERIENCE
            );

            EducationalExperienceDTO educationalExperience =
                buildEducationalExperience();

            boolean wasRegistered =
                educationalExperienceRegistrationManager
                    .registerEducationalExperience(
                        educationalExperience
                    );

            if (wasRegistered) {
                FormAlertSupport.showInformation(
                    "Registro exitoso",
                    "Experiencia educativa registrada correctamente."
                );

                clearForm();
            }
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al registrar experiencia educativa",
                businessException
            );

            FormAlertSupport.showError(
                "Error",
                businessException.getMessage()
            );
        }
    }

    private EducationalExperienceDTO buildEducationalExperience()
            throws BusinessException {
        ProfessorDTO selectedProfessor =
            cmbProfessor
                .getSelectionModel()
                .getSelectedItem();

        if (selectedProfessor == null) {
            throw new BusinessException(
                "Debe seleccionar un profesor."
            );
        }

        String nrc =
            txtNrc.getText().trim();

        String schoolPeriod =
            txtSchoolPeriod.getText().trim();

        String section =
            txtSection.getText().trim();

        int professorId =
            selectedProfessor.getId();

        boolean isActive =
            chkActive.isSelected();

        EducationalExperienceDTO educationalExperience =
            new EducationalExperienceDTO(
                nrc,
                schoolPeriod,
                section,
                professorId,
                isActive
            );

        return educationalExperience;
    }

    @FXML
    private void clearForm() {
        txtNrc.clear();

        txtSchoolPeriod.clear();

        txtSection.clear();

        cmbProfessor
            .getSelectionModel()
            .clearSelection();

        chkActive.setSelected(true);
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView(
            "CoordinatorProfessorHomeDashboard.fxml"
        );
    }

    @FXML
    private void goEducationalExperienceModule(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.REGISTER_EDUCATIONAL_EXPERIENCE,
            "EducationalExperienceRegisterDashboard.fxml",
            "Acceso denegado al módulo de experiencia educativa"
        );
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        openViewWithPermission(
            Permission.CONSULT_INTERN,
            "InternModuleDashboard.fxml",
            "Acceso denegado al módulo de alumnos"
        );
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        openViewWithPermission(
            Permission.CONSULT_PROJECT,
            "ProjectsModuleDashboard.fxml",
            "Acceso denegado al módulo de proyectos"
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

    private void loadProfessors()
            throws BusinessException {
        ProfessorDAO professorDAO =
            new ProfessorDAO();

        cmbProfessor.getItems().setAll(
            professorDAO.findAll()
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