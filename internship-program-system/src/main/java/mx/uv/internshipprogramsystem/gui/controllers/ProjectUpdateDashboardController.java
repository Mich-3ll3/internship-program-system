package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectResponsibleDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectScheduleDAO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectScheduleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.managers.ProjectUpdateManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.gui.util.FormAlertSupport;

public class ProjectUpdateDashboardController implements Initializable {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ProjectUpdateDashboardController.class
        );

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

    private mx.uv.internshipprogramsystem.logic.managers.ProjectManager projectManager;
    private LinkedOrganizationDAO linkedOrganizationDAO;

    private ProjectResponsibleDAO projectResponsibleDAO;

    private List<LinkedOrganizationDTO> linkedOrganizations;

    private List<ProjectResponsibleDTO> projectResponsibles;

    private ProjectDTO selectedProject;

    public void setProjectData(ProjectDTO project) {
        selectedProject = project;
        fillProjectFields();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        projectManager = new mx.uv.internshipprogramsystem.logic.managers.ProjectManager();
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
            validatePermission(
                Permission.UPDATE_PROJECT
            );

            ProjectDTO project =
                buildProjectFromForm();

            projectManager.updateProject(project);

            FormAlertSupport.showInformation(
                "Proyecto actualizado",
                "Proyecto actualizado correctamente."
            );

            WindowManagerController.changeView(
                "ProjectsModuleDashboard.fxml"
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error actualizando proyecto.",
                businessException
            );

            FormAlertSupport.showWarning(
                "Validación fallida",
                businessException
            );
        } catch (DataAccessException dataAccessException) {
            LOGGER.error(
                "Error de conexion al actualizar proyecto",
                dataAccessException
            );

            FormAlertSupport.showError(
                "Error de conexión",
                "No se pudo conectar con la base de datos para guardar los cambios del proyecto. Por favor intente mas tarde."
            );
        }
    }

    @FXML
    private void handleBtnCancel(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.CONSULT_PROJECT,
            "ProjectsModuleDashboard.fxml",
            "Acceso denegado al módulo de proyectos."
        );
    }



    private void fillProjectFields() {
        txtProjectName.setText(
            selectedProject.getName()
        );

        txaGeneralDescription.setText(
            selectedProject.getGeneralDescription()
        );

        txaGeneralObjective.setText(
            selectedProject.getGeneralObjective()
        );

        txaImmediateObjectives.setText(
            selectedProject.getImmediateObjectives()
        );

        txaMediateObjectives.setText(
            selectedProject.getMediateObjective()
        );

        txtMethodology.setText(
            selectedProject.getMethodology()
        );

        txaResources.setText(
            selectedProject.getResources()
        );

        txtResponsibilities.setText(
            selectedProject.getResponsibilities()
        );

        selectOrganization();

        selectResponsible();
    }

    private void loadComboBoxData() {
        try {
            linkedOrganizations =
                linkedOrganizationDAO.findAll();

            projectResponsibles =
                projectResponsibleDAO.findAll();

            loadOrganizations();

            loadResponsibles();
        } catch (BusinessException businessException) {
            FormAlertSupport.showError(
                "Error",
                "No se pudieron cargar las organizaciones o responsables."
            );
        } catch (DataAccessException dataAccessException) {
            LOGGER.error(
                "Error de conexion al cargar catalogos",
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

    private void selectOrganization() {
        for (String organization : cmbOrganization.getItems()) {
            if (organization.startsWith(
                    selectedProject.getLinkedOrganizationId()
                        + " - "
            )) {
                cmbOrganization.setValue(
                    organization
                );
            }
        }
    }

    private void selectResponsible() {
        for (String responsible : cmbResponsible.getItems()) {
            if (responsible.startsWith(
                    selectedProject.getProjectResponsibleId()
                        + " - "
            )) {
                cmbResponsible.setValue(
                    responsible
                );
            }
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
            selectedProject.getId(),
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
            selectedProject.getIsActive()
        );

        return project;
    }

    private Integer getSelectedOrganizationId()
            throws BusinessException {
        Integer organizationId =
            getIdFromComboValue(
                cmbOrganization.getValue(),
                "Debe seleccionar una organización."
            );

        return organizationId;
    }

    private Integer getSelectedResponsibleId()
            throws BusinessException {
        Integer responsibleId =
            getIdFromComboValue(
                cmbResponsible.getValue(),
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
            throw new BusinessException(
                emptyMessage
            );
        }

        String[] parts =
            selectedValue.split(
                " - "
            );

        id =
            Integer.valueOf(
                parts[0]
            );

        return id;
    }

    @FXML
    private void goHome(
            ActionEvent event
    ) {
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
            "Acceso denegado al módulo de experiencia educativa."
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
    private void goLinkedOrganizationModule(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.CONSULT_ORGANIZATION,
            "LinkedOrganizationManagementGUI.fxml",
            "Acceso denegado al módulo de organizaciones vinculadas."
        );
    }

    @FXML
    private void goProjectsModule(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.CONSULT_PROJECT,
            "ProjectsModuleDashboard.fxml",
            "Acceso denegado al módulo de proyectos."
        );
    }

    @FXML
    private void goDocumentsModule(
            ActionEvent event
    ) {
        LOGGER.info(
            "Acceso al módulo de documentos."
        );
    }

    @FXML
    private void goReportsModule(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.CONSULT_REPORT,
            "ReportHomeDashboard.fxml",
            "Acceso denegado al módulo de reportes."
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
}