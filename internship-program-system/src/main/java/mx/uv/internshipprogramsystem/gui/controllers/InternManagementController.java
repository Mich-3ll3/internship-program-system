package mx.uv.internshipprogramsystem.gui.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.gui.handlers.InternSelectionListener;
import mx.uv.internshipprogramsystem.gui.handlers.InternActionCellFactory;
import mx.uv.internshipprogramsystem.gui.util.ExperienceHistoryRow;
import mx.uv.internshipprogramsystem.logic.managers.EducationalExperienceInternAssignmentManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class InternManagementController {
    
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            InternManagementController.class
        );

    private static final boolean DEFAULT_COUNTS_OPPORTUNITY = true;
    private static final int DEFAULT_OPPORTUNITY_NUMBER = 0;
    private static final String DEFAULT_STATUS_BUTTON_TEXT =
        "Desactivar alumno";
    private static final List<EducationalExperienceInternStatus>
            CLOSURE_STATUSES =
                List.of(
                    EducationalExperienceInternStatus.APROBADA,
                    EducationalExperienceInternStatus.REPROBADA,
                    EducationalExperienceInternStatus.BAJA_NO_CONTABILIZADA,
                    EducationalExperienceInternStatus.BAJA_EXTEMPORANEA
                );

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
    private TableColumn<InternDTO, String> colStatus;

    @FXML
    private TableColumn<InternDTO, Void> colActions;

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
        configureTable();
        configureSelectionListener();

        tblInterns.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );

        loadInternsData();
        loadEducationalExperiences();
        disableActionButtons();

        LOGGER.info(
            "Modulo de gestion de estudiantes cargado correctamente."
        );
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

        colStatus.setCellValueFactory(
            new PropertyValueFactory<InternDTO, String>(
                "active"
            )
        );

        configureActionsColumn();
    }

    private void configureActionsColumn() {
        colActions.setCellFactory(
            new InternActionCellFactory(this)
        );
    }

    private Button buildViewButton() {
        Button viewButton =
            new Button("Ver");

        viewButton.setStyle(
            "-fx-background-color: #e0f2fe; "
            + "-fx-background-radius: 10; "
            + "-fx-text-fill: #075985; "
            + "-fx-font-weight: bold; "
            + "-fx-cursor: hand; "
            + "-fx-padding: 6 14 6 14;"
        );

        return viewButton;
    }

    private void configureSelectionListener() {
        tblInterns
            .getSelectionModel()
            .selectedItemProperty()
            .addListener(
                new InternSelectionListener(this)
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
        } catch (DataAccessException exception) {
            LOGGER.error(
                "Error de conexion al cargar estudiantes.",
                exception
            );

            showNotification(
                Alert.AlertType.ERROR,
                "Error de conexion",
                "No se pudo conectar con la base de datos para cargar los estudiantes. Por favor intente mas tarde."
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
        } catch (DataAccessException exception) {
            LOGGER.error(
                "Error de conexion al cargar experiencias educativas.",
                exception
            );

            showNotification(
                Alert.AlertType.ERROR,
                "Error de conexion",
                "No se pudo conectar con la base de datos para cargar las experiencias educativas. Por favor intente mas tarde."
            );
        }
    }

    private List<EducationalExperienceDTO> getActiveEducationalExperiences()
            throws BusinessException, DataAccessException {
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
        String searchableText =
            String.join(
                " ",
                getTextValue(intern.getEnrollmentNumber()),
                getTextValue(intern.getInstitutionalEmail()),
                getTextValue(intern.getFullName()),
                getTextValue(intern.getNrc()),
                getTextValue(intern.getActive()),
                getTextValue(intern.getCurrentEducationalExperienceDisplay()),
                getTextValue(intern.getEducationalExperienceHistoryDisplay())
            );

        return searchableText.contains(
            searchValue
        );
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
    private void handleSelectIntern() {
        InternDTO selectedIntern =
            tblInterns.getSelectionModel().getSelectedItem();

        updateInternSelection(
            selectedIntern
        );
    }

    public void updateInternSelection(
            InternDTO selectedIntern
    ) {
        if (selectedIntern == null) {
            disableActionButtons();
        } else {
            enableActionButtons();

            updateStatusButtonText(
                selectedIntern
            );
        }
    }

    public void showInternDetailsDialog(
            InternDTO intern
    ) {
        Dialog<Void> detailsDialog =
            new Dialog<>();

        detailsDialog.setTitle(
            "Detalle de estudiante"
        );

        DialogPane dialogPane =
            detailsDialog.getDialogPane();

        dialogPane.getButtonTypes().add(
            ButtonType.CLOSE
        );

        dialogPane.setContent(
            buildInternDetailsContent(
                intern
            )
        );

        dialogPane.setStyle(
            "-fx-background-color: #f8fafc;"
        );

        detailsDialog.showAndWait();
    }

    private VBox buildInternDetailsContent(
            InternDTO intern
    ) {
        VBox content =
            new VBox(16);

        content.setPrefWidth(780);
        content.setStyle(
            "-fx-background-color: #f8fafc; "
            + "-fx-padding: 18;"
        );

        content.getChildren().addAll(
            buildInternDetailsHeader(
                intern
            ),
            buildInternDetailsSummary(
                intern
            ),
            buildInternHistoryCard(
                intern
            )
        );

        return content;
    }

    private VBox buildInternDetailsHeader(
            InternDTO intern
    ) {
        VBox header =
            new VBox(6);

        header.setStyle(
            "-fx-background-color: white; "
            + "-fx-background-radius: 14; "
            + "-fx-border-color: #dbeafe; "
            + "-fx-border-radius: 14; "
            + "-fx-padding: 18;"
        );

        Label title =
            new Label(
                intern.getFullName()
            );

        title.setStyle(
            "-fx-text-fill: #14213d; "
            + "-fx-font-size: 22px; "
            + "-fx-font-weight: bold;"
        );

        Label subtitle =
            new Label(
                intern.getEnrollmentNumber()
                + " | " + intern.getInstitutionalEmail()
            );

        subtitle.setStyle(
            "-fx-text-fill: #475569; "
            + "-fx-font-size: 14px;"
        );

        header.getChildren().addAll(
            title,
            subtitle
        );

        return header;
    }

    private HBox buildInternDetailsSummary(
            InternDTO intern
    ) {
        HBox summary =
            new HBox(10);

        summary.getChildren().addAll(
            buildSummaryItem(
                "Estado interno",
                intern.getActive()
            ),
            buildSummaryItem(
                "Estado en EE",
                intern.getEducationalExperienceStatusDisplayName()
            ),
            buildSummaryItem(
                "Experiencia actual",
                intern.getCurrentEducationalExperienceDisplay()
            )
        );

        return summary;
    }

    private VBox buildInternHistoryCard(
            InternDTO intern
    ) {
        VBox card =
            new VBox(8);

        card.setStyle(
            "-fx-background-color: white; "
            + "-fx-background-radius: 12; "
            + "-fx-border-color: #e2e8f0; "
            + "-fx-border-radius: 12; "
            + "-fx-padding: 14;"
        );

        Label title =
            new Label("Historial de NRC");

        title.setStyle(
            "-fx-text-fill: #64748b; "
            + "-fx-font-size: 12px;"
        );

        card.getChildren().addAll(
            title,
            buildExperienceHistoryTable(
                intern.getEducationalExperienceHistory()
            )
        );

        return card;
    }

    private VBox buildSummaryItem(
            String label,
            String value
    ) {
        VBox summaryItem =
            new VBox(4);

        summaryItem.setPrefWidth(246);
        summaryItem.setStyle(
            "-fx-background-color: white; "
            + "-fx-background-radius: 12; "
            + "-fx-border-color: #e2e8f0; "
            + "-fx-border-radius: 12; "
            + "-fx-padding: 12;"
        );

        Label title =
            new Label(label);

        title.setStyle(
            "-fx-text-fill: #64748b; "
            + "-fx-font-size: 12px;"
        );

        Label text =
            new Label(value);

        text.setWrapText(true);
        text.setStyle(
            "-fx-text-fill: #14213d; "
            + "-fx-font-size: 14px; "
            + "-fx-font-weight: bold;"
        );

        summaryItem.getChildren().addAll(
            title,
            text
        );

        return summaryItem;
    }

    private TableView<ExperienceHistoryRow> buildExperienceHistoryTable(
            String history
    ) {
        TableView<ExperienceHistoryRow> historyTable =
            new TableView<>();

        historyTable.setPrefHeight(220);
        historyTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );
        historyTable.setItems(
            FXCollections.observableArrayList(
                buildExperienceHistoryRows(
                    history
                )
            )
        );
        historyTable.setPlaceholder(
            new Label("Sin NRC registrados")
        );
        historyTable.setStyle(
            "-fx-background-color: white; "
            + "-fx-background-radius: 10; "
            + "-fx-border-color: #e2e8f0; "
            + "-fx-border-radius: 10;"
        );

        historyTable.getColumns().addAll(
            buildHistoryColumn(
                "NRC",
                "nrc",
                90
            ),
            buildHistoryColumn(
                "Periodo",
                "period",
                120
            ),
            buildHistoryColumn(
                "Seccion",
                "section",
                120
            ),
            buildHistoryColumn(
                "Estado",
                "status",
                150
            )
        );

        return historyTable;
    }

    private TableColumn<ExperienceHistoryRow, String> buildHistoryColumn(
            String title,
            String property,
            double width
    ) {
        TableColumn<ExperienceHistoryRow, String> column =
            new TableColumn<>(title);

        column.setPrefWidth(width);
        column.setCellValueFactory(
            new PropertyValueFactory<ExperienceHistoryRow, String>(
                property
            )
        );

        return column;
    }

    private List<ExperienceHistoryRow> buildExperienceHistoryRows(
            String history
    ) {
        ObservableList<ExperienceHistoryRow> rows =
            FXCollections.observableArrayList();

        if (history != null && !history.isBlank()) {
            String normalizedHistory =
                history.replace(
                    ", ",
                    "\n"
                );

            for (String experience : normalizedHistory.split("\\R")) {
                if (!experience.isBlank()) {
                    rows.add(
                        buildExperienceHistoryRow(
                            experience
                        )
                    );
                }
            }
        }

        return rows;
    }

    private ExperienceHistoryRow buildExperienceHistoryRow(
            String experience
    ) {
        String[] parts =
            experience.split(
                " - ",
                -1
            );

        return new ExperienceHistoryRow(
            getPartValue(parts, 0),
            getPartValue(parts, 1),
            getPartValue(parts, 2),
            getPartValue(parts, 3)
        );
    }

    private String getPartValue(
            String[] parts,
            int index
    ) {
        String value =
            "-";

        if (parts.length > index && !parts[index].isBlank()) {
            value =
                parts[index].trim();
        }

        return value;
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

            activateInternWithEducationalExperience(
                selectedIntern,
                assignment
            );

            showNotification(
                Alert.AlertType.INFORMATION,
                "Asignacion exitosa",
                "La experiencia educativa fue asignada correctamente."
            );

            loadInternsData();
            disableActionButtons();
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "No se pudo asignar experiencia educativa al estudiante.",
                businessException
            );

            showNotification(
                Alert.AlertType.WARNING,
                "Asignacion no realizada",
                businessException.getMessage()
            );
        } catch (DataAccessException exception) {
            LOGGER.error(
                "Error de conexion al asignar experiencia educativa.",
                exception
            );

            showNotification(
                Alert.AlertType.ERROR,
                "Error de conexion",
                "No se pudo conectar con la base de datos para asignar la experiencia educativa. Por favor intente mas tarde."
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
        return new EducationalExperienceInternDTO(
            educationalExperience.getNrc(),
            intern.getId(),
            LocalDate.now(),
            DEFAULT_COUNTS_OPPORTUNITY,
            DEFAULT_OPPORTUNITY_NUMBER,
            EducationalExperienceInternStatus.ACTIVA
        );
    }

    private void activateInternWithEducationalExperience(
            InternDTO intern,
            EducationalExperienceInternDTO assignment
    ) throws BusinessException, DataAccessException {
        educationalExperienceInternAssignmentManager
            .assignInternToEducationalExperience(
                assignment
            );

        changeUserStatus(
            intern.getId(),
            true
        );
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

            showNotification(
                Alert.AlertType.INFORMATION,
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
        } catch (DataAccessException exception) {
            LOGGER.error(
                "Error de conexion al cambiar estado del estudiante.",
                exception
            );

            showNotification(
                Alert.AlertType.ERROR,
                "Error de conexion",
                "No se pudo conectar con la base de datos para cambiar el estado del estudiante. Por favor intente mas tarde."
            );
        }
    }

    private void changeInternStatus(
            InternDTO intern
    ) throws BusinessException, DataAccessException {
        if (!intern.hasActiveEducationalExperience()) {
            throw new BusinessException(
                "Seleccione el NRC activo para desactivar al estudiante."
            );
        }

        closeInternEducationalExperience(
            intern
        );
    }

    private void closeInternEducationalExperience(
            InternDTO intern
    ) throws BusinessException, DataAccessException {
        EducationalExperienceInternStatus closureStatus =
            requestClosureStatus();

        educationalExperienceInternAssignmentManager
            .closeActiveEducationalExperience(
                intern.getId(),
                closureStatus
            );

        changeUserStatus(
            intern.getId(),
            false
        );
    }

    private EducationalExperienceInternStatus requestClosureStatus()
            throws BusinessException {
        ChoiceDialog<EducationalExperienceInternStatus> dialog =
            new ChoiceDialog<>(
                EducationalExperienceInternStatus.APROBADA,
                CLOSURE_STATUSES
            );

        dialog.setTitle(
            "Motivo de baja"
        );

        dialog.setHeaderText(
            "Seleccione el motivo de baja del estudiante."
        );

        dialog.setContentText(
            "Motivo:"
        );

        Optional<EducationalExperienceInternStatus> selectedStatus =
            dialog.showAndWait();

        if (selectedStatus.isEmpty()) {
            throw new BusinessException(
                "Operacion cancelada."
            );
        }

        return selectedStatus.get();
    }

    private void changeUserStatus(
            int internId,
            boolean isActive
    ) throws BusinessException, DataAccessException {
        boolean wasChanged =
            userDAO.changeStatus(
                internId,
                isActive
            );

        if (!wasChanged) {
            throw new BusinessException(
                "No se pudo cambiar el estado del estudiante."
            );
        }
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
                "Redireccion a actualizacion de estudiante."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "No se selecciono estudiante para modificacion.",
                businessException
            );

            showNotification(
                Alert.AlertType.WARNING,
                "Seleccion requerida",
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

        btnChangeInternStatus.setManaged(
            true
        );

        btnChangeInternStatus.setVisible(
            true
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
            DEFAULT_STATUS_BUTTON_TEXT
        );

        btnChangeInternStatus.setManaged(
            true
        );

        btnChangeInternStatus.setVisible(
            true
        );
    }

    private void updateStatusButtonText(
            InternDTO intern
    ) {
        if (intern.hasActiveEducationalExperience()) {
            btnChangeInternStatus.setText(
                DEFAULT_STATUS_BUTTON_TEXT
            );

            btnChangeInternStatus.setDisable(
                false
            );

            btnChangeInternStatus.setManaged(
                true
            );

            btnChangeInternStatus.setVisible(
                true
            );
        } else {
            btnChangeInternStatus.setDisable(
                true
            );

            btnChangeInternStatus.setManaged(
                false
            );

            btnChangeInternStatus.setVisible(
                false
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
        WindowManagerController.changeView(
            "ProfessorModuleDashboard.fxml"
        );

        LOGGER.info(
            "Acceso al modulo de profesores."
        );
    }

    @FXML
    private void handleGoInternModule(
            ActionEvent event
    ) {
        WindowManagerController.changeView(
            "InternModuleDashboard.fxml"
        );

        LOGGER.info(
            "Acceso al modulo de estudiantes."
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
            "Cierre de sesion realizado correctamente."
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
