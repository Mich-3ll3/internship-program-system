package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.gui.handlers.GlobalNavigationHandler;
import mx.uv.internshipprogramsystem.gui.util.FormAlertSupport;
import mx.uv.internshipprogramsystem.gui.util.TextFormatterUtil;
import mx.uv.internshipprogramsystem.gui.util.InternRegistrationTask;
import mx.uv.internshipprogramsystem.gui.handlers.InternRegistrationSuccessHandler;
import mx.uv.internshipprogramsystem.gui.handlers.InternRegistrationFailureHandler;
import mx.uv.internshipprogramsystem.logic.validations.InputCleaner;

public class RegisterInternFormController {
    
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            RegisterInternFormController.class
        );

    private static final int ENROLLMENT_MAX_LENGTH = 9;
    private static final int NAME_MAX_LENGTH = 60;
    private static final String STUDENT_EMAIL_DOMAIN =
        "@estudiantes.uv.mx";

    @FXML
    private TextField txtInstitutionalEmail;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtFirstSurname;

    @FXML
    private TextField txtSecondSurname;

    @FXML
    private TextField txtEnrollment;

    @FXML
    public void initialize() {
        txtInstitutionalEmail.setEditable(false);
        txtInstitutionalEmail.setFocusTraversable(false);
        configureTextLimits();
        configureEnrollmentListener();
    }

    private void configureTextLimits() {
        limitTextField(txtName, NAME_MAX_LENGTH);
        limitTextField(txtFirstSurname, NAME_MAX_LENGTH);
        limitTextField(txtSecondSurname, NAME_MAX_LENGTH);
        limitTextField(txtEnrollment, ENROLLMENT_MAX_LENGTH);
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

    private void configureEnrollmentListener() {
        txtEnrollment.textProperty().addListener(
            new mx.uv.internshipprogramsystem.gui.handlers.EnrollmentTextListener(this)
        );
    }

    public void handleEnrollmentChanged(String currentValue) {
        String normalizedEnrollment =
            normalizeEnrollment(
                currentValue
            );

        if (!currentValue.equals(normalizedEnrollment)) {
            txtEnrollment.setText(
                normalizedEnrollment
            );
        } else {
            updateInstitutionalEmail();
        }
    }

    private String normalizeEnrollment(
            String enrollment
        ) {
        String digits =
            enrollment.replaceAll("\\D", "");
        String normalizedEnrollment =
            "S" + digits;

        if (normalizedEnrollment.length() > ENROLLMENT_MAX_LENGTH) {
            normalizedEnrollment =
                normalizedEnrollment.substring(
                    0,
                    ENROLLMENT_MAX_LENGTH
                );
        }

        return normalizedEnrollment;
    }

    private void updateInstitutionalEmail() {
        String enrollment =
            txtEnrollment.getText().trim();

        if (enrollment.matches("^S\\d{8}$")) {
            txtInstitutionalEmail.setText(
                "z" + enrollment + STUDENT_EMAIL_DOMAIN
            );
        } else {
            txtInstitutionalEmail.clear();
        }
    }

    @FXML
    private void validateRegisterInternForm() {
        try {
            if (isFormValid()) {
                registerIntern();
            }
        } catch (Exception exception) {
            LOGGER.error("Error inesperado en la validacion del formulario de estudiante", exception);
            String message = exception.getMessage();
            if (message == null || message.trim().isEmpty()) {
                message = "Datos de formulario invalidos o incompletos.";
            }
            FormAlertSupport.showError(
                "Error inesperado",
                message
            );
        }
    }

    private boolean isFormValid() {
        String email =
            txtInstitutionalEmail.getText().trim();
        String name =
            txtName.getText().trim();
        String firstSurname =
            txtFirstSurname.getText().trim();
        String enrollment =
            txtEnrollment.getText().trim();

        if (email.isEmpty() || name.isEmpty()
                || firstSurname.isEmpty() || enrollment.isEmpty()) {
            FormAlertSupport.showWarning(
                "Campos incompletos",
                "Por favor, llene todos los campos obligatorios."
            );
            return false;
        }

        if (!enrollment.matches("^S\\d{8}$")) {
            FormAlertSupport.showError(
                "Matricula invalida",
                "El formato debe ser S seguido de 8 numeros."
            );
            return false;
        }

        if (!email.equals("z" + enrollment + STUDENT_EMAIL_DOMAIN)) {
            FormAlertSupport.showError(
                "Correo invalido",
                "El correo debe generarse automaticamente con la matricula."
            );
            return false;
        }

        return true;
    }

    private void registerIntern() {
        UserDTO user = buildUser();
        InternDTO intern = buildIntern(0);

        InternRegistrationTask task = new InternRegistrationTask(user, intern);
        task.setOnSucceeded(new InternRegistrationSuccessHandler(this));
        task.setOnFailed(new InternRegistrationFailureHandler(this));

        new Thread(task).start();
    }

    public void handleRegistrationSuccess() {
        LOGGER.info("Estudiante registrado correctamente.");
        FormAlertSupport.showInformation(
            "Registro exitoso",
            "El estudiante ha sido registrado. Se envio un correo de activacion."
        );
        clearForm();
    }

    public void handleRegistrationFailure(Throwable exception) {
        LOGGER.error("Error al registrar estudiante", exception);
        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = "Ocurrio un error inesperado al registrar el estudiante.";
        }
        FormAlertSupport.showError(
            "Error de registro",
            message
        );
    }

    private UserDTO buildUser() {
        String cleanEmail =
            InputCleaner.sanitizeText(
                txtInstitutionalEmail.getText()
            );
        String cleanName = TextFormatterUtil.formatToTitleCase(
            InputCleaner.sanitizeText(txtName.getText())
        );
        String cleanFirstSurname = TextFormatterUtil.formatToTitleCase(
            InputCleaner.sanitizeText(txtFirstSurname.getText())
        );
        String cleanSecondSurname = TextFormatterUtil.formatToTitleCase(
            InputCleaner.sanitizeText(txtSecondSurname.getText())
        );

        return new UserDTO(
            cleanEmail,
            null,
            cleanName,
            cleanFirstSurname,
            cleanSecondSurname,
            false,
            UserRole.STUDENT
        );
    }

    private InternDTO buildIntern(int userId) {
        String cleanEnrollment =
            InputCleaner.sanitizeText(
                txtEnrollment.getText()
            );

        return new InternDTO(
            cleanEnrollment,
            userId
        );
    }

    @FXML
    private void clearForm() {
        txtInstitutionalEmail.clear();

        txtName.clear();

        txtFirstSurname.clear();

        txtSecondSurname.clear();

        txtEnrollment.clear();
    }

    @FXML
    private void goHome(ActionEvent event) {
        GlobalNavigationHandler.goBack();
    }

    @FXML
    private void goProfessorModule(ActionEvent event) {
        GlobalNavigationHandler.changeView("ProfessorModuleDashboard.fxml");
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        GlobalNavigationHandler.changeView("InternModuleDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        GlobalNavigationHandler.logOut();
    }
}
