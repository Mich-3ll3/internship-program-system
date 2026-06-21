package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
// Importa aquí tus DTOs correspondientes
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.dto.ApplicationDTO;

public class ProjectHomeDashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectHomeDashboardController.class);

    @FXML private Button btnHome;
    @FXML private Button btnProjects;
    @FXML private Button btnDocuments;
    @FXML private Button btnReports;
    @FXML private Button btnSelfAssessments;
    @FXML private Button btnExit;

    @FXML private Button btnCancelApplication;
    @FXML private TableView<ApplicationDTO> tblApplications;
    @FXML private TableColumn<ApplicationDTO, String> colAppProject;
    @FXML private TableColumn<ApplicationDTO, String> colAppOrganization;
    @FXML private TableColumn<ApplicationDTO, String> colAppDate;
    @FXML private TableColumn<ApplicationDTO, String> colAppStatus;

    @FXML private TextField txtSearchProject;
    @FXML private ComboBox<String> cmbAreaFilter;
    @FXML private Button btnSearch;
    @FXML private Button btnClearFilters;
    
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

        cmbAreaFilter.getItems().addAll("Todas las áreas", "Desarrollo Web", "Bases de Datos", "Redes", "Soporte Técnico");

        ApplicationSelectionListener appListener = new ApplicationSelectionListener();
        tblApplications.getSelectionModel().selectedItemProperty().addListener(appListener);

        ProjectSelectionListener projListener = new ProjectSelectionListener();
        tblAvailableProjects.getSelectionModel().selectedItemProperty().addListener(projListener);

    }


    @FXML
    private void cancelApplication(ActionEvent event) {
        ApplicationDTO selectedApp = tblApplications.getSelectionModel().getSelectedItem();
        
        if (selectedApp != null) {
            // Aquí iría tu lógica de ReportManager/ProjectManager para cancelar
            LOGGER.info("Cancelando postulación para el proyecto: {}", selectedApp.getProjectName());
            showSuccess("La postulación ha sido cancelada correctamente.");
            btnCancelApplication.setDisable(true);
            tblApplications.getSelectionModel().clearSelection();
        } else {
            showWarning("Debes seleccionar una postulación de la tabla para cancelarla.");
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = txtSearchProject.getText();
        String selectedArea = cmbAreaFilter.getValue();

        LOGGER.info("Buscando proyectos con palabra clave: '{}' y área: '{}'", keyword, selectedArea);

    }

    @FXML
    private void clearFilters(ActionEvent event) {
        txtSearchProject.clear();
        cmbAreaFilter.getSelectionModel().clearSelection();
        LOGGER.info("Filtros de búsqueda limpiados.");
        
    }

    @FXML
    private void viewProjectDetails(ActionEvent event) {
        ProjectDTO selectedProject = tblAvailableProjects.getSelectionModel().getSelectedItem();
        
        if (selectedProject != null) {
            LOGGER.info("Viendo detalles del proyecto: {}", selectedProject.getName());
            showInfo("Abriendo detalles del proyecto...\n\n(Aquí puedes abrir una ventana modal o cambiar de vista con WindowManagerController)");
        } else {
            showWarning("Debes seleccionar un proyecto para ver sus detalles.");
        }
    }

    @FXML
    private void applyForProject(ActionEvent event) {
        ProjectDTO selectedProject = tblAvailableProjects.getSelectionModel().getSelectedItem();
        
        if (selectedProject != null) {
            LOGGER.info("Iniciando solicitud para el proyecto: {}", selectedProject.getName());
            showSuccess("Has solicitado el proyecto exitosamente. El académico revisará tu postulación.");
            btnViewProjectDetails.setDisable(true);
            btnApplyForProject.setDisable(true);
            tblAvailableProjects.getSelectionModel().clearSelection();
        } else {
            showWarning("Debes seleccionar un proyecto de la lista para solicitarlo.");
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView("InternHomeDashboard.fxml");
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        WindowManagerController.changeView("DocumentsDashboard.fxml");
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

    public class ApplicationSelectionListener implements ChangeListener<ApplicationDTO> {
        @Override
        public void changed(ObservableValue<? extends ApplicationDTO> observable, ApplicationDTO oldValue, ApplicationDTO newValue) {
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