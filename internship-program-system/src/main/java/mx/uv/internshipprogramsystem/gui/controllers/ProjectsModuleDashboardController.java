package mx.uv.internshipprogramsystem.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.ProjectManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;

public class ProjectsModuleDashboardController
        implements Initializable, EventHandler<ActionEvent> {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ProjectsModuleDashboardController.class
        );

    private static final String PROJECT_CARD_FXML_PATH =
        "/mx/uv/internshipprogramsystem/gui/fxml/ProjectCard.fxml";

    @FXML
    private Button btnAddProject;

    @FXML
    private ScrollPane scpProjects;

    @FXML
    private VBox vbxProjectsContainer;

    private ProjectManager projectManager;

    @Override
    public void initialize(
            URL url,
            ResourceBundle resourceBundle
    ) {
        try {
            validatePermission(
                Permission.CONSULT_PROJECT
            );

            projectManager =
                new ProjectManager();

            loadProjects();
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado al módulo de proyectos.",
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

    @Override
    public void handle(
            ActionEvent event
    ) {
        handleBtnEditProject(
            event
        );
    }

    @FXML
    private void handleBtnAddProject(
            ActionEvent event
    ) {
        try {
            validatePermission(
                Permission.REGISTER_PROJECT
            );

            WindowManagerController.changeView(
                "ProjectRegisterDashboard.fxml"
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
        }
    }

    @FXML
    private void handleBtnEditProject(
            ActionEvent event
    ) {
        try {
            validatePermission(
                Permission.UPDATE_PROJECT
            );

            Button btnEditProject =
                (Button) event.getSource();

            ProjectDTO project =
                (ProjectDTO) btnEditProject.getUserData();

            WindowManagerController.changeViewToUpdateProject(
                "ProjectUpdateDashboard.fxml",
                project
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado a la modificación de proyectos.",
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                businessException.getMessage()
            );
        }
    }

    private void loadProjects() {
        try {
            List<ProjectDTO> projects =
                projectManager.findAllProjects();

            vbxProjectsContainer.getChildren().clear();

            if (projects.isEmpty()) {
                showEmptyProjectsMessage();
            } else {
                showProjectCards(
                    projects
                );
            }
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error cargando proyectos.",
                businessException
            );

            FormAlertSupport.showError(
                "Error",
                "No se pudieron cargar los proyectos."
            );
        }
    }

    private void showProjectCards(
            List<ProjectDTO> projects
    ) {
        for (ProjectDTO project : projects) {
            loadProjectCard(
                project
            );
        }
    }

    private void loadProjectCard(
            ProjectDTO project
    ) {
        try {
            FXMLLoader loader =
                new FXMLLoader(
                    getClass().getResource(
                        PROJECT_CARD_FXML_PATH
                    )
                );

            VBox vbxProjectCard =
                loader.load();

            fillProjectCard(
                vbxProjectCard,
                project
            );

            vbxProjectsContainer
                .getChildren()
                .add(
                    vbxProjectCard
                );
        } catch (IOException ioException) {
            LOGGER.error(
                "Error cargando tarjeta de proyecto.",
                ioException
            );

            FormAlertSupport.showError(
                "Error",
                "No se pudo cargar una tarjeta de proyecto."
            );
        }
    }

    private void fillProjectCard(
            VBox vbxProjectCard,
            ProjectDTO project
    ) {
        Label lblProjectName =
            (Label) vbxProjectCard.lookup(
                "#lblProjectName"
            );

        Label lblMethodology =
            (Label) vbxProjectCard.lookup(
                "#lblMethodology"
            );

        Label lblDescription =
            (Label) vbxProjectCard.lookup(
                "#lblDescription"
            );

        Label lblOrganization =
            (Label) vbxProjectCard.lookup(
                "#lblOrganization"
            );

        Label lblResponsible =
            (Label) vbxProjectCard.lookup(
                "#lblResponsible"
            );

        Label lblDuration =
            (Label) vbxProjectCard.lookup(
                "#lblDuration"
            );

        Label lblStatus =
            (Label) vbxProjectCard.lookup(
                "#lblStatus"
            );

        Button btnEditProject =
            (Button) vbxProjectCard.lookup(
                "#btnEditProject"
            );

        lblProjectName.setText(
            project.getName()
        );

        lblMethodology.setText(
            "Metodología: "
                + project.getMethodology()
        );

        lblDescription.setText(
            project.getGeneralDescription()
        );

        lblOrganization.setText(
            "Organización: "
                + project.getLinkedOrganizationId()
        );

        lblResponsible.setText(
            "Responsable: "
                + project.getProjectResponsibleId()
        );

        lblDuration.setText(
            "Duración: "
                + project.getDuration()
                + " meses"
        );

        setProjectStatusLabel(
            lblStatus,
            project
        );

        configureEditButton(
            btnEditProject,
            project
        );
    }

    private void setProjectStatusLabel(
            Label lblStatus,
            ProjectDTO project
    ) {
        if (Boolean.TRUE.equals(project.getIsActive())) {
            lblStatus.setText(
                "Activo"
            );

            lblStatus.setStyle(
                "-fx-background-color: #dcfce7;"
                    + "-fx-background-radius: 10;"
                    + "-fx-padding: 8 12 8 12;"
                    + "-fx-font-size: 13px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-text-fill: #166534;"
            );
        } else {
            lblStatus.setText(
                "Inactivo"
            );

            lblStatus.setStyle(
                "-fx-background-color: #fee2e2;"
                    + "-fx-background-radius: 10;"
                    + "-fx-padding: 8 12 8 12;"
                    + "-fx-font-size: 13px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-text-fill: #991b1b;"
            );
        }
    }

    private void configureEditButton(
            Button btnEditProject,
            ProjectDTO project
    ) {
        btnEditProject.setUserData(
            project
        );

        btnEditProject.setOnAction(
            this
        );
    }

    private void showEmptyProjectsMessage() {
        Label lblEmptyProjects =
            new Label(
                "No hay proyectos registrados."
            );

        lblEmptyProjects.setStyle(
            "-fx-font-size: 15px;"
                + "-fx-text-fill: #64748b;"
                + "-fx-padding: 24;"
        );

        vbxProjectsContainer
            .getChildren()
            .add(
                lblEmptyProjects
            );
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