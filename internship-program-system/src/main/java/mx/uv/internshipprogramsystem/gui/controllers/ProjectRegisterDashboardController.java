package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectResponsibleDAO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectScheduleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.managers.ProjectRegisterManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.gui.util.FormAlertSupport;

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
    private ComboBox<String> cmbOrganization;

    @FXML
    private ComboBox<String> cmbResponsible;

    private ProjectRegisterManager projectRegisterManager;
    private LinkedOrganizationDAO linkedOrganizationDAO;
    private ProjectResponsibleDAO projectResponsibleDAO;
    private List<LinkedOrganizationDTO> linkedOrganizations;
    private List<ProjectResponsibleDTO> projectResponsibles;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        projectRegisterManager = new ProjectRegisterManager();
        linkedOrganizationDAO = new LinkedOrganizationDAO();
        projectResponsibleDAO = new ProjectResponsibleDAO();

        loadComboBoxData();

        cmbOrganization.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            filterResponsiblesByOrganization();
        });
    }

    @FXML
    private void handleBtnSaveProject(ActionEvent event) {
        FormAlertSupport.clearFieldErrorsFromActiveWindow();

        try {
            ProjectDTO project = buildProjectFromForm();

            projectRegisterManager.registerProject(
                project,
                List.of()
            );

            LOGGER.info(
                "Proyecto registrado correctamente."
            );

            FormAlertSupport.showInformation(
                "Proyecto registrado",
                "Proyecto registrado correctamente."
            );

            WindowManagerController.changeView(
                "ProjectsModuleDashboard.fxml"
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error registrando proyecto",
                businessException
            );

            FormAlertSupport.showWarning(
                "Validación fallida",
                businessException
            );
        } catch (DataAccessException dataAccessException) {
            LOGGER.error(
                "Error de conexion al guardar proyecto",
                dataAccessException
            );

            FormAlertSupport.showError(
                "Error de conexión",
                "No se pudo conectar con la base de datos para registrar el proyecto. Por favor intente mas tarde."
            );
        }
    }

    @FXML
    private void handleBtnCancel(ActionEvent event) {
        WindowManagerController.changeView(
            "ProjectsModuleDashboard.fxml"
        );
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
    private void logOut(ActionEvent event) {
        UserSessionManager.clearSession();

        LOGGER.info(
            "Cierre de sesión realizado correctamente."
        );

        WindowManagerController.changeView(
            "LoginDashboard.fxml"
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

            FormAlertSupport.showError(
                "Error",
                "No se pudieron cargar las organizaciones o responsables."
            );
        } catch (DataAccessException dataAccessException) {
            LOGGER.error(
                "Error de conexion al cargar datos para proyecto",
                dataAccessException
            );

            FormAlertSupport.showError(
                "Error de conexión",
                "No se pudo conectar con la base de datos para cargar las organizaciones o responsables. Por favor intente mas tarde."
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
                    + (responsible.getLastNameFather() != null ? responsible.getLastNameFather() : "")
            );
        }
    }

    private void filterResponsiblesByOrganization() {
        String selectedOrg = cmbOrganization.getValue();
        if (selectedOrg == null || selectedOrg.trim().isEmpty()) {
            cmbResponsible.getItems().clear();
            cmbResponsible.setValue(null);
            return;
        }
        try {
            Integer orgId = getIdFromComboValue(selectedOrg, "Debe seleccionar una organización.");
            
            Integer currentRespId = null;
            String selectedResp = cmbResponsible.getValue();
            if (selectedResp != null && !selectedResp.trim().isEmpty()) {
                try {
                    currentRespId = getIdFromComboValue(selectedResp, "");
                } catch (Exception e) {
                    // Ignore
                }
            }

            cmbResponsible.getItems().clear();
            String matchedItem = null;
            for (ProjectResponsibleDTO responsible : projectResponsibles) {
                if (responsible.getOrganizationId() == orgId) {
                    String item = responsible.getId()
                            + " - "
                            + responsible.getFirstName()
                            + " "
                            + (responsible.getLastNameFather() != null ? responsible.getLastNameFather() : "");
                    cmbResponsible.getItems().add(item);
                    if (currentRespId != null && responsible.getId() == currentRespId) {
                        matchedItem = item;
                    }
                }
            }
            if (matchedItem != null) {
                cmbResponsible.setValue(matchedItem);
            } else {
                cmbResponsible.setValue(null);
            }
        } catch (BusinessException e) {
            cmbResponsible.getItems().clear();
            cmbResponsible.setValue(null);
        }
    }



    private ProjectDTO buildProjectFromForm() {
        Integer organizationId = null;
        try {
            organizationId = getSelectedOrganizationId();
        } catch (BusinessException e) {
            // Checked by validator
        }

        Integer responsibleId = null;
        try {
            responsibleId = getSelectedResponsibleId();
        } catch (BusinessException e) {
            // Checked by validator
        }

        ProjectDTO project = new ProjectDTO(
            txtProjectName.getText().trim(),
            txaGeneralDescription.getText().trim(),
            txaGeneralObjective.getText().trim(),
            txaImmediateObjectives.getText().trim(),
            txaMediateObjectives.getText().trim(),
            txtMethodology.getText().trim(),
            txaResources.getText().trim(),
            txtResponsibilities.getText().trim(),
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
        Integer id;

        if (selectedValue == null || selectedValue.trim().isEmpty()) {
            throw new BusinessException(emptyMessage);
        }

        String[] parts = selectedValue.split(" - ");
        id = Integer.valueOf(parts[0]);

        return id;
    }
}