package mx.uv.internshipprogramsystem.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class InternHomeDashboardController implements Initializable {
    private Button btnHome;
    private Button btnProjects;
    private Button btnDocuments;
    private Button btnReports;
    private Button btnExit;
    
    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView("InternHomeDashboard.fxml");
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        //Brian
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        //Brian
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        //Brian
    }

    @FXML
    private void logOut(ActionEvent event) {
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}