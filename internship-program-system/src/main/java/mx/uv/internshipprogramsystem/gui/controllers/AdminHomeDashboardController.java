package mx.uv.internshipprogramsystem.gui.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.gui.util.ShowErrorAlertRunnable;

public class AdminHomeDashboardController {
    private static final Logger LOGGER = LoggerFactory.getLogger(
        AdminHomeDashboardController.class
    );

    @FXML
    private Label lblTotalProfessors;

    @FXML
    private Label lblActiveProfessors;

    @FXML
    private Label lblInactiveProfessors;

    @FXML
    private Label lblPendingProfessors;

    private final ProfessorDAO professorDAO = new ProfessorDAO();

    @FXML
    private void initialize() {
        try {
            loadStatistics();
            LOGGER.info("Dashboard de administrador cargado correctamente.");
        } catch (BusinessException businessException) {
            LOGGER.error("Error cargando dashboard de administrador", businessException);
            showErrorAlert("Error", businessException.getMessage());
        } catch (DataAccessException dataAccessException) {
            LOGGER.error("Error de conexion cargando dashboard de administrador",
                dataAccessException);
            showErrorAlert("Error de conexion", "No se pudo establecer conexion con la "
                + "base de datos para cargar las estadisticas. Intente mas tarde.");
        }
    }

    private void loadStatistics() throws BusinessException, DataAccessException {
        int total = professorDAO.countAll();
        int active = professorDAO.countActive();
        int inactive = professorDAO.countInactive();
        int pending = professorDAO.countPending();

        lblTotalProfessors.setText(String.valueOf(total));
        lblActiveProfessors.setText(String.valueOf(active));
        lblInactiveProfessors.setText(String.valueOf(inactive));
        lblPendingProfessors.setText(String.valueOf(pending));
    }

    @FXML
    private void handleBtnConsultaClick(ActionEvent event) {
        WindowManagerController.changeView("ProfessorModuleDashboard.fxml");
    }

    @FXML
    private void handleBtnRegistroClick(ActionEvent event) {
        WindowManagerController.changeView("RegisterProfessorDashboard.fxml");
    }

    @FXML
    private void handleBtnModificacionClick(ActionEvent event) {
        WindowManagerController.changeView("ProfessorModuleDashboard.fxml");
    }

    @FXML
    private void handleBtnCambioEstadoClick(ActionEvent event) {
        WindowManagerController.changeView("ProfessorModuleDashboard.fxml");
    }

    private void showErrorAlert(String title, String message) {
        javafx.application.Platform.runLater(
            new ShowErrorAlertRunnable(title, message)
        );
    }
}