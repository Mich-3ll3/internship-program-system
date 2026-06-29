package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Button;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.gui.util.FormAlertSupport;
import mx.uv.internshipprogramsystem.gui.util.TextFormatterUtil;
import mx.uv.internshipprogramsystem.gui.util.ProjectResponsibleUpdateTask;
import mx.uv.internshipprogramsystem.gui.handlers.NameTextFormatterFilter;
import mx.uv.internshipprogramsystem.gui.handlers.LengthFilterTextFormatter;
import mx.uv.internshipprogramsystem.gui.handlers.ProjectResponsibleUpdateSuccessHandler;
import mx.uv.internshipprogramsystem.gui.handlers.ProjectResponsibleUpdateFailureHandler;
import mx.uv.internshipprogramsystem.logic.validations.InputCleaner;
import mx.uv.internshipprogramsystem.logic.validations.ProjectResponsibleValidator;
import mx.uv.internshipprogramsystem.gui.navigation.NavigationManager;

public class ProjectResponsibleUpdateDashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectResponsibleUpdateDashboardController.class);

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
    @FXML
    private Button btnValidate;
    @FXML
    private Button btnCancel;

    private ProjectResponsibleDTO currentResponsible;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            validatePermission(Permission.MODIFY_PROJECT_RESPONSIBLE);
            setupFormatFilters();
            loadLinkedOrganizations();
            LOGGER.info("Vista de modificación de responsable cargada correctamente.");
        } catch (BusinessException businessException) {
            LOGGER.warn("Acceso denegado a modificación de responsable de proyecto.", businessException);
            FormAlertSupport.showError("Acceso denegado", businessException.getMessage());
            NavigationManager.changeView("CoordinatorProfessorHomeDashboard.fxml");
        }
    }

    public void setResponsibleToUpdate(ProjectResponsibleDTO responsible) {
        this.currentResponsible = responsible;
        populateFields();
    }

    private void populateFields() {
        if (currentResponsible != null) {
            txtFirstName.setText(currentResponsible.getFirstName());
            txtLastNameFather.setText(currentResponsible.getLastNameFather());
            txtLastNameMother.setText(currentResponsible.getLastNameMother());
            txtEmail.setText(currentResponsible.getEmail());
            txtPosition.setText(currentResponsible.getPosition());
            
            for (LinkedOrganizationDTO org : cmbLinkedOrganization.getItems()) {
                if (org.getId() == currentResponsible.getOrganizationId()) {
                    cmbLinkedOrganization.getSelectionModel().select(org);
                    break;
                }
            }
        }
    }

    private void setupFormatFilters() {
        txtFirstName.setTextFormatter(new TextFormatter<>(new NameTextFormatterFilter()));
        txtLastNameFather.setTextFormatter(new TextFormatter<>(new NameTextFormatterFilter()));
        txtLastNameMother.setTextFormatter(new TextFormatter<>(new NameTextFormatterFilter()));
        txtPosition.setTextFormatter(new TextFormatter<>(new LengthFilterTextFormatter(255)));
    }

    @FXML
    private void handleUpdateAction(ActionEvent event) {
        setFormDisabledState(true);
        try {
            validatePermission(Permission.MODIFY_PROJECT_RESPONSIBLE);
            FormAlertSupport.clearFieldErrorsFromActiveWindow();

            List<String> errors = validateFormInputs();
            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }

            ProjectResponsibleDTO updatedResponsible = buildProjectResponsible();
            reflectCleanedDataInFields(updatedResponsible);
            executeUpdate(updatedResponsible);

        } catch (ValidationException exception) {
            setFormDisabledState(false);
            LOGGER.warn("Advertencia: Validación de formulario fallida por datos incorrectos.");
            FormAlertSupport.showWarning("Validación fallida", exception.getMessage());
        } catch (BusinessException businessException) {
            setFormDisabledState(false);
            LOGGER.warn("No se pudo actualizar el responsable de proyecto.", businessException);
            FormAlertSupport.showError("Error", businessException.getMessage());
        } catch (RuntimeException exception) {
            setFormDisabledState(false);
            LOGGER.error("Error: Excepción inesperada procesando el formulario de actualización.", exception);
            FormAlertSupport.showWarning("Error", "Ocurrió un error inesperado en la validación.");
        }
    }

    private List<String> validateFormInputs() {
        List<String> errors = new ArrayList<>();
        LinkedOrganizationDTO selectedOrganization = cmbLinkedOrganization.getSelectionModel().getSelectedItem();

        if (selectedOrganization == null) {
            errors.add("Debe seleccionar una organización vinculada.");
            return errors;
        }

        ProjectResponsibleDTO tempResponsible = new ProjectResponsibleDTO(
            InputCleaner.sanitizeText(txtFirstName.getText()),
            InputCleaner.sanitizeText(txtLastNameFather.getText()),
            InputCleaner.sanitizeText(txtLastNameMother.getText()),
            InputCleaner.sanitizeText(txtEmail.getText()),
            InputCleaner.sanitizeText(txtPosition.getText()),
            selectedOrganization.getId()
        );

        ProjectResponsibleValidator validator = new ProjectResponsibleValidator();
        try {
            validator.validateForRegistration(tempResponsible);
        } catch (ValidationException e) {
            errors.addAll(e.getErrors());
        }

        return errors;
    }

    private ProjectResponsibleDTO buildProjectResponsible() {
        LinkedOrganizationDTO selectedOrganization = cmbLinkedOrganization.getSelectionModel().getSelectedItem();
        String firstName = TextFormatterUtil.formatToTitleCase(InputCleaner.sanitizeText(txtFirstName.getText()));
        String lastNameFather = TextFormatterUtil.formatToTitleCase(InputCleaner.sanitizeText(txtLastNameFather.getText()));
        String lastNameMother = TextFormatterUtil.formatToTitleCase(InputCleaner.sanitizeText(txtLastNameMother.getText()));
        
        ProjectResponsibleDTO updated = new ProjectResponsibleDTO(
            firstName,
            lastNameFather,
            lastNameMother,
            currentResponsible.getEmail(),
            InputCleaner.sanitizeText(txtPosition.getText()),
            selectedOrganization.getId()
        );
        updated.setId(currentResponsible.getId());
        return updated;
    }

    private void reflectCleanedDataInFields(ProjectResponsibleDTO responsible) {
        txtFirstName.setText(responsible.getFirstName());
        txtLastNameFather.setText(responsible.getLastNameFather());
        txtLastNameMother.setText(responsible.getLastNameMother());
        txtPosition.setText(responsible.getPosition());
    }

    private void executeUpdate(ProjectResponsibleDTO responsible) {
        ProjectResponsibleUpdateTask task = new ProjectResponsibleUpdateTask(responsible);
        task.setOnSucceeded(new ProjectResponsibleUpdateSuccessHandler(this));
        task.setOnFailed(new ProjectResponsibleUpdateFailureHandler(this));
        
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void handleUpdateSuccess() {
        setFormDisabledState(false);
        LOGGER.info("Información: Actualización de responsable completada con éxito.");
        FormAlertSupport.showInformation(
            "Actualización exitosa",
            "El responsable ha sido modificado correctamente."
        );
        NavigationManager.changeView("ProjectResponsibleModuleDashboard.fxml");
    }

    public void handleUpdateFailure(Throwable exception) {
        setFormDisabledState(false);
        LOGGER.error("Error: Error técnico detectado al intentar actualizar al responsable.", exception);
        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = "Ocurrió un error inesperado al actualizar el responsable.";
        }
        FormAlertSupport.showError("Error de actualización", message);
    }

    private void setFormDisabledState(boolean disabled) {
        String buttonText = "Guardar cambios";
        if (disabled) {
            buttonText = "Procesando...";
        }
        
        if (btnValidate != null) {
            btnValidate.setDisable(disabled);
            btnValidate.setText(buttonText);
        }
        if (btnCancel != null) {
            btnCancel.setDisable(disabled);
        }
        
        txtFirstName.setDisable(disabled);
        txtLastNameFather.setDisable(disabled);
        txtLastNameMother.setDisable(disabled);
        txtPosition.setDisable(disabled);
        cmbLinkedOrganization.setDisable(disabled);
    }

    private void loadLinkedOrganizations() {
        try {
            LinkedOrganizationDAO linkedOrganizationDAO = new LinkedOrganizationDAO();
            cmbLinkedOrganization.getItems().setAll(linkedOrganizationDAO.findAll());
        } catch (BusinessException businessException) {
            LOGGER.error("No se pudieron cargar las organizaciones vinculadas", businessException);
            FormAlertSupport.showError("Error de carga", "No se pudieron cargar las organizaciones vinculadas.");
        } catch (DataAccessException dataAccessException) {
            LOGGER.error("Error de conexión al cargar organizaciones vinculadas", dataAccessException);
            FormAlertSupport.showError("Error de conexión", "No se pudo conectar con la base de datos para cargar las organizaciones.");
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        NavigationManager.changeView("CoordinatorProfessorHomeDashboard.fxml");
    }

    @FXML
    private void goResponsibleModule(ActionEvent event) {
        NavigationManager.changeView("ProjectResponsibleModuleDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        UserSessionManager.clearSession();
        LOGGER.info("Cierre de sesión realizado correctamente.");
        NavigationManager.changeView("LoginDashboard.fxml");
    }

    private void validatePermission(Permission permission) throws BusinessException {
        AccessControlManager accessControlManager = new AccessControlManager();
        accessControlManager.validatePermission(
            UserSessionManager.getCurrentUser(),
            permission
        );
    }
}
