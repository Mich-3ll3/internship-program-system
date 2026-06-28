package mx.uv.internshipprogramsystem.gui.controllers;

import java.io.File;
import java.io.IOException;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.SelfAssessmentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.SelfAssessmentManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class SelfAssessmentHomeDashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelfAssessmentHomeDashboardController.class);

    // Constantes para el manejo de archivos y diseño del PDF
    private static final String PDF_EXTENSION_FILTER = "*.pdf";
    private static final String PDF_EXTENSION_DESC = "Archivos PDF (*.pdf)";
    
    private static final int PDF_FONT_SIZE = 12;
    private static final int PDF_START_X = 50;
    private static final int PDF_START_Y = 700;
    private static final int PDF_LINE_OFFSET_X = 0;
    private static final int PDF_LINE_OFFSET_Y = -20;

    @FXML private Button btnRegisterSelfAssessment;
    @FXML private Button btnPrintSelfAssessment;
    @FXML private Button btnConsultSelfAssessment;

    @FXML private TableView<SelfAssessmentDTO> tblSelfAssessments;

    @FXML private TableColumn<SelfAssessmentDTO, String> colStudent;
    @FXML private TableColumn<SelfAssessmentDTO, String> colProject;
    @FXML private TableColumn<SelfAssessmentDTO, String> colResponsible;
    @FXML private TableColumn<SelfAssessmentDTO, String> colOrganization;
    @FXML private TableColumn<SelfAssessmentDTO, LocalDate> colDate;
    @FXML private TableColumn<SelfAssessmentDTO, String> colDepartment;
    @FXML private TableColumn<SelfAssessmentDTO, String> colPlace;

    @FXML private TextField txtSearchName;

    private final SelfAssessmentManager selfAssessmentManager = new SelfAssessmentManager();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("Inicializando ventana de Autoevaluaciones.");

        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colProject.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        colResponsible.setCellValueFactory(new PropertyValueFactory<>("responsibleName"));
        colOrganization.setCellValueFactory(new PropertyValueFactory<>("organizationName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colPlace.setCellValueFactory(new PropertyValueFactory<>("place"));

        loadAssessments();
    }

    private void loadAssessments() {
        try {
            Optional<InternDTO> currentIntern = UserSessionManager.getCurrentIntern();
            if (currentIntern.isPresent()) {
                int studentId = currentIntern.get().getId();
                List<SelfAssessmentDTO> assessments = selfAssessmentManager.getSelfAssessmentsByStudentId(studentId);
                tblSelfAssessments.setItems(FXCollections.observableArrayList(assessments));
            } else {
                showError("No hay una sesión de estudiante activa.");
            }
        } catch (BusinessException businessException) {
            LOGGER.error("Error al cargar autoevaluaciones desde la BD: {}", businessException.getMessage());
            showError("Error cargando autoevaluaciones: " + businessException.getMessage());
        } finally {
            LOGGER.debug("Proceso de carga de autoevaluaciones finalizado.");
        }
    }

    // ==========================================
    // LÓGICA DE NEGOCIO Y PDF
    // ==========================================

    @FXML
    private void openRegisterSelfAssessment(ActionEvent actionEvent) {
        try {
            LOGGER.info("Abriendo ventana de registro de autoevaluación.");
            WindowManagerController.changeView("RegisterSelfAssessment.fxml");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al abrir registro de autoevaluación: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Intento de apertura de registro finalizado.");
        }
    }

    @FXML
    private void printSelfAssessment(ActionEvent actionEvent) {
        try {
            SelfAssessmentDTO selected = tblSelfAssessments.getSelectionModel().getSelectedItem();
            
            if (selected != null) {
                processPdfGeneration(selected);
            } else {
                showWarning("Debes seleccionar una autoevaluación para imprimir.");
            }
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error visual al preparar impresión de PDF: {}", runtimeException.getMessage());
            showError("Ocurrió un problema inesperado con la interfaz.");
        } finally {
            LOGGER.debug("Intento de preparación de impresión de PDF finalizado.");
        }
    }

    private void processPdfGeneration(SelfAssessmentDTO selected) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar autoevaluación como PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(PDF_EXTENSION_DESC, PDF_EXTENSION_FILTER));

            Stage stage = (Stage) tblSelfAssessments.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                createPDF(selected, file);
                showSuccess("La autoevaluación del proyecto " + selected.getProjectName() +
                            " se guardó correctamente en:\n" + file.getAbsolutePath());
                LOGGER.info("PDF de autoevaluación generado exitosamente en: {}", file.getAbsolutePath());
            } else {
                LOGGER.info("El usuario canceló la selección del directorio para guardar el PDF.");
            }
        } catch (IOException ioException) {
            LOGGER.error("Error de escritura al generar el PDF: {}", ioException.getMessage());
            showError("Ocurrió un problema al guardar el archivo PDF: " + ioException.getMessage());
        } finally {
            LOGGER.debug("Proceso de generación de PDF finalizado.");
        }
    }

    private void createPDF(SelfAssessmentDTO assessment, File file) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA, PDF_FONT_SIZE);

                contentStream.beginText();
                contentStream.newLineAtOffset(PDF_START_X, PDF_START_Y);
                contentStream.showText("Autoevaluación del Alumno");
                
                contentStream.newLineAtOffset(PDF_LINE_OFFSET_X, PDF_LINE_OFFSET_Y);
                contentStream.showText("Fecha: " + assessment.getDate());
                
                contentStream.newLineAtOffset(PDF_LINE_OFFSET_X, PDF_LINE_OFFSET_Y);
                contentStream.showText("Proyecto: " + assessment.getProjectName());
                
                contentStream.newLineAtOffset(PDF_LINE_OFFSET_X, PDF_LINE_OFFSET_Y);
                contentStream.showText("Departamento: " + assessment.getDepartment());
                
                contentStream.newLineAtOffset(PDF_LINE_OFFSET_X, PDF_LINE_OFFSET_Y);
                contentStream.showText("Lugar: " + assessment.getPlace());
                contentStream.endText();
            }
            
            document.save(file);
        }
    }

    @FXML
    private void consultSelfAssessment(ActionEvent actionEvent) {
        try {
            SelfAssessmentDTO selected = tblSelfAssessments.getSelectionModel().getSelectedItem();
            
            if (selected != null) {
                LOGGER.info("Consultando autoevaluación del proyecto: {}", selected.getProjectName());
                showInfo("Consulta de autoevaluación del proyecto: " + selected.getProjectName());
            } else {
                showWarning("Debes seleccionar una autoevaluación para consultar.");
            }
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al consultar autoevaluación: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Acción de consulta de autoevaluación finalizada.");
        }
    }

    // ==========================================
    // BÚSQUEDA Y FILTRADO (SIN LAMBDAS)
    // ==========================================

    @FXML
    private void handleSearchByName(ActionEvent actionEvent) {
        try {
            String searchName = txtSearchName.getText().trim().toLowerCase();
            
            if (searchName.isEmpty()) {
                showWarning("Ingresa un nombre de proyecto para buscar.");
            } else {
                executeSearchQuery(searchName);
            }
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error visual al procesar la búsqueda: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Intento de búsqueda por nombre finalizado.");
        }
    }

    private void executeSearchQuery(String searchName) {
        try {
            Optional<InternDTO> currentIntern = UserSessionManager.getCurrentIntern();
            if (currentIntern.isPresent()) {
                int studentId = currentIntern.get().getId();
                List<SelfAssessmentDTO> assessments = selfAssessmentManager.getSelfAssessmentsByStudentId(studentId);
                List<SelfAssessmentDTO> filtered = new ArrayList<>();

                for (SelfAssessmentDTO assessment : assessments) {
                    if (assessment.getProjectName() != null && 
                        assessment.getProjectName().toLowerCase().contains(searchName)) {
                        filtered.add(assessment);
                    }
                }

                tblSelfAssessments.setItems(FXCollections.observableArrayList(filtered));
                LOGGER.info("Búsqueda completada. Se encontraron {} resultados.", filtered.size());
                
                if (filtered.isEmpty()) {
                    showInfo("No se encontraron autoevaluaciones con ese nombre de proyecto.");
                }
            }
        } catch (BusinessException businessException) {
            LOGGER.error("Error de BD al buscar autoevaluaciones: {}", businessException.getMessage());
            showError("Error al buscar autoevaluaciones: " + businessException.getMessage());
        } finally {
            LOGGER.debug("Proceso de filtrado de autoevaluaciones finalizado.");
        }
    }

    @FXML
    private void handleClearSearchByName(ActionEvent actionEvent) {
        try {
            LOGGER.info("Limpiando búsqueda de autoevaluaciones.");
            txtSearchName.clear();
            loadAssessments();
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al limpiar la búsqueda: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Limpieza de búsqueda finalizada.");
        }
    }

    // ==========================================
    // MÉTODOS DE NAVEGACIÓN DEL MÓDULO
    // ==========================================

    @FXML
    private void goHome(ActionEvent actionEvent) {
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
    private void goProjectsModule(ActionEvent actionEvent) {
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
    private void goDocumentsModule(ActionEvent actionEvent) {
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
    private void goReportsModule(ActionEvent actionEvent) {
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
    private void goSelfAssessmentsModule(ActionEvent actionEvent) {
        try {
            LOGGER.info("Navegando al módulo de Autoevaluaciones (Recarga).");
            WindowManagerController.changeView("SelfAssessmentHomeDashboard.fxml");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al navegar al módulo de Autoevaluaciones: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("Intento de navegación al módulo de Autoevaluaciones finalizado.");
        }
    }

    @FXML
    private void logOut(ActionEvent actionEvent) {
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

    // ==========================================
    // ALERTAS
    // ==========================================

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