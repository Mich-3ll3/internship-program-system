package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers
        .EducationalExperienceRegistrationManager;

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

    private EducationalExperienceRegistrationManager educationalExperienceRegistrationManager;

    @Override
    public void initialize(
            URL url,
            ResourceBundle resourceBundle
    ) {
        educationalExperienceRegistrationManager =
            new EducationalExperienceRegistrationManager();

        loadProfessors();

        chkActive.setSelected(true);
    }

    @FXML
    private void validateRegisterEducationalExperienceForm(
            ActionEvent event
    ) {
        try {
            EducationalExperienceDTO educationalExperience =
                buildEducationalExperience();

            boolean wasRegistered =
                educationalExperienceRegistrationManager
                    .registerEducationalExperience(
                        educationalExperience
                    );

            if (wasRegistered) {
                showInformationAlert(
                    "Experiencia educativa registrada correctamente."
                );

                clearForm();
            }
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al registrar experiencia educativa",
                businessException
            );

            showErrorAlert(
                businessException.getMessage()
            );
        }
    }

    private EducationalExperienceDTO
            buildEducationalExperience()
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
        WindowManagerController.changeView(
            "EducationalExperienceRegisterDashboard.fxml"
        );
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        LOGGER.info(
            "Acceso al módulo de alumnos."
        );
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        LOGGER.info(
            "Acceso al módulo de proyectos."
        );
    }

    @FXML
    private void logOut(ActionEvent event) {
        LOGGER.info(
            "Cierre de sesión realizado correctamente."
        );

        WindowManagerController.changeView(
            "LoginDashboard.fxml"
        );
    }

    private void loadProfessors() {
        try {
            ProfessorDAO professorDAO =
                new ProfessorDAO();

            cmbProfessor.getItems().setAll(
                professorDAO.findAll()
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "No se pudieron cargar los profesores",
                businessException
            );

            showErrorAlert(
                "No se pudieron cargar los profesores."
            );
        }
    }

    private void showInformationAlert(
            String message
    ) {
        Alert informationAlert =
            new Alert(Alert.AlertType.INFORMATION);

        informationAlert.setTitle(
            "Registro exitoso"
        );

        informationAlert.setHeaderText(null);

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

        errorAlert.setHeaderText(null);

        errorAlert.setContentText(
            message
        );

        errorAlert.showAndWait();
    }
}