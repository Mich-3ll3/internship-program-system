package mx.uv.internshipprogramsystem.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.RolUsuario;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;
import mx.uv.internshipprogramsystem.logic.validations.InternValidator;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;

public class RegisterInternGUI extends Application {

    private TextField textFieldEmail;
    private PasswordField passwordField;
    private TextField textFieldName;
    private TextField textFieldFirstSurname;
    private TextField textFieldSecondSurname;
    private TextField textFieldEnrollment;
    private Button buttonRegister;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Registrar Estudiante");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label labelTitle = new Label("Registro de Estudiante");
        labelTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        textFieldEmail = createTextField(grid, "Correo institucional:", 0);
        passwordField = new PasswordField();
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(passwordField, 1, 1);

        textFieldName = createTextField(grid, "Nombre:", 2);
        textFieldFirstSurname = createTextField(grid, "Apellido paterno:", 3);
        textFieldSecondSurname = createTextField(grid, "Apellido materno:", 4);
        textFieldEnrollment = createTextField(grid, "Matrícula:", 5);

        buttonRegister = new Button("Registrar");
        buttonRegister.setOnAction(e -> handleRegister());

        root.getChildren().addAll(labelTitle, grid, buttonRegister);

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TextField createTextField(GridPane grid, String label, int row) {
        Label fieldLabel = new Label(label);
        TextField textField = new TextField();
        grid.add(fieldLabel, 0, row);
        grid.add(textField, 1, row);
        return textField;
    }

    private void handleRegister() {
        try {
            UserDTO user = new UserDTO(
                textFieldEmail.getText(),
                passwordField.getText(),
                textFieldName.getText(),
                textFieldFirstSurname.getText(),
                textFieldSecondSurname.getText(),
                true,
                RolUsuario.ESTUDIANTE
            );
            
            UserValidator userValidator = new UserValidator();
            userValidator.validateUniqueName(textFieldName.getText());

            InternValidator internValidator = new InternValidator();
            internValidator.validateEnrollmentNumber(textFieldEnrollment.getText());

            UserDAO userDAO = new UserDAO();
            int userId = userDAO.create(user, passwordField.getText());

            InternDTO intern = new InternDTO(
                textFieldEnrollment.getText(),
                userId,
                user.getInstitucionalEmail(),
                user.getName(),
                user.getFirstSurname(),
                user.getSecondSurname(),
                user.getIsActive(),
                user.getRol()
            );

            InternDAO internDAO = new InternDAO();
            internDAO.create(intern);

            showAlert(Alert.AlertType.INFORMATION, "Registro exitoso", "El estudiante fue registrado correctamente.");

        } catch (BusinessException ex) {
            showAlert(Alert.AlertType.ERROR, "Error de negocio", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error inesperado", "Ocurrió un error al registrar el estudiante.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
