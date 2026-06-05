package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import mx.uv.internshipprogramsystem.logic.dto.InternDTO; 

public class InternHomeDashboardController implements Initializable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InternHomeDashboardController.class);

    private static final String DATE_PATTERN = "EEEE, d 'de' MMMM 'de' yyyy";
    private static final String LOCALE_LANGUAGE = "es";
    private static final String LOCALE_COUNTRY = "MX";
    private static final int FIRST_LETTER_INDEX = 0;
    private static final int REST_OF_WORD_INDEX = 1;

    private static final String DEFAULT_MAJOR = "Ingeniería en Software";
    private static final String DETAILS_FORMAT = "%s • Matrícula %s • %s";

    @FXML
    private Button btnHome;

    @FXML
    private Button btnProjects;

    @FXML
    private Button btnDocuments;

    @FXML
    private Button btnReports;

    @FXML
    private Button btnExit;

    @FXML
    private Label lblFecha;

    @FXML
    private Label lblStudentName;

    @FXML
    private Label lblStudentDetails;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LOGGER.info("Dashboard de estudiante cargado correctamente.");
        configureCurrentDate();
        configureInternData();
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

    @FXML
    private void goHome(ActionEvent event) {
        LOGGER.info("Acceso al inicio del estudiante.");
        WindowManagerController.changeView("InternHomeDashboard.fxml");
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        LOGGER.info("Acceso al módulo de proyectos.");
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        LOGGER.info("Acceso al módulo de documentos.");
    }
    
    @FXML
    private void goReportsModule(ActionEvent event) {
        WindowManagerController.changeView("ReportHomeDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        UserSessionManager.clearSession();
        LOGGER.info("Cierre de sesión realizado correctamente.");
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

    @FXML
    private void abrirProyecto(ActionEvent event) {
        LOGGER.info("Apertura de detalle de proyecto.");
    }

    @FXML
    private void abrirDocumentos(ActionEvent event) {
        LOGGER.info("Acceso a documentos del practicante.");
    }

    @FXML
    private void subirDocumento(ActionEvent event) {
        LOGGER.info("Carga de documento iniciada.");
    }
    
    @FXML
    private void goSelfAssessmentsModule(ActionEvent event) {
        WindowManagerController.changeView("SelfAssessmentHomeDashboard.fxml");
    }

    @FXML
    private void registrarAutoevaluacion(ActionEvent event) {
        WindowManagerController.changeView("RegisterSelfAssessment.fxml");
    }

    @FXML
    private void verComentariosDocumento(ActionEvent event) {
        LOGGER.info("Consulta de comentarios del documento.");
    }

    @FXML
    private void abrirReportes(ActionEvent event) {
        LOGGER.info("Consulta de historial de reportes.");
    }

    @FXML
    private void abrirReporte(ActionEvent event) {
        LOGGER.info("Apertura de reporte individual.");
    }

    @FXML
    private void generarReporte(ActionEvent event) {
        LOGGER.info("Generación de reporte iniciada.");
    }

    @FXML
    private void enviarReporte(ActionEvent event) {
        LOGGER.info("Envío de reporte realizado.");
    }
}