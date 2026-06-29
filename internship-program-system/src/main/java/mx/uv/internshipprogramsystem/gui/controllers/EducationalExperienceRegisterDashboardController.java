package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.managers.EducationalExperienceRegistrationManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class EducationalExperienceRegisterDashboardController
        implements Initializable {
    
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            EducationalExperienceRegisterDashboardController.class
        );

    private static final String FEBRUARY_JULY_TERM =
        "Febrero - Julio";
    private static final String AUGUST_JANUARY_TERM =
        "Agosto - Enero";

    @FXML
    private TextField txtNrc;

    @FXML
    private ComboBox<String> cmbSchoolTerm;

    @FXML
    private TextField txtSchoolYear;

    @FXML
    private TextField txtSection;

    @FXML
    private DatePicker dpStartDate;

    @FXML
    private DatePicker dpEndDate;

    @FXML
    private ComboBox<ProfessorDTO> cmbProfessor;

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

        configureInputs();
        loadProfessors();
    }

    private void configureInputs() {
        cmbSchoolTerm.getItems().setAll(
            FEBRUARY_JULY_TERM,
            AUGUST_JANUARY_TERM
        );
        limitTextField(txtNrc, 5);
        limitTextField(txtSchoolYear, 4);
        limitTextField(txtSection, 2);
    }

    private void limitTextField(
            TextField textField,
            int maxLength
    ) {
        textField.setTextFormatter(
            new TextFormatter<String>(
                new mx.uv.internshipprogramsystem.gui.handlers.LengthFilterTextFormatter(maxLength)
            )
        );
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

            validateDateRange(
                educationalExperience
            );

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
        } catch (DataAccessException exception) {
            LOGGER.error(
                "Error de conexion al registrar experiencia educativa",
                exception
            );

            showErrorAlert(
                "No se pudo conectar con la base de datos para registrar la experiencia educativa. Por favor intente mas tarde."
            );
        }
    }

    private EducationalExperienceDTO buildEducationalExperience()
            throws BusinessException {
        ProfessorDTO selectedProfessor =
            cmbProfessor
                .getSelectionModel()
                .getSelectedItem();

        String selectedTerm =
            cmbSchoolTerm
                .getSelectionModel()
                .getSelectedItem();

        if (selectedProfessor == null) {
            throw new BusinessException(
                "Debe seleccionar un profesor."
            );
        }

        if (selectedTerm == null) {
            throw new BusinessException(
                "Debe seleccionar el ciclo escolar."
            );
        }

        int schoolYear =
            parseSchoolYear();

        String schoolPeriod =
            buildSchoolPeriod(
                selectedTerm,
                schoolYear
            );

        EducationalExperienceDTO educationalExperience =
            new EducationalExperienceDTO(
                txtNrc.getText().trim(),
                schoolPeriod,
                txtSection.getText().trim(),
                selectedProfessor.getId(),
                true
            );

        educationalExperience.setStartDate(
            dpStartDate.getValue()
        );
        educationalExperience.setEndDate(
            dpEndDate.getValue()
        );
        educationalExperience.setProfessorName(
            selectedProfessor.getFullName()
        );

        return educationalExperience;
    }

    private int parseSchoolYear() throws BusinessException {
        String yearText =
            txtSchoolYear.getText().trim();

        if (!yearText.matches("\\d{4}")) {
            throw new BusinessException(
                "El anio debe tener 4 digitos."
            );
        }

        return Integer.parseInt(
            yearText
        );
    }

    private String buildSchoolPeriod(
            String selectedTerm,
            int schoolYear
    ) {
        String periodCode;

        if (FEBRUARY_JULY_TERM.equals(selectedTerm)) {
            periodCode =
                schoolYear + "51";
        } else {
            periodCode =
                (schoolYear + 1) + "01";
        }

        return periodCode;
    }

    private void validateDateRange(
            EducationalExperienceDTO educationalExperience
    ) throws BusinessException {
        LocalDate startDate =
            educationalExperience.getStartDate();
        LocalDate endDate =
            educationalExperience.getEndDate();

        if (startDate == null || endDate == null) {
            throw new BusinessException(
                "Debe capturar fecha de inicio y fecha de fin."
            );
        }

        if (endDate.isBefore(startDate)) {
            throw new BusinessException(
                "La fecha de fin no puede ser anterior a la fecha de inicio."
            );
        }

        String period =
            educationalExperience.getSchoolPeriod();
        int periodYear =
            Integer.parseInt(
                period.substring(
                    0,
                    4
                )
            );

        if (period.endsWith("51")) {
            validateDateBetween(
                startDate,
                LocalDate.of(periodYear, 2, 1),
                LocalDate.of(periodYear, 3, 31),
                "La fecha de inicio debe estar entre febrero y marzo."
            );
            validateDateBetween(
                endDate,
                LocalDate.of(periodYear, 6, 1),
                LocalDate.of(periodYear, 7, 31),
                "La fecha de fin debe estar entre junio y julio."
            );
        } else {
            validateDateBetween(
                startDate,
                LocalDate.of(periodYear - 1, 8, 1),
                LocalDate.of(periodYear - 1, 8, 31),
                "La fecha de inicio debe estar en agosto del anio anterior."
            );
            validateDateBetween(
                endDate,
                LocalDate.of(periodYear, 1, 1),
                LocalDate.of(periodYear, 1, 31),
                "La fecha de fin debe estar en enero del anio del periodo."
            );
        }
    }

    private void validateDateBetween(
            LocalDate date,
            LocalDate minDate,
            LocalDate maxDate,
            String message
    ) throws BusinessException {
        if (date.isBefore(minDate) || date.isAfter(maxDate)) {
            throw new BusinessException(
                message
            );
        }
    }

    @FXML
    private void clearForm() {
        txtNrc.clear();
        cmbSchoolTerm.getSelectionModel().clearSelection();
        txtSchoolYear.clear();
        txtSection.clear();
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
        cmbProfessor.getSelectionModel().clearSelection();
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
        } catch (DataAccessException exception) {
            LOGGER.error(
                "Error de conexion al cargar profesores",
                exception
            );

            showErrorAlert(
                "No se pudo conectar con la base de datos para cargar los profesores. Por favor intente mas tarde."
            );
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView(
            "CoordinatorProfessorHomeDashboard.fxml"
        );
    }

    @FXML
    private void goEducationalExperienceModule(ActionEvent event) {
        WindowManagerController.changeView(
            "EducationalExperienceModuleDashboard.fxml"
        );
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        WindowManagerController.changeView(
            "InternModuleDashboard.fxml"
        );
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        WindowManagerController.changeView(
            "ProjectsModuleDashboard.fxml"
        );
    }

    @FXML
    private void logOut(ActionEvent event) {
        UserSessionManager.clearSession();
        WindowManagerController.changeView(
            "LoginDashboard.fxml"
        );
    }

    private void showInformationAlert(
            String message
    ) {
        Alert informationAlert =
            new Alert(Alert.AlertType.INFORMATION);

        accessControlManager.validatePermission(
            UserSessionManager.getCurrentUser(),
            permission
        );
        informationAlert.setHeaderText(null);
        informationAlert.setContentText(message);
        informationAlert.showAndWait();
    }

    private void showErrorAlert(
            String message
    ) {
        Alert errorAlert =
            new Alert(Alert.AlertType.ERROR);

        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }
}
