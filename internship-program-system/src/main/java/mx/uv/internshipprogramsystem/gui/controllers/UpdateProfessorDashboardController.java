package mx.uv.internshipprogramsystem.gui.controllers;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;

public class UpdateProfessorDashboardController {

    private static final Logger LOGGER = Logger.getLogger(RegisterProfessorFormController.class.getName());

    @FXML private TextField txtInstitutionalEmail;
    @FXML private TextField txtName;
    @FXML private TextField txtFirstSurname;
    @FXML private TextField txtSecondSurname;
    @FXML private TextField txtStaffNumber;
    @FXML private CheckBox chkCoordinator;
    
    private ProfessorDTO currentProfessor;
    private final ProfessorDAO professorDAO = new ProfessorDAO();
    
    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView("AdminHomeDashboard.fxml");
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
    
    public void setProfessorData(ProfessorDTO professor) {
        this.currentProfessor = professor;
        
        txtName.setText(professor.getName());
        txtFirstSurname.setText(professor.getFirstSurname());
        txtSecondSurname.setText(professor.getSecondSurname());
        txtInstitutionalEmail.setText(professor.getInstitutionalEmail());
        txtStaffNumber.setText(String.valueOf(professor.getStaffNumber()));
        chkCoordinator.setSelected(professor.getIsCoordinator());

        txtInstitutionalEmail.setEditable(false);
        txtStaffNumber.setEditable(false);
    }
    
    @FXML
    private void handleUpdateAction() {
        if (isFormValid()) {
            updateProfessor();
        }
    }

    private void updateProfessor() {
        try {
            currentProfessor.setName(txtName.getText().trim());
            currentProfessor.setFirstSurname(txtFirstSurname.getText().trim());
            currentProfessor.setSecondSurname(txtSecondSurname.getText().trim());
            currentProfessor.setIsCoordinator(chkCoordinator.isSelected());
            
            currentProfessor.setRole(mx.uv.internshipprogramsystem.logic.dto.UserRole.PROFESSOR);
            
            new UserValidator().validateUserForUpdate(currentProfessor);

            UserDAO userDAO = new UserDAO(); 
            boolean userUpdated = userDAO.update(currentProfessor);

            boolean professorUpdated = professorDAO.update(currentProfessor);

            if (userUpdated && professorUpdated) {
                showNotification(Alert.AlertType.INFORMATION, "Ã‰xito", "Los datos se actualizaron correctamente.");
                goProfessorModule(null);
            } else {
                showNotification(Alert.AlertType.WARNING, "AtenciÃ³n", "No se pudieron actualizar todos los registros.");
            }

        } catch (BusinessException exception) {
            LOGGER.log(Level.SEVERE, "Error al actualizar", exception);
            showNotification(Alert.AlertType.ERROR, "Error", exception.getMessage());
        }
    }

    private boolean isFormValid() {
        if (txtName.getText().trim().isEmpty() || txtFirstSurname.getText().trim().isEmpty()) {
            showNotification(Alert.AlertType.WARNING, "Campos vacios", "El nombre y primer apellido son obligatorios.");
            return false;
        }
        return true;
    }
    

    @FXML
    private void clearForm() {
        txtInstitutionalEmail.clear();
        txtName.clear();
        txtFirstSurname.clear();
        txtSecondSurname.clear();
        txtStaffNumber.clear();
        chkCoordinator.setSelected(false);
    }

    private void showNotification(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}