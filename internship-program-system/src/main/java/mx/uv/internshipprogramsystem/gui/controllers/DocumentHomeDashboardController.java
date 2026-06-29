package mx.uv.internshipprogramsystem.gui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.managers.DocumentManager;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.DocumentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public class DocumentHomeDashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentHomeDashboardController.class);

    private static final String TYPE_SCHEDULE = "Horario";
    private static final String TYPE_SOCIAL_SECURITY = "Seguro Social";
    private static final String TYPE_ACTIVITY_SCHEDULE = "Cronograma de Actividades";

    @FXML private TableView<DocumentDTO> tblDocuments;
    @FXML private TableColumn<DocumentDTO, String> colDocumentType;
    @FXML private TableColumn<DocumentDTO, String> colUploadDate;
    @FXML private TableColumn<DocumentDTO, String> colStatus;

    @FXML private ComboBox<String> cmbDocumentType;
    @FXML private Button btnUploadDocument;
    @FXML private Button btnViewComments;
    @FXML private Label lblSystemMessage;

    @FXML private Button btnHome;
    @FXML private Button btnProjects;
    @FXML private Button btnReports;
    @FXML private Button btnSelfAssessments;
    @FXML private Button btnExit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LOGGER.info("Document Dashboard module loaded.");
        configureTableColumns();
        populateDocumentTypes();
        loadStudentDocuments();
    }

    private void configureTableColumns() {
        colDocumentType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colUploadDate.setCellValueFactory(new PropertyValueFactory<>("uploadDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void populateDocumentTypes() {
        cmbDocumentType.getItems().addAll(
            TYPE_SCHEDULE, 
            TYPE_SOCIAL_SECURITY, 
            TYPE_ACTIVITY_SCHEDULE
        );
    }

    private void loadStudentDocuments() {
        try {
            LOGGER.info("Fetching previously uploaded documents for the current intern.");
            Optional<InternDTO> currentInternOptional = UserSessionManager.getCurrentIntern();

            if (currentInternOptional.isPresent()) {
                int internId = currentInternOptional.get().getId();
                DocumentManager documentManager = new DocumentManager();
                
                List<DocumentDTO> internDocuments = documentManager.getDocumentsByInternId(internId);
                ObservableList<DocumentDTO> observableDocuments = FXCollections.observableArrayList(internDocuments);
                
                tblDocuments.setItems(observableDocuments);
            } else {
                LOGGER.warn("No active intern session found to load documents.");
                updateSystemMessage("Error: No se encontró la sesión del estudiante.", false);
            }
        } catch (BusinessException businessLogicException) {
            LOGGER.error("Business rule violation while loading documents: {}", businessLogicException.getMessage());
            updateSystemMessage("Ocurrió un error al cargar sus documentos.", false);
        } finally {
            LOGGER.debug("Load student documents process finished.");
        }
    }

    // ==========================================
    // LÓGICA REFACTORIZADA DE SUBIDA DE ARCHIVOS
    // ==========================================

    @FXML
    private void uploadSelectedDocument(ActionEvent event) {
        try {
            LOGGER.info("Upload document action triggered.");
            boolean isValidSelection = validateDocumentSelection();

            if (isValidSelection) {
                processDocumentUpload();
            } else {
                updateSystemMessage("Por favor, seleccione qué documento desea subir.", false);
            }
        } catch (IOException ioException) {
            LOGGER.error("File system error during document copy: {}", ioException.getMessage());
            updateSystemMessage("Error al copiar el archivo al directorio del sistema.", false);
        } catch (BusinessException businessException) {
            LOGGER.error("Business error during document registration: {}", businessException.getMessage());
            updateSystemMessage(businessException.getMessage(), false);
        } finally {
            LOGGER.debug("Upload document evaluation concluded.");
        }
    }

    private void processDocumentUpload() throws IOException, BusinessException {
        Optional<InternDTO> currentInternOptional = UserSessionManager.getCurrentIntern();

        if (currentInternOptional.isPresent()) {
            InternDTO currentIntern = currentInternOptional.get();
            File selectedFile = chooseFileFromSystem();
            
            if (selectedFile != null) {
                File destinationFile = copyFileToProject(selectedFile, currentIntern.getEnrollmentNumber());
                registerDocumentInSystem(destinationFile, currentIntern.getId());
            } else {
                updateSystemMessage("Operación cancelada. No se seleccionó ningún archivo.", false);
            }
        } else {
            updateSystemMessage("La sesión ha expirado. Vuelva a iniciar sesión.", false);
        }
    }

    private File chooseFileFromSystem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Documento PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documentos PDF", "*.pdf"));
        
        Stage currentStage = (Stage) btnUploadDocument.getScene().getWindow();
        return fileChooser.showOpenDialog(currentStage);
    }

    private File copyFileToProject(File selectedFile, String enrollmentNumber) throws IOException {
        File destinationDirectory = new File("uploaded_documents" + File.separator + enrollmentNumber);
        
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdirs();
        }
        
        File destinationFile = new File(destinationDirectory, selectedFile.getName());
        Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        return destinationFile;
    }

    private void registerDocumentInSystem(File destinationFile, int internId) throws BusinessException {
        String selectedDocumentType = cmbDocumentType.getValue();
        int assignedProfessorId = 5; // Temporal, por ajustar a consulta dinámica
        
        DocumentDTO newDocument = new DocumentDTO();
        newDocument.setType(selectedDocumentType);
        newDocument.setPath(destinationFile.getAbsolutePath());
        newDocument.setName(destinationFile.getName());
        
        DocumentManager documentManager = new DocumentManager();
        boolean isRegistered = documentManager.registerDocument(newDocument, internId, assignedProfessorId);
        
        if (isRegistered) {
            updateSystemMessage("¡Documento subido y registrado con éxito!", true);
            loadStudentDocuments(); 
        } else {
            updateSystemMessage("Error al registrar el documento en la base de datos.", false);
        }
    }

    @FXML
    private void viewDocumentComments(ActionEvent event) {
        try {
            DocumentDTO selectedDocument = tblDocuments.getSelectionModel().getSelectedItem();
            boolean hasSelection = (selectedDocument != null);

            if (hasSelection) {
                LOGGER.info("Opening comments for document ID: {}", selectedDocument.getId());
                // TODO: Abrir ventana o Dialog con los comentarios del profesor
            } else {
                updateSystemMessage("Seleccione un documento de la tabla para ver sus comentarios.", false);
            }
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error opening document comments: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug("View document comments evaluation concluded.");
        }
    }

    private boolean validateDocumentSelection() {
        boolean isValid = false;
        String selectedType = cmbDocumentType.getValue();
        
        if (selectedType != null && !selectedType.trim().isEmpty()) {
            isValid = true;
        }
        
        return isValid;
    }

    private void updateSystemMessage(String message, boolean isSuccess) {
        if (lblSystemMessage != null) {
            lblSystemMessage.setText(message);
            if (isSuccess) {
                lblSystemMessage.setStyle("-fx-text-fill: #16a34a;"); // Verde
            } else {
                lblSystemMessage.setStyle("-fx-text-fill: #dc2626;"); // Rojo
            }
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        LOGGER.info("Navigating to Intern Home module.");
        WindowManagerController.changeView("InternHomeDashboard.fxml");
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        LOGGER.info("Navigating to Projects module.");
        WindowManagerController.changeView("ProjectHomeDashboard.fxml");
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        LOGGER.info("Navigating to Reports module.");
        WindowManagerController.changeView("ReportHomeDashboard.fxml");
    }

    @FXML
    private void goSelfAssessmentsModule(ActionEvent event) {
        LOGGER.info("Navigating to Self-Assessments module.");
        WindowManagerController.changeView("SelfAssessmentHomeDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        UserSessionManager.clearSession();
        LOGGER.info("User session cleared successfully.");
        WindowManagerController.changeView("LoginDashboard.fxml");
    }
}