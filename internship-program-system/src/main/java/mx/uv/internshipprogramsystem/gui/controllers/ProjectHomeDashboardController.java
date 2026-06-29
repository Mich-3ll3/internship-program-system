package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.managers.ProjectManager;
import mx.uv.internshipprogramsystem.logic.managers.ProjectApplicationManager;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectApplicationDTO;

public class ProjectHomeDashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectHomeDashboardController.class);

    private static final int MAX_ACTIVE_APPLICATIONS = 3;
    private static final int PRIORITY_ONE = 1;
    private static final int PRIORITY_TWO = 2;
    private static final int PRIORITY_THREE = 3;
    private static final int FIRST_INDEX = 0;

    private static final String CRITERIA_NAME = "Nombre del Proyecto";
    private static final String CRITERIA_ORG = "Organización";
    private static final String CRITERIA_SPOTS = "Cupos Disponibles";

    private final ProjectManager projectManager = new ProjectManager();
    private final ProjectApplicationManager applicationManager = new ProjectApplicationManager();

    @FXML private Button btnHome;
    @FXML private Button btnProjects;
    @FXML private Button btnDocuments;
    @FXML private Button btnReports;
    @FXML private Button btnSelfAssessments;
    @FXML private Button btnExit;

    @FXML private Button btnCancelApplication;
    @FXML private TableView<ProjectApplicationDTO> tblApplications;
    @FXML private TableColumn<ProjectApplicationDTO, String> colAppProject;
    @FXML private TableColumn<ProjectApplicationDTO, String> colAppOrganization;
    @FXML private TableColumn<ProjectApplicationDTO, String> colAppDate;
    @FXML private TableColumn<ProjectApplicationDTO, String> colAppStatus;

    @FXML private TextField txtSearchProject;
    @FXML private ComboBox<String> cmbSearchCriteria; 
    @FXML private Button btnSearch;
    @FXML private Button btnClearFilters;
    
    private List<ProjectDTO> allAvailableProjects;
    
    @FXML private TableView<ProjectDTO> tblAvailableProjects;
    @FXML private TableColumn<ProjectDTO, String> colProjName;
    @FXML private TableColumn<ProjectDTO, String> colProjOrganization;
    @FXML private TableColumn<ProjectDTO, String> colProjArea;
    @FXML private TableColumn<ProjectDTO, Integer> colProjSpots;
    
    @FXML private Button btnViewProjectDetails;
    @FXML private Button btnApplyForProject;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("Inicializando ventana de Módulo de Proyectos.");

        colAppProject.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        colAppOrganization.setCellValueFactory(new PropertyValueFactory<>("organizationName"));
        colAppDate.setCellValueFactory(new PropertyValueFactory<>("applicationDate"));
        colAppStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colProjName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProjOrganization.setCellValueFactory(new PropertyValueFactory<>("organizationName"));
        colProjArea.setCellValueFactory(new PropertyValueFactory<>("area"));
        colProjSpots.setCellValueFactory(new PropertyValueFactory<>("availableSpots"));

        cmbSearchCriteria.getItems().addAll(CRITERIA_NAME, CRITERIA_ORG, CRITERIA_SPOTS);
        cmbSearchCriteria.getSelectionModel().selectFirst();

        ApplicationSelectionListener appListener = new ApplicationSelectionListener();
        tblApplications.getSelectionModel().selectedItemProperty().addListener(appListener);

        ProjectSelectionListener projListener = new ProjectSelectionListener();
        tblAvailableProjects.getSelectionModel().selectedItemProperty().addListener(projListener);

        loadAvailableProjects();
        loadActiveApplications(); 
    }

    private void loadAvailableProjects() {
        try {
            allAvailableProjects = projectManager.getAvailableProjectsForUI();
            tblAvailableProjects.setItems(FXCollections.observableArrayList(allAvailableProjects));
            LOGGER.info("Se cargaron {} proyectos disponibles en la tabla.", allAvailableProjects.size());
        } catch (BusinessException ex) {
            LOGGER.error("Error al cargar los proyectos: {}", ex.getMessage());
            showError("No se pudieron cargar los proyectos disponibles. Por favor, intenta de nuevo más tarde.");
        }
    }

    private void loadActiveApplications() {
        try {
            // Obtenemos dinámicamente el ID del usuario en sesión
            int estudianteIdActual = UserSessionManager.getCurrentUser().getId();
            
            List<ProjectApplicationDTO> activeApps = applicationManager.getActiveApplications(estudianteIdActual);
            tblApplications.setItems(FXCollections.observableArrayList(activeApps));
            LOGGER.info("Se cargaron {} postulaciones activas en la tabla superior.", activeApps.size());
        } catch (BusinessException ex) {
            LOGGER.error("Error al cargar postulaciones activas: {}", ex.getMessage());
            showError("No se pudieron cargar tus postulaciones activas.");
        }
    }

    @FXML
    private void cancelApplication(ActionEvent event) {
        ProjectApplicationDTO selectedApp = tblApplications.getSelectionModel().getSelectedItem();
        
        if (selectedApp != null) {
            try {
                LOGGER.info("Cancelando postulación para el proyecto ID: {}", selectedApp.getProjectId());
                
                // Obtenemos dinámicamente el ID del usuario en sesión
                int estudianteIdActual = UserSessionManager.getCurrentUser().getId();
                
                boolean success = applicationManager.cancelApplication(estudianteIdActual, selectedApp.getProjectId());
                
                if (success) {
                    showSuccess("La postulación ha sido cancelada correctamente.");
                    btnCancelApplication.setDisable(true);
                    tblApplications.getSelectionModel().clearSelection();
                    
                    loadActiveApplications();
                }
            } catch (BusinessException ex) {
                LOGGER.error("No se pudo cancelar la postulación: {}", ex.getMessage());
                showError("Ocurrió un error al intentar cancelar la postulación. Intenta más tarde.");
            }
        } else {
            showWarning("Debes seleccionar una postulación de la tabla para cancelarla.");
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = txtSearchProject.getText().trim().toLowerCase();
        String criteria = cmbSearchCriteria.getValue();

        if (keyword.isEmpty() || criteria == null) {
            restaurarTablaCompleta();
            LOGGER.info("Búsqueda vacía, mostrando todos los proyectos.");
        } else {
            List<ProjectDTO> filteredList = filtrarProyectos(keyword, criteria);
            
            if (filteredList.isEmpty()) {
                LOGGER.info("Búsqueda sin resultados para '{}'. La tabla se queda como estaba.", keyword);
                showInfo("No se encontraron proyectos que coincidan con tu búsqueda.");
            } else {
                tblAvailableProjects.setItems(FXCollections.observableArrayList(filteredList));
                LOGGER.info("Búsqueda aplicada. Resultados encontrados: {}", filteredList.size());
            }
        }
    }

    private void restaurarTablaCompleta() {
        if (allAvailableProjects != null) {
            tblAvailableProjects.setItems(FXCollections.observableArrayList(allAvailableProjects));
        }
    }

    private List<ProjectDTO> filtrarProyectos(String keyword, String criteria) {
        List<ProjectDTO> filteredList = new ArrayList<>();
        if (allAvailableProjects != null) {
            for (ProjectDTO project : allAvailableProjects) {
                if (cumpleCriterio(project, criteria, keyword)) {
                    filteredList.add(project);
                }
            }
        }
        return filteredList;
    }

    private boolean cumpleCriterio(ProjectDTO project, String criteria, String keyword) {
        boolean matches = false;
        switch (criteria) {
            case CRITERIA_NAME:
                if (project.getName() != null) {
                    matches = project.getName().toLowerCase().contains(keyword);
                }
                break;
            case CRITERIA_ORG:
                if (project.getOrganizationName() != null) {
                    matches = project.getOrganizationName().toLowerCase().contains(keyword);
                }
                break;
            case CRITERIA_SPOTS:
                matches = String.valueOf(project.getAvailableSpots()).contains(keyword);
                break;
            default:
                break;
        }
        return matches;
    }

    @FXML
    private void clearFilters(ActionEvent event) {
        txtSearchProject.clear();
        cmbSearchCriteria.getSelectionModel().selectFirst();
        restaurarTablaCompleta();
        LOGGER.info("Filtros de búsqueda limpiados.");
    }

    @FXML
    private void viewProjectDetails(ActionEvent event) {
        ProjectDTO selectedProject = tblAvailableProjects.getSelectionModel().getSelectedItem();
        
        if (selectedProject == null) {
            showWarning("Debes seleccionar un proyecto para ver sus detalles.");
            return;
        }

        try {
            LOGGER.info("Abriendo detalles del proyecto: {}", selectedProject.getName());
            
            mx.uv.internshipprogramsystem.logic.interfaces.IProjectActivityDAO activityDAO = 
                new mx.uv.internshipprogramsystem.logic.dao.ProjectActivityDAO();
            
            java.util.List<mx.uv.internshipprogramsystem.logic.dto.ProjectActivityDTO> activities = 
                activityDAO.findByProjectId(selectedProject.getId());
            
            selectedProject.setPlannedActivities(activities);
            
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/mx/uv/internshipprogramsystem/gui/fxml/ProjectDetailsIntern.fxml")
            );
            javafx.scene.Parent root = loader.load();

            ProjectDetailsInternController controller = loader.getController();
            
            controller.initData(selectedProject);

            javafx.stage.Stage detailsStage = new javafx.stage.Stage();
            detailsStage.setTitle("Detalles del Proyecto");
            detailsStage.setResizable(false);
            detailsStage.setScene(new javafx.scene.Scene(root));
            
            detailsStage.show(); 

        } catch (mx.uv.internshipprogramsystem.logic.exceptions.BusinessException ex) {
            LOGGER.error("Error de base de datos al consultar las actividades.", ex);
            showError("No se pudieron cargar las actividades de este proyecto. Verifique su conexión.");
        } catch (java.io.IOException ex) {
            LOGGER.error("Error al cargar la ventana de detalles del proyecto.", ex);
            showError("Ocurrió un error al intentar abrir los detalles del proyecto.");
        }
    }

    @FXML
    private void applyForProject(ActionEvent event) {
        ProjectDTO selectedProject = tblAvailableProjects.getSelectionModel().getSelectedItem();
        
        if (selectedProject == null) {
            showWarning("Debes seleccionar un proyecto de la lista para solicitarlo.");
            return;
        }

        List<ProjectApplicationDTO> currentApplications = tblApplications.getItems();

        if (hasAlreadyApplied(currentApplications, selectedProject.getId())) {
            showWarning("Ya estás postulado a este proyecto.\nSi deseas cambiar tu elección, primero debes cancelarlo en la tabla superior.");
            return;
        }

        if (currentApplications.size() >= MAX_ACTIVE_APPLICATIONS) {
            showWarning("Has alcanzado el límite máximo de " + MAX_ACTIVE_APPLICATIONS + 
                        " postulaciones activas. Cancela una postulación existente para elegir otro proyecto.");
            return;
        }

        List<Integer> availablePriorities = getAvailablePriorities(currentApplications);
        
        javafx.scene.control.ChoiceDialog<Integer> priorityDialog = new javafx.scene.control.ChoiceDialog<>(
            availablePriorities.get(FIRST_INDEX), 
            availablePriorities
        );
        priorityDialog.setTitle("Seleccionar Prioridad");
        priorityDialog.setHeaderText("Postulación para: " + selectedProject.getName());
        priorityDialog.setContentText("Asigna el nivel de prioridad para este proyecto:");

        java.util.Optional<Integer> selectedPriorityOpt = priorityDialog.showAndWait();

        if (selectedPriorityOpt.isPresent()) {
            int chosenPriority = selectedPriorityOpt.get();
            processApplication(selectedProject, chosenPriority);
        }
    }

    private boolean hasAlreadyApplied(List<ProjectApplicationDTO> currentApplications, int projectId) {
        boolean alreadyApplied = false;
        for (ProjectApplicationDTO activeApp : currentApplications) {
            if (activeApp.getProjectId().equals(projectId)) {
                alreadyApplied = true;
                break;
            }
        }
        return alreadyApplied;
    }

    private List<Integer> getAvailablePriorities(List<ProjectApplicationDTO> currentApplications) {
        List<Integer> availablePriorities = new ArrayList<>();
        availablePriorities.add(PRIORITY_ONE);
        availablePriorities.add(PRIORITY_TWO);
        availablePriorities.add(PRIORITY_THREE);
        
        for (ProjectApplicationDTO activeApp : currentApplications) {
            availablePriorities.remove(activeApp.getPriority());
        }
        return availablePriorities;
    }

    private void processApplication(ProjectDTO selectedProject, int chosenPriority) {
        try {
            // Obtenemos dinámicamente el ID del usuario en sesión
            int estudianteIdActual = UserSessionManager.getCurrentUser().getId();
            
            LOGGER.info("Iniciando solicitud para el proyecto ID: {} con prioridad: {}", 
                    selectedProject.getId(), chosenPriority);
            
            boolean success = applicationManager.registerApplication(
                estudianteIdActual, 
                selectedProject.getId(), 
                chosenPriority
            );
            
            if (success) {
                showSuccess("Has solicitado el proyecto exitosamente. Tu prioridad asignada es: " + chosenPriority);
                btnViewProjectDetails.setDisable(true);
                btnApplyForProject.setDisable(true);
                tblAvailableProjects.getSelectionModel().clearSelection();
                
                loadActiveApplications(); 
            }
        } catch (BusinessException ex) {
            LOGGER.error("No se pudo completar la postulación: {}", ex.getMessage());
            showWarning(ex.getMessage()); 
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView("InternHomeDashboard.fxml");
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        try {
            LOGGER.info("Navegando al módulo de Documentos.");
            WindowManagerController.changeView("DocumentHomeDashboard.fxml");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al navegar al módulo de Documentos: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Intento de navegación al módulo de Documentos finalizado.");
        }
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        WindowManagerController.changeView("ReportHomeDashboard.fxml");
    }

    @FXML
    private void goSelfAssessmentsModule(ActionEvent event) {
        WindowManagerController.changeView("SelfAssessmentHomeDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        UserSessionManager.clearSession();
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

    public class ApplicationSelectionListener implements ChangeListener<ProjectApplicationDTO> {
        @Override
        public void changed(ObservableValue<? extends ProjectApplicationDTO> observable, ProjectApplicationDTO oldValue, ProjectApplicationDTO newValue) {
            if (newValue != null) {
                btnCancelApplication.setDisable(false);
            } else {
                btnCancelApplication.setDisable(true);
            }
        }
    }

    public class ProjectSelectionListener implements ChangeListener<ProjectDTO> {
        @Override
        public void changed(ObservableValue<? extends ProjectDTO> observable, ProjectDTO oldValue, ProjectDTO newValue) {
            if (newValue != null) {
                btnViewProjectDetails.setDisable(false);
                btnApplyForProject.setDisable(false);
            } else {
                btnViewProjectDetails.setDisable(true);
                btnApplyForProject.setDisable(true);
            }
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atención");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}