package mx.uv.internshipprogramsystem.gui.controllers;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ReportDAO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.MonthlyReportContextDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import mx.uv.internshipprogramsystem.logic.managers.ReportManager;
import mx.uv.internshipprogramsystem.logic.managers.ReportExporterManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class ReportHomeDashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportHomeDashboardController.class);

    private static final String PDF_EXTENSION_FILTER = "*.pdf";
    private static final String PDF_EXTENSION_DESC = "Archivos PDF (*.pdf)";
    private static final String PDF_FILE_PREFIX = "Reporte_";
    private static final String PDF_NUMBER_PREFIX = "_No_";
    private static final String PDF_FILE_EXTENSION = ".pdf";

    @FXML private Button btnRegisterMonthlyReport;
    @FXML private Button btnRegisterPartialReport; 
    @FXML private Button btnGenerateReport;
    @FXML private Button btnUploadPDF;
    @FXML private Button btnSendReport;
    @FXML private TextArea txtReportNotes;

    @FXML private TableView<ReportDTO> tblReports;
    @FXML private TableColumn<ReportDTO, Integer> colNumber;
    @FXML private TableColumn<ReportDTO, String> colType;
    @FXML private TableColumn<ReportDTO, LocalDate> colDate;
    @FXML private TableColumn<ReportDTO, String> colStatus;
    @FXML private TextField txtSearchNumber;

    private final ReportDAO reportDAO = new ReportDAO();
    private final ReportManager reportManager = new ReportManager();
    private final ReportExporterManager exporterManager = new ReportExporterManager();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("Inicializando ventana de Módulo de Reportes.");
        
        colNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        try {
            List<ReportDTO> reports = reportDAO.getAllReports();
            tblReports.setItems(FXCollections.observableArrayList(reports));
        } catch (BusinessException businessException) {
            LOGGER.error("Error al inicializar la tabla de reportes: {}", businessException.getMessage());
            showError("Error cargando reportes: " + businessException.getMessage());
        } finally {
            LOGGER.debug("Proceso de inicialización de la tabla finalizado.");
        }
        
        validatePartialReportButton();
    }

    @FXML
    private void generateReportPDF(ActionEvent event) {
        try {
            ReportDTO selectedReport = tblReports.getSelectionModel().getSelectedItem();

            if (selectedReport != null) {
                processPdfGeneration(selectedReport);
            } else {
                showWarning("Debes seleccionar un reporte de la lista antes de generar el PDF.");
            }
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error inesperado en la interfaz al preparar generación de PDF: {}", runtimeException.getMessage());
            showError("Ocurrió un problema inesperado con la interfaz.");
        } finally {
            LOGGER.debug("Intento de preparación de PDF finalizado.");
        }
    }

    private void processPdfGeneration(ReportDTO selectedReport) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar reporte PDF como...");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(PDF_EXTENSION_DESC, PDF_EXTENSION_FILTER));
            fileChooser.setInitialFileName(PDF_FILE_PREFIX + selectedReport.getType() + PDF_NUMBER_PREFIX + selectedReport.getNumber() + PDF_FILE_EXTENSION);

            Stage stage = (Stage) tblReports.getScene().getWindow();
            File selectedFile = fileChooser.showSaveDialog(stage);

            if (selectedFile != null) {
                executePdfExport(selectedReport, selectedFile);
            } else {
                LOGGER.info("El usuario canceló la selección del directorio para guardar el PDF.");
            }
        } catch (ValidationException validationException) {
            LOGGER.error("Error de validación de negocio al generar el PDF: {}", validationException.getMessage());
            showError("Error de validación al generar el PDF: " + validationException.getMessage());
        } finally {
            LOGGER.debug("Proceso de configuración de archivo PDF finalizado.");
        }
    }

    private void executePdfExport(ReportDTO selectedReport, File selectedFile) throws ValidationException {
        Optional<InternDTO> currentIntern = UserSessionManager.getCurrentIntern();
        
        if (currentIntern.isPresent()) {
            Optional<MonthlyReportContextDTO> contextOpt = reportManager.generateMonthlyContext(currentIntern.get().getId());

            if (contextOpt.isPresent()) {
                boolean success = exporterManager.generatePlainPdfReport(selectedReport, contextOpt.get(), selectedFile.getAbsolutePath());
                
                if (success) {
                    showSuccess("El reporte PDF se generó y guardó correctamente en:\n" + selectedFile.getAbsolutePath());
                } else {
                    showError("Ocurrió un problema interno al escribir el archivo PDF.");
                }
            } else {
                showError("No se pudo obtener el contexto de la base de datos para llenar el PDF.");
            }
        } else {
            showError("No hay un Estudiante activo en la sesión.");
        }
    }

    @FXML
    private void uploadReportFile(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar PDF para subir");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(PDF_EXTENSION_DESC, PDF_EXTENSION_FILTER));

            Stage stage = (Stage) tblReports.getScene().getWindow();
            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                processFileUpload(file);
            } else {
                showInfo("No seleccionaste ningún archivo.");
            }
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error visual al intentar abrir selector de archivos: {}", runtimeException.getMessage());
            showError("No se pudo abrir el selector de archivos.");
        } finally {
            LOGGER.debug("Intento de subida de archivo finalizado.");
        }
    }

    private void processFileUpload(File file) {
        try {
            String filePath = file.getAbsolutePath();
            ReportDTO selectedReport = tblReports.getSelectionModel().getSelectedItem();
            
            if (selectedReport != null) {
                selectedReport.setFilePath(filePath);
                reportDAO.updateReportFilePath(selectedReport.getNumber(), filePath);
                showSuccess("La ruta del archivo PDF se guardó en la base de datos:\n" + filePath);
                LOGGER.info("Ruta de archivo actualizada para el reporte No. {}", selectedReport.getNumber());
            } else {
                showWarning("Debes seleccionar un reporte de la lista para asociar la ruta del archivo.");
            }
        } catch (BusinessException businessException) {
            LOGGER.error("Error al actualizar la base de datos con el nuevo archivo: {}", businessException.getMessage());
            showError("No se pudo guardar la ruta del archivo en la base de datos.");
        } finally {
            LOGGER.debug("Procesamiento de vinculación de archivo finalizado.");
        }
    }

    @FXML
    private void sendReport(ActionEvent event) {
        try {
            LOGGER.info("Acción de enviar reporte disparada.");
            showInfo("Funcionalidad de envío pendiente de implementación.");
        } finally {
            LOGGER.debug("Acción de envío de reporte finalizada.");
        }
    }

    private void validatePartialReportButton() {
        try {
            Optional<InternDTO> currentIntern = UserSessionManager.getCurrentIntern();
            if (currentIntern.isPresent()) {
                InternDTO intern = currentIntern.get();
                int studentId = intern.getId();
                
                boolean isEligible = reportManager.canSubmitPartialReport(studentId);
                
                if (isEligible) {
                    btnRegisterPartialReport.setDisable(false);
                } else {
                    btnRegisterPartialReport.setDisable(true);
                }
            }
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al validar elegibilidad para reporte parcial: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Validación de estado del botón de reporte parcial finalizada.");
        }
    }

    @FXML
    private void handleSearch() {
        try {
            LOGGER.info("Iniciando búsqueda de reporte por número.");
            int searchNumber = Integer.parseInt(txtSearchNumber.getText());
            List<ReportDTO> allReports = reportDAO.getAllReports();
            List<ReportDTO> filteredList = new ArrayList<>();
            
            for (ReportDTO report : allReports) {
                if (report.getNumber() == searchNumber) {
                    filteredList.add(report);
                }
            }
            
            tblReports.setItems(FXCollections.observableArrayList(filteredList));

            if (filteredList.isEmpty()) {
                showInfo("No se encontró ningún reporte con número " + searchNumber);
            }
        } catch (NumberFormatException numberException) {
            LOGGER.warn("El usuario ingresó un formato inválido en la búsqueda.");
            showError("El número de búsqueda debe ser un valor entero.");
        } catch (BusinessException businessException) {
            LOGGER.error("Error de base de datos al buscar reportes: {}", businessException.getMessage());
            showError("Error buscando reportes: " + businessException.getMessage());
        } finally {
            LOGGER.debug("Proceso de búsqueda de reportes finalizado.");
        }
    }

    @FXML
    private void handleClearSearch() {
        try {
            LOGGER.info("Limpiando filtros de búsqueda de reportes.");
            List<ReportDTO> reports = reportDAO.getAllReports();
            tblReports.setItems(FXCollections.observableArrayList(reports));
            txtSearchNumber.clear();
        } catch (BusinessException businessException) {
            LOGGER.error("Error al recargar todos los reportes: {}", businessException.getMessage());
            showError("No se pudieron recargar los reportes: " + businessException.getMessage());
        } finally {
            LOGGER.debug("Proceso de limpieza de búsqueda finalizado.");
        }
    }

    @FXML
    private void openRegisterReport(ActionEvent event) {
        try {
            LOGGER.info("Abriendo ventana para registrar reporte mensual.");
            WindowManagerController.changeView("RegisterReport.fxml");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al abrir registro de reporte: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Intento de apertura de registro de reporte finalizado.");
        }
    }

    @FXML
    private void openRegisterPartialReport(ActionEvent event) {
        try {
            LOGGER.info("Abriendo ventana para registrar reporte parcial.");
            WindowManagerController.changeView("RegisterPartialReport.fxml");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al abrir registro de reporte parcial: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Intento de apertura de registro de reporte parcial finalizado.");
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        try {
            LOGGER.info("Navegando al módulo de Inicio del Estudiante.");
            WindowManagerController.changeView("InternHomeDashboard.fxml");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al navegar al módulo de Inicio: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Intento de navegación al módulo de Inicio finalizado.");
        }
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        try {
            LOGGER.info("Navegando al módulo de Proyectos.");
            WindowManagerController.changeView("ProjectHomeDashboard.fxml");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al navegar al módulo de Proyectos: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Intento de navegación al módulo de Proyectos finalizado.");
        }
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        try {
            LOGGER.info("Navegando al módulo de Documentos.");
            WindowManagerController.changeView("DocumentHomeDashboard.fxml");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al navegar al módulo de Documentos: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Intento de navegación al módulo de Documentos finalizado.");
        }
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        try {
            LOGGER.info("Navegando al módulo de Reportes.");
            WindowManagerController.changeView("ReportHomeDashboard.fxml");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al navegar al módulo de Reportes: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Intento de navegación al módulo de Reportes finalizado.");
        }
    }

    @FXML
    private void goSelfAssessmentsModule(ActionEvent event) {
        try {
            LOGGER.info("Navegando al módulo de Autoevaluaciones.");
            WindowManagerController.changeView("SelfAssessmentHomeDashboard.fxml");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al navegar al módulo de Autoevaluaciones: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Intento de navegación al módulo de Autoevaluaciones finalizado.");
        }
    }

    @FXML
    private void logOut(ActionEvent event) {
        try {
            LOGGER.info("Cerrando la sesión del usuario.");
            UserSessionManager.clearSession();
            WindowManagerController.changeView("LoginDashboard.fxml");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al cerrar la sesión: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Intento de cierre de sesión finalizado.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}