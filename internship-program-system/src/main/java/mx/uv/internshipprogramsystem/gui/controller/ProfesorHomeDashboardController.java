package mx.uv.internshipprogramsystem.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ProfesorHomeDashboardController implements Initializable {

    private Button btnHome;
    private Button btnIntern;
    private Button btnReports;
    private Button btnProjects;
    private Button btnExit;
    private Label lblDate;
    private Label lblTotalInterns;
    private Label lblPendingCount;
    private Label lblReportStats;
    private Label lblFormStats;
    private Label lblSelfEvaluationStats;
    private Label lblProjectStats;
    private ProgressBar pbGlobalProgress;
    private ChoiceBox<String> cbReportStatus;
    private TableView<?> tblReportDocuments; 
    private TableColumn<?, ?> colNameAlumno;
    private TableColumn<?, ?> colTypeDocument;
    private TableColumn<?, ?> colDeliveryDate;
    private TableColumn<?, ?> colStateDocument;
    private TextArea taDocComment;
    private TextField txtSearchIntern;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupStaticInfo();
        loadComponentDefaults();
    }

    private void setupStaticInfo() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM");
        String dateText = now.format(formatter);
        lblDate.setText(dateText.substring(0, 1).toUpperCase() + dateText.substring(1));
    }

    private void loadComponentDefaults() {
        if (cbReportStatus != null) {
            cbReportStatus.getItems().addAll("Pendiente", "Aprobado", "Rechazado", "En corrección");
            cbReportStatus.setValue("Pendiente");
        }
    }

    @FXML
    void goHome(ActionEvent event) {
         WindowManagerController.changeView("CoordinatorProfessorHomeDashboard.fxml");
    }

    @FXML
    void goInternModule(ActionEvent event) {
        WindowManagerController.changeView("InternModuleDashboard.fxml");
    }

    @FXML
    void logOut(ActionEvent event) {
        WindowManagerController.changeView("LoginDashboard.fxml");
    }
}