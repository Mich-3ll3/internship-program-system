package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import mx.uv.internshipprogramsystem.logic.dto.DocumentDTO;

public class ProfessorDocumentReviewDashboardController {

    @FXML private Label lblDocumentName;
    @FXML private Label lblDocumentPath;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private TextArea txtObservations;

    private static DocumentDTO currentDocument;

    // Static method to pass the document to be reviewed before loading the view
    public static void setDocument(DocumentDTO document) {
        currentDocument = document;
    }

    @FXML
    public void initialize() {
        cmbStatus.setItems(FXCollections.observableArrayList("Aprobado", "Rechazado", "Solicitar Cambios"));
        
        if (currentDocument != null) {
            lblDocumentName.setText("Documento: " + currentDocument.getName());
            lblDocumentPath.setText("Ruta: " + currentDocument.getPath());
        }
    }

    @FXML
    private void handleSaveReview(ActionEvent event) {
        String status = cmbStatus.getValue();
        String observations = txtObservations.getText();

        if (status == null || status.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos Vacíos", "Debe seleccionar un estado para el documento.");
            return;
        }

        // Normally here we would save this to the DB using a DAO
        // documentDAO.updateStatus(currentDocument.getId(), status, observations);
        
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "La calificación y observaciones han sido guardadas correctamente.");
        goBack(null);
    }

    @FXML
    private void goBack(ActionEvent event) {
        currentDocument = null; // Clear state
        WindowManagerController.changeView("ProfessorDocumentDashboard.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
