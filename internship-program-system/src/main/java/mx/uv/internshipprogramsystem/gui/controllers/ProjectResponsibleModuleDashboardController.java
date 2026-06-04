package mx.uv.internshipprogramsystem.gui.controllers;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.ProjectResponsibleManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class ProjectResponsibleModuleDashboardController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ProjectResponsibleModuleDashboardController.class
        );

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnShowAll;

    @FXML
    private Button btnNewResponsible;

    @FXML
    private TableView<ProjectResponsibleDTO> tblProjectResponsibles;

    @FXML
    private TableColumn<ProjectResponsibleDTO, String> colFullName;

    @FXML
    private TableColumn<ProjectResponsibleDTO, String> colEmail;

    @FXML
    private TableColumn<ProjectResponsibleDTO, String> colPosition;

    @FXML
    private TableColumn<ProjectResponsibleDTO, String> colOrganization;

    @FXML
    private TableColumn<ProjectResponsibleDTO, String> colProject;

    private ProjectResponsibleManager projectResponsibleManager;

    @FXML
    private void initialize() {
        projectResponsibleManager =
            new ProjectResponsibleManager();

        configureTable();
        configureTableSize();
        loadProjectResponsibles();
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
        WindowManagerController.changeView(
            "EducationalExperienceRegisterDashboard.fxml"
        );
    }

    @FXML
    private void goInternModule(
            ActionEvent event
    ) {
        WindowManagerController.changeView(
            "InternModuleDashboard.fxml"
        );
    }

    @FXML
    private void goLinkedOrganizationModule(
            ActionEvent event
    ) {
        WindowManagerController.changeView(
            "LinkedOrganizationManagementGUI.fxml"
        );
    }

    @FXML
    private void goProjectsModule(
            ActionEvent event
    ) {
        LOGGER.info(
            "Acceso al módulo de proyectos."
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
        LOGGER.info(
            "Acceso al módulo de reportes."
        );
    }

    @FXML
    private void goProjectRequestModule(
            ActionEvent event
    ) {
        LOGGER.info(
            "Acceso al módulo de solicitudes."
        );
    }

    @FXML
    private void goTrackingModule(
            ActionEvent event
    ) {
        LOGGER.info(
            "Acceso al módulo de seguimiento."
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

    @FXML
    private void handleSearchResponsible(
            ActionEvent event
    ) {
        String searchText =
            txtSearch.getText().trim();

        if (searchText.isEmpty()) {
            loadProjectResponsibles();
        } else {
            searchProjectResponsibles(
                searchText
            );
        }
    }

    @FXML
    private void handleShowAllResponsibles(
            ActionEvent event
    ) {
        loadProjectResponsibles();
    }

    @FXML
    private void handleRegisterResponsible(
            ActionEvent event
    ) {
        WindowManagerController.changeView(
            "ProjectResponsibleRegisterDashboard.fxml"
        );
    }

    private void configureTable() {
        colFullName.setCellValueFactory(
            new PropertyValueFactory<ProjectResponsibleDTO, String>(
                "fullName"
            )
        );

        colEmail.setCellValueFactory(
            new PropertyValueFactory<ProjectResponsibleDTO, String>(
                "email"
            )
        );

        colPosition.setCellValueFactory(
            new PropertyValueFactory<ProjectResponsibleDTO, String>(
                "position"
            )
        );

        colOrganization.setCellValueFactory(
            new PropertyValueFactory<ProjectResponsibleDTO, String>(
                "organizationName"
            )
        );

        colProject.setCellValueFactory(
            new PropertyValueFactory<ProjectResponsibleDTO, String>(
                "projectName"
            )
        );
    }

    private void configureTableSize() {
        tblProjectResponsibles.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );
    }

    private void loadProjectResponsibles() {
        List<ProjectResponsibleDTO> projectResponsibles;

        try {
            projectResponsibles =
                projectResponsibleManager.getAllProjectResponsibles();

            showProjectResponsiblesInTable(
                projectResponsibles
            );

            txtSearch.clear();
        } catch (BusinessException businessException) {
            showErrorAlert(
                "Error al consultar responsables",
                businessException.getMessage()
            );
        }
    }

    private void searchProjectResponsibles(
            String searchText
    ) {
        List<ProjectResponsibleDTO> projectResponsibles;

        try {
            projectResponsibles =
                projectResponsibleManager.searchProjectResponsibles(
                    searchText
                );

            showProjectResponsiblesInTable(
                projectResponsibles
            );
        } catch (BusinessException businessException) {
            showErrorAlert(
                "Error al buscar responsables",
                businessException.getMessage()
            );
        }
    }

    private void showProjectResponsiblesInTable(
            List<ProjectResponsibleDTO> projectResponsibles
    ) {
        ObservableList<ProjectResponsibleDTO> observableProjectResponsibles =
            FXCollections.observableArrayList(
                projectResponsibles
            );

        tblProjectResponsibles.setItems(
            observableProjectResponsibles
        );
    }

    private void showErrorAlert(
            String title,
            String message
    ) {
        Alert alert =
            new Alert(
                Alert.AlertType.ERROR
            );

        alert.setTitle(
            title
        );

        alert.setHeaderText(
            null
        );

        alert.setContentText(
            message
        );

        alert.showAndWait();
    }
}