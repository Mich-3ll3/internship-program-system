package mx.uv.internshipprogramsystem.gui.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectDAO;
import mx.uv.internshipprogramsystem.logic.dao.ReportDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public class CoordinatorProfessorHomeDashboardController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            CoordinatorProfessorHomeDashboardController.class
        );

    @FXML
    private TableView<InternDTO> tvStudents;

    @FXML
    private TableView<ProjectDTO> tvProjects;

    @FXML
    private TableView<ReportDTO> tvReports;

    @FXML
    private TableColumn<ReportDTO, Integer> tcReportNumber;

    @FXML
    private TableColumn<ReportDTO, String> tcReportType;

    @FXML
    private TableColumn<ReportDTO, String> tcReportStatus;

    private final InternDAO internDAO =
        new InternDAO();

    private final ProjectDAO projectDAO =
        new ProjectDAO();

    private final ReportDAO reportDAO =
        new ReportDAO();

    private final LinkedOrganizationDAO organizationDAO =
        new LinkedOrganizationDAO();

    private Map<Integer, String> organizationNames;

    @FXML
    public void initialize() {
        try {
            configureTables();
            loadAllData();

            LOGGER.info(
                "Dashboard de coordinador cargado correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error cargando dashboard de coordinador",
                businessException
            );
        }
    }

    private void configureTables() {
        tcReportNumber.setCellValueFactory(
            new PropertyValueFactory<>("id")
        );

        tcReportType.setCellValueFactory(
            new PropertyValueFactory<>("type")
        );

        tcReportStatus.setCellValueFactory(
            new PropertyValueFactory<>("status")
        );

        setupReportStatusStyle();
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView(
            "CoordinatorProfessorHomeDashboard.fxml"
        );
    }

    @FXML
    private void logOut(ActionEvent event) {
        LOGGER.info(
            "Cierre de sesión realizado correctamente."
        );

        WindowManagerController.changeView(
            "LoginDashboard.fxml"
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
    private void goInternModule(ActionEvent event) {
        WindowManagerController.changeView(
            "InternModuleDashboard.fxml"
        );
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        LOGGER.info(
            "Acceso al módulo de reportes."
        );
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        LOGGER.info(
            "Acceso al módulo de proyectos."
        );
    }

    @FXML
    private void saveStateReport(ActionEvent event) {
        LOGGER.info(
            "Guardado de estado de reporte ejecutado."
        );
    }

    @FXML
    private void approveDocument(ActionEvent event) {
        LOGGER.info(
            "Documento aprobado correctamente."
        );
    }

    @FXML
    private void requestChangesDocument(
            ActionEvent event
    ) {
        LOGGER.info(
            "Solicitud de cambios enviada correctamente."
        );
    }

    @FXML
    private void rejectDocument(ActionEvent event) {
        LOGGER.info(
            "Documento rechazado correctamente."
        );
    }

    @FXML
    private void goConsultIntern(ActionEvent event) {
        LOGGER.info(
            "Consulta de estudiante ejecutada."
        );
    }

    @FXML
    private void evaluateIntern(ActionEvent event) {
        LOGGER.info(
            "Evaluación de estudiante ejecutada."
        );
    }

    @FXML
    private void commentIntern(ActionEvent event) {
        LOGGER.info(
            "Comentario de estudiante registrado."
        );
    }

    @FXML
    private void addLinkedOrganization(
            ActionEvent event
    ) {
        LOGGER.info(
            "Registro de organización vinculada ejecutado."
        );
    }

    @FXML
    private void addProject(ActionEvent event) {
        LOGGER.info(
            "Registro de proyecto ejecutado."
        );
    }

    @FXML
    private void filterResponsiblesByOrganization(
            ActionEvent event
    ) {
        LOGGER.info(
            "Filtrado de responsables ejecutado."
        );
    }

    @FXML
    private void assignInternProject(ActionEvent event) {
        LOGGER.info(
            "Asignación de proyecto realizada."
        );
    }

    private void setupReportStatusStyle() {
        tcReportStatus.setCellFactory(column ->
            new TableCell<>() {
                @Override
                protected void updateItem(
                        String status,
                        boolean empty
                ) {
                    super.updateItem(status, empty);

                    if (empty || status == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(status);

                        switch (status.toLowerCase()) {
                            case "aprobado":
                                setStyle(
                                    "-fx-background-color: #dcfce7;"
                                    + "-fx-text-fill: #166534;"
                                    + "-fx-background-radius: 4;"
                                    + "-fx-alignment: CENTER;"
                                );
                                break;

                            case "pendiente":
                                setStyle(
                                    "-fx-background-color: #fef3c7;"
                                    + "-fx-text-fill: #92400e;"
                                    + "-fx-background-radius: 4;"
                                    + "-fx-alignment: CENTER;"
                                );
                                break;

                            case "rechazado":
                                setStyle(
                                    "-fx-background-color: #fee2e2;"
                                    + "-fx-text-fill: #991b1b;"
                                    + "-fx-background-radius: 4;"
                                    + "-fx-alignment: CENTER;"
                                );
                                break;

                            default:
                                setStyle("");
                                break;
                        }
                    }
                }
            }
        );
    }

    private void loadAllData()
            throws BusinessException {
        List<LinkedOrganizationDTO> organizations =
            organizationDAO.findAll();

        organizationNames =
            organizations.stream().collect(
                Collectors.toMap(
                    LinkedOrganizationDTO::getId,
                    LinkedOrganizationDTO::getName
                )
            );

        tvStudents.setItems(
            FXCollections.observableArrayList(
                internDAO.findAll()
            )
        );

        tvProjects.setItems(
            FXCollections.observableArrayList(
                projectDAO.findAll()
            )
        );

        LOGGER.info(
            "Dashboard sincronizado correctamente."
        );
    }

    @FXML
    private void handleEvaluateReport(
            ReportDTO selectedReport
    ) {
        try {
            boolean wasEvaluated =
                reportDAO.evaluateReport(
                    selectedReport.getId(),
                    "Aprobado",
                    "Excelente trabajo."
                );

            if (wasEvaluated) {
                loadAllData();

                LOGGER.info(
                    "Reporte evaluado correctamente."
                );
            }
        } catch (BusinessException businessException) {
            LOGGER.error(
                "No se pudo evaluar el reporte",
                businessException
            );
        }
    }
}