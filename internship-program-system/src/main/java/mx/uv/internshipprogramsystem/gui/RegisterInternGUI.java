package mx.uv.internshipprogramsystem.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
        root.setPadding(new Insets(20, 20, 20, 20));
        root.setAlignment(Pos.CENTER);

        Label labelTitle = new Label("Registro de Estudiante");
        labelTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        textFieldEmail = new TextField();
        passwordField = new PasswordField();
        textFieldName = new TextField();
        textFieldFirstSurname = new TextField();
        textFieldSecondSurname = new TextField();
        textFieldEnrollment = new TextField();

        addFormField(grid, "Correo (@uv.mx):", textFieldEmail, 0);
        addFormField(grid, "Contraseña:", passwordField, 1);
        addFormField(grid, "Nombre(s):", textFieldName, 2);
        addFormField(grid, "Apellido Paterno:", textFieldFirstSurname, 3);
        addFormField(grid, "Apellido Materno:", textFieldSecondSurname, 4);
        addFormField(grid, "Matrícula:", textFieldEnrollment, 5);

        buttonRegister = new Button("Registrar Estudiante");
        buttonRegister.setMinWidth(150);
        buttonRegister.setOnAction(e -> handleRegistration());

        root.getChildren().addAll(labelTitle, grid, buttonRegister);

        Scene scene = new Scene(root, 450, 550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addFormField(GridPane grid, String labelText, TextField field, int row) {
        grid.add(new Label(labelText), 0, row);
        grid.add(field, 1, row);
    }

    private void handleRegistration() {
        try {
            String institutionalEmail = textFieldEmail.getText();
            String plainPassword = passwordField.getText();
            String enrollmentNumber = textFieldEnrollment.getText();

            UserDTO user = new UserDTO();
            user.setInstitucionalEmail(institutionalEmail);
            user.setName(textFieldName.getText());
            user.setFirstSurname(textFieldFirstSurname.getText());
            user.setSecondSurname(textFieldSecondSurname.getText());
            user.setIsActive(true);
            user.setRol(RolUsuario.ESTUDIANTE);

            InternDTO intern = new InternDTO();
            intern.setEnrollmentNumber(enrollmentNumber);

            UserValidator userValidator = new UserValidator();
            userValidator.validateUserForCreation(user, plainPassword);

            InternValidator internValidator = new InternValidator();
            internValidator.validateEnrollmentNumber(enrollmentNumber);

            showAlert(AlertType.INFORMATION, "Éxito", "Registro exitoso");

        } catch (BusinessException businessException) {
            showAlert(AlertType.ERROR, "Error", businessException.getMessage());
        } catch (Exception exception) {
            showAlert(AlertType.ERROR, "Error Grave", "Ocurrió un error inesperado.");
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
