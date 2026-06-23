package mx.uv.internshipprogramsystem.gui.controllers;

import mx.uv.internshipprogramsystem.gui.navigation.NavigationManager;
import mx.uv.internshipprogramsystem.gui.util.FormAlertSupport;

import java.text.Normalizer;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.EducationalExperienceDAO;
import mx.uv.internshipprogramsystem.logic.dao.EducationalExperienceInternDAO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceDTO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceInternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;

public class EducationalExperienceManagementController {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            EducationalExperienceManagementController.class
        );

    @FXML
    private TextField txtSearchEducationalExperience;

    @FXML
    private TableView<EducationalExperienceDTO> tblEducationalExperiences;

    @FXML
    private TableColumn<EducationalExperienceDTO, String> colNrc;

    @FXML
    private TableColumn<EducationalExperienceDTO, String> colSchoolPeriod;

    @FXML
    private TableColumn<EducationalExperienceDTO, String> colSection;

    @FXML
    private TableColumn<EducationalExperienceDTO, String> colProfessor;

    @FXML
    private TableColumn<EducationalExperienceDTO, String> colStartDate;

    @FXML
    private TableColumn<EducationalExperienceDTO, String> colEndDate;

    @FXML
    private TableColumn<EducationalExperienceDTO, String> colStatus;

    @FXML
    private TableColumn<EducationalExperienceDTO, Void> colActions;

    private final EducationalExperienceDAO educationalExperienceDAO =
        new EducationalExperienceDAO();

    private final EducationalExperienceInternDAO educationalExperienceInternDAO =
        new EducationalExperienceInternDAO();

    private final ObservableList<EducationalExperienceDTO> masterData =
        FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            validatePermission(
                Permission.REGISTER_EDUCATIONAL_EXPERIENCE
            );

            configureTableColumns();

            loadEducationalExperiences();

            LOGGER.info(
                "Modulo de experiencias educativas cargado correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado al modulo de experiencias educativas.",
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                businessException.getMessage()
            );

            NavigationManager.changeView(
                "CoordinatorProfessorHomeDashboard.fxml"
            );
        } catch (DataAccessException exception) {
            LOGGER.error(
                "Error de conexion al validar permisos en experiencias educativas.",
                exception
            );

            FormAlertSupport.showError(
                "Error de conexion",
                "No se pudo conectar con la base de datos para validar los permisos. Por favor intente mas tarde."
            );

            NavigationManager.changeView(
                "CoordinatorProfessorHomeDashboard.fxml"
            );
        }
    }

    private void configureTableColumns() {
        tblEducationalExperiences.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );

        colNrc.setCellValueFactory(
            new PropertyValueFactory<EducationalExperienceDTO, String>(
                "nrc"
            )
        );

        colSchoolPeriod.setCellValueFactory(
            new PropertyValueFactory<EducationalExperienceDTO, String>(
                "schoolPeriod"
            )
        );

        colSection.setCellValueFactory(
            new PropertyValueFactory<EducationalExperienceDTO, String>(
                "section"
            )
        );

        colProfessor.setCellValueFactory(
            new PropertyValueFactory<EducationalExperienceDTO, String>(
                "professorName"
            )
        );

        colStartDate.setCellValueFactory(
            new PropertyValueFactory<EducationalExperienceDTO, String>(
                "startDateDisplay"
            )
        );

        colEndDate.setCellValueFactory(
            new PropertyValueFactory<EducationalExperienceDTO, String>(
                "endDateDisplay"
            )
        );

        colStatus.setCellValueFactory(
            new PropertyValueFactory<EducationalExperienceDTO, String>(
                "activeStatus"
            )
        );

        configureActionsColumn();
    }

    private void configureActionsColumn() {
        colActions.setCellFactory(
            new mx.uv.internshipprogramsystem.gui.handlers.EducationalExperienceActionCellFactory(this)
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

    public void showEducationalExperienceDetails(
            EducationalExperienceDTO educationalExperience
    ) {
        try {
            List<EducationalExperienceInternDTO> assignments =
                educationalExperienceInternDAO.findByNrc(
                    educationalExperience.getNrc()
                );

            showEducationalExperienceDetailsDialog(
                educationalExperience,
                assignments
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al cargar detalles del NRC {}.",
                educationalExperience.getNrc(),
                businessException
            );

            FormAlertSupport.showError(
                "Error de carga",
                "No se pudieron cargar los alumnos inscritos al NRC."
            );
        } catch (DataAccessException exception) {
            LOGGER.error(
                "Error de conexion al cargar detalles del NRC {}.",
                educationalExperience.getNrc(),
                exception
            );

            FormAlertSupport.showError(
                "Error de conexion",
                "No se pudo conectar con la base de datos para cargar los alumnos inscritos al NRC. Por favor intente mas tarde."
            );
        }
    }

    private void showEducationalExperienceDetailsDialog(
            EducationalExperienceDTO educationalExperience,
            List<EducationalExperienceInternDTO> assignments
    ) {
        Dialog<Void> detailsDialog =
            new Dialog<>();

        detailsDialog.setTitle(
            "Detalle de experiencia educativa"
        );

        DialogPane dialogPane =
            detailsDialog.getDialogPane();

        dialogPane.getButtonTypes().add(
            ButtonType.CLOSE
        );

        dialogPane.setContent(
            buildDetailsContent(
                educationalExperience,
                assignments
            )
        );

        dialogPane.setStyle(
            "-fx-background-color: #f8fafc;"
        );

        detailsDialog.showAndWait();
    }

    private VBox buildDetailsContent(
            EducationalExperienceDTO educationalExperience,
            List<EducationalExperienceInternDTO> assignments
    ) {
        VBox content =
            new VBox(16);

        content.setPrefWidth(820);
        content.setStyle(
            "-fx-background-color: #f8fafc; "
            + "-fx-padding: 18;"
        );

        content.getChildren().addAll(
            buildDetailsHeader(
                educationalExperience,
                assignments.size()
            ),
            buildDetailsSummary(
                educationalExperience
            ),
            buildEnrolledInternsTable(
                assignments
            )
        );

        return content;
    }

    private VBox buildDetailsHeader(
            EducationalExperienceDTO educationalExperience,
            int enrolledInternCount
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
                "NRC " + educationalExperience.getNrc()
                + " - Seccion "
                + educationalExperience.getSection()
            );

        title.setStyle(
            "-fx-text-fill: #14213d; "
            + "-fx-font-size: 22px; "
            + "-fx-font-weight: bold;"
        );

        Label professor =
            new Label(
                educationalExperience.getProfessorName()
            );

        professor.setStyle(
            "-fx-text-fill: #475569; "
            + "-fx-font-size: 14px;"
        );

        Label counter =
            new Label(
                "Alumnos inscritos: " + enrolledInternCount
            );

        counter.setStyle(
            "-fx-text-fill: #2563eb; "
            + "-fx-font-size: 14px; "
            + "-fx-font-weight: bold;"
        );

        header.getChildren().addAll(
            title,
            professor,
            counter
        );

        return header;
    }

    private HBox buildDetailsSummary(
            EducationalExperienceDTO educationalExperience
    ) {
        HBox summary =
            new HBox(10);

        summary.getChildren().addAll(
            buildSummaryItem(
                "Periodo",
                educationalExperience.getSchoolPeriod()
            ),
            buildSummaryItem(
                "Inicio",
                educationalExperience.getStartDateDisplay()
            ),
            buildSummaryItem(
                "Fin",
                educationalExperience.getEndDateDisplay()
            ),
            buildSummaryItem(
                "Estado",
                educationalExperience.getActiveStatus()
            )
        );

        return summary;
    }

    private VBox buildSummaryItem(
            String label,
            String value
    ) {
        VBox summaryItem =
            new VBox(4);

        summaryItem.setPrefWidth(190);
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

    private TableView<EducationalExperienceInternDTO> buildEnrolledInternsTable(
            List<EducationalExperienceInternDTO> assignments
    ) {
        TableView<EducationalExperienceInternDTO> internsTable =
            new TableView<>();

        internsTable.setPrefHeight(260);
        internsTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );
        internsTable.setItems(
            FXCollections.observableArrayList(assignments)
        );
        internsTable.setStyle(
            "-fx-background-color: white; "
            + "-fx-background-radius: 12; "
            + "-fx-border-radius: 12; "
            + "-fx-border-color: #e2e8f0;"
        );

        internsTable.getColumns().addAll(
            buildInternColumn(
                "Matricula",
                "enrollmentNumber",
                105
            ),
            buildInternColumn(
                "Alumno",
                "internName",
                210
            ),
            buildInternColumn(
                "Correo",
                "institutionalEmail",
                230
            ),
            buildInternColumn(
                "Asignacion",
                "assignmentDateDisplay",
                115
            ),
            buildInternColumn(
                "Intento",
                "opportunityNumberDisplay",
                80
            ),
            buildInternColumn(
                "Estado",
                "statusDisplayName",
                180
            )
        );

        return internsTable;
    }

    private TableColumn<EducationalExperienceInternDTO, String>
            buildInternColumn(
                    String title,
                    String property,
                    double width
    ) {
        TableColumn<EducationalExperienceInternDTO, String> column =
            new TableColumn<>(title);

        column.setPrefWidth(width);
        column.setCellValueFactory(
            new PropertyValueFactory<EducationalExperienceInternDTO, String>(
                property
            )
        );

        return column;
    }

    private void loadEducationalExperiences() {
        try {
            validatePermission(
                Permission.REGISTER_EDUCATIONAL_EXPERIENCE
            );

            List<EducationalExperienceDTO> educationalExperiences =
                educationalExperienceDAO.findAll();

            masterData.setAll(
                educationalExperiences
            );

            tblEducationalExperiences.setItems(
                masterData
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al cargar experiencias educativas.",
                businessException
            );

            FormAlertSupport.showError(
                "Error de carga",
                "No se pudo cargar la lista de experiencias educativas."
            );
        } catch (DataAccessException exception) {
            LOGGER.error(
                "Error de conexion al cargar experiencias educativas.",
                exception
            );

            FormAlertSupport.showError(
                "Error de conexion",
                "No se pudo conectar con la base de datos para cargar las experiencias educativas. Por favor intente mas tarde."
            );
        }
    }

    @FXML
    private void handleLiveSearchEducationalExperience(
            KeyEvent event
    ) {
        searchEducationalExperience();
    }

    @FXML
    private void searchEducationalExperience() {
        String searchValue =
            normalizeSearchText(
                txtSearchEducationalExperience.getText()
            );

        if (searchValue.isBlank()) {
            tblEducationalExperiences.setItems(
                masterData
            );
        } else {
            tblEducationalExperiences.setItems(
                getFilteredEducationalExperiences(searchValue)
            );
        }
    }

    private ObservableList<EducationalExperienceDTO>
            getFilteredEducationalExperiences(
                    String searchValue
    ) {
        ObservableList<EducationalExperienceDTO> filteredExperiences =
            FXCollections.observableArrayList();

        for (EducationalExperienceDTO experience : masterData) {
            if (containsSearchValue(
                    experience,
                    searchValue
            )) {
                filteredExperiences.add(
                    experience
                );
            }
        }

        return filteredExperiences;
    }

    private boolean containsSearchValue(
            EducationalExperienceDTO experience,
            String searchValue
    ) {
        String searchableText =
            buildSearchableText(
                experience
            );

        return searchableText.contains(
            searchValue
        );
    }

    private String buildSearchableText(
            EducationalExperienceDTO experience
    ) {
        String searchableText =
            String.join(
                " ",
                normalizeSearchText(experience.getNrc()),
                normalizeSearchText(experience.getSchoolPeriod()),
                normalizeSearchText(experience.getSection()),
                normalizeSearchText(experience.getProfessorName()),
                normalizeSearchText(experience.getActiveStatus()),
                normalizeSearchText(getStatusSearchTerms(experience)),
                normalizeSearchText(experience.getStartDateDisplay()),
                normalizeSearchText(experience.getEndDateDisplay()),
                normalizeSearchText(experience.getDateRangeDisplay()),
                normalizeSearchText(getSchoolPeriodSearchTerms(experience))
            );

        return searchableText;
    }

    private String getStatusSearchTerms(
            EducationalExperienceDTO experience
    ) {
        String statusSearchTerms =
            "inactiva inactivo baja cerrada";

        if (experience.getIsActive()) {
            statusSearchTerms =
                "activa activo vigente abierta";
        }

        return statusSearchTerms;
    }

    private String getSchoolPeriodSearchTerms(
            EducationalExperienceDTO experience
    ) {
        String searchTerms =
            "";
        String schoolPeriod =
            experience.getSchoolPeriod();

        if (schoolPeriod != null && schoolPeriod.matches("\\d{4}(51|01)")) {
            int periodYear =
                Integer.parseInt(
                    schoolPeriod.substring(
                        0,
                        4
                    )
                );

            if (schoolPeriod.endsWith("51")) {
                searchTerms =
                    "febrero julio febrero-julio "
                    + periodYear
                    + " "
                    + schoolPeriod;
            } else {
                searchTerms =
                    "agosto enero agosto-enero "
                    + (periodYear - 1)
                    + " "
                    + periodYear
                    + " "
                    + schoolPeriod;
            }
        }

        return searchTerms;
    }

    private String normalizeSearchText(
            String text
    ) {
        String value =
            "";

        if (text != null) {
            value =
                Normalizer
                    .normalize(
                        text,
                        Normalizer.Form.NFD
                    )
                    .replaceAll(
                        "\\p{M}",
                        ""
                    )
                    .trim()
                    .toLowerCase();
        }

        return value;
    }

    @FXML
    private void goRegisterEducationalExperience(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.REGISTER_EDUCATIONAL_EXPERIENCE,
            "EducationalExperienceRegisterDashboard.fxml",
            "Acceso denegado al registro de experiencia educativa."
        );
    }

    @FXML
    private void goHome(ActionEvent event) {
        NavigationManager.changeView(
            "CoordinatorProfessorHomeDashboard.fxml"
        );
    }

    @FXML
    private void goEducationalExperienceModule(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.REGISTER_EDUCATIONAL_EXPERIENCE,
            "EducationalExperienceModuleDashboard.fxml",
            "Acceso denegado al modulo de experiencia educativa."
        );
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        openViewWithPermission(
            Permission.CONSULT_INTERN,
            "InternModuleDashboard.fxml",
            "Acceso denegado al modulo de alumnos."
        );
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        openViewWithPermission(
            Permission.CONSULT_PROJECT,
            "ProjectsModuleDashboard.fxml",
            "Acceso denegado al modulo de proyectos."
        );
    }

    @FXML
    private void logOut(ActionEvent event) {
        UserSessionManager.clearSession();

        LOGGER.info(
            "Cierre de sesion realizado correctamente."
        );

        NavigationManager.changeView(
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

            NavigationManager.changeView(
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
        } catch (DataAccessException exception) {
            LOGGER.error(
                "Error de conexion al validar permisos para abrir vista.",
                exception
            );

            FormAlertSupport.showError(
                "Error de conexion",
                "No se pudo conectar con la base de datos para validar los permisos. Por favor intente mas tarde."
            );
        }
    }

    private void validatePermission(
            Permission permission
    ) throws BusinessException, DataAccessException {
        AccessControlManager accessControlManager =
            new AccessControlManager();

        accessControlManager.validatePermission(
            UserSessionManager.getCurrentUser(),
            permission
        );
    }
}


