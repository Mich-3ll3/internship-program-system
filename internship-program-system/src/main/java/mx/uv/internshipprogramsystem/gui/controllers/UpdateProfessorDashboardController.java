package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import mx.uv.internshipprogramsystem.gui.handlers.NameTextFormatterFilter;
import mx.uv.internshipprogramsystem.gui.handlers.StaffNumberTextFormatterFilter;
import mx.uv.internshipprogramsystem.gui.util.FormAlertSupport;
import mx.uv.internshipprogramsystem.gui.util.TextFormatterUtil;
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.validations.InputCleaner;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;
import mx.uv.internshipprogramsystem.logic.validations.ProfessorValidator;

public class UpdateProfessorDashboardController {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            UpdateProfessorDashboardController.class
        );

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

    @FXML private TextField txtInstitutionalEmail;
    @FXML private TextField txtName;
    @FXML private TextField txtFirstSurname;
    @FXML private TextField txtSecondSurname;
    @FXML private TextField txtStaffNumber;
    @FXML private CheckBox chkCoordinator;
    @FXML private javafx.scene.control.Label lblCoordinatorWarning;
    
    private ProfessorDTO currentProfessor;

    private final ProfessorDAO professorDAO =
        new ProfessorDAO();

    @FXML
    public void initialize() {
        setupFormatFilters();
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

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView("AdminHomeDashboard.fxml");
    }

    @FXML
    private void goProfessorModule(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.CONSULT_PROFESSOR,
            "ProfessorModuleDashboard.fxml",
            "Acceso denegado al módulo de profesores."
        );
    }

    @FXML
    private void goInternModule(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.CONSULT_INTERN,
            "InternModuleDashboard.fxml",
            "Acceso denegado al módulo de estudiantes."
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

        txtInstitutionalEmail.setEditable(false);
        txtStaffNumber.setEditable(false);

        lblCoordinatorWarning.setVisible(false);
        lblCoordinatorWarning.setManaged(false);
        chkCoordinator.setDisable(false);

        if (professor.getIsCoordinator() != null && professor.getIsCoordinator()) {
            chkCoordinator.setDisable(false);
        } else {
            checkCoordinatorStatus();
        }
    }

    private void checkCoordinatorStatus() {
        mx.uv.internshipprogramsystem.gui.util.CoordinatorCheckTask task =
            new mx.uv.internshipprogramsystem.gui.util.CoordinatorCheckTask();
        task.setOnSucceeded(
            new mx.uv.internshipprogramsystem.gui.handlers.UpdateProfessorCoordinatorCheckSuccessHandler(this)
        );
        task.setOnFailed(
            new mx.uv.internshipprogramsystem.gui.handlers.UpdateProfessorCoordinatorCheckFailureHandler(this)
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
        LOGGER.log(Level.SEVERE, "Error técnico al verificar coordinador en edición: " + exception.getMessage(), exception);
        chkCoordinator.setSelected(false);
        chkCoordinator.setDisable(true);
        lblCoordinatorWarning.setText("No disponible (Error de conexión)");
        lblCoordinatorWarning.setVisible(true);
        lblCoordinatorWarning.setManaged(true);
    }

    @FXML
    private void handleUpdateAction() {
        updateProfessor();
    }

    private void updateProfessor() {
        try {
            FormAlertSupport.clearFieldErrorsFromActiveWindow();

            String rawName = InputCleaner.sanitizeText(txtName.getText());
            String rawFirstSurname = InputCleaner.sanitizeText(txtFirstSurname.getText());
            String rawSecondSurname = InputCleaner.sanitizeText(txtSecondSurname.getText());

            ProfessorDTO tempProfessor = new ProfessorDTO();
            tempProfessor.setName(rawName);
            tempProfessor.setFirstSurname(rawFirstSurname);
            tempProfessor.setSecondSurname(rawSecondSurname);
            tempProfessor.setStaffNumber(currentProfessor.getStaffNumber());
            tempProfessor.setInstitutionalEmail(currentProfessor.getInstitutionalEmail());

            java.util.List<String> errors = new java.util.ArrayList<>();

            try {
                new ProfessorValidator().validateProfessorForUpdate(tempProfessor);
            } catch (ValidationException e) {
                errors.addAll(e.getErrors());
            }

            try {
                new UserValidator().validateUserForUpdate(tempProfessor);
            } catch (ValidationException e) {
                errors.addAll(e.getErrors());
            } catch (BusinessException e) {
                errors.add(e.getMessage());
            }

            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }

            String cleanName = TextFormatterUtil.formatToTitleCase(rawName);
            String cleanFirstSurname = TextFormatterUtil.formatToTitleCase(rawFirstSurname);
            String cleanSecondSurname = TextFormatterUtil.formatToTitleCase(rawSecondSurname);

            txtName.setText(cleanName);
            txtFirstSurname.setText(cleanFirstSurname);
            txtSecondSurname.setText(cleanSecondSurname);

            currentProfessor.setName(cleanName);
            currentProfessor.setFirstSurname(cleanFirstSurname);
            currentProfessor.setSecondSurname(cleanSecondSurname);
            currentProfessor.setIsCoordinator(chkCoordinator.isSelected());
            currentProfessor.setRole(mx.uv.internshipprogramsystem.logic.dto.UserRole.PROFESSOR);

            UserDAO userDAO = new UserDAO(); 
            boolean userUpdated = userDAO.update(currentProfessor);
            boolean professorUpdated = professorDAO.update(currentProfessor);

            if (userUpdated && professorUpdated) {
                FormAlertSupport.showInformation("Exito", "Los datos se actualizaron correctamente.");
                goProfessorModule(null);
            } else {
                FormAlertSupport.showWarning("Atencion", "No se pudieron actualizar todos los registros.");
            }

        } catch (BusinessException exception) {
            LOGGER.log(Level.WARNING, "Error de validacion o negocio al actualizar profesor: " + exception.getMessage(), exception);
            String message = exception.getMessage();
            if (message == null || message.trim().isEmpty()) {
                message = "Datos de formulario invalidos o incompletos.";
            }
            FormAlertSupport.showWarning("Validacion fallida", message);
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Error tecnico al actualizar profesor", exception);
            String message = exception.getMessage();
            if (message == null || message.trim().isEmpty()) {
                message = "Ocurrio un error inesperado al actualizar el profesor.";
            }
            FormAlertSupport.showError("Error", message);
        }
    }


    

    @FXML
    private void clearForm() {
        txtInstitutionalEmail.clear();

        txtName.clear();

        txtFirstSurname.clear();

        txtSecondSurname.clear();

        txtStaffNumber.clear();

        chkCoordinator.setSelected(
            false
        );
    }
}