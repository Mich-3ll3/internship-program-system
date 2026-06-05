package mx.uv.internshipprogramsystem.gui.controllers;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;

public class ProfessorManagementController {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ProfessorManagementController.class
        );

    @FXML
    private TextField txtSearchStaffNumber;

    @FXML
    private TableView<ProfessorDTO> tblProfessors;

    @FXML
    private TableColumn<ProfessorDTO, String> colStaffNumber;

    @FXML
    private TableColumn<ProfessorDTO, String> colName;

    @FXML
    private TableColumn<ProfessorDTO, String> colEmail;

    @FXML
    private TableColumn<ProfessorDTO, String> colCoordinator;

    @FXML
    private TableColumn<ProfessorDTO, String> colStatus;

    private final ProfessorDAO professorDAO =
        new ProfessorDAO();

    private final UserDAO userDAO =
        new UserDAO();

    private final ObservableList<ProfessorDTO> masterData =
        FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            validatePermission(
                Permission.CONSULT_PROFESSOR
            );

            configureTableColumns();

            loadProfessorsData();

            LOGGER.info(
                "Módulo de gestión de profesores cargado correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado al módulo de gestión de profesores.",
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                businessException.getMessage()
            );

            WindowManagerController.changeView(
                "AdminHomeDashboard.fxml"
            );
        }
    }

    private void configureTableColumns() {
        colStaffNumber.setCellValueFactory(
            new PropertyValueFactory<ProfessorDTO, String>(
                "staffNumber"
            )
        );

        colName.setCellValueFactory(
            new PropertyValueFactory<ProfessorDTO, String>(
                "fullName"
            )
        );

        colEmail.setCellValueFactory(
            new PropertyValueFactory<ProfessorDTO, String>(
                "institutionalEmail"
            )
        );

        colCoordinator.setCellValueFactory(
            new PropertyValueFactory<ProfessorDTO, String>(
                "coordinator"
            )
        );

        colStatus.setCellValueFactory(
            new PropertyValueFactory<ProfessorDTO, String>(
                "active"
            )
        );
    }

    private void loadProfessorsData() {
        try {
            validatePermission(
                Permission.CONSULT_PROFESSOR
            );

            List<ProfessorDTO> professors =
                professorDAO.findAll();

            masterData.setAll(
                professors
            );

            tblProfessors.setItems(
                masterData
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al cargar profesores.",
                businessException
            );

            FormAlertSupport.showError(
                "Error de carga",
                "No se pudo cargar la lista de profesores."
            );
        }
    }

    @FXML
    private void handleLiveSearchProfessor(
            KeyEvent event
    ) {
        searchProfessor();
    }

    @FXML
    private void searchProfessor() {
        try {
            validatePermission(
                Permission.CONSULT_PROFESSOR
            );

            String searchValue =
                txtSearchStaffNumber.getText().trim().toLowerCase();

            if (searchValue.isBlank()) {
                tblProfessors.setItems(
                    masterData
                );
            } else {
                filterProfessors(
                    searchValue
                );
            }
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Búsqueda de profesor inválida.",
                businessException
            );

            FormAlertSupport.showError(
                "Error de búsqueda",
                businessException.getMessage()
            );
        }
    }

    private void filterProfessors(
            String searchValue
    ) {
        ObservableList<ProfessorDTO> filteredProfessors =
            FXCollections.observableArrayList();

        if (searchValue.isEmpty()) {
            filteredProfessors.setAll(
                masterData
            );
        } else {
            addMatchingProfessors(
                filteredProfessors,
                searchValue
            );
        }

        tblProfessors.setItems(
            filteredProfessors
        );
    }

    private void addMatchingProfessors(
            ObservableList<ProfessorDTO> filteredProfessors,
            String searchValue
    ) {
        for (ProfessorDTO professor : masterData) {
            if (containsSearchValue(
                    professor,
                    searchValue
            )) {
                filteredProfessors.add(
                    professor
                );
            }
        }
    }

    private boolean containsSearchValue(
            ProfessorDTO professor,
            String searchValue
    ) {
        boolean containsValue =
            false;

        String staffNumber =
            getTextValue(
                professor.getStaffNumber()
            );

        String email =
            getTextValue(
                professor.getInstitutionalEmail()
            );

        String fullName =
            getTextValue(
                professor.getFullName()
            );

        if (staffNumber.contains(searchValue)
                || email.contains(searchValue)
                || fullName.contains(searchValue)) {
            containsValue =
                true;
        }

        return containsValue;
    }

    private String getTextValue(
            String text
    ) {
        String value =
            "";

        if (text != null) {
            value =
                text.toLowerCase();
        }

        return value;
    }

    @FXML
    private void refreshProfessors() {
        txtSearchStaffNumber.clear();

        loadProfessorsData();
    }

    @FXML
    private void handleChangeProfessorStatus() {
        try {
            validatePermission(
                Permission.CHANGE_PROFESSOR_STATUS
            );

            ProfessorDTO professor =
                getSelectedProfessor();

            changeProfessorStatus(
                professor
            );

            FormAlertSupport.showInformation(
                "Estado actualizado",
                "El estado del profesor fue actualizado correctamente."
            );

            loadProfessorsData();
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al cambiar estado del profesor.",
                businessException
            );

            FormAlertSupport.showError(
                "Error",
                businessException.getMessage()
            );
        }
    }

    private ProfessorDTO getSelectedProfessor()
            throws BusinessException {
        ProfessorDTO professor =
            tblProfessors
                .getSelectionModel()
                .getSelectedItem();

        if (professor == null) {
            throw new BusinessException(
                "Seleccione un profesor de la tabla."
            );
        }

        return professor;
    }

    private void changeProfessorStatus(
            ProfessorDTO professor
    ) throws BusinessException {
        boolean newStatus =
            !professor.getIsActive();

        boolean wasChanged =
            userDAO.changeStatus(
                professor.getId(),
                newStatus
            );

        if (!wasChanged) {
            throw new BusinessException(
                "No se pudo cambiar el estado del profesor."
            );
        }
    }

    @FXML
    private void goUpdateProfessor() {
        try {
            validatePermission(
                Permission.UPDATE_PROFESSOR
            );

            ProfessorDTO selectedProfessor =
                getSelectedProfessor();

            WindowManagerController.changeViewToUpdateProfessor(
                "UpdateProfessorDashboard.fxml",
                selectedProfessor
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "No se seleccionó profesor para modificación.",
                businessException
            );

            FormAlertSupport.showWarning(
                "Selección requerida",
                businessException.getMessage()
            );
        }
    }

    @FXML
    private void goHome(
            ActionEvent event
    ) {
        WindowManagerController.changeView(
            "AdminHomeDashboard.fxml"
        );
    }

    @FXML
    private void goProfessorModule(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.CONSULT_PROFESSOR,
            "ProfessorModuleDashboard.fxml",
            "Acceso denegado al módulo de profesores."
        );
    }

    @FXML
    private void goRegisterProfessor(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.REGISTER_PROFESSOR,
            "RegisterProfessorDashboard.fxml",
            "Acceso denegado al registro de profesores."
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