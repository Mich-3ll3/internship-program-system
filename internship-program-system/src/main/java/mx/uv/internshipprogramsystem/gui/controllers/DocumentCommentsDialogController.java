package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentCommentsDialogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentCommentsDialogController.class);
    
    private static final String DEFAULT_NO_COMMENTS_MESSAGE = "El revisor aún no ha dejado observaciones para este documento. Todo parece estar en orden hasta el momento.";
    private static final String LOG_INIT_FINISHED = "Proceso de inicialización de ventana de comentarios finalizado.";
    private static final String LOG_CLOSE_FINISHED = "Intento de cierre de ventana finalizado.";

    @FXML private TextArea txtComments;
    @FXML private Button btnClose;

    public void initData(String comments) {
        try {
            LOGGER.info("Inicializando los datos en la ventana de comentarios.");
            
            if (comments != null && !comments.trim().isEmpty()) {
                txtComments.setText(comments);
            } else {
                txtComments.setText(DEFAULT_NO_COMMENTS_MESSAGE);
            }
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error inesperado al cargar el texto de los comentarios: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug(LOG_INIT_FINISHED);
        }
    }

    @FXML
    private void closeDialog(ActionEvent event) {
        try {
            LOGGER.info("Acción de cerrar ventana de observaciones disparada.");
            
            Stage stage = (Stage) btnClose.getScene().getWindow();
            stage.close();
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al intentar cerrar la ventana de observaciones: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug(LOG_CLOSE_FINISHED);
        }
    }
}