package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.managers.ProjectApplicationManager;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO; 
import mx.uv.internshipprogramsystem.logic.dto.ProjectApplicationDTO;

public class InternHomeDashboardController implements Initializable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InternHomeDashboardController.class);

    private static final String DATE_PATTERN = "EEEE, d 'de' MMMM 'de' yyyy";
    private static final String LOCALE_LANGUAGE = "es";
    private static final String LOCALE_COUNTRY = "MX";
    private static final int FIRST_LETTER_INDEX = 0;
    private static final int REST_OF_WORD_INDEX = 1;

    private static final String DEFAULT_MAJOR = "Ingeniería en Software";
    private static final String DETAILS_FORMAT = "%s • Matrícula %s • %s";

    @FXML private Button btnHome;
    @FXML private Button btnProjects;
    @FXML private Button btnDocuments;
    @FXML private Button btnReports;
    @FXML private Button btnExit;
    @FXML private Label lblFecha;
    @FXML private Label lblStudentName;
    @FXML private Label lblStudentDetails;
    @FXML private Label lblEstadoProyecto;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LOGGER.info("Dashboard de estudiante cargado correctamente.");
        configureCurrentDate();
        configureInternData();
        checkAndSetProjectStatus();
    }

    private void configureCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        Locale regionalConfiguration = new Locale(LOCALE_LANGUAGE, LOCALE_COUNTRY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN, regionalConfiguration);
        
        String formattedDate = currentDate.format(formatter);
        
        if (formattedDate != null && !formattedDate.isEmpty()) {
            formattedDate = Character.toUpperCase(formattedDate.charAt(FIRST_LETTER_INDEX)) 
                            + formattedDate.substring(REST_OF_WORD_INDEX);
        }
        
        lblFecha.setText(formattedDate);
    }

    private void configureInternData() {
        Optional<InternDTO> currentInternOpt = UserSessionManager.getCurrentIntern();

        if (currentInternOpt.isPresent()) {
            InternDTO currentIntern = currentInternOpt.get();

            String fullName = String.format("%s %s %s", 
                currentIntern.getName(), 
                currentIntern.getFirstSurname(), 
                currentIntern.getSecondSurname()
            ).trim();

            String internDetails = String.format(DETAILS_FORMAT, 
                DEFAULT_MAJOR, 
                currentIntern.getEnrollmentNumber(), 
                currentIntern.getInstitutionalEmail() 
            );

            lblStudentName.setText(fullName);
            lblStudentDetails.setText(internDetails);
        } else {
            LOGGER.warn("No se encontró información del practicante en la sesión activa.");
            lblStudentName.setText("Usuario Desconocido");
            lblStudentDetails.setText("Información no disponible");
        }
    }

    private void checkAndSetProjectStatus() {
        try {
            Optional<InternDTO> currentInternOpt = UserSessionManager.getCurrentIntern();
            
            if (currentInternOpt.isPresent()) {
                int estudianteIdActual = currentInternOpt.get().getId();
                ProjectApplicationManager applicationManager = new ProjectApplicationManager();
                List<ProjectApplicationDTO> applications = applicationManager.getActiveApplications(estudianteIdActual);
                
                boolean isAccepted = false;
                int pendingCount = 0;

                for (ProjectApplicationDTO app : applications) {
                    if ("ACEPTADA".equalsIgnoreCase(app.getStatus())) {
                        isAccepted = true;
                        break; 
                    } else if ("PENDIENTE".equalsIgnoreCase(app.getStatus())) {
                        pendingCount++;
                    }
                }

                if (isAccepted) {
                    lblEstadoProyecto.setText("Activo");
                    lblEstadoProyecto.setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-padding: 3 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 16;");
                    
                } else if (pendingCount == 1) {
                    lblEstadoProyecto.setText("Postulación pendiente");
                    lblEstadoProyecto.setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-padding: 3 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 13;");
                    
                } else if (pendingCount > 1) {
                    lblEstadoProyecto.setText("Postulaciones pendientes");
                    lblEstadoProyecto.setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-padding: 3 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 13;");
                    
                } else {
                    setProjectStatusInactive();
                }
            } else {
                setProjectStatusInactive();
            }

        } catch (Exception ex) {
            LOGGER.error("Error al cargar el estado de las postulaciones: {}", ex.getMessage());
            setProjectStatusInactive();
        } finally {
            LOGGER.debug("Proceso de validación de estado de proyecto finalizado.");
        }
    }

    private void setProjectStatusInactive() {
        if (lblEstadoProyecto != null) {
            lblEstadoProyecto.setText("Inactivo");
            lblEstadoProyecto.setStyle("-fx-background-color: #e2e3e5; -fx-text-fill: #383d41; -fx-padding: 3 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 16;");
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        LOGGER.info("Acceso al inicio del estudiante.");
        WindowManagerController.changeView("InternHomeDashboard.fxml");
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        LOGGER.info("Acceso al módulo de proyectos.");
        WindowManagerController.changeView("ProjectHomeDashboard.fxml");
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        LOGGER.info("Acceso al módulo de documentos.");
        WindowManagerController.changeView("DocumentHomeDashboard.fxml"); // ¡Ya puedes descomentarlo!
    }
    
    @FXML
    private void goReportsModule(ActionEvent event) {
        LOGGER.info("Acceso al módulo de reportes.");
        WindowManagerController.changeView("ReportHomeDashboard.fxml");
    }

    @FXML
    private void goSelfAssessmentsModule(ActionEvent event) {
        LOGGER.info("Acceso al módulo de autoevaluaciones.");
        WindowManagerController.changeView("SelfAssessmentHomeDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        UserSessionManager.clearSession();
        LOGGER.info("Cierre de sesión realizado.");
        WindowManagerController.changeView("LoginDashboard.fxml");
    }
}