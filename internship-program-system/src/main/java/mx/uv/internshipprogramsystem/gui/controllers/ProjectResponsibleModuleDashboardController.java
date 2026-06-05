package mx.uv.internshipprogramsystem.gui.controllers;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.ProjectResponsibleManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;

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
        try {
            validatePermission(
                Permission.CONSULT_PROJECT_RESPONSIBLE
            );

            projectResponsibleManager =
                new ProjectResponsibleManager();

            configureTable();

            configureTableSize();

            loadProjectResponsibles();

            LOGGER.info(
                "Módulo de responsables de proyecto cargado correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado al módulo de responsables de proyecto.",
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
        try {
            validatePermission(
                Permission.CONSULT_PROJECT_RESPONSIBLE
            );

            String searchText =
                txtSearch.getText().trim();

            if (searchText.isEmpty()) {
                loadProjectResponsibles();
            } else {
                searchProjectResponsibles(
                    searchText
                );
            }
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado a la búsqueda de responsables.",
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                businessException.getMessage()
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
        openViewWithPermission(
            Permission.REGISTER_PROJECT_RESPONSIBLE,
            "ProjectResponsibleRegisterDashboard.fxml",
            "Acceso denegado al registro de responsables."
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
        try {
            validatePermission(
                Permission.CONSULT_PROJECT_RESPONSIBLE
            );

            List<ProjectResponsibleDTO> projectResponsibles =
                projectResponsibleManager.getAllProjectResponsibles();

            showProjectResponsiblesInTable(
                projectResponsibles
            );

            txtSearch.clear();
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al consultar responsables.",
                businessException
            );

            FormAlertSupport.showError(
                "Error al consultar responsables",
                businessException.getMessage()
            );
        }
    }

    private void searchProjectResponsibles(
            String searchText
    ) {
        try {
            validatePermission(
                Permission.CONSULT_PROJECT_RESPONSIBLE
            );

            List<ProjectResponsibleDTO> projectResponsibles =
                projectResponsibleManager.searchProjectResponsibles(
                    searchText
                );

            showProjectResponsiblesInTable(
                projectResponsibles
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al buscar responsables.",
                businessException
            );

            FormAlertSupport.showError(
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

            LOGGER.info(
                "Acceso permitido a la vista {}.",
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