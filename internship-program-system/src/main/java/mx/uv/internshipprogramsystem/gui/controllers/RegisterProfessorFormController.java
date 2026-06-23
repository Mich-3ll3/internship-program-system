package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import mx.uv.internshipprogramsystem.gui.handlers.NameTextFormatterFilter;
import mx.uv.internshipprogramsystem.gui.handlers.StaffNumberTextFormatterFilter;
import mx.uv.internshipprogramsystem.gui.handlers.ProfessorRegistrationSuccessHandler;
import mx.uv.internshipprogramsystem.gui.handlers.ProfessorRegistrationFailureHandler;
import mx.uv.internshipprogramsystem.gui.handlers.CoordinatorCheckSuccessHandler;
import mx.uv.internshipprogramsystem.gui.handlers.CoordinatorCheckFailureHandler;
import mx.uv.internshipprogramsystem.gui.util.CoordinatorCheckTask;
import mx.uv.internshipprogramsystem.gui.util.ProfessorRegistrationTask;
import mx.uv.internshipprogramsystem.gui.util.FormAlertSupport;
import mx.uv.internshipprogramsystem.gui.util.TextFormatterUtil;
import mx.uv.internshipprogramsystem.logic.security.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.validations.InputCleaner;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;
import mx.uv.internshipprogramsystem.logic.validations.ProfessorValidator;

public class RegisterProfessorFormController implements Initializable {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(RegisterProfessorFormController.class);

    @FXML
    private TextField txtInstitutionalEmail;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtFirstSurname;
    @FXML
    private TextField txtSecondSurname;
    @FXML
    private TextField txtStaffNumber;
    @FXML
    private CheckBox chkCoordinator;
    @FXML
    private Label lblCoordinatorWarning;
    @FXML
    private Button btnValidate;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnProfessor;
    @FXML
    private Button btnIntern;
    @FXML
    private Button btnExit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnHome.setOnAction(new mx.uv.internshipprogramsystem.gui.handlers.NavigationActionHandler("AdminHomeDashboard.fxml"));
        btnProfessor.setOnAction(new mx.uv.internshipprogramsystem.gui.handlers.NavigationActionHandler("ProfessorModuleDashboard.fxml"));
        btnIntern.setOnAction(new mx.uv.internshipprogramsystem.gui.handlers.NavigationActionHandler("InternModuleDashboard.fxml", Permission.CONSULT_INTERN));
        btnExit.setOnAction(new mx.uv.internshipprogramsystem.gui.handlers.LogoutActionHandler());

        setupFormatFilters();
        checkCoordinatorStatus();
    }



    @FXML
    private void validateRegisterProfessorForm() {
        btnValidate.setDisable(true);
        btnValidate.setText("Procesando");

        try {
            FormAlertSupport.clearFieldErrorsFromActiveWindow();

            String rawEmail = InputCleaner.sanitizeText(txtInstitutionalEmail.getText());
            String rawName = InputCleaner.sanitizeText(txtName.getText());
            String rawFirstSurname = InputCleaner.sanitizeText(txtFirstSurname.getText());
            String rawSecondSurname = InputCleaner.sanitizeText(txtSecondSurname.getText());
            String rawStaffNumber = InputCleaner.sanitizeText(txtStaffNumber.getText());

            UserValidator userValidator = new UserValidator();
            ProfessorValidator professorValidator = new ProfessorValidator();

            java.util.List<String> errors = new java.util.ArrayList<>();

            try {
                userValidator.validateEmailFormat(rawEmail);
            } catch (ValidationException e) {
                errors.addAll(e.getErrors());
            } catch (BusinessException e) {
                errors.add(e.getMessage());
            }

            try {
                professorValidator.validateStaffNumber(rawStaffNumber);
            } catch (ValidationException e) {
                errors.addAll(e.getErrors());
            }

            ProfessorDTO tempProfessor = new ProfessorDTO();
            tempProfessor.setName(rawName);
            tempProfessor.setFirstSurname(rawFirstSurname);
            tempProfessor.setSecondSurname(rawSecondSurname);
            tempProfessor.setStaffNumber(rawStaffNumber);
            tempProfessor.setInstitutionalEmail(rawEmail);

            try {
                professorValidator.validateProfessorForUpdate(tempProfessor);
            } catch (ValidationException e) {
                errors.addAll(e.getErrors());
            }

            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }

            UserDTO user = buildUser();
            ProfessorDTO professor = buildProfessor(0);

            txtName.setText(user.getName());
            txtFirstSurname.setText(user.getFirstSurname());
            txtSecondSurname.setText(user.getSecondSurname());
            txtStaffNumber.setText(professor.getStaffNumber());
            txtInstitutionalEmail.setText(user.getInstitutionalEmail());

            executeProfessorRegistration(user, professor);
        } catch (Exception exception) {
            btnValidate.setDisable(false);
            btnValidate.setText("Validar registro");
            String message = exception.getMessage();
            if (message == null || message.trim().isEmpty()) {
                message = "Datos de formulario invalidos o incompletos.";
            }
            FormAlertSupport.showWarning(
                "Validacion fallida",
                message
            );
        }
    }

    private void setupFormatFilters() {
        txtInstitutionalEmail.setTextFormatter(
            new TextFormatter<>(new mx.uv.internshipprogramsystem.gui.handlers.LengthFilterTextFormatter(255))
        );
        txtName.setTextFormatter(
            new TextFormatter<>(new NameTextFormatterFilter())
        );
        txtFirstSurname.setTextFormatter(
            new TextFormatter<>(new NameTextFormatterFilter())
        );
        txtSecondSurname.setTextFormatter(
            new TextFormatter<>(new NameTextFormatterFilter())
        );
        txtStaffNumber.setTextFormatter(
            new TextFormatter<>(new StaffNumberTextFormatterFilter())
        );
    }

    private void checkCoordinatorStatus() {
        CoordinatorCheckTask task = new CoordinatorCheckTask();
        task.setOnSucceeded(
            new CoordinatorCheckSuccessHandler(this)
        );
        task.setOnFailed(
            new CoordinatorCheckFailureHandler(this)
        );
        new Thread(task).start();
    }

    public void handleCoordinatorCheckSuccess(boolean exists) {
        if (exists) {
            chkCoordinator.setSelected(false);
            chkCoordinator.setDisable(true);
            lblCoordinatorWarning.setVisible(true);
            lblCoordinatorWarning.setManaged(true);
        } else {
            chkCoordinator.setDisable(false);
            lblCoordinatorWarning.setVisible(false);
            lblCoordinatorWarning.setManaged(false);
        }
    }

    public void handleCoordinatorCheckFailure(Throwable exception) {
        LOGGER.error(
            "Error técnico al verificar coordinador: {}",
            exception.getMessage(),
            exception
        );
        chkCoordinator.setSelected(false);
        chkCoordinator.setDisable(true);
        lblCoordinatorWarning.setText(
            "No disponible (Error de conexión)"
        );
        lblCoordinatorWarning.setVisible(true);
        lblCoordinatorWarning.setManaged(true);
    }

    private void executeProfessorRegistration(
        UserDTO user,
        ProfessorDTO professor
    ) {
        ProfessorRegistrationTask task =
            new ProfessorRegistrationTask(user, professor);

        task.setOnSucceeded(
            new ProfessorRegistrationSuccessHandler(this)
        );
        task.setOnFailed(
            new ProfessorRegistrationFailureHandler(this)
        );

        new Thread(task).start();
    }

    public void handleRegistrationSuccess() {
        btnValidate.setDisable(false);
        btnValidate.setText("Validar registro");
        FormAlertSupport.showInformation(
            "Registro exitoso",
            "El profesor ha sido registrado. Se envio un correo de activacion."
        );
        clearForm();
    }

    public void handleRegistrationFailure(Throwable exception) {
        btnValidate.setDisable(false);
        btnValidate.setText("Validar registro");
        LOGGER.error(
            "Error técnico al registrar profesor con número "
            + "de personal {}: {}",
            txtStaffNumber.getText(),
            exception.getMessage(),
            exception
        );
        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = "Ocurrio un error inesperado al registrar el profesor.";
        }
        FormAlertSupport.showError(
            "Error de registro",
            message
        );
    }

    private UserDTO buildUser() {
        String cleanEmail = InputCleaner.sanitizeText(
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
            UserRole.PROFESSOR
        );
    }

    private ProfessorDTO buildProfessor(int userId) {
        String cleanStaffNumber = InputCleaner.sanitizeText(
            txtStaffNumber.getText()
        );
        return new ProfessorDTO(
            cleanStaffNumber,
            chkCoordinator.isSelected(),
            userId
        );
    }

    @FXML
    private void clearForm() {
        txtInstitutionalEmail.clear();
        txtName.clear();
        txtFirstSurname.clear();
        txtSecondSurname.clear();
        txtStaffNumber.clear();
        chkCoordinator.setSelected(false);
        checkCoordinatorStatus();
    }


}