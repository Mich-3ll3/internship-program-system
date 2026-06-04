package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectResponsibleDAO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.ProjectManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class ProjectRegisterDashboardController implements Initializable {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ProjectRegisterDashboardController.class
        );

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSaveProject;

    @FXML
    private TextField txtProjectName;

    @FXML
    private TextArea txaGeneralDescription;

    @FXML
    private TextArea txaGeneralObjective;

    @FXML
    private TextArea txaImmediateObjectives;

    @FXML
    private TextArea txaMediateObjectives;

    @FXML
    private TextField txtMethodology;

    @FXML
    private TextArea txaResources;

    @FXML
    private TextField txtResponsibilities;

    @FXML
    private TextField txtDuration;

    @FXML
    private ComboBox<String> cmbOrganization;

    @FXML
    private ComboBox<String> cmbResponsible;

    private ProjectManager projectManager;
    private LinkedOrganizationDAO linkedOrganizationDAO;
    private ProjectResponsibleDAO projectResponsibleDAO;
    private List<LinkedOrganizationDTO> linkedOrganizations;
    private List<ProjectResponsibleDTO> projectResponsibles;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        projectManager = new ProjectManager();
        linkedOrganizationDAO = new LinkedOrganizationDAO();
        projectResponsibleDAO = new ProjectResponsibleDAO();

        loadComboBoxData();
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
            "EducationalExperienceRegisterDashboard.fxml"
        );
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        WindowManagerController.changeView(
            "InternModuleDashboard.fxml"
        );
    }

    @FXML
    private void goLinkedOrganizationModule(ActionEvent event) {
        WindowManagerController.changeView(
            "LinkedOrganizationManagementGUI.fxml"
        );
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        LOGGER.info(
            "Acceso al módulo de proyectos."
        );

        WindowManagerController.changeView(
            "ProjectsModuleDashboard.fxml"
        );
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        LOGGER.info(
            "Acceso al módulo de documentos."
        );
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        LOGGER.info(
            "Acceso al módulo de reportes."
        );
    }

    @FXML
    private void goResponsibleModule(ActionEvent event) {
        WindowManagerController.changeView(
            "ProjectResponsibleModuleDashboard.fxml"
        );
    }

    @FXML
    private void goProjectRequestModule(ActionEvent event) {
        LOGGER.info(
            "Acceso al módulo de solicitudes."
        );
    }

    @FXML
    private void goTrackingModule(ActionEvent event) {
        LOGGER.info(
            "Acceso al módulo de seguimiento."
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
    private void handleBtnSaveProject(ActionEvent event) {
        try {
            ProjectDTO project = buildProjectFromForm();

            projectManager.registerProject(project);

            LOGGER.info(
                "Proyecto registrado correctamente."
            );

            showInformationAlert(
                "Proyecto registrado correctamente."
            );

            WindowManagerController.changeView(
                "ProjectsModuleDashboard.fxml"
            );
        } catch (NumberFormatException numberFormatException) {
            LOGGER.error(
                "Error convirtiendo la duración del proyecto",
                numberFormatException
            );

            showErrorAlert(
                "La duración debe ser un número válido."
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error registrando proyecto",
                businessException
            );

            showErrorAlert(
                businessException.getMessage()
            );
        }
    }

    @FXML
    private void handleBtnCancel(ActionEvent event) {
        WindowManagerController.changeView(
            "ProjectsModuleDashboard.fxml"
        );
    }

    private void loadComboBoxData() {
        try {
            linkedOrganizations = linkedOrganizationDAO.findAll();
            projectResponsibles = projectResponsibleDAO.findAll();

            loadOrganizations();
            loadResponsibles();
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error cargando datos para registrar proyecto",
                businessException
            );

            showErrorAlert(
                "No se pudieron cargar las organizaciones o responsables."
            );
        }
    }

    private void loadOrganizations() {
        cmbOrganization.getItems().clear();

        for (LinkedOrganizationDTO organization : linkedOrganizations) {
            cmbOrganization.getItems().add(
                organization.getId()
                    + " - "
                    + organization.getName()
            );
        }
    }

    private void loadResponsibles() {
        cmbResponsible.getItems().clear();

        for (ProjectResponsibleDTO responsible : projectResponsibles) {
            cmbResponsible.getItems().add(
                responsible.getId()
                    + " - "
                    + responsible.getFirstName()
                    + " "
                    + responsible.getLastNameFather()
            );
        }
    }

    private ProjectDTO buildProjectFromForm()
            throws BusinessException {
        Integer duration = Integer.valueOf(
            txtDuration.getText().trim()
        );

        Integer organizationId =
            getSelectedOrganizationId();

        Integer responsibleId =
            getSelectedResponsibleId();

        ProjectDTO project = new ProjectDTO(
            getTrimmedText(txtProjectName),
            getTrimmedText(txaGeneralDescription),
            getTrimmedText(txaGeneralObjective),
            getTrimmedText(txaImmediateObjectives),
            getTrimmedText(txaMediateObjectives),
            getTrimmedText(txtMethodology),
            getTrimmedText(txaResources),
            getTrimmedText(txtResponsibilities),
            duration,
            organizationId,
            responsibleId,
            true
        );

        return project;
    }

    private Integer getSelectedOrganizationId()
            throws BusinessException {
        String selectedOrganization =
            cmbOrganization.getValue();

        Integer organizationId =
            getIdFromComboValue(
                selectedOrganization,
                "Debe seleccionar una organización."
            );

        return organizationId;
    }

    private Integer getSelectedResponsibleId()
            throws BusinessException {
        String selectedResponsible =
            cmbResponsible.getValue();

        Integer responsibleId =
            getIdFromComboValue(
                selectedResponsible,
                "Debe seleccionar un responsable."
            );

        return responsibleId;
    }

    private Integer getIdFromComboValue(
            String selectedValue,
            String emptyMessage
    ) throws BusinessException {
        if (selectedValue == null || selectedValue.trim().isEmpty()) {
            throw new BusinessException(emptyMessage);
        }

        String[] parts = selectedValue.split(" - ");
        Integer id = Integer.valueOf(parts[0]);

        return id;
    }

    private String getTrimmedText(TextField textField) {
        String trimmedText = textField.getText().trim();

        return trimmedText;
    }

    private String getTrimmedText(TextArea textArea) {
        String trimmedText = textArea.getText().trim();

        return trimmedText;
    }

    private void showInformationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Registro exitoso");
        alert.setHeaderText("Operación completada");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("No se pudo registrar el proyecto");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
