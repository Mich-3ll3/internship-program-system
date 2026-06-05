package mx.uv.internshipprogramsystem.gui.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.EducationalExperienceDAO;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceDTO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceInternDTO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceInternStatus;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.EducationalExperienceInternAssignmentManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;

public class InternManagementController {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            InternManagementController.class
        );

    private static final boolean DEFAULT_COUNTS_OPPORTUNITY = true;
    private static final int DEFAULT_OPPORTUNITY_NUMBER = 0;

    @FXML
    private TextField txtSearchEnrollment;

    @FXML
    private ComboBox<EducationalExperienceDTO> cmbEducationalExperience;

    @FXML
    private TableView<InternDTO> tblInterns;

    @FXML
    private TableColumn<InternDTO, String> colEnrollment;

    @FXML
    private TableColumn<InternDTO, String> colName;

    @FXML
    private TableColumn<InternDTO, String> colEmail;

    @FXML
    private TableColumn<InternDTO, String> colNRC;

    @FXML
    private TableColumn<InternDTO, String> colStatus;

    @FXML
    private Button btnUpdateIntern;

    @FXML
    private Button btnChangeInternStatus;

    private final InternDAO internDAO =
        new InternDAO();

    private final UserDAO userDAO =
        new UserDAO();

    private final EducationalExperienceDAO educationalExperienceDAO =
        new EducationalExperienceDAO();

    private final EducationalExperienceInternAssignmentManager
            educationalExperienceInternAssignmentManager =
                new EducationalExperienceInternAssignmentManager();

    private final ObservableList<InternDTO> masterData =
        FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            validatePermission(
                Permission.CONSULT_INTERN
            );

            configureTable();

            tblInterns.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
            );

            loadInternsData();

            loadEducationalExperiences();

            disableActionButtons();

            LOGGER.info(
                "Módulo de gestión de estudiantes cargado correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado al módulo de gestión de estudiantes.",
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

    private void configureTable() {
        colEnrollment.setCellValueFactory(
            new PropertyValueFactory<InternDTO, String>(
                "enrollmentNumber"
            )
        );

        colName.setCellValueFactory(
            new PropertyValueFactory<InternDTO, String>(
                "fullName"
            )
        );

        colEmail.setCellValueFactory(
            new PropertyValueFactory<InternDTO, String>(
                "institutionalEmail"
            )
        );

        colNRC.setCellValueFactory(
            new PropertyValueFactory<InternDTO, String>(
                "nrc"
            )
        );

        colStatus.setCellValueFactory(
            new PropertyValueFactory<InternDTO, String>(
                "active"
            )
        );
    }

    private void loadInternsData() {
        try {
            validatePermission(
                Permission.CONSULT_INTERN
            );

            List<InternDTO> interns =
                internDAO.findAll();

            masterData.setAll(
                interns
            );

            tblInterns.setItems(
                masterData
            );

            LOGGER.info(
                "Lista de estudiantes cargada correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al cargar estudiantes.",
                businessException
            );

            FormAlertSupport.showError(
                "Error de carga",
                "No se pudo cargar la lista de estudiantes."
            );
        }
    }

    private void loadEducationalExperiences() {
        try {
            validatePermission(
                Permission.ASSIGN_EDUCATIONAL_EXPERIENCE
            );

            List<EducationalExperienceDTO> activeExperiences =
                getActiveEducationalExperiences();

            cmbEducationalExperience.getItems().setAll(
                activeExperiences
            );

            LOGGER.info(
                "Experiencias educativas activas cargadas correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al cargar experiencias educativas.",
                businessException
            );

            FormAlertSupport.showError(
                "Error de carga",
                "No se pudieron cargar las experiencias educativas."
            );
        }
    }

    private List<EducationalExperienceDTO> getActiveEducationalExperiences()
            throws BusinessException {
        List<EducationalExperienceDTO> allExperiences =
            educationalExperienceDAO.findAll();

        List<EducationalExperienceDTO> activeExperiences =
            new ArrayList<>();

        for (EducationalExperienceDTO experience : allExperiences) {
            if (experience.getIsActive()) {
                activeExperiences.add(
                    experience
                );
            }
        }

        return List.copyOf(
            activeExperiences
        );
    }

    @FXML
    private void handleSearchIntern() {
        try {
            validatePermission(
                Permission.CONSULT_INTERN
            );

            String searchValue =
                txtSearchEnrollment.getText()
                    .trim()
                    .toLowerCase();

            if (searchValue.isBlank()) {
                tblInterns.setItems(
                    masterData
                );

                disableActionButtons();
            } else {
                filterInterns(
                    searchValue
                );
            }
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Búsqueda de estudiante inválida.",
                businessException
            );

            FormAlertSupport.showError(
                "Error de búsqueda",
                businessException.getMessage()
            );
        }
    }

    private void filterInterns(
            String searchValue
    ) {
        ObservableList<InternDTO> filteredInterns =
            FXCollections.observableArrayList();

        for (InternDTO intern : masterData) {
            if (containsSearchValue(
                    intern,
                    searchValue
            )) {
                filteredInterns.add(
                    intern
                );
            }
        }

        tblInterns.setItems(
            filteredInterns
        );

        disableActionButtons();
    }

    private boolean containsSearchValue(
            InternDTO intern,
            String searchValue
    ) {
        String enrollment =
            getTextValue(
                intern.getEnrollmentNumber()
            );

        String email =
            getTextValue(
                intern.getInstitutionalEmail()
            );

        String fullName =
            getTextValue(
                intern.getFullName()
            );

        String nrc =
            getTextValue(
                intern.getNrc()
            );

        return enrollment.contains(searchValue)
            || email.contains(searchValue)
            || fullName.contains(searchValue)
            || nrc.contains(searchValue);
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
    private void handleRefreshInterns(
            ActionEvent event
    ) {
        txtSearchEnrollment.clear();

        loadInternsData();

        tblInterns.setItems(
            masterData
        );

        disableActionButtons();

        LOGGER.info(
            "Lista de estudiantes actualizada correctamente."
        );
    }

    @FXML
    private void handleSelectIntern() {
        InternDTO selectedIntern =
            tblInterns.getSelectionModel().getSelectedItem();

        if (selectedIntern == null) {
            disableActionButtons();
        } else {
            enableActionButtons();

            updateStatusButtonText(
                selectedIntern
            );
        }
    }

    @FXML
    private void handleAssignEducationalExperience(
            ActionEvent event
    ) {
        try {
            validatePermission(
                Permission.ASSIGN_EDUCATIONAL_EXPERIENCE
            );

            InternDTO selectedIntern =
                getSelectedIntern();

            EducationalExperienceDTO selectedExperience =
                getSelectedEducationalExperience();

            EducationalExperienceInternDTO assignment =
                buildEducationalExperienceInternAssignment(
                    selectedIntern,
                    selectedExperience
                );

            educationalExperienceInternAssignmentManager
                .assignInternToEducationalExperience(
                    assignment
                );

            FormAlertSupport.showInformation(
                "Asignación exitosa",
                "La experiencia educativa fue asignada correctamente."
            );

            loadInternsData();

            disableActionButtons();
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "No se pudo asignar experiencia educativa al estudiante.",
                businessException
            );

            FormAlertSupport.showWarning(
                "Asignación no realizada",
                businessException.getMessage()
            );
        }
    }

    private InternDTO getSelectedIntern()
            throws BusinessException {
        InternDTO selectedIntern =
            tblInterns.getSelectionModel().getSelectedItem();

        if (selectedIntern == null) {
            throw new BusinessException(
                "Seleccione un estudiante de la tabla."
            );
        }

        return selectedIntern;
    }

    private EducationalExperienceDTO getSelectedEducationalExperience()
            throws BusinessException {
        EducationalExperienceDTO selectedExperience =
            cmbEducationalExperience
                .getSelectionModel()
                .getSelectedItem();

        if (selectedExperience == null) {
            throw new BusinessException(
                "Seleccione una experiencia educativa."
            );
        }

        return selectedExperience;
    }

    private EducationalExperienceInternDTO
            buildEducationalExperienceInternAssignment(
                    InternDTO intern,
                    EducationalExperienceDTO educationalExperience
    ) {
        EducationalExperienceInternDTO assignment =
            new EducationalExperienceInternDTO(
                educationalExperience.getNrc(),
                intern.getId(),
                LocalDate.now(),
                DEFAULT_COUNTS_OPPORTUNITY,
                DEFAULT_OPPORTUNITY_NUMBER,
                EducationalExperienceInternStatus.ACTIVA
            );

        return assignment;
    }

    @FXML
    private void handleChangeInternStatus(
            ActionEvent event
    ) {
        try {
            validatePermission(
                Permission.CHANGE_INTERN_STATUS
            );

            InternDTO selectedIntern =
                getSelectedIntern();

            changeInternStatus(
                selectedIntern
            );

            FormAlertSupport.showInformation(
                "Estado actualizado",
                "El estado del estudiante fue actualizado correctamente."
            );

            loadInternsData();

            disableActionButtons();
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al cambiar estado del estudiante.",
                businessException
            );

            FormAlertSupport.showError(
                "Error",
                businessException.getMessage()
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
                "No se pudo cambiar el estado del estudiante."
            );
        }

        LOGGER.info(
            "Estado del estudiante actualizado correctamente."
        );
    }

    @FXML
    private void handleUpdateIntern(
            ActionEvent event
    ) {
        try {
            validatePermission(
                Permission.UPDATE_INTERN
            );

            InternDTO selectedIntern =
                getSelectedIntern();

            WindowManagerController.changeViewToUpdateIntern(
                "UpdateInternDashboard.fxml",
                selectedIntern
            );

            LOGGER.info(
                "Redirección a actualización de estudiante."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "No se seleccionó estudiante para modificación.",
                businessException
            );

            FormAlertSupport.showWarning(
                "Selección requerida",
                businessException.getMessage()
            );
        }
    }

    private void enableActionButtons() {
        btnUpdateIntern.setDisable(
            false
        );

        btnChangeInternStatus.setDisable(
            false
        );
    }

    private void disableActionButtons() {
        btnUpdateIntern.setDisable(
            true
        );

        btnChangeInternStatus.setDisable(
            true
        );

        btnChangeInternStatus.setText(
            "Activar / Inactivar"
        );
    }

    private void updateStatusButtonText(
            InternDTO intern
    ) {
        if (intern.getIsActive()) {
            btnChangeInternStatus.setText(
                "Inactivar estudiante"
            );
        } else {
            btnChangeInternStatus.setText(
                "Activar estudiante"
            );
        }
    }

    @FXML
    private void handleGoHome(
            ActionEvent event
    ) {
        WindowManagerController.goBack();

        LOGGER.info(
            "Regreso al dashboard principal."
        );
    }

    @FXML
    private void handleGoProfessorModule(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.CONSULT_PROFESSOR,
            "ProfessorModuleDashboard.fxml",
            "Acceso denegado al módulo de profesores."
        );
    }

    @FXML
    private void handleGoInternModule(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.CONSULT_INTERN,
            "InternModuleDashboard.fxml",
            "Acceso denegado al módulo de estudiantes."
        );
    }

    @FXML
    private void handleGoRegisterIntern(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.REGISTER_INTERN,
            "RegisterInternDashboard.fxml",
            "Acceso denegado al registro de estudiantes."
        );
    }

    @FXML
    private void handleLogOut(
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