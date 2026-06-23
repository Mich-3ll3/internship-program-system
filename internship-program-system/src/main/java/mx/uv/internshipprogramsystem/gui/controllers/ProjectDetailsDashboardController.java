package mx.uv.internshipprogramsystem.gui.controllers;
 
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
 
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.util.List;
import mx.uv.internshipprogramsystem.logic.dao.ProjectActivityDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectActivityDTO;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectResponsibleDAO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
 
public class ProjectDetailsDashboardController implements Initializable {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ProjectDetailsDashboardController.class);
 
    @FXML
    private Button btnBack;
 
    @FXML
    private StackPane pneHeaderCover;
 
    @FXML
    private Label lblLargeInitial;
 
    @FXML
    private Label lblStatus;
 
    @FXML
    private Label lblProjectName;
 
    @FXML
    private Label lblOrganization;
 
    @FXML
    private Label lblResponsible;
 
    @FXML
    private Label lblMethodology;
 
    @FXML
    private Label lblDescription;
 
    @FXML
    private Label lblGeneralObjective;
 
    @FXML
    private Label lblImmediateObjectives;
 
    @FXML
    private Label lblMediateObjectives;
 
    @FXML
    private FlowPane fpnResources;
 
    @FXML
    private Label lblResponsibilities;

    @FXML
    private VBox vbxActivitiesContainer;
 
    private final LinkedOrganizationDAO linkedOrganizationDAO = new LinkedOrganizationDAO();
    private final ProjectResponsibleDAO projectResponsibleDAO = new ProjectResponsibleDAO();
 
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Optional<ProjectDTO> projectOpt = ProjectsModuleDashboardController.getSelectedProject();
        if (projectOpt.isPresent()) {
            showProjectDetails(projectOpt.get());
        } else {
            LOGGER.warn("No se seleccionó ningún proyecto para ver detalles. Volviendo al listado.");
            handleBtnBack();
        }
    }
 
    private void showProjectDetails(ProjectDTO project) {
        // Name, description, objectives, methodology, duration, responsibilities
        lblProjectName.setText(project.getName());
        lblDescription.setText(project.getGeneralDescription());
        lblGeneralObjective.setText(project.getGeneralObjective());
 
        lblImmediateObjectives.setText(
            project.getImmediateObjectives() != null && !project.getImmediateObjectives().isBlank()
            ? project.getImmediateObjectives() : "No especificados"
        );
 
        lblMediateObjectives.setText(
            project.getMediateObjective() != null && !project.getMediateObjective().isBlank()
            ? project.getMediateObjective() : "No especificados"
        );
 
        lblMethodology.setText(project.getMethodology());
 
        lblResponsibilities.setText(
            project.getResponsibilities() != null && !project.getResponsibilities().isBlank()
            ? project.getResponsibilities() : "No especificadas"
        );
 
        if (project.getName() != null && !project.getName().isEmpty()) {
            lblLargeInitial.setText(project.getName().substring(0, 1).toUpperCase());
        }
 
        // Style status badge
        if (Boolean.TRUE.equals(project.getIsActive())) {
            lblStatus.setText("PUBLICADO/ACTIVO");
            lblStatus.setStyle(
                "-fx-background-color: #dcfce7; " +
                "-fx-text-fill: #166534; " +
                "-fx-font-size: 11px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 20; " +
                "-fx-padding: 4 10 4 10;"
            );
        } else {
            lblStatus.setText("INACTIVO");
            lblStatus.setStyle(
                "-fx-background-color: #fee2e2; " +
                "-fx-text-fill: #991b1b; " +
                "-fx-font-size: 11px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 20; " +
                "-fx-padding: 4 10 4 10;"
            );
        }
 
        // Set header cover visual gradient
        int idVal = project.getId() != null ? project.getId() : 0;
        String[] gradients = {
            "-fx-background-color: linear-gradient(to bottom right, #3b82f6, #1d4ed8);", // Blue
            "-fx-background-color: linear-gradient(to bottom right, #10b981, #047857);", // Emerald
            "-fx-background-color: linear-gradient(to bottom right, #f59e0b, #b45309);", // Amber
            "-fx-background-color: linear-gradient(to bottom right, #f43f5e, #be123c);", // Rose
            "-fx-background-color: linear-gradient(to bottom right, #0ea5e9, #0369a1);", // Sky
            "-fx-background-color: linear-gradient(to bottom right, #8b5cf6, #5b21b6);"  // Purple
        };
        int index = idVal % gradients.length;
        pneHeaderCover.setStyle(gradients[index] + " -fx-background-radius: 12;");
 
        // Populate resource tag chips
        fpnResources.getChildren().clear();
        String resourcesText = project.getResources();
        if (resourcesText != null && !resourcesText.isBlank()) {
            String[] splitResources = resourcesText.split(",|;|\n");
            for (String res : splitResources) {
                String cleanRes = res.trim();
                if (!cleanRes.isEmpty()) {
                    Label chip = new Label(cleanRes);
                    chip.setStyle(
                        "-fx-background-color: #f1f5f9; " +
                        "-fx-text-fill: #475569; " +
                        "-fx-font-size: 11px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8; " +
                        "-fx-padding: 4 10 4 10;"
                    );
                    fpnResources.getChildren().add(chip);
                }
            }
        } else {
            Label noResourcesLabel = new Label("Ninguno");
            noResourcesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
            fpnResources.getChildren().add(noResourcesLabel);
        }
 
        // Fetch organization and responsible names asynchronously / gracefully
        try {
            // Find organization name
            Integer orgId = project.getLinkedOrganizationId();
            if (orgId != null) {
                Optional<LinkedOrganizationDTO> orgOpt = linkedOrganizationDAO.findAll().stream()
                    .filter(org -> org.getId() == orgId)
                    .findFirst();
                if (orgOpt.isPresent()) {
                    lblOrganization.setText(orgOpt.get().getName());
                } else {
                    lblOrganization.setText("ID Organización: " + orgId);
                }
            } else {
                lblOrganization.setText("Sin organización");
            }
 
            // Find responsible name
            Integer respId = project.getProjectResponsibleId();
            if (respId != null) {
                Optional<ProjectResponsibleDTO> respOpt = projectResponsibleDAO.findById(respId);
                if (respOpt.isPresent()) {
                    lblResponsible.setText(respOpt.get().getFullName());
                } else {
                    lblResponsible.setText("ID Responsable: " + respId);
                }
            } else {
                lblResponsible.setText("Sin responsable");
            }
        } catch (BusinessException | DataAccessException e) {
            LOGGER.error("Error cargando detalles relacionales del proyecto", e);
            lblOrganization.setText("Error cargando organización");
            lblResponsible.setText("Error cargando responsable");
        }

        // Populate activities
        vbxActivitiesContainer.getChildren().clear();
        try {
            ProjectActivityDAO projectActivityDAO = new ProjectActivityDAO();
            List<ProjectActivityDTO> activities = projectActivityDAO.findByProjectId(project.getId());
            if (activities.isEmpty()) {
                Label lblNoActivities = new Label("Sin actividades registradas en el plan de trabajo.");
                lblNoActivities.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-style: italic;");
                vbxActivitiesContainer.getChildren().add(lblNoActivities);
            } else {
                for (ProjectActivityDTO act : activities) {
                    HBox actRow = new HBox();
                    actRow.setSpacing(15);
                    actRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    actRow.setStyle("-fx-padding: 4 0 4 0;");

                    Label lblActName = new Label("• " + act.getName());
                    lblActName.setStyle("-fx-font-size: 13px; -fx-text-fill: #1e293b; -fx-font-weight: bold;");
                    lblActName.setPrefWidth(280);
                    lblActName.setWrapText(true);

                    Label lblActMonth = new Label("Mes: " + act.getMonth());
                    lblActMonth.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");
                    lblActMonth.setPrefWidth(120);

                    Label lblActWeeks = new Label("Semanas: " + act.getStartWeek() + " - " + act.getEndWeek());
                    lblActWeeks.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");
                    lblActWeeks.setPrefWidth(120);

                    Label lblActHours = new Label("Horas planeadas: " + act.getPlannedHours());
                    lblActHours.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");
                    lblActHours.setPrefWidth(150);

                    actRow.getChildren().addAll(lblActName, lblActMonth, lblActWeeks, lblActHours);
                    vbxActivitiesContainer.getChildren().add(actRow);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error al cargar actividades para los detalles del proyecto", e);
            Label lblError = new Label("Error al cargar las actividades del plan de trabajo.");
            lblError.setStyle("-fx-font-size: 13px; -fx-text-fill: #991b1b;");
            vbxActivitiesContainer.getChildren().add(lblError);
        }
    }
 
    @FXML
    private void handleBtnBack() {
        WindowManagerController.changeView("ProjectsModuleDashboard.fxml");
    }
}
