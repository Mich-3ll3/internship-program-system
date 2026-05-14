package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectDAO;
import mx.uv.internshipprogramsystem.logic.dao.ReportDAO;
import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;

public class CoordinatorProfessorHomeDashboardController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfesorHomeDashboardController.class);

    @FXML private TableView<InternDTO> tvStudents;
    @FXML private TableView<ProjectDTO> tvProjects;
    @FXML private TableView<ReportDTO> tvReports;
    @FXML private TableColumn<ReportDTO, Integer> tcReportNumber;
    @FXML private TableColumn<ReportDTO, String> tcReportType;
    @FXML private TableColumn<ReportDTO, String> tcReportStatus;
    private final InternDAO internDAO = new InternDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final ReportDAO reportDAO = new ReportDAO();
    private final LinkedOrganizationDAO organizationDAO = new LinkedOrganizationDAO();

    private Map<Integer, String> organizationNames;

    @FXML
    public void initialize() {
        configureTables();
        loadAllData();
    }
    private void configureTables() {
    }
    
    @FXML
     private void goHome(ActionEvent event) {
        WindowManagerController.changeView("CoordinatorProfessorHomeDashboard.fxml");
    }
     
    @FXML
    private void logOut(ActionEvent event) {
        WindowManagerController.changeView("LoginDashboard.fxml");
    }
    
    @FXML
    private void goLinkedOrganizationModule(ActionEvent event) {
        WindowManagerController.changeView("LinkedOrganizationManagementGUI.fxml");
    }
    
    @FXML
    private void goInternModule(ActionEvent event) {
        WindowManagerController.changeView("InternModuleDashboard.fxml");
    }
    
    @FXML
    private void goReportsModule(ActionEvent event) {
        // TODO: lógica para abrir el módulo de reportes
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        // TODO: lógica para abrir el módulo de proyectos
    }
    
    @FXML
    private void saveStateReport(ActionEvent event) {
        // TODO: lógica para guardar estado de reporte
    }

    @FXML
    private void approveDocument(ActionEvent event) {
        // TODO: lógica para aprobar documento
    }

    @FXML
    private void requestChangesDocument(ActionEvent event) {
        // TODO: lógica para solicitar cambios
    }

    @FXML
    private void rejectDocument(ActionEvent event) {
        // TODO: lógica para rechazar documento
    }

    @FXML
    private void goConsultIntern(ActionEvent event) {
        // TODO: lógica para buscar alumno
    }

    @FXML
    private void EvaluateIntern(ActionEvent event) {
        // TODO: lógica para evaluar alumno
    }

    @FXML
    private void CommentIntern(ActionEvent event) {
        // TODO: lógica para comentar sobre alumno
    }
    
    @FXML
    private void addLinkedOrganization(ActionEvent event) {
        // TODO: lógica para agregar organización vinculada
    }
    
    @FXML
    private void addProject(ActionEvent event) {
        // TODO: lógica para agregar proyecto
    }

    @FXML
    private void filtrarResponsablesPorOrganizacion(ActionEvent event) {
        // TODO: lógica para filtrar responsables según organización
    }

    @FXML
    private void assignInternProject(ActionEvent event) {
        // TODO: lógica para asignar proyecto a alumno
    }
    
    private void setupReportStatusStyle() {
        tcReportStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status.toLowerCase()) {
                        case "aprobado":
                            setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #166534; -fx-background-radius: 4; -fx-alignment: CENTER;");
                            break;
                        case "pendiente":
                            setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #92400e; -fx-background-radius: 4; -fx-alignment: CENTER;");
                            break;
                        case "rechazado":
                            setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-background-radius: 4; -fx-alignment: CENTER;");
                            break;
                    }
                }
            }
        });
    }

    private void loadAllData() {
        try {
            List<LinkedOrganizationDTO> orgs = organizationDAO.findAll();
            organizationNames = orgs.stream().collect(Collectors.toMap(LinkedOrganizationDTO::getId, LinkedOrganizationDTO::getName));

            tvStudents.setItems(FXCollections.observableArrayList(internDAO.findAll()));
            tvProjects.setItems(FXCollections.observableArrayList(projectDAO.findAll()));
            
            LOGGER.info("Dashboard cargado con éxito incluyendo Reportes.");
        } catch (BusinessException e) {
            LOGGER.error("Error al sincronizar datos con la base de datos", e);
        }
    }

    @FXML
    private void handleEvaluateReport(ReportDTO selectedReport) {
        try {
            boolean success = reportDAO.evaluateReport(selectedReport.getId(), "Aprobado", "Excelente trabajo.");
            if (success) {
                loadAllData();
            }
        } catch (BusinessException e) {
            LOGGER.error("No se pudo evaluar el reporte", e);
        }
    }
}