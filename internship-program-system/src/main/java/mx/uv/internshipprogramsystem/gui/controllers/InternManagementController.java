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

import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.InternValidator;

public class InternManagementController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            InternManagementController.class
        );

    @FXML
    private TextField txtSearchEnrollment;

    @FXML
    private TableView<InternDTO> tblInterns;

    @FXML
    private TableColumn<InternDTO, String> colEnrollment;

    @FXML
    private TableColumn<InternDTO, String> colName;

    @FXML
    private TableColumn<InternDTO, String> colEmail;

    @FXML
    private TableColumn<InternDTO, Boolean> colStatus;

    @FXML
    private TableColumn<InternDTO, String> colNRC;

    private final InternDAO internDAO =
        new InternDAO();

    private final UserDAO userDAO =
        new UserDAO();

    private final ObservableList<InternDTO> masterData =
        FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTable();
        loadInternsData();

        LOGGER.info(
            "Módulo de gestión de estudiantes cargado correctamente."
        );
    }

    private void configureTable() {
        colEnrollment.setCellValueFactory(
            new PropertyValueFactory<>(
                "enrollmentNumber"
            )
        );

        colName.setCellValueFactory(
            new PropertyValueFactory<>(
                "fullName"
            )
        );

        colEmail.setCellValueFactory(
            new PropertyValueFactory<>(
                "institutionalEmail"
            )
        );

        colStatus.setCellValueFactory(
            new PropertyValueFactory<>(
                "isActive"
            )
        );

        colNRC.setCellValueFactory(
            new PropertyValueFactory<>(
                "nrc"
            )
        );
    }

    private void loadInternsData() {
        try {
            List<InternDTO> interns =
                internDAO.findAll();

            masterData.setAll(interns);

            tblInterns.setItems(masterData);

            LOGGER.info(
                "Lista de estudiantes cargada correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al cargar estudiantes",
                businessException
            );

            showNotification(
                Alert.AlertType.ERROR,
                "Error de carga",
                "No se pudo cargar la lista "
                    + "de estudiantes."
            );
        }
    }

    @FXML
    private void searchIntern() {
        try {
            String enrollmentNumber =
                txtSearchEnrollment
                    .getText()
                    .trim();

            InternValidator validator =
                new InternValidator();

            validator.validateEnrollmentNumber(
                enrollmentNumber
            );

            InternDTO intern =
                internDAO.findByEnrollmentNumber(
                    enrollmentNumber
                ).orElse(null);

            handleSearchResult(intern);
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Búsqueda de estudiante inválida",
                businessException
            );

            showNotification(
                Alert.AlertType.ERROR,
                "Error de búsqueda",
                businessException.getMessage()
            );
        }
    }

    private void handleSearchResult(
            InternDTO intern
    ) {
        if (intern != null) {
            tblInterns.setItems(
                FXCollections.observableArrayList(
                    intern
                )
            );

            LOGGER.info(
                "Estudiante encontrado correctamente."
            );
        } else {
            LOGGER.warn(
                "No se encontró estudiante "
                    + "con la matrícula proporcionada."
            );

            showNotification(
                Alert.AlertType.WARNING,
                "Sin resultados",
                "No se encontró ningún estudiante "
                    + "con esa matrícula."
            );
        }
    }

    @FXML
    private void handleChangeInternStatus() {
        InternDTO selectedIntern =
            tblInterns.getSelectionModel()
                .getSelectedItem();

        try {
            validateSelectedIntern(
                selectedIntern
            );

            changeInternStatus(
                selectedIntern
            );

            loadInternsData();
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al cambiar estado del estudiante",
                businessException
            );

            showNotification(
                Alert.AlertType.ERROR,
                "Error",
                businessException.getMessage()
            );
        }
    }

    private void validateSelectedIntern(
            InternDTO selectedIntern
    ) throws BusinessException {
        if (selectedIntern == null) {
            throw new BusinessException(
                "Seleccione un estudiante de la tabla."
            );
        }
    }

    private void changeInternStatus(
            InternDTO intern
    ) throws BusinessException {
        boolean newStatus =
            !intern.getIsActive();

        boolean wasChanged =
            userDAO.changeStatus(
                intern.getId(),
                newStatus
            );

        if (!wasChanged) {
            throw new BusinessException(
                "No se pudo cambiar el estado "
                    + "del estudiante."
            );
        }

        LOGGER.info(
            "Estado del estudiante {} actualizado correctamente.",
            intern.getEnrollmentNumber()
        );
    }

    @FXML
    private void goUpdateIntern() {
        InternDTO selectedIntern =
            tblInterns.getSelectionModel()
                .getSelectedItem();

        if (selectedIntern != null) {
            WindowManagerController
                .changeViewToUpdateIntern(
                    "UpdateInternDashboard.fxml",
                    selectedIntern
                );

            LOGGER.info(
                "Redirección a actualización "
                    + "de estudiante."
            );
        } else {
            showNotification(
                Alert.AlertType.WARNING,
                "Selección requerida",
                "Seleccione un estudiante "
                    + "de la tabla para modificarlo."
            );
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.goBack();

        LOGGER.info(
            "Regreso al dashboard principal."
        );
    }

    @FXML
    private void goProfessorModule(
            ActionEvent event
    ) {
        WindowManagerController.changeView(
            "ProfessorModuleDashboard.fxml"
        );

        LOGGER.info(
            "Acceso al módulo de profesores."
        );
    }

    @FXML
    private void goInternModule(
            ActionEvent event
    ) {
        WindowManagerController.changeView(
            "InternModuleDashboard.fxml"
        );

        LOGGER.info(
            "Acceso al módulo de estudiantes."
        );
    }

    @FXML
    private void logOut(ActionEvent event) {
        WindowManagerController.changeView(
            "LoginDashboard.fxml"
        );

        LOGGER.info(
            "Cierre de sesión realizado correctamente."
        );
    }

    @FXML
    private void goRegisterIntern(
            ActionEvent event
    ) {
        WindowManagerController.changeView(
            "RegisterInternDashboard.fxml"
        );

        LOGGER.info(
            "Acceso al registro de estudiantes."
        );
    }

    private void showNotification(
            Alert.AlertType type,
            String title,
            String content
    ) {
        Alert alert =
            new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}