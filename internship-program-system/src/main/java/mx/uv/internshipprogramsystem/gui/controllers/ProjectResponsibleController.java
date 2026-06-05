package mx.uv.internshipprogramsystem.gui.controllers;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ProjectResponsibleDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;

public class ProjectResponsibleController {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ProjectResponsibleController.class
        );

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastNameFather;

    @FXML
    private TextField txtLastNameMother;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPosition;

    @FXML
    private TextField txtOrganizationId;

    private final ProjectResponsibleDAO responsibleDAO =
        new ProjectResponsibleDAO();

    @FXML
    private void onSaveResponsible() {
        try {
            validatePermission(
                Permission.REGISTER_PROJECT_RESPONSIBLE
            );

            ProjectResponsibleDTO responsible =
                buildProjectResponsible();

            boolean inserted =
                responsibleDAO.insert(
                    responsible
                );

            if (inserted) {
                FormAlertSupport.showInformation(
                    "Registro exitoso",
                    "Responsable guardado correctamente."
                );

                clearForm();
            } else {
                FormAlertSupport.showError(
                    "Error",
                    "No se pudo guardar el responsable."
                );
            }
        } catch (NumberFormatException numberFormatException) {
            LOGGER.warn(
                "Formato inválido al registrar responsable.",
                numberFormatException
            );

            FormAlertSupport.showError(
                "Error de formato",
                "El identificador de la organización debe ser numérico."
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al guardar responsable.",
                businessException
            );

            FormAlertSupport.showError(
                "Error",
                businessException.getMessage()
            );
        }
    }

    @FXML
    private void onSearchResponsible() {
        try {
            validatePermission(
                Permission.CONSULT_PROJECT_RESPONSIBLE
            );

            int id =
                Integer.parseInt(
                    txtOrganizationId.getText().trim()
                );

            Optional<ProjectResponsibleDTO> optional =
                responsibleDAO.findById(
                    id
                );

            ProjectResponsibleDTO found =
                unwrapOptional(
                    optional
                );

            fillForm(
                found
            );

            FormAlertSupport.showInformation(
                "Búsqueda exitosa",
                "Responsable encontrado."
            );
        } catch (NumberFormatException numberFormatException) {
            LOGGER.warn(
                "Formato inválido al buscar responsable.",
                numberFormatException
            );

            FormAlertSupport.showError(
                "Error de formato",
                "El identificador debe ser numérico."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Error al buscar responsable.",
                businessException
            );

            FormAlertSupport.showError(
                "Error",
                businessException.getMessage()
            );
        }
    }

    @FXML
    private void onDeleteResponsible() {
        try {
            validatePermission(
                Permission.REGISTER_PROJECT_RESPONSIBLE
            );

            int id =
                Integer.parseInt(
                    txtOrganizationId.getText().trim()
                );

            boolean deleted =
                responsibleDAO.delete(
                    id
                );

            if (deleted) {
                FormAlertSupport.showInformation(
                    "Eliminación exitosa",
                    "Responsable eliminado correctamente."
                );

                clearForm();
            } else {
                FormAlertSupport.showError(
                    "Error",
                    "No se pudo eliminar responsable con ese ID."
                );
            }
        } catch (NumberFormatException numberFormatException) {
            LOGGER.warn(
                "Formato inválido al eliminar responsable.",
                numberFormatException
            );

            FormAlertSupport.showError(
                "Error de formato",
                "El identificador debe ser numérico."
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al eliminar responsable.",
                businessException
            );

            FormAlertSupport.showError(
                "Error",
                businessException.getMessage()
            );
        }
    }

    private ProjectResponsibleDTO buildProjectResponsible() {
        ProjectResponsibleDTO responsible =
            new ProjectResponsibleDTO(
                0,
                txtFirstName.getText().trim(),
                txtLastNameFather.getText().trim(),
                txtLastNameMother.getText().trim(),
                txtEmail.getText().trim(),
                txtPosition.getText().trim(),
                Integer.parseInt(
                    txtOrganizationId.getText().trim()
                )
            );

        return responsible;
    }

    private void fillForm(
            ProjectResponsibleDTO responsible
    ) {
        txtFirstName.setText(
            responsible.getFirstName()
        );

        txtLastNameFather.setText(
            responsible.getLastNameFather()
        );

        txtLastNameMother.setText(
            responsible.getLastNameMother()
        );

        txtEmail.setText(
            responsible.getEmail()
        );

        txtPosition.setText(
            responsible.getPosition()
        );

        txtOrganizationId.setText(
            String.valueOf(
                responsible.getOrganizationId()
            )
        );
    }

    private ProjectResponsibleDTO unwrapOptional(
            Optional<ProjectResponsibleDTO> optional
    ) throws BusinessException {
        ProjectResponsibleDTO responsible;

        if (optional.isPresent()) {
            responsible =
                optional.get();
        } else {
            throw new BusinessException(
                "No se encontró responsable con ese ID."
            );
        }

        return responsible;
    }

    @FXML
    private void clearForm() {
        txtFirstName.clear();

        txtLastNameFather.clear();

        txtLastNameMother.clear();

        txtEmail.clear();

        txtPosition.clear();

        txtOrganizationId.clear();
    }

    private void validatePermission(
            Permission permission
    ) throws BusinessException {
        AccessControlManager accessControlManager =
            new AccessControlManager();

        accessControlManager.validatePermission(
            UserSessionManager.getCurrentUser(),
            permission
        );
    }
}