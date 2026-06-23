package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ProjectActivityDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectActivityDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import mx.uv.internshipprogramsystem.logic.validations.ProjectActivityValidator;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.gui.util.FormAlertSupport;

public class RegisterActivityFormController implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterActivityFormController.class);

    @FXML
    private ComboBox<String> cmbProject;

    @FXML
    private TextField txtActivityName;

    @FXML
    private TextField txtActivityMonth;

    @FXML
    private TextField txtActivityStartWeek;

    @FXML
    private TextField txtActivityEndWeek;

    @FXML
    private TextField txtActivityPlannedHours;

    @FXML
    private TableView<ProjectActivityDTO> tblActivities;

    @FXML
    private TableColumn<ProjectActivityDTO, String> colName;

    @FXML
    private TableColumn<ProjectActivityDTO, String> colMonth;

    @FXML
    private TableColumn<ProjectActivityDTO, Integer> colStartWeek;

    @FXML
    private TableColumn<ProjectActivityDTO, Integer> colEndWeek;

    @FXML
    private TableColumn<ProjectActivityDTO, Integer> colPlannedHours;

    @FXML
    private Button btnAddActivityToList;

    public static ProjectDTO preSelectedProject = null;

    private final ProjectDAO projectDAO = new ProjectDAO();
    private final ProjectActivityDAO projectActivityDAO = new ProjectActivityDAO();
    private final ObservableList<ProjectActivityDTO> activitiesList = FXCollections.observableArrayList();
    private ProjectActivityDTO editingActivity = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTableColumns();
        loadProjectsComboBox();

        cmbProject.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadProjectActivities();
        });

        tblActivities.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            handleTableSelectionChanged(newVal);
        });

        if (preSelectedProject != null) {
            cmbProject.setValue(preSelectedProject.getId() + " - " + preSelectedProject.getName());
            preSelectedProject = null;
        }
    }

    private void handleTableSelectionChanged(ProjectActivityDTO selected) {
        if (selected != null) {
            editingActivity = selected;
            txtActivityName.setText(selected.getName());
            txtActivityMonth.setText(selected.getMonth());
            txtActivityStartWeek.setText(selected.getStartWeek() != null ? String.valueOf(selected.getStartWeek()) : "");
            txtActivityEndWeek.setText(selected.getEndWeek() != null ? String.valueOf(selected.getEndWeek()) : "");
            txtActivityPlannedHours.setText(selected.getPlannedHours() != null ? String.valueOf(selected.getPlannedHours()) : "");
            btnAddActivityToList.setText("Actualizar actividad");
            btnAddActivityToList.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 16 8 16; -fx-cursor: hand;");
        }
    }

    private void clearFormFields() {
        editingActivity = null;
        txtActivityName.clear();
        txtActivityMonth.clear();
        txtActivityStartWeek.clear();
        txtActivityEndWeek.clear();
        txtActivityPlannedHours.clear();
        btnAddActivityToList.setText("Agregar al plan");
        btnAddActivityToList.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 16 8 16; -fx-cursor: hand;");
    }

    private void configureTableColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colMonth.setCellValueFactory(new PropertyValueFactory<>("month"));
        colStartWeek.setCellValueFactory(new PropertyValueFactory<>("startWeek"));
        colEndWeek.setCellValueFactory(new PropertyValueFactory<>("endWeek"));
        colPlannedHours.setCellValueFactory(new PropertyValueFactory<>("plannedHours"));
        tblActivities.setItems(activitiesList);
    }

    private void loadProjectsComboBox() {
        try {
            List<ProjectDTO> projects = projectDAO.findAll();
            cmbProject.getItems().clear();
            for (ProjectDTO project : projects) {
                cmbProject.getItems().add(project.getId() + " - " + project.getName());
            }
        } catch (BusinessException | DataAccessException exception) {
            LOGGER.error("Error al cargar proyectos.", exception);
            FormAlertSupport.showError("Error", "No se pudieron cargar los proyectos.");
        }
    }

    private void loadProjectActivities() {
        activitiesList.clear();
        clearFormFields();
        String selectedProjectStr = cmbProject.getValue();
        if (selectedProjectStr == null || selectedProjectStr.trim().isEmpty()) {
            return;
        }

        try {
            String[] parts = selectedProjectStr.split(" - ");
            Integer projectId = Integer.valueOf(parts[0]);
            List<ProjectActivityDTO> existingActivities = projectActivityDAO.findByProjectId(projectId);
            activitiesList.addAll(existingActivities);
        } catch (Exception exception) {
            LOGGER.error("Error al cargar actividades del proyecto.", exception);
        }
    }

    @FXML
    private void handleBtnAddActivityToList(ActionEvent event) {
        FormAlertSupport.clearFieldErrorsFromActiveWindow();

        List<String> errors = new ArrayList<>();
        String name = txtActivityName.getText().trim();
        String month = txtActivityMonth.getText().trim();
        String startText = txtActivityStartWeek.getText().trim();
        String endText = txtActivityEndWeek.getText().trim();
        String plannedHoursText = txtActivityPlannedHours.getText().trim();

        if (name.isEmpty()) {
            errors.add("El nombre de la actividad es obligatorio.");
        }
        if (month.isEmpty()) {
            errors.add("El mes de la actividad es obligatorio.");
        }

        Integer startWeek = null;
        if (startText.isEmpty()) {
            errors.add("La semana de inicio es obligatoria.");
        } else {
            try {
                startWeek = Integer.valueOf(startText);
            } catch (NumberFormatException e) {
                startWeek = -1;
            }
        }

        Integer endWeek = null;
        if (endText.isEmpty()) {
            errors.add("La semana de fin es obligatoria.");
        } else {
            try {
                endWeek = Integer.valueOf(endText);
            } catch (NumberFormatException e) {
                endWeek = -1;
            }
        }

        Integer plannedHours = null;
        if (plannedHoursText.isEmpty()) {
            errors.add("Las horas planeadas son obligatorias.");
        } else {
            try {
                plannedHours = Integer.valueOf(plannedHoursText);
            } catch (NumberFormatException e) {
                plannedHours = -1;
            }
        }

        String selectedProjectStr = cmbProject.getValue();
        Integer projectId = null;
        if (selectedProjectStr == null || selectedProjectStr.trim().isEmpty()) {
            errors.add("Debe seleccionar un proyecto asociado antes de agregar actividades.");
        } else {
            try {
                String[] parts = selectedProjectStr.split(" - ");
                projectId = Integer.valueOf(parts[0]);
            } catch (Exception e) {
                errors.add("El proyecto seleccionado no es válido.");
            }
        }

        ProjectActivityDTO tempActivity = new ProjectActivityDTO(
            name,
            month,
            startWeek,
            endWeek,
            plannedHours,
            projectId != null ? projectId : 0
        );

        try {
            ProjectActivityValidator.validateActivity(tempActivity);
        } catch (ValidationException validationException) {
            errors.addAll(validationException.getErrors());
        }

        for (ProjectActivityDTO act : activitiesList) {
            if (editingActivity != null && act == editingActivity) {
                continue;
            }
            if (act.getName().trim().equalsIgnoreCase(name)
                    && act.getMonth().trim().equalsIgnoreCase(month)) {
                errors.add("No se permiten actividades duplicadas en el mismo mes.");
                break;
            }
        }

        if (!errors.isEmpty()) {
            FormAlertSupport.showWarning("Validación fallida", String.join("\n", errors));
            return;
        }

        if (editingActivity != null) {
            editingActivity.setName(name);
            editingActivity.setMonth(month);
            editingActivity.setStartWeek(startWeek);
            editingActivity.setEndWeek(endWeek);
            editingActivity.setPlannedHours(plannedHours);
            editingActivity.setProjectId(projectId != null ? projectId : 0);

            tblActivities.refresh();
            tblActivities.getSelectionModel().clearSelection();
            clearFormFields();
        } else {
            activitiesList.add(tempActivity);
            clearFormFields();
        }
    }

    @FXML
    private void handleBtnRemoveActivityFromList(ActionEvent event) {
        ProjectActivityDTO selectedActivity = tblActivities.getSelectionModel().getSelectedItem();
        if (selectedActivity != null) {
            activitiesList.remove(selectedActivity);
            if (selectedActivity == editingActivity) {
                clearFormFields();
            }
        } else {
            FormAlertSupport.showWarning("Selección requerida", "Debe seleccionar una actividad de la tabla para quitarla.");
        }
    }

    @FXML
    private void handleBtnSaveActivity(ActionEvent event) {
        FormAlertSupport.clearFieldErrorsFromActiveWindow();

        String selectedProjectStr = cmbProject.getValue();
        if (selectedProjectStr == null || selectedProjectStr.trim().isEmpty()) {
            FormAlertSupport.showWarning("Selección requerida", "Debe seleccionar un proyecto asociado.");
            return;
        }

        if (activitiesList.isEmpty()) {
            FormAlertSupport.showWarning("Validación fallida", "Debe registrar al menos una actividad antes de guardar el plan.");
            return;
        }

        try {
            String[] parts = selectedProjectStr.split(" - ");
            Integer projectId = Integer.valueOf(parts[0]);

            projectActivityDAO.saveAll(projectId, new ArrayList<>(activitiesList));

            LOGGER.info("Plan de trabajo guardado correctamente.");
            FormAlertSupport.showInformation(
                "Plan guardado",
                "El plan de trabajo del proyecto se ha guardado correctamente con todas sus actividades."
            );

            WindowManagerController.changeView("ProjectsModuleDashboard.fxml");
        } catch (BusinessException businessException) {
            LOGGER.error("Error guardando plan de trabajo", businessException);
            FormAlertSupport.showWarning(
                "Validación fallida",
                businessException.getMessage()
            );
        } catch (DataAccessException dataAccessException) {
            LOGGER.error("Error de conexión al guardar plan de trabajo", dataAccessException);
            FormAlertSupport.showError(
                "Error de conexión",
                "No se pudo conectar con la base de datos para guardar el plan de trabajo. Por favor intente más tarde."
            );
        }
    }

    @FXML
    private void handleBtnCancel(ActionEvent event) {
        WindowManagerController.changeView("ProjectsModuleDashboard.fxml");
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView("CoordinatorProfessorHomeDashboard.fxml");
    }

    @FXML
    private void goEducationalExperienceModule(ActionEvent event) {
        WindowManagerController.changeView("EducationalExperienceRegisterDashboard.fxml");
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        WindowManagerController.changeView("InternModuleDashboard.fxml");
    }

    @FXML
    private void goLinkedOrganizationModule(ActionEvent event) {
        WindowManagerController.changeView("LinkedOrganizationManagementGUI.fxml");
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        WindowManagerController.changeView("ProjectsModuleDashboard.fxml");
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        LOGGER.info("Acceso al módulo de documentos.");
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        LOGGER.info("Acceso al módulo de reportes.");
    }

    @FXML
    private void logOut(ActionEvent event) {
        UserSessionManager.clearSession();
        WindowManagerController.changeView("LoginDashboard.fxml");
    }
}
