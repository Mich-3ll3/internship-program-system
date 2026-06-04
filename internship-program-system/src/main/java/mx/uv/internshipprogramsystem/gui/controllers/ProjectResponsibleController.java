package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import mx.uv.internshipprogramsystem.logic.dao.ProjectResponsibleDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

import java.util.Optional;

public class ProjectResponsibleController {

    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastNameFather;
    @FXML private TextField txtLastNameMother;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPosition;
    @FXML private TextField txtOrganizationId;

    private final ProjectResponsibleDAO responsibleDAO = new ProjectResponsibleDAO();

    @FXML
    private void onSaveResponsible() {
        try {
            ProjectResponsibleDTO responsible = new ProjectResponsibleDTO(
                0,
                txtFirstName.getText(),
                txtLastNameFather.getText(),
                txtLastNameMother.getText(),
                txtEmail.getText(),
                txtPosition.getText(),
                Integer.parseInt(txtOrganizationId.getText())
            );
            boolean inserted = responsibleDAO.insert(responsible);
            if (inserted) {
                showInfo("Responsable guardado correctamente.");
            } else {
                showError("No se pudo guardar el responsable.");
            }
        } catch (BusinessException | NumberFormatException e) {
            showError("Error al guardar responsable: " + e.getMessage());
        }
    }

    @FXML
    private void onSearchResponsible() {
        try {
            int id = Integer.parseInt(txtOrganizationId.getText());
            Optional<ProjectResponsibleDTO> optional = responsibleDAO.findById(id);

            ProjectResponsibleDTO found = unwrapOptional(optional);

            txtFirstName.setText(found.getFirstName());
            txtLastNameFather.setText(found.getLastNameFather());
            txtLastNameMother.setText(found.getLastNameMother());
            txtEmail.setText(found.getEmail());
            txtPosition.setText(found.getPosition());
            txtOrganizationId.setText(String.valueOf(found.getOrganizationId()));
            showInfo("Responsable encontrado.");

        } catch (BusinessException | NumberFormatException e) {
            showError("Error al buscar responsable: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteResponsible() {
        try {
            int id = Integer.parseInt(txtOrganizationId.getText());
            boolean deleted = responsibleDAO.delete(id);
            if (deleted) {
                showInfo("Responsable eliminado correctamente.");
            } else {
                showError("No se pudo eliminar responsable con ese ID.");
            }
        } catch (BusinessException | NumberFormatException e) {
            showError("Error al eliminar responsable: " + e.getMessage());
        }
    }

    private ProjectResponsibleDTO unwrapOptional(Optional<ProjectResponsibleDTO> optional) throws BusinessException {
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new BusinessException("No se encontró responsable con ese ID.");
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
