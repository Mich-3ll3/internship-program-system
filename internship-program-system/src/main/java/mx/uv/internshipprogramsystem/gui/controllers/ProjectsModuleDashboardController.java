package mx.uv.internshipprogramsystem.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.managers.ProjectManager;
import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectResponsibleDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectActivityDAO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectActivityDTO;
import java.util.HashMap;
import java.util.Map;
import mx.uv.internshipprogramsystem.logic.managers.SessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;

public class ProjectsModuleDashboardController implements Initializable {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ProjectsModuleDashboardController.class
        );

    private static final String PROJECT_CARD_FXML_PATH =
        "/mx/uv/internshipprogramsystem/gui/fxml/ProjectCard.fxml";

    private static Optional<ProjectDTO> selectedProject =
        Optional.empty();

    @FXML
    private Button btnAddProject;

    @FXML
    private ScrollPane scpProjects;

    @FXML
    private VBox vbxProjectsContainer;
    
    @FXML
    private VBox vbxStudentAssignments;
    
    @FXML
    private javafx.scene.control.TableView<String> tblAssignments;
    
    @FXML
    private javafx.scene.control.TableColumn<String, String> colProjectName;
    
    @FXML
    private javafx.scene.control.TableColumn<String, String> colDate;
    
    @FXML
    private javafx.scene.control.TableColumn<String, String> colAssignmentStatus;

    private ProjectManager projectManager;
    private final LinkedOrganizationDAO linkedOrganizationDAO = new LinkedOrganizationDAO();
    private final ProjectResponsibleDAO projectResponsibleDAO = new ProjectResponsibleDAO();
    private final ProjectActivityDAO projectActivityDAO = new ProjectActivityDAO();
    private final Map<Integer, String> organizationNameMap = new HashMap<>();
    private final Map<Integer, String> responsibleNameMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        projectManager = new ProjectManager();
        
        List<Permission> userPermissions = SessionManager.getInstance().getPermissions();
        if (!userPermissions.contains(Permission.REGISTER_PROJECT)) {
            btnAddProject.setVisible(false);
            btnAddProject.setManaged(false);
        }
        
        if (userPermissions.contains(Permission.REQUEST_PROJECT)) {
            vbxStudentAssignments.setVisible(true);
            vbxStudentAssignments.setManaged(true);
            setupAssignmentsTable();
        } else {
            vbxStudentAssignments.setVisible(false);
            vbxStudentAssignments.setManaged(false);
        }
        
        loadProjects();
    }
    
    private void setupAssignmentsTable() {
        // Mock data logic for assignments
        colProjectName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty("Sistema de Inventario QR"));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty("2026-06-25"));
        colAssignmentStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty("Aceptada"));
        tblAssignments.getItems().add("dummy");
    }

    public static Optional<ProjectDTO> getSelectedProject() {
        return selectedProject;
    }

    public static void clearSelectedProject() {
        selectedProject = Optional.empty();
    }

    @FXML
    private void handleBtnAddProject(ActionEvent event) {
        clearSelectedProject();

        WindowManagerController.changeView(
            "ProjectRegisterDashboard.fxml"
        );
    }

    @FXML
    private void handleBtnEditProject(ActionEvent event) {
        Button btnEditProject = (Button) event.getSource();
        ProjectDTO project = (ProjectDTO) btnEditProject.getUserData();

        selectedProject = Optional.of(project);

        WindowManagerController.changeViewToUpdateProject(
            "ProjectUpdateDashboard.fxml",
            project
        );
    }

    private void loadProjects() {
        try {
            // Cache organization names
            organizationNameMap.clear();
            for (LinkedOrganizationDTO org : linkedOrganizationDAO.findAll()) {
                organizationNameMap.put(org.getId(), org.getName());
            }

            // Cache responsible names
            responsibleNameMap.clear();
            for (ProjectResponsibleDTO resp : projectResponsibleDAO.findAll()) {
                responsibleNameMap.put(resp.getId(), resp.getFullName());
            }

            List<ProjectDTO> projects =
                projectManager.findAllProjects();

            vbxProjectsContainer.getChildren().clear();

            if (projects.isEmpty()) {
                showEmptyProjectsMessage();
            } else {
                showProjectCards(projects);
            }
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error cargando proyectos",
                businessException
            );

            showErrorAlert(
                "No se pudieron cargar los proyectos."
            );
        } catch (DataAccessException dataAccessException) {
            LOGGER.error(
                "Error de conexion al cargar proyectos",
                dataAccessException
            );

            showErrorAlert(
                "No se pudo conectar con la base de datos para cargar los proyectos. Por favor intente mas tarde."
            );
        }
    }

    private void showProjectCards(List<ProjectDTO> projects) {
        for (ProjectDTO project : projects) {
            loadProjectCard(project);
        }
    }

    private void loadProjectCard(ProjectDTO project) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                    PROJECT_CARD_FXML_PATH
                )
            );

            VBox vbxProjectCard = loader.load();

            fillProjectCard(vbxProjectCard, project);

            vbxProjectsContainer.getChildren().add(
                vbxProjectCard
            );
        } catch (IOException ioException) {
            LOGGER.error(
                "Error cargando tarjeta de proyecto",
                ioException
            );

            showErrorAlert(
                "No se pudo cargar una tarjeta de proyecto."
            );
        }
    }

    private void fillProjectCard(
            VBox vbxProjectCard,
            ProjectDTO project
    ) {
        Label lblProjectName = (Label) vbxProjectCard.lookup("#lblProjectName");
        Label lblMethodology = (Label) vbxProjectCard.lookup("#lblMethodology");
        Label lblDescription = (Label) vbxProjectCard.lookup("#lblDescription");
        Label lblOrganization = (Label) vbxProjectCard.lookup("#lblOrganization");
        Label lblResponsible = (Label) vbxProjectCard.lookup("#lblResponsible");
        Label lblObjective = (Label) vbxProjectCard.lookup("#lblObjective");
        Label lblStatus = (Label) vbxProjectCard.lookup("#lblStatus");
        Button btnEditProject = (Button) vbxProjectCard.lookup("#btnEditProject");
        Button btnViewProject = (Button) vbxProjectCard.lookup("#btnViewProject");
        Button btnRequestProject = (Button) vbxProjectCard.lookup("#btnRequestProject");
        Label lblInitials = (Label) vbxProjectCard.lookup("#lblInitials");
        StackPane pneImagePlaceholder = (StackPane) vbxProjectCard.lookup("#pneImagePlaceholder");

        if (lblProjectName != null) {
            lblProjectName.setText(project.getName());
        }

        if (lblMethodology != null) {
            lblMethodology.setText(project.getMethodology());
        }

        if (lblDescription != null) {
            lblDescription.setText(project.getGeneralDescription());
        }

        if (lblObjective != null) {
            String obj = project.getGeneralObjective();
            if (obj != null && obj.length() > 40) {
                obj = obj.substring(0, 37) + "...";
            }
            lblObjective.setText(obj);
        }

        if (lblOrganization != null) {
            String orgName = organizationNameMap.get(project.getLinkedOrganizationId());
            lblOrganization.setText(orgName != null ? orgName : "No asignada");
        }

        if (lblResponsible != null) {
            String respName = responsibleNameMap.get(project.getProjectResponsibleId());
            lblResponsible.setText(respName != null ? respName : "No asignado");
        }

        // Set dynamic visual theme based on project id
        if (pneImagePlaceholder != null && lblInitials != null) {
            int idVal = project.getId() != null ? project.getId() : 0;
            String[] gradients = {
                "-fx-background-color: linear-gradient(to bottom right, #e0e7ff, #c7d2fe);", // Indigo
                "-fx-background-color: linear-gradient(to bottom right, #d1fae5, #a7f3d0);", // Emerald
                "-fx-background-color: linear-gradient(to bottom right, #fef3c7, #fde68a);", // Amber
                "-fx-background-color: linear-gradient(to bottom right, #ffe4e6, #fecdd3);", // Rose
                "-fx-background-color: linear-gradient(to bottom right, #e0f2fe, #bae6fd);", // Sky
                "-fx-background-color: linear-gradient(to bottom right, #f3e8ff, #e9d5ff);"  // Purple
            };
            String[] textColors = {
                "-fx-text-fill: #4f46e5;",
                "-fx-text-fill: #059669;",
                "-fx-text-fill: #d97706;",
                "-fx-text-fill: #e11d48;",
                "-fx-text-fill: #0284c7;",
                "-fx-text-fill: #7c3aed;"
            };
            int index = idVal % gradients.length;
            pneImagePlaceholder.setStyle(gradients[index] + " -fx-background-radius: 16 0 0 16; -fx-border-radius: 16 0 0 16;");
            
            if (project.getName() != null && !project.getName().isEmpty()) {
                lblInitials.setText(project.getName().substring(0, 1).toUpperCase());
            }
            lblInitials.setStyle(textColors[index] + " -fx-font-size: 40px; -fx-font-weight: bold; -fx-opacity: 0.85;");
        }

        setProjectStatusLabel(lblStatus, project);
        
        List<Permission> userPermissions = SessionManager.getInstance().getPermissions();
        if (userPermissions.contains(Permission.UPDATE_PROJECT)) {
            configureEditButton(btnEditProject, project);
        } else {
            if (btnEditProject != null) {
                btnEditProject.setVisible(false);
                btnEditProject.setManaged(false);
            }
        }
        
        if (userPermissions.contains(Permission.REQUEST_PROJECT)) {
            if (btnRequestProject != null) {
                btnRequestProject.setVisible(true);
                btnRequestProject.setManaged(true);
                btnRequestProject.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Solicitud");
                    alert.setHeaderText(null);
                    alert.setContentText("Has solicitado el proyecto: " + project.getName());
                    alert.showAndWait();
                });
            }
        } else {
            if (btnRequestProject != null) {
                btnRequestProject.setVisible(false);
                btnRequestProject.setManaged(false);
            }
        }

        if (btnViewProject != null) {
            btnViewProject.setUserData(project);
            btnViewProject.setOnAction(event -> {
                Button btn = (Button) event.getSource();
                ProjectDTO proj = (ProjectDTO) btn.getUserData();
                selectedProject = Optional.of(proj);
                WindowManagerController.changeView("ProjectDetailsDashboard.fxml");
            });
        }
    }

    private void setProjectStatusLabel(
            Label lblStatus,
            ProjectDTO project
    ) {
        if (lblStatus != null) {
            if (Boolean.TRUE.equals(project.getIsActive())) {
                lblStatus.setText("Activo");
                lblStatus.setStyle(
                    "-fx-background-color: #dcfce7; " +
                    "-fx-text-fill: #166534; " +
                    "-fx-font-size: 11px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 20; " +
                    "-fx-padding: 3 8 3 8;"
                );
            } else {
                lblStatus.setText("Inactivo");
                lblStatus.setStyle(
                    "-fx-background-color: #fee2e2; " +
                    "-fx-text-fill: #991b1b; " +
                    "-fx-font-size: 11px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 20; " +
                    "-fx-padding: 3 8 3 8;"
                );
            }
        }
    }

    private void configureEditButton(
            Button btnEditProject,
            ProjectDTO project
    ) {
        btnEditProject.setUserData(project);
        btnEditProject.setOnAction(
            new EditProjectEventHandler()
        );
    }

    private void showEmptyProjectsMessage() {
        Label lblEmptyProjects = new Label(
            "No hay proyectos registrados."
        );

        lblEmptyProjects.setStyle(
            "-fx-font-size: 15px;"
            + "-fx-text-fill: #64748b;"
            + "-fx-padding: 24;"
        );

        vbxProjectsContainer.getChildren().add(
            lblEmptyProjects
        );
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("Ocurrió un problema");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private final class EditProjectEventHandler
            implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            handleBtnEditProject(event);
        }
    }
}