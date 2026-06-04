package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectActivityDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectResponsibleDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectScheduleDAO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectActivityDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectScheduleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.ProjectUpdateManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class ProjectUpdateDashboardController implements Initializable {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ProjectUpdateDashboardController.class);

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

    private ProjectUpdateManager projectUpdateManager;
    private LinkedOrganizationDAO linkedOrganizationDAO;
    private ProjectResponsibleDAO projectResponsibleDAO;
    private ProjectActivityDAO projectActivityDAO;
    private ProjectScheduleDAO projectScheduleDAO;

    private List<LinkedOrganizationDTO> linkedOrganizations;
    private List<ProjectResponsibleDTO> projectResponsibles;
    private List<ProjectActivityDTO> activities;
    private List<ProjectScheduleDTO> schedules;

    private ProjectDTO selectedProject;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        projectUpdateManager = new ProjectUpdateManager();
        linkedOrganizationDAO = new LinkedOrganizationDAO();
        projectResponsibleDAO = new ProjectResponsibleDAO();
        projectActivityDAO = new ProjectActivityDAO();
        projectScheduleDAO = new ProjectScheduleDAO();

        activities = new ArrayList<>();
        schedules = new ArrayList<>();

        loadScheduleDays();
        loadComboBoxData();
        loadSelectedProject();
    }

    @FXML
    private void handleBtnSaveProject(ActionEvent event) {
        try {
            ProjectDTO project = buildProjectFromForm();

            projectUpdateManager.updateProject(
                project,
                activities,
                schedules
            );

            showInformationAlert(
                "Proyecto actualizado correctamente."
            );

            WindowManagerController.changeView(
                "ProjectsModuleDashboard.fxml"
            );
        } catch (NumberFormatException numberFormatException) {
            showErrorAlert(
                "Revisa los campos numéricos: duración y semanas."
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error actualizando proyecto",
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

    @FXML
    private void handleBtnAddActivity(ActionEvent event) {
        try {
            ProjectActivityDTO activity = buildActivityFromForm();

            activities.add(activity);
            lstActivities.getItems().add(
                buildActivityDisplayText(activity)
            );

            clearActivityFields();
        } catch (NumberFormatException numberFormatException) {
            showErrorAlert(
                "Las semanas de la actividad deben ser números válidos."
            );
        } catch (BusinessException businessException) {
            showErrorAlert(
                businessException.getMessage()
            );
        }
    }

    @FXML
    private void handleBtnRemoveActivity(ActionEvent event) {
        int selectedIndex =
            lstActivities.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0) {
            activities.remove(selectedIndex);
            lstActivities.getItems().remove(selectedIndex);
        } else {
            showErrorAlert(
                "Debe seleccionar una actividad para eliminar."
            );
        }
    }

    @FXML
    private void handleBtnAddSchedule(ActionEvent event) {
        try {
            ProjectScheduleDTO schedule = buildScheduleFromForm();

            schedules.add(schedule);
            lstSchedules.getItems().add(
                buildScheduleDisplayText(schedule)
            );

            clearScheduleFields();
        } catch (BusinessException businessException) {
            showErrorAlert(
                businessException.getMessage()
            );
        }
    }

    @FXML
    private void handleBtnRemoveSchedule(ActionEvent event) {
        int selectedIndex =
            lstSchedules.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0) {
            schedules.remove(selectedIndex);
            lstSchedules.getItems().remove(selectedIndex);
        } else {
            showErrorAlert(
                "Debe seleccionar un horario para eliminar."
            );
        }
    }

    private void loadSelectedProject() {
        try {
            Optional<ProjectDTO> optionalProject =
                ProjectsModuleDashboardController.getSelectedProject();

            if (optionalProject.isPresent()) {
                selectedProject = optionalProject.get();

                fillProjectFields();
                loadProjectActivities();
                loadProjectSchedules();
            } else {
                showErrorAlert(
                    "No se seleccionó un proyecto para modificar."
                );

                WindowManagerController.changeView(
                    "ProjectsModuleDashboard.fxml"
                );
            }
        } catch (BusinessException businessException) {
            showErrorAlert(
                businessException.getMessage()
            );
        }
    }

    private void fillProjectFields() {
        txtProjectName.setText(selectedProject.getName());
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
        txtDuration.setText(
            String.valueOf(selectedProject.getDuration())
        );

        selectOrganization();
        selectResponsible();
    }

    private void loadProjectActivities()
            throws BusinessException {
        activities = new ArrayList<>(
            projectActivityDAO.findByProjectId(
                selectedProject.getId()
            )
        );

        lstActivities.getItems().clear();

        for (ProjectActivityDTO activity : activities) {
            lstActivities.getItems().add(
                buildActivityDisplayText(activity)
            );
        }
    }

    private void loadProjectSchedules()
            throws BusinessException {
        schedules = new ArrayList<>(
            projectScheduleDAO.findByProjectId(
                selectedProject.getId()
            )
        );

        lstSchedules.getItems().clear();

        for (ProjectScheduleDTO schedule : schedules) {
            lstSchedules.getItems().add(
                buildScheduleDisplayText(schedule)
            );
        }
    }

    private void loadComboBoxData() {
        try {
            linkedOrganizations = linkedOrganizationDAO.findAll();
            projectResponsibles = projectResponsibleDAO.findAll();

            loadOrganizations();
            loadResponsibles();
        } catch (BusinessException businessException) {
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

    private void loadScheduleDays() {
        cmbScheduleDay.getItems().clear();
        cmbScheduleDay.getItems().add("LUNES");
        cmbScheduleDay.getItems().add("MARTES");
        cmbScheduleDay.getItems().add("MIERCOLES");
        cmbScheduleDay.getItems().add("JUEVES");
        cmbScheduleDay.getItems().add("VIERNES");
    }

    private void selectOrganization() {
        for (String organization : cmbOrganization.getItems()) {
            if (organization.startsWith(
                    selectedProject.getLinkedOrganizationId()
                        + " - "
            )) {
                cmbOrganization.setValue(organization);
            }
        }
    }

    private void selectResponsible() {
        for (String responsible : cmbResponsible.getItems()) {
            if (responsible.startsWith(
                    selectedProject.getProjectResponsibleId()
                        + " - "
            )) {
                cmbResponsible.setValue(responsible);
            }
        }
    }

    private ProjectDTO buildProjectFromForm()
            throws BusinessException {
        Integer duration = Integer.valueOf(
            txtDuration.getText().trim()
        );

        ProjectDTO project = new ProjectDTO(
            selectedProject.getId(),
            getTrimmedText(txtProjectName),
            getTrimmedText(txaGeneralDescription),
            getTrimmedText(txaGeneralObjective),
            getTrimmedText(txaImmediateObjectives),
            getTrimmedText(txaMediateObjectives),
            getTrimmedText(txtMethodology),
            getTrimmedText(txaResources),
            getTrimmedText(txtResponsibilities),
            duration,
            getSelectedOrganizationId(),
            getSelectedResponsibleId(),
            selectedProject.getIsActive()
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

        ProjectActivityDTO activity = new ProjectActivityDTO(
            getTrimmedText(txtActivityName),
            getTrimmedText(txtActivityMonth),
            Integer.valueOf(
                txtActivityStartWeek.getText().trim()
            ),
            Integer.valueOf(
                txtActivityEndWeek.getText().trim()
            ),
            selectedProject.getId()
        );

        return activity;
    }

    private ProjectScheduleDTO buildScheduleFromForm()
            throws BusinessException {
        String selectedDay = cmbScheduleDay.getValue();

        if (selectedDay == null || selectedDay.trim().isEmpty()) {
            throw new BusinessException(
                "Debe seleccionar un día para el horario."
            );
        }

        ProjectScheduleDTO schedule = new ProjectScheduleDTO(
            selectedDay,
            LocalTime.parse(txtEntryTime.getText().trim()),
            LocalTime.parse(txtExitTime.getText().trim()),
            selectedProject.getId()
        );

        return schedule;
    }

    private Integer getSelectedOrganizationId()
            throws BusinessException {
        Integer organizationId = getIdFromComboValue(
            cmbOrganization.getValue(),
            "Debe seleccionar una organización."
        );

        return organizationId;
    }

    private Integer getSelectedResponsibleId()
            throws BusinessException {
        Integer responsibleId = getIdFromComboValue(
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
            throw new BusinessException(emptyMessage);
        }

        String[] parts = selectedValue.split(" - ");
        id = Integer.valueOf(parts[0]);

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
        cmbScheduleDay.getSelectionModel().clearSelection();
        txtEntryTime.clear();
        txtExitTime.clear();
    }

    private void validateRequiredTextField(
            TextField textField,
            String message
    ) throws BusinessException {
        if (textField.getText() == null
                || textField.getText().trim().isEmpty()) {
            throw new BusinessException(message);
        }
    }

    private String getTrimmedText(TextField textField) {
        String trimmedText = textField.getText().trim();

        return trimmedText;
    }

    private String getTrimmedText(TextArea textArea) {
        String trimmedText = textArea.getText().trim();

        return trimmedText;
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

        WindowManagerController.changeView(
            "LoginDashboard.fxml"
        );
    }

    private void showInformationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Actualización exitosa");
        alert.setHeaderText("Operación completada");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("No se pudo modificar el proyecto");
        alert.setContentText(message);
        alert.showAndWait();
    }
}