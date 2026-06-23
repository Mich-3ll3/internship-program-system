package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.validations.InputCleaner;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;

public class UpdateInternDashboardController {
    
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            UpdateInternDashboardController.class
        );

    private static final int NAME_MAX_LENGTH = 60;

    @FXML
    private TextField txtInstitutionalEmail;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtFirstSurname;

    @FXML
    private TextField txtSecondSurname;

    @FXML
    private TextField txtEnrollment;

    private InternDTO currentIntern;
    private final InternDAO internDAO =
        new InternDAO();

    @FXML
    public void initialize() {
        configureTextLimits();
        txtInstitutionalEmail.setEditable(false);
        txtEnrollment.setEditable(false);
    }

    private void configureTextLimits() {
        limitTextField(txtName, NAME_MAX_LENGTH);
        limitTextField(txtFirstSurname, NAME_MAX_LENGTH);
        limitTextField(txtSecondSurname, NAME_MAX_LENGTH);
    }

    private void limitTextField(
            TextField textField,
            int maxLength
    ) {
        textField.setTextFormatter(
            new TextFormatter<String>(
                new mx.uv.internshipprogramsystem.gui.handlers.LengthFilterTextFormatter(maxLength)
            )
        );
    }

    public void setInternData(
            InternDTO intern
    ) {
        currentIntern =
            intern;

        txtName.setText(
            intern.getName()
        );
        txtFirstSurname.setText(
            intern.getFirstSurname()
        );
        txtSecondSurname.setText(
            intern.getSecondSurname()
        );
        txtInstitutionalEmail.setText(
            intern.getInstitutionalEmail()
        );
        txtEnrollment.setText(
            intern.getEnrollmentNumber()
        );
    }

    @FXML
    private void handleUpdateAction() {
        if (isFormValid()) {
            updateIntern();
        }
    }

    private boolean isFormValid() {
        boolean valid =
            true;

        if (txtName.getText().trim().isEmpty()
                || txtFirstSurname.getText().trim().isEmpty()) {
            showNotification(
                Alert.AlertType.WARNING,
                "Campos vacios",
                "El nombre y primer apellido son obligatorios."
            );
            valid =
                false;
        }

        return valid;
    }

    private void updateIntern() {
        try {
            currentIntern.setName(
                InputCleaner.sanitizeText(
                    txtName.getText()
                )
            );
            currentIntern.setFirstSurname(
                InputCleaner.sanitizeText(
                    txtFirstSurname.getText()
                )
            );
            currentIntern.setSecondSurname(
                InputCleaner.sanitizeText(
                    txtSecondSurname.getText()
                )
            );
            currentIntern.setRole(
                UserRole.STUDENT
            );

            new UserValidator().validateUserForUpdate(
                currentIntern
            );

            UserDAO userDAO =
                new UserDAO();
            boolean userUpdated =
                userDAO.update(
                    currentIntern
                );
            boolean internUpdated =
                internDAO.update(
                    currentIntern
                );

            if (userUpdated && internUpdated) {
                showNotification(
                    Alert.AlertType.INFORMATION,
                    "Exito",
                    "Los datos se actualizaron correctamente."
                );
                goInternModule(null);
            } else {
                showNotification(
                    Alert.AlertType.WARNING,
                    "Atencion",
                    "No se pudieron actualizar todos los registros."
                );
            }
        } catch (BusinessException exception) {
            LOGGER.error(
                "Error al actualizar estudiante",
                exception
            );
            showNotification(
                Alert.AlertType.ERROR,
                "Error",
                exception.getMessage()
            );
        } catch (DataAccessException exception) {
            LOGGER.error(
                "Error de conexion al actualizar estudiante",
                exception
            );
            showNotification(
                Alert.AlertType.ERROR,
                "Error de conexion",
                "No se pudo conectar con la base de datos para actualizar el estudiante. Por favor intente mas tarde."
            );
        }
    }

    @FXML
    private void clearForm() {
        if (currentIntern != null) {
            setInternData(
                currentIntern
            );
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.goBack();
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
        LOGGER.info(
            "Cierre de sesion realizado correctamente."
        );
        WindowManagerController.changeView(
            "LoginDashboard.fxml"
        );
    }

    private void showNotification(
            Alert.AlertType type,
            String title,
            String content
    ) {
        Alert alert =
            new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
