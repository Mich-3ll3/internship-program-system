package mx.uv.internshipprogramsystem.gui.controllers;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class ProfessorManagementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ProfessorManagementController.class);

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



    private final ProfessorDAO professorDAO = new ProfessorDAO();

    private final UserDAO userDAO = new UserDAO();

    private final ObservableList<ProfessorDTO> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTableColumns();
        loadProfessorsData();

        LOGGER.info(
                "Modulo de gestion de profesores cargado correctamente.");
    }

    private void configureTableColumns() {
        tblProfessors.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        colStaffNumber.setCellValueFactory(
                new PropertyValueFactory<ProfessorDTO, String>(
                        "staffNumber"));

        colName.setCellValueFactory(
                new PropertyValueFactory<ProfessorDTO, String>(
                        "fullName"));

        colEmail.setCellValueFactory(
                new PropertyValueFactory<ProfessorDTO, String>(
                        "institutionalEmail"));

        colCoordinator.setCellValueFactory(
                new PropertyValueFactory<ProfessorDTO, String>(
                        "coordinator"));

        colStatus.setCellValueFactory(
                new PropertyValueFactory<ProfessorDTO, String>(
                        "active"));
    }



    private void loadProfessorsData() {
        try {
            List<ProfessorDTO> professors = professorDAO.findAll();

            masterData.setAll(
                    professors);

            tblProfessors.setItems(
                    masterData);
        } catch (BusinessException businessException) {
            LOGGER.error(
                    "Error al cargar profesores.",
                    businessException);

            showNotification(
                    Alert.AlertType.ERROR,
                    "Error de carga",
                    "No se pudo cargar la lista de profesores.");
        } catch (DataAccessException dataAccessException) {
            LOGGER.error(
                    "Error de conexion al cargar profesores.",
                    dataAccessException);

            showNotification(
                    Alert.AlertType.ERROR,
                    "Error de conexion",
                    "No se pudo conectar con la base de datos para cargar los profesores. Por favor intente mas tarde.");
        }
    }

    @FXML
    private void handleLiveSearchProfessor(
            KeyEvent event) {
        searchProfessor();
    }

    @FXML
    private void searchProfessor() {
        String searchValue = txtSearchStaffNumber.getText()
                .trim()
                .toLowerCase();

        if (searchValue.isBlank()) {
            tblProfessors.setItems(
                    masterData);
        } else {
            filterProfessors(
                    searchValue);
        }
    }

    private void filterProfessors(
            String searchValue) {
        ObservableList<ProfessorDTO> filteredProfessors = FXCollections.observableArrayList();

        for (ProfessorDTO professor : masterData) {
            if (containsSearchValue(
                    professor,
                    searchValue)) {
                filteredProfessors.add(
                        professor);
            }
        }

        tblProfessors.setItems(
                filteredProfessors);
    }

    private boolean containsSearchValue(
            ProfessorDTO professor,
            String searchValue) {
        String searchableText = String.join(
                " ",
                getTextValue(professor.getStaffNumber()),
                getTextValue(professor.getFullName()),
                getTextValue(professor.getInstitutionalEmail()),
                getTextValue(professor.getCoordinator()),
                getTextValue(professor.getActive()),
                getTextValue(professor.getCurrentEducationalExperienceDisplay()),
                getTextValue(professor.getEducationalExperienceHistoryDisplay()));

        return searchableText.contains(
                searchValue);
    }

    private String getTextValue(
            String text) {
        String value = "";

        if (text != null) {
            value = text.toLowerCase();
        }

        return value;
    }



    @FXML
    private void handleChangeProfessorStatus() {
        ProfessorDTO professor = tblProfessors.getSelectionModel().getSelectedItem();

        try {
            if (professor == null) {
                showNotification(
                        Alert.AlertType.WARNING,
                        "Seleccion requerida",
                        "Seleccione un profesor de la tabla.");
            } else {
                changeProfessorStatus(
                        professor);
                loadProfessorsData();
            }
        } catch (BusinessException businessException) {
            LOGGER.error(
                    "Error al cambiar estado del profesor.",
                    businessException);

            showNotification(
                    Alert.AlertType.ERROR,
                    "Error",
                    businessException.getMessage());
        } catch (DataAccessException dataAccessException) {
            LOGGER.error(
                    "Error de conexion al cambiar estado del profesor.",
                    dataAccessException);

            showNotification(
                    Alert.AlertType.ERROR,
                    "Error de conexion",
                    "No se pudo conectar con la base de datos para cambiar el estado del profesor. Por favor intente mas tarde.");
        }
    }

    private void changeProfessorStatus(
            ProfessorDTO professor) throws BusinessException, DataAccessException {
        boolean newStatus = !professor.getIsActive();

        if (!newStatus
                && professorDAO.hasActiveEducationalExperience(
                        professor.getId())) {
            throw new BusinessException(
                    "No se puede desactivar al profesor porque tiene "
                            + "una experiencia educativa activa asignada.");
        }

        boolean wasChanged = userDAO.changeStatus(
                professor.getId(),
                newStatus);

        if (!wasChanged) {
            throw new BusinessException(
                    "No se pudo cambiar el estado del profesor.");
        }
    }

    @FXML
    private void goUpdateProfessor() {
        ProfessorDTO selectedProfessor = tblProfessors.getSelectionModel().getSelectedItem();

            WindowManagerController.changeViewToUpdateProfessor(
                    "UpdateProfessorDashboard.fxml",
                    selectedProfessor);
        } else {
            showNotification(
                    Alert.AlertType.WARNING,
                    "Seleccion requerida",
                    "Seleccione un profesor de la tabla para modificarlo.");
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
        LOGGER.info("Cierre de sesion realizado correctamente.");
        WindowManagerController.changeView(
                "LoginDashboard.fxml");
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

    private void showNotification(
            Alert.AlertType type,
            String title,
            String content) {
        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
