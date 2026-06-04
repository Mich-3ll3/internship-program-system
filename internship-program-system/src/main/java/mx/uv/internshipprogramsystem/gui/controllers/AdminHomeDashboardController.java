package mx.uv.internshipprogramsystem.gui.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class AdminHomeDashboardController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            AdminHomeDashboardController.class
        );

    @FXML
    private Label lblDate;

    @FXML
    private Button btnAddProfessor;

    @FXML
    private TextField txtSearchProfessor;

    @FXML
    private TableView<ProfessorDTO> tblProfessors;

    @FXML
    private TableColumn<ProfessorDTO, String> colNameProfessor;

    @FXML
    private TableColumn<ProfessorDTO, Integer> colGroupsProfessor;

    @FXML
    private TableColumn<ProfessorDTO, Boolean> colStatusProfessor;

    @FXML
    private Button btnAddIntern;

    @FXML
    private TextField txtSearchIntern;

    @FXML
    private TableView<InternDTO> tblIntern;

    @FXML
    private TableColumn<InternDTO, String> colNameIntern;

    @FXML
    private TableColumn<InternDTO, String> colNRCIntern;

    @FXML
    private TableColumn<InternDTO, Boolean> colStatusIntern;

    @FXML
    private Label lblTotalProfessors;

    @FXML
    private Label lblTotalInterns;

    @FXML
    private Label lblTotalActiveUsers;

    @FXML
    private void initialize() {
        try {
            initializeDate();
            initializeTables();
            loadStatistics();

            LOGGER.info(
                "Dashboard de administrador cargado correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error cargando dashboard de administrador",
                businessException
            );
        }
    }

    private void initializeDate() {
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                "EEEE, d 'de' MMMM 'de' yyyy",
                new Locale("es", "MX")
            );

        String formattedDate =
            LocalDate.now().format(formatter);

        lblDate.setText(formattedDate);
    }

    private void initializeTables() {
        colNameProfessor.setCellValueFactory(
            new PropertyValueFactory<>("name")
        );

        colGroupsProfessor.setCellValueFactory(
            new PropertyValueFactory<>("groups")
        );

        colStatusProfessor.setCellValueFactory(
            new PropertyValueFactory<>("isActive")
        );

        colNameIntern.setCellValueFactory(
            new PropertyValueFactory<>("name")
        );

        colNRCIntern.setCellValueFactory(
            new PropertyValueFactory<>("nrc")
        );

        colStatusIntern.setCellValueFactory(
            new PropertyValueFactory<>("isActive")
        );
    }

    private void loadStatistics()
            throws BusinessException {
        InternDAO internDAO = new InternDAO();
        ProfessorDAO professorDAO = new ProfessorDAO();
        UserDAO userDAO = new UserDAO();

        int totalProfessors =
            professorDAO.countAll();

        int totalInterns =
            internDAO.countAll();

        int totalActiveUsers =
            userDAO.countActiveUsers();

        lblTotalProfessors.setText(
            String.valueOf(totalProfessors)
        );

        lblTotalInterns.setText(
            String.valueOf(totalInterns)
        );

        lblTotalActiveUsers.setText(
            String.valueOf(totalActiveUsers)
        );
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView(
            "AdminHomeDashboard.fxml"
        );
    }

    @FXML
    private void goFormAddProfessor(ActionEvent event) {
        WindowManagerController.changeView(
            "RegisterProfessorDashboard.fxml"
        );
    }

    @FXML
    private void goConsultProfessor(ActionEvent event) {
        try {
            ProfessorDAO professorDAO =
                new ProfessorDAO();

            List<ProfessorDTO> professors =
                professorDAO.findAll();

            tblProfessors.setItems(
                FXCollections.observableArrayList(
                    professors
                )
            );

            LOGGER.info(
                "Consulta de profesores realizada correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error consultando profesores",
                businessException
            );
        }
    }

    @FXML
    private void goFormAddIntern(ActionEvent event) {
        WindowManagerController.changeView(
            "RegisterInternDashboard.fxml"
        );
    }

    @FXML
    private void goConsultIntern(ActionEvent event) {
        try {
            InternDAO internDAO =
                new InternDAO();

            List<InternDTO> interns =
                internDAO.findAll();

            tblIntern.setItems(
                FXCollections.observableArrayList(
                    interns
                )
            );

            LOGGER.info(
                "Consulta de estudiantes realizada correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error consultando estudiantes",
                businessException
            );
        }
    }

    @FXML
    private void goProfessorModule(ActionEvent event) {
        WindowManagerController.changeView(
            "ProfessorModuleDashboard.fxml"
        );
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        WindowManagerController.changeView(
            "InternModuleDashboard.fxml"
        );
    }

    @FXML
    private void logOut(ActionEvent event) {
        UserSessionManager.clearSession();
        LOGGER.info("Cierre de sesión realizado correctamente.");
        WindowManagerController.changeView(
            "LoginDashboard.fxml"
        );
    }
}