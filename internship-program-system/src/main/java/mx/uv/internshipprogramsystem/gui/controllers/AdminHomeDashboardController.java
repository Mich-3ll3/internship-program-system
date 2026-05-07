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
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;


public class AdminHomeDashboardController {
    private Label lblDate;
    private Button btnAddProfessor;
    private TextField txtSearchProfessor;
    private TableView<ProfessorDTO> tblProfessors;
    private TableColumn<ProfessorDTO, String> colNameProfessor;
    private TableColumn<ProfessorDTO, String> colGroupsProfessor;
    private TableColumn<ProfessorDTO, String> colStatusProfessor;
    private Button btnAddIntern;
    private TextField txtSearchIntern;
    private TableView<InternDTO> tblIntern;
    private TableColumn<InternDTO, String> colNameIntern;
    private TableColumn<InternDTO, String> colNRCIntern;
    private TableColumn<InternDTO, String> colStatusIntern;
    private Label lblTotalProfessors;
    private Label lblTotalInterns;
    private Label lblTotalActiveUsers;
    
    
    private void initialize() throws BusinessException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "MX"));
        String fechaFormateada = LocalDate.now().format(formatter);

        lblDate.setText(fechaFormateada);
        
        colNameProfessor.setCellValueFactory(new PropertyValueFactory<>("name"));
        colGroupsProfessor.setCellValueFactory(new PropertyValueFactory<>("groups"));
        colStatusProfessor.setCellValueFactory(new PropertyValueFactory<>("isActive"));

        colNameIntern.setCellValueFactory(new PropertyValueFactory<>("name"));
        colNRCIntern.setCellValueFactory(new PropertyValueFactory<>("NRC"));
        colStatusIntern.setCellValueFactory(new PropertyValueFactory<>("isActive"));
        
        InternDAO intern = new InternDAO();
        ProfessorDAO professor = new ProfessorDAO();
        UserDAO user = new UserDAO();
        
        int totalProfessors = professor.countAll();
        int totalInterns = intern.countAll();
        int totalActiveUsers = user.countActiveUsers();

        lblTotalProfessors.setText(String.valueOf(totalProfessors));
        lblTotalInterns.setText(String.valueOf(totalInterns));
        lblTotalActiveUsers.setText(String.valueOf(totalActiveUsers));
    }
    
    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView("AdminHomeDashboard.fxml");
    }
    
    @FXML
    private void goFormAddProfessor(ActionEvent event) {
        WindowManagerController.changeView("RegisterProfessorDashboard.fxml");
    }

    @FXML
    private void goConsultProfessor(ActionEvent event) throws BusinessException {
        ProfessorDAO professor = new ProfessorDAO();
        List<ProfessorDTO> professors = professor.findAll();
        tblProfessors.setItems(FXCollections.observableArrayList(professors));
    }

    @FXML
    private void goFormAddIntern(ActionEvent event) {
        WindowManagerController.changeView("RegisterInternDashboard.fxml");
    }

    @FXML
    private void goConsultIntern(ActionEvent event) throws BusinessException {
        InternDAO intern = new InternDAO();
        List<InternDTO> interns = intern.findAll();
        tblIntern.setItems(FXCollections.observableArrayList(interns));
    }

    @FXML
    private void goProfessorModule(ActionEvent event) {
        WindowManagerController.changeView("ProfessorModuleDashboard.fxml");
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        WindowManagerController.changeView("InternModuleDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

}
