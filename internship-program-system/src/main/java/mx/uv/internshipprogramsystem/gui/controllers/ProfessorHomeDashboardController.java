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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class ProfessorHomeDashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorHomeDashboardController.class);

    // ==========================================
    // COMPONENTES DE LA INTERFAZ (FXML)
    // ==========================================
    @FXML private Button btnHome;
    @FXML private Button btnAlumnosNav;
    @FXML private Button btnDocumentosNav;
    @FXML private Button btnReportesNav;
    @FXML private Button btnSalir;

    @FXML private Label lblFecha;
    @FXML private Label lblProfessorInitials;
    @FXML private Label lblProfessorName;
    @FXML private Label lblProfessorDetails;
    
    @FXML private Label lblTotalAlumnos;
    @FXML private Label lblTotalGrupos;
    @FXML private Label lblAvanceProyecto;
    @FXML private ProgressBar progressProyecto;

    @FXML private Label lblPorRevisar;
    @FXML private Label lblReportesRecibidos;
    @FXML private Label lblAprobados;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("Inicializando el panel principal del profesor.");
        configureCurrentDate();
        loadPersonalData();
    }

    private void configureCurrentDate() {
        try {
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "MX"));
            
            String formattedDate = currentDate.format(formatter);
            formattedDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
            lblFecha.setText(formattedDate);
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al configurar la fecha del sistema: {}", runtimeException.getMessage());
            lblFecha.setText("Fecha no disponible");
        }
    }

    private void loadPersonalData() {
        try {
            LOGGER.info("Cargando datos personales del profesor desde la sesión.");
            
            Optional<ProfessorDTO> currentProfessorOpt = UserSessionManager.getCurrentProfessor();
            
            if (currentProfessorOpt.isPresent()) {
                ProfessorDTO professor = currentProfessorOpt.get();
                
                lblProfessorName.setText(professor.getFullName());
                
                String initials = "";
                if (professor.getName() != null && !professor.getName().trim().isEmpty()) {
                    initials += professor.getName().trim().charAt(0);
                }
                if (professor.getFirstSurname() != null && !professor.getFirstSurname().trim().isEmpty()) {
                    initials += professor.getFirstSurname().trim().charAt(0);
                }
                lblProfessorInitials.setText(initials.toUpperCase());
                
                String roleDisplay = "Profesor";
                if (Boolean.TRUE.equals(professor.getIsCoordinator())) {
                    roleDisplay = "Coordinador";
                }
                
                lblProfessorDetails.setText(roleDisplay + " • Número de personal: " + professor.getStaffNumber());
            } else {
                LOGGER.warn("No se encontró una sesión de profesor activa.");
                lblProfessorName.setText("Profesor Invitado");
                lblProfessorInitials.setText("PI");
                lblProfessorDetails.setText("Sesión temporal o caducada");
            }
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al cargar los datos personales: {}", runtimeException.getMessage());
            lblProfessorName.setText("Error de Carga");
        }
    }

    // ==========================================
    // MÉTODOS DE NAVEGACIÓN EN INGLÉS
    // ==========================================

    @FXML
    private void goHome(ActionEvent event) {
        LOGGER.info("Recargando la vista del panel principal del profesor.");
        WindowManagerController.changeView("ProfessorHomeDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        LOGGER.info("Cerrando la sesión del profesor.");
        UserSessionManager.clearSession();
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

    @FXML
    private void goInternsModule(ActionEvent event) {
        LOGGER.info("Navegando al módulo de Alumnos (Interns).");
        WindowManagerController.changeView("ProfessorInternsDashboard.fxml");
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        LOGGER.info("Navegando al módulo de Documentos.");
        WindowManagerController.changeView("ProfessorDocumentsDashboard.fxml");
    }
    
    @FXML
    private void goReportsModule(ActionEvent event) {
        LOGGER.info("Navegando al módulo de Reportes.");
        WindowManagerController.changeView("ProfessorReportsDashboard.fxml");
    }
}