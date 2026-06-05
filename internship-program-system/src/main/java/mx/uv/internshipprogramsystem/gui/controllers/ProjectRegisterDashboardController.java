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
import mx.uv.internshipprogramsystem.logic.dto.ProjectActivityDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectScheduleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.ProjectRegisterManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;

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

    @FXML
    private TextField txtActivityName;

    @FXML
    private TextField txtActivityMonth;

    @FXML
    private TextField txtActivityStartWeek;

    @FXML
    private TextField txtActivityEndWeek;

    @FXML
    private ListView<String> lstActivities;

    @FXML
    private ComboBox<String> cmbScheduleDay;

    @FXML
    private TextField txtEntryTime;

    @FXML
    private TextField txtExitTime;

    @FXML
    private ListView<String> lstSchedules;

    private ProjectRegisterManager projectRegisterManager;

    private LinkedOrganizationDAO linkedOrganizationDAO;

    private ProjectResponsibleDAO projectResponsibleDAO;

    private List<LinkedOrganizationDTO> linkedOrganizations;

    private List<ProjectResponsibleDTO> projectResponsibles;

    private List<ProjectActivityDTO> activities;

    private List<ProjectScheduleDTO> schedules;

    @Override
    public void initialize(
            URL url,
            ResourceBundle resourceBundle
    ) {
        try {
            validatePermission(
                Permission.REGISTER_PROJECT
            );

            projectRegisterManager =
                new ProjectRegisterManager();

            linkedOrganizationDAO =
                new LinkedOrganizationDAO();

            projectResponsibleDAO =
                new ProjectResponsibleDAO();

            activities =
                new ArrayList<>();

            schedules =
                new ArrayList<>();

            loadComboBoxData();

            loadScheduleDays();

            LOGGER.info(
                "Vista de registro de proyecto cargada correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado al registro de proyectos.",
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                businessException.getMessage()
            );

            WindowManagerController.changeView(
                "CoordinatorProfessorHomeDashboard.fxml"
            );
        }
    }

    @FXML
    private void handleBtnSaveProject(
            ActionEvent event
    ) {
        try {
            validatePermission(
                Permission.REGISTER_PROJECT
            );

            ProjectDTO project =
                buildProjectFromForm();

            projectRegisterManager.registerProject(
                project,
                activities,
                schedules
            );

            LOGGER.info(
                "Proyecto registrado correctamente."
            );

            FormAlertSupport.showInformation(
                "Registro exitoso",
                "Proyecto registrado correctamente."
            );

            WindowManagerController.changeView(
                "ProjectsModuleDashboard.fxml"
            );
        } catch (NumberFormatException numberFormatException) {
            LOGGER.error(
                "Error convirtiendo datos numéricos del proyecto.",
                numberFormatException
            );

            FormAlertSupport.showError(
                "Error de formato",
                "Revisa los campos numéricos: duración y semanas."
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error registrando proyecto.",
                businessException
            );

            FormAlertSupport.showError(
                "Error",
                businessException.getMessage()
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

    @FXML
    private void handleBtnAddActivity(
            ActionEvent event
    ) {
        try {
            validatePermission(
                Permission.ADD_PROJECT_ACTIVITIES
            );

            ProjectActivityDTO activity =
                buildActivityFromForm();

            activities.add(
                activity
            );

            lstActivities.getItems().add(
                buildActivityDisplayText(
                    activity
                )
            );

            clearActivityFields();
        } catch (NumberFormatException numberFormatException) {
            LOGGER.warn(
                "Error en formato numérico de actividad.",
                numberFormatException
            );

            FormAlertSupport.showError(
                "Error de formato",
                "Las semanas de la actividad deben ser números válidos."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "No se pudo agregar la actividad.",
                businessException
            );

            FormAlertSupport.showError(
                "Error",
                businessException.getMessage()
            );
        }
    }

    @FXML
    private void handleBtnRemoveActivity(
            ActionEvent event
    ) {
        int selectedIndex =
            lstActivities
                .getSelectionModel()
                .getSelectedIndex();

        if (selectedIndex >= 0) {
            activities.remove(
                selectedIndex
            );

            lstActivities.getItems().remove(
                selectedIndex
            );
        } else {
            FormAlertSupport.showWarning(
                "Selección requerida",
                "Debe seleccionar una actividad para eliminar."
            );
        }
    }

    @FXML
    private void handleBtnAddSchedule(
            ActionEvent event
    ) {
        try {
            validatePermission(
                Permission.ADD_PROJECT_ACTIVITIES
            );

            ProjectScheduleDTO schedule =
                buildScheduleFromForm();

            schedules.add(
                schedule
            );

            lstSchedules.getItems().add(
                buildScheduleDisplayText(
                    schedule
                )
            );

            clearScheduleFields();
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "No se pudo agregar el horario.",
                businessException
            );

            FormAlertSupport.showError(
                "Error",
                businessException.getMessage()
            );
        }
    }

    @FXML
    private void handleBtnRemoveSchedule(
            ActionEvent event
    ) {
        int selectedIndex =
            lstSchedules
                .getSelectionModel()
                .getSelectedIndex();

        if (selectedIndex >= 0) {
            schedules.remove(
                selectedIndex
            );

            lstSchedules.getItems().remove(
                selectedIndex
            );
        } else {
            FormAlertSupport.showWarning(
                "Selección requerida",
                "Debe seleccionar un horario para eliminar."
            );
        }
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

    private void loadComboBoxData() {
        try {
            linkedOrganizations =
                linkedOrganizationDAO.findAll();

            projectResponsibles =
                projectResponsibleDAO.findAll();

            loadOrganizations();

            loadResponsibles();
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error cargando datos para registrar proyecto.",
                businessException
            );

            FormAlertSupport.showError(
                "Error de carga",
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

    private void loadScheduleDays() {
        cmbScheduleDay.getItems().clear();

        cmbScheduleDay.getItems().add(
            "LUNES"
        );

        cmbScheduleDay.getItems().add(
            "MARTES"
        );

        cmbScheduleDay.getItems().add(
            "MIERCOLES"
        );

        cmbScheduleDay.getItems().add(
            "JUEVES"
        );

        cmbScheduleDay.getItems().add(
            "VIERNES"
        );
    }

    private ProjectDTO buildProjectFromForm()
            throws BusinessException {
        Integer duration =
            Integer.valueOf(
                txtDuration.getText().trim()
            );

        Integer organizationId =
            getSelectedOrganizationId();

        Integer responsibleId =
            getSelectedResponsibleId();

        ProjectDTO project =
            new ProjectDTO(
                getTrimmedText(
                    txtProjectName
                ),
                getTrimmedText(
                    txaGeneralDescription
                ),
                getTrimmedText(
                    txaGeneralObjective
                ),
                getTrimmedText(
                    txaImmediateObjectives
                ),
                getTrimmedText(
                    txaMediateObjectives
                ),
                getTrimmedText(
                    txtMethodology
                ),
                getTrimmedText(
                    txaResources
                ),
                getTrimmedText(
                    txtResponsibilities
                ),
                duration,
                organizationId,
                responsibleId,
                true
            );

        return project;
    }

    private ProjectActivityDTO buildActivityFromForm()
            throws BusinessException {
        validateRequiredTextField(
            txtActivityName,
            "El nombre de la actividad es obligatorio."
        );

        validateRequiredTextField(
            txtActivityMonth,
            "El mes de la actividad es obligatorio."
        );

        Integer startWeek =
            Integer.valueOf(
                txtActivityStartWeek.getText().trim()
            );

        Integer endWeek =
            Integer.valueOf(
                txtActivityEndWeek.getText().trim()
            );

        ProjectActivityDTO activity =
            new ProjectActivityDTO(
                getTrimmedText(
                    txtActivityName
                ),
                getTrimmedText(
                    txtActivityMonth
                ),
                startWeek,
                endWeek,
                0
            );

        return activity;
    }

    private ProjectScheduleDTO buildScheduleFromForm()
            throws BusinessException {
        String selectedDay =
            cmbScheduleDay.getValue();

        if (selectedDay == null || selectedDay.trim().isEmpty()) {
            throw new BusinessException(
                "Debe seleccionar un día para el horario."
            );
        }

        validateRequiredTextField(
            txtEntryTime,
            "La hora de entrada es obligatoria."
        );

        validateRequiredTextField(
            txtExitTime,
            "La hora de salida es obligatoria."
        );

        ProjectScheduleDTO schedule =
            new ProjectScheduleDTO(
                selectedDay,
                LocalTime.parse(
                    txtEntryTime.getText().trim()
                ),
                LocalTime.parse(
                    txtExitTime.getText().trim()
                ),
                0
            );

        return schedule;
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

    private String buildActivityDisplayText(
            ProjectActivityDTO activity
    ) {
        String displayText =
            activity.getName()
                + " | "
                + activity.getMonth()
                + " | Semana "
                + activity.getStartWeek()
                + " a "
                + activity.getEndWeek();

        return displayText;
    }

    private String buildScheduleDisplayText(
            ProjectScheduleDTO schedule
    ) {
        String displayText =
            schedule.getWeekDay()
                + " | "
                + schedule.getEntryTime()
                + " - "
                + schedule.getExitTime();

        return displayText;
    }

    private void clearActivityFields() {
        txtActivityName.clear();

        txtActivityMonth.clear();

        txtActivityStartWeek.clear();

        txtActivityEndWeek.clear();
    }

    private void clearScheduleFields() {
        cmbScheduleDay
            .getSelectionModel()
            .clearSelection();

        txtEntryTime.clear();

        txtExitTime.clear();
    }

    private void validateRequiredTextField(
            TextField textField,
            String message
    ) throws BusinessException {
        if (textField.getText() == null
                || textField.getText().trim().isEmpty()) {
            throw new BusinessException(
                message
            );
        }
    }

    private String getTrimmedText(
            TextField textField
    ) {
        String trimmedText =
            textField.getText().trim();

        return trimmedText;
    }

    private String getTrimmedText(
            TextArea textArea
    ) {
        String trimmedText =
            textArea.getText().trim();

        return trimmedText;
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