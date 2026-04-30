package mx.uv.internshipprogramsystem.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.RolUsuario;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;
import mx.uv.internshipprogramsystem.logic.validations.ProfessorValidator;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;

public class RegisterProfessorGUI extends Application {

    private TextField textFieldEmail;
    private PasswordField passwordField;
    private TextField textFieldName;
    private TextField textFieldFirstSurname;
    private TextField textFieldSecondSurname;
    private TextField textFieldStaffNumber;
    private CheckBox checkBoxCoordinator;
    private Button buttonRegister;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Registrar Profesor");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20, 20, 20, 20));
        root.setAlignment(Pos.CENTER);

        Label labelTitle = new Label("Registro de Profesor");
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
        textFieldStaffNumber = new TextField();
        checkBoxCoordinator = new CheckBox("Es coordinador");

        addFormField(grid, "Correo institucional:", textFieldEmail, 0);
        addFormField(grid, "Contraseña:", passwordField, 1);
        addFormField(grid, "Nombre:", textFieldName, 2);
        addFormField(grid, "Apellido paterno:", textFieldFirstSurname, 3);
        addFormField(grid, "Apellido materno:", textFieldSecondSurname, 4);
        addFormField(grid, "Número de personal:", textFieldStaffNumber, 5);
        grid.add(checkBoxCoordinator, 1, 6);

        buttonRegister = new Button("Registrar");
        buttonRegister.setOnAction(e -> registerProfessor());

        root.getChildren().addAll(labelTitle, grid, buttonRegister);

        Scene scene = new Scene(root, 450, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addFormField(GridPane grid, String labelText, Control field, int row) {
        Label label = new Label(labelText);
        grid.add(label, 0, row);
        grid.add(field, 1, row);
    }

    private void registerProfessor() {
        try {
            UserDTO user = new UserDTO(
                textFieldEmail.getText(),
                passwordField.getText(),
                textFieldName.getText(),
                textFieldFirstSurname.getText(),
                textFieldSecondSurname.getText(),
                true,
                RolUsuario.PROFESOR
            );

            UserValidator userValidator = new UserValidator();
            userValidator.validateUniqueName(textFieldName.getText());

            ProfessorValidator professorValidator = new ProfessorValidator();
            professorValidator.validateStaffNumber(Integer.parseInt(textFieldStaffNumber.getText()));

            UserDAO userDAO = new UserDAO();
            int userId = userDAO.create(user, passwordField.getText());

            ProfessorDAO professorDAO = new ProfessorDAO();
            ProfessorDTO professor = new ProfessorDTO(
                Integer.parseInt(textFieldStaffNumber.getText()),
                checkBoxCoordinator.isSelected(),
                userId,
                textFieldEmail.getText(),
                textFieldName.getText(),
                textFieldFirstSurname.getText(),
                textFieldSecondSurname.getText(),
                true
            );
            professorDAO.create(professor);

            showAlert(Alert.AlertType.INFORMATION, "Profesor registrado correctamente.");

        } catch (BusinessException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "El número de personal debe ser numérico.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Registro de Profesor");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
