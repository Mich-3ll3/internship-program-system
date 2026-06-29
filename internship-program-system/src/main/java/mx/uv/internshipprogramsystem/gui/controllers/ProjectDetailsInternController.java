package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectActivityDTO;

public class ProjectDetailsInternController implements Initializable {

    private static final String DEFAULT_DESCRIPTION = "No especificada";
    private static final String DEFAULT_OBJECTIVES = "No especificados";
    private static final String DEFAULT_METHODOLOGY = "No especificada";
    private static final String DEFAULT_RESPONSIBILITIES = "No especificadas";
    private static final String DEFAULT_NAME = "Nombre no disponible";
    private static final String DEFAULT_ORGANIZATION = "Organización no disponible";

    @FXML private Label lblProjectName;
    @FXML private Label lblOrganization;
    @FXML private Label lblDescription;
    @FXML private Label lblObjectives;
    @FXML private Label lblMethodology;
    @FXML private Label lblResponsibilities;
    @FXML private VBox vboxActivitiesList;
    @FXML private Button btnClose;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void initData(ProjectDTO project) {
        if (project != null) {
            setLabelContent(lblProjectName, project.getName(), DEFAULT_NAME);
            setLabelContent(lblOrganization, project.getOrganizationName(), DEFAULT_ORGANIZATION);
            
            setLabelContent(lblDescription, project.getGeneralDescription(), DEFAULT_DESCRIPTION);
            setLabelContent(lblObjectives, project.getGeneralObjective(), DEFAULT_OBJECTIVES);
            setLabelContent(lblMethodology, project.getMethodology(), DEFAULT_METHODOLOGY);
            setLabelContent(lblResponsibilities, project.getResponsibilities(), DEFAULT_RESPONSIBILITIES);

            loadActivities(project.getPlannedActivities());
        }
    }

    private void setLabelContent(Label label, String content, String defaultContent) {
        if (content != null) {
            label.setText(content);
        } else {
            label.setText(defaultContent);
        }
    }

    private void loadActivities(List<ProjectActivityDTO> activities) {
        vboxActivitiesList.getChildren().clear(); 

        if (activities == null || activities.isEmpty()) {
            Label lblEmpty = new Label("No hay un cronograma de actividades registrado para este proyecto.");
            lblEmpty.setTextFill(Color.web("#6b7280"));
            vboxActivitiesList.getChildren().add(lblEmpty);
            return;
        }

        for (ProjectActivityDTO activity : activities) {
            HBox row = new HBox();
            row.setSpacing(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label lblName = new Label("• " + activity.getName());
            lblName.setTextFill(Color.web("#374151"));
            lblName.setWrapText(true);
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            String periodoTexto = activity.getMonth() + " (Sem " + activity.getStartWeek() + " a " + activity.getEndWeek() + ") • " + activity.getPlannedHours() + " hrs";
            
            Label lblPeriod = new Label(periodoTexto);
            lblPeriod.setStyle("-fx-background-color: #e0f2fe; -fx-text-fill: #0284c7; -fx-padding: 2 8 2 8; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 11px;");

            row.getChildren().addAll(lblName, spacer, lblPeriod);
            vboxActivitiesList.getChildren().add(row);
        }
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Stage currentStage = (Stage) btnClose.getScene().getWindow();
        currentStage.close();
    }
}