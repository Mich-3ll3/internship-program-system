package mx.uv.internshipprogramsystem.gui.controllers;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.ProfessorValidator;

public class ProfessorManagementController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ProfessorManagementController.class);

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
    private TableColumn<ProfessorDTO, Boolean> colStatus;

    private final ProfessorDAO professorDAO = new ProfessorDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<ProfessorDTO> masterData =
        FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTableColumns();
        loadProfessorsData();
    }

    private void configureTableColumns() {
        colStaffNumber.setCellValueFactory(
            new PropertyValueFactory<>("staffNumber")
        );
        colName.setCellValueFactory(
            new PropertyValueFactory<>("fullName")
        );
        colEmail.setCellValueFactory(
            new PropertyValueFactory<>("institutionalEmail")
        );
        colCoordinator.setCellValueFactory(
            new PropertyValueFactory<>("coordinator")
        );
        colStatus.setCellValueFactory(
            new PropertyValueFactory<>("isActive")
        );
    }

    private void loadProfessorsData() {
        try {
            List<ProfessorDTO> professors = professorDAO.findAll();
            masterData.setAll(professors);
            tblProfessors.setItems(masterData);
        } catch (BusinessException businessException) {
            LOGGER.error("Error al cargar profesores", businessException);
            showNotification(
                Alert.AlertType.ERROR,
                "Error de carga",
                "No se pudo cargar la lista de profesores."
            );
        }
    }

    @FXML
    private void searchProfessor() {
        try {
            String staffNumber = txtSearchStaffNumber.getText().trim();
            ProfessorValidator validator = new ProfessorValidator();
            validator.validateStaffNumber(staffNumber);

            ProfessorDTO professor =
                professorDAO.findByStaffNumber(staffNumber).orElse(null);

            if (professor != null) {
                tblProfessors.setItems(
                    FXCollections.observableArrayList(professor)
                );
            } else {
                showNotification(
                    Alert.AlertType.WARNING,
                    "Sin resultados",
                    "No se encontró ningún profesor con ese número de personal."
                );
            }
        } catch (BusinessException businessException) {
            LOGGER.warn("Búsqueda de profesor inválida", businessException);
            showNotification(
                Alert.AlertType.ERROR,
                "Error de búsqueda",
                businessException.getMessage()
            );
        }
    }

    @FXML
    private void handleChangeProfessorStatus() {
        ProfessorDTO professor =
            tblProfessors.getSelectionModel().getSelectedItem();

        try {
            if (professor == null) {
                showNotification(
                    Alert.AlertType.WARNING,
                    "Selección requerida",
                    "Seleccione un profesor de la tabla."
                );
            } else {
                changeProfessorStatus(professor);
                loadProfessorsData();
            }
        } catch (BusinessException businessException) {
            LOGGER.error("Error al cambiar estado del profesor", businessException);
            showNotification(
                Alert.AlertType.ERROR,
                "Error",
                businessException.getMessage()
            );
        }
    }

    private void changeProfessorStatus(ProfessorDTO professor) throws BusinessException {
        boolean newStatus = !professor.getIsActive();

        boolean wasChanged = userDAO.changeStatus(
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
        ProfessorDTO selectedProfessor =
            tblProfessors.getSelectionModel().getSelectedItem();

        if (selectedProfessor != null) {
            WindowManagerController.changeViewToUpdateProfessor(
                "UpdateProfessorDashboard.fxml",
                selectedProfessor
            );
        } else {
            showNotification(
                Alert.AlertType.WARNING,
                "Selección requerida",
                "Seleccione un profesor de la tabla para modificarlo."
            );
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView("AdminHomeDashboard.fxml");
    }

    @FXML
    private void goProfessorModule(ActionEvent event) {
        WindowManagerController.changeView("ProfessorModuleDashboard.fxml");
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        WindowManagerController.changeView("InternModuleDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

    @FXML
    private void goRegisterProfessor(ActionEvent event) {
        WindowManagerController.changeView("RegisterProfessorDashboard.fxml");
    }

    private void showNotification(
            Alert.AlertType type,
            String title,
            String content
    ) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}