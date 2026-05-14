package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;

public class UpdateInternDashboardController {

    private static final Logger LOGGER = Logger.getLogger(RegisterInternFormController.class.getName());

    @FXML private TextField txtInstitutionalEmail;
    @FXML private TextField txtName;
    @FXML private TextField txtFirstSurname;
    @FXML private TextField txtSecondSurname;
    @FXML private TextField txtEnrollment;
    
    private InternDTO currentIntern;
    private final InternDAO internDAO = new InternDAO();
    
    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.goBack();
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

    public void setInternData(InternDTO intern) {
        this.currentIntern = intern;
        
        txtName.setText(intern.getName());
        txtFirstSurname.setText(intern.getFirstSurname());
        txtSecondSurname.setText(intern.getSecondSurname());
        txtInstitutionalEmail.setText(intern.getInstitucionalEmail());
        txtInstitutionalEmail.setEditable(false);
        txtEnrollment.setEditable(false);
    }
    
    @FXML
    private void handleUpdateAction() {
        if (isFormValid()) {
            updateIntern();
        }
    }

    private void updateIntern() {
        try {
            currentIntern.setName(txtName.getText().trim());
            currentIntern.setFirstSurname(txtFirstSurname.getText().trim());
            currentIntern.setSecondSurname(txtSecondSurname.getText().trim());
            
            currentIntern.setRol(mx.uv.internshipprogramsystem.logic.dto.RolUsuario.ESTUDIANTE);
            
            new UserValidator().validateUserForUpdate(currentIntern);

            UserDAO userDAO = new UserDAO(); 
            boolean userUpdated = userDAO.update(currentIntern);

            boolean internUpdated = internDAO.update(currentIntern);

            if (userUpdated && internUpdated) {
                showNotification(Alert.AlertType.INFORMATION, "Éxito", "Los datos se actualizaron correctamente.");
                goInternModule(null);
            } else {
                showNotification(Alert.AlertType.WARNING, "Atención", "No se pudieron actualizar todos los registros.");
            }

        } catch (BusinessException exception) {
            LOGGER.log(Level.SEVERE, "Error al actualizar", exception);
            showNotification(Alert.AlertType.ERROR, "Error", exception.getMessage());
        }
    }
    
    private boolean isFormValid() {
        if (txtName.getText().trim().isEmpty() || txtFirstSurname.getText().trim().isEmpty()) {
            showNotification(Alert.AlertType.WARNING, "Campos vacíos", "El nombre y primer apellido son obligatorios.");
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
        txtEnrollment.clear();
    }

    private void showNotification(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
