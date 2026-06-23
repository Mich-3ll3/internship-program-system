package mx.uv.internshipprogramsystem.gui.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import java.util.ArrayList;
import java.util.List;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public final class FormAlertSupport {

    private FormAlertSupport() {
    }

    public static void showInformation(String title, String message) {
        clearFieldErrorsFromActiveWindow();
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    public static void showWarning(String title, String message) {
        if (!tryShowFieldOrInlineMessage(Alert.AlertType.WARNING, message)) {
            showAlert(Alert.AlertType.WARNING, title, message);
        }
    }

    public static void showWarning(String title, BusinessException exception) {
        String message;
        if (exception instanceof mx.uv.internshipprogramsystem.logic.exceptions.ValidationException valEx) {
            message = String.join("\n", valEx.getErrors());
        } else {
            message = exception.getMessage();
        }
        showWarning(title, message);
    }

    public static void showError(String title, String message) {
        if (!tryShowFieldOrInlineMessage(Alert.AlertType.ERROR, message)) {
            showAlert(Alert.AlertType.ERROR, title, message);
        }
    }

    public static void showError(String title, BusinessException exception) {
        String message;
        if (exception instanceof mx.uv.internshipprogramsystem.logic.exceptions.ValidationException valEx) {
            message = String.join("\n", valEx.getErrors());
        } else {
            message = exception.getMessage();
        }
        showError(title, message);
    }

    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void clearFieldErrorsFromActiveWindow() {
        Platform.runLater(() -> {
            Window activeWindow = getActiveWindow();
            if (activeWindow != null && activeWindow.getScene() != null) {
                clearFieldErrors(activeWindow.getScene().getRoot());
            }
        });
    }

    private static List<String> splitMessageIntoErrors(String message) {
        List<String> result = new ArrayList<>();
        if (message == null) {
            return result;
        }
        String[] parts = message.split("\n|, ");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    private static boolean tryShowFieldOrInlineMessage(Alert.AlertType alertType, String message) {
        try {
            Window activeWindow = getActiveWindow();
            if (activeWindow == null || activeWindow.getScene() == null) {
                return false;
            }

            Parent root = activeWindow.getScene().getRoot();
            if (root == null) {
                return false;
            }

            List<String> errors = splitMessageIntoErrors(message);
            if (errors.isEmpty()) {
                return false;
            }

            Platform.runLater(() -> {
                // 1. Clear previous validation states
                clearFieldErrors(root);

                // 2. Collect editable controls
                List<javafx.scene.control.Control> controls = new ArrayList<>();
                collectEditableControls(root, controls);

                List<String> unmappedErrors = new ArrayList<>();

                // 3. Match error message context to specific field controls
                for (String err : errors) {
                    String errLower = err.toLowerCase();
                    if (errLower.contains("correo o contraseña incorrectos") || errLower.contains("credenciales son incorrectas") || errLower.contains("contraseña incorrecta")) {
                        javafx.scene.control.Control emailCtrl = findControlById(controls, "txtEmail");
                        javafx.scene.control.Control pwdCtrl = findControlById(controls, "pwdPassword");
                        if (pwdCtrl != null && !pwdCtrl.isVisible()) {
                            pwdCtrl = findControlById(controls, "txtVisiblePassword");
                        }
                        if (emailCtrl != null) applyFieldError(emailCtrl, err);
                        if (pwdCtrl != null) applyFieldError(pwdCtrl, err);
                        if (emailCtrl == null && pwdCtrl == null) {
                            unmappedErrors.add(err);
                        }
                    } else if (errLower.contains("desactivada") || errLower.contains("activada") || errLower.contains("bloqueada")) {
                        javafx.scene.control.Control emailCtrl = findControlById(controls, "txtEmail");
                        if (emailCtrl != null) {
                            applyFieldError(emailCtrl, err);
                        } else {
                            unmappedErrors.add(err);
                        }
                    } else {
                        javafx.scene.control.Control targetControl = findMatchingControl(controls, err);
                        if (targetControl != null) {
                            // Highlight field control and show error label under it
                            applyFieldError(targetControl, err);
                        } else {
                            unmappedErrors.add(err);
                        }
                    }
                }

                // 4. If there are any unmapped errors, display them in the form banner or as popup
                if (!unmappedErrors.isEmpty()) {
                    String combinedUnmapped = String.join("\n", unmappedErrors);
                    VBox formContainer = findFormContainer(root);
                    if (formContainer != null) {
                        showInlineFormBanner(formContainer, alertType, combinedUnmapped);
                    } else {
                        showAlert(alertType, alertType == Alert.AlertType.ERROR ? "Error" : "Atencion", combinedUnmapped);
                    }
                }
            });
            return true;
        } catch (Exception e) {
            System.err.println("Error rendering field or inline validation: " + e.getMessage());
            return false;
        }
    }

    private static void showInlineFormBanner(VBox formContainer, Alert.AlertType alertType, String message) {
        HBox banner = new HBox(8);
        banner.setId("formErrorBanner");
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(10, 14, 10, 14));

        String bgColor = "#fee2e2"; // Tailwind red-100
        String borderColor = "#fca5a5"; // Tailwind red-300
        String textColor = "#991b1b"; // Tailwind red-800
        String icon = "⚠ ";

        if (alertType == Alert.AlertType.INFORMATION) {
            bgColor = "#dbeafe"; // Tailwind blue-100
            borderColor = "#bfdbfe"; // Tailwind blue-300
            textColor = "#1e40af"; // Tailwind blue-800
            icon = "ⓘ ";
        }

        banner.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;",
            bgColor, borderColor
        ));

        Label lblIcon = new Label(icon);
        lblIcon.setStyle(String.format("-fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: 14px;", textColor));

        Label lblMessage = new Label(message);
        lblMessage.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 13px; -fx-font-weight: bold;", textColor));
        lblMessage.setWrapText(true);
        lblMessage.setMaxWidth(Double.MAX_VALUE);

        banner.getChildren().addAll(lblIcon, lblMessage);

        // Click to dismiss
        banner.setOnMouseClicked(event -> formContainer.getChildren().remove(banner));

        int insertIndex = 0;
        if (!formContainer.getChildren().isEmpty()) {
            Node firstChild = formContainer.getChildren().get(0);
            if (firstChild instanceof HBox || firstChild instanceof Label) {
                insertIndex = 1;
            }
        }
        formContainer.getChildren().add(insertIndex, banner);
    }

    private static void applyFieldError(javafx.scene.control.Control control, String message) {
        applyErrorStyle(control);

        Parent parent = control.getParent();
        VBox vBox = null;
        Node targetNode = control;

        if (parent instanceof VBox) {
            vBox = (VBox) parent;
        } else if (parent != null && parent.getParent() instanceof VBox) {
            vBox = (VBox) parent.getParent();
            targetNode = parent;
        }

        if (vBox != null) {
            removeFieldErrorLabel(vBox, control);

            Label errorLabel = new Label("⚠ " + message);
            errorLabel.setId("errorLabel_" + control.getId());
            // Style it with red color, bold, small size, matching user screenshots layout
            errorLabel.setStyle("-fx-text-fill: #b91c1c; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3 0 0 4;");
            errorLabel.setWrapText(true);
            errorLabel.setMaxWidth(Double.MAX_VALUE);

            int index = vBox.getChildren().indexOf(targetNode);
            vBox.getChildren().add(index + 1, errorLabel);
        }
    }

    private static void removeFieldErrorLabel(VBox vBox, javafx.scene.control.Control control) {
        vBox.getChildren().removeIf(node -> ("errorLabel_" + control.getId()).equals(node.getId()));
    }

    private static void applyErrorStyle(javafx.scene.control.Control control) {
        if (!control.getProperties().containsKey("originalStyle")) {
            control.getProperties().put("originalStyle", control.getStyle() != null ? control.getStyle() : "");
        }

        String currentStyle = control.getStyle();
        if (currentStyle == null) {
            currentStyle = "";
        }
        currentStyle = removeStyleProperty(currentStyle, "-fx-border-color");
        currentStyle = removeStyleProperty(currentStyle, "-fx-border-width");

        // Red outline style: pinkish-red border color, 1.5px border width
        control.setStyle(currentStyle + " -fx-border-color: #fca5a5; -fx-border-width: 1.5;");
    }

    private static void clearErrorStyle(javafx.scene.control.Control control) {
        if (control.getProperties().containsKey("originalStyle")) {
            control.setStyle((String) control.getProperties().get("originalStyle"));
            control.getProperties().remove("originalStyle");
        }
    }

    private static String removeStyleProperty(String style, String property) {
        String[] parts = style.split(";");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.trim().startsWith(property)) {
                sb.append(part).append(";");
            }
        }
        return sb.toString();
    }

    private static void clearFieldErrors(Parent root) {
        List<javafx.scene.control.Control> controls = new ArrayList<>();
        collectEditableControls(root, controls);
        for (javafx.scene.control.Control c : controls) {
            clearErrorStyle(c);
            Parent parent = c.getParent();
            VBox vBox = null;
            if (parent instanceof VBox) {
                vBox = (VBox) parent;
            } else if (parent != null && parent.getParent() instanceof VBox) {
                vBox = (VBox) parent.getParent();
            }
            if (vBox != null) {
                removeFieldErrorLabel(vBox, c);
            }
        }

        // Remove active banner
        VBox formContainer = findFormContainer(root);
        if (formContainer != null) {
            formContainer.getChildren().removeIf(node -> "formErrorBanner".equals(node.getId()));
        }
    }

    public static void clearFieldError(javafx.scene.control.Control control) {
        if (control == null) return;
        clearErrorStyle(control);
        Parent parent = control.getParent();
        VBox vBox = null;
        if (parent instanceof VBox) {
            vBox = (VBox) parent;
        } else if (parent != null && parent.getParent() instanceof VBox) {
            vBox = (VBox) parent.getParent();
        }
        if (vBox != null) {
            removeFieldErrorLabel(vBox, control);
        }

        if (control.getScene() != null && control.getScene().getRoot() != null) {
            VBox formContainer = findFormContainer(control.getScene().getRoot());
            if (formContainer != null) {
                formContainer.getChildren().removeIf(node -> "formErrorBanner".equals(node.getId()));
            }
        }
    }

    private static void collectEditableControls(Node node, List<javafx.scene.control.Control> list) {
        if (node instanceof javafx.scene.control.TextField ||
            node instanceof javafx.scene.control.ComboBox ||
            node instanceof javafx.scene.control.TextArea ||
            node instanceof javafx.scene.control.DatePicker) {
            list.add((javafx.scene.control.Control) node);
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                collectEditableControls(child, list);
            }
        }
    }

    private static javafx.scene.control.Control findMatchingControl(List<javafx.scene.control.Control> controls, String message) {
        String msgLower = message.toLowerCase();

        if (msgLower.contains("nombre")) {
            javafx.scene.control.Control c = findControlById(controls, "txtName");
            if (c == null) c = findControlById(controls, "txtProjectName");
            if (c == null) c = findControlById(controls, "txtActivityName");
            return c;
        }
        if (msgLower.contains("apellido paterno") || msgLower.contains("primer apellido")) {
            return findControlById(controls, "txtFirstSurname");
        }
        if (msgLower.contains("apellido materno") || msgLower.contains("segundo apellido")) {
            return findControlById(controls, "txtSecondSurname");
        }
        if (msgLower.contains("personal")) {
            return findControlById(controls, "txtStaffNumber");
        }
        if (msgLower.contains("correo") || msgLower.contains("email") || msgLower.contains("institucional")) {
            javafx.scene.control.Control c = findControlById(controls, "txtInstitutionalEmail");
            if (c == null) c = findControlById(controls, "txtEmail");
            return c;
        }
        if (msgLower.contains("matrícula") || msgLower.contains("matricula")) {
            return findControlById(controls, "txtEnrollment");
        }
        if (msgLower.contains("nrc")) {
            javafx.scene.control.Control c = findControlById(controls, "txtNrc");
            if (c == null) c = findControlById(controls, "cmbNrc");
            return c;
        }
        if (msgLower.contains("fecha inicio") || msgLower.contains("fecha de inicio")) {
            return findControlById(controls, "dpStartDate");
        }
        if (msgLower.contains("fecha fin") || msgLower.contains("fecha de fin")) {
            return findControlById(controls, "dpEndDate");
        }
        if (msgLower.contains("fecha")) {
            javafx.scene.control.Control c = findControlById(controls, "dpFecha");
            if (c == null) c = findControlById(controls, "dpFechaAsignacion");
            return c;
        }
        if (msgLower.contains("dirección") || msgLower.contains("direccion")) {
            return findControlById(controls, "txtAddress");
        }
        if (msgLower.contains("teléfono") || msgLower.contains("telefono") || msgLower.contains("tel") || msgLower.contains("phone")) {
            return findControlById(controls, "txtPhoneNumber");
        }
        if (msgLower.contains("país") || msgLower.contains("pais")) {
            return findControlById(controls, "cmbCountry");
        }
        if (msgLower.contains("estado")) {
            return findControlById(controls, "cmbState");
        }
        if (msgLower.contains("ciudad")) {
            return findControlById(controls, "cmbCity");
        }
        if (msgLower.contains("sector")) {
            return findControlById(controls, "txtSector");
        }
        if (msgLower.contains("directos")) {
            return findControlById(controls, "txtDirectUserCount");
        }
        if (msgLower.contains("indirectos")) {
            return findControlById(controls, "txtIndirectUserCount");
        }

        if (msgLower.contains("metodología") || msgLower.contains("metodologia")) {
            return findControlById(controls, "txtMethodology");
        }
        if (msgLower.contains("objetivos inmediatos")) {
            return findControlById(controls, "txaImmediateObjectives");
        }
        if (msgLower.contains("objetivos mediatos")) {
            return findControlById(controls, "txaMediateObjectives");
        }
        if (msgLower.contains("objetivo general")) {
            return findControlById(controls, "txaGeneralObjective");
        }
        if (msgLower.contains("descripción") || msgLower.contains("descripcion")) {
            return findControlById(controls, "txaGeneralDescription");
        }
        if (msgLower.contains("responsabilidades")) {
            return findControlById(controls, "txtResponsibilities");
        }
        if (msgLower.contains("recursos")) {
            return findControlById(controls, "txaResources");
        }
        if (msgLower.contains("organización") || msgLower.contains("organizacion")) {
            return findControlById(controls, "cmbOrganization");
        }
        if (msgLower.contains("responsable")) {
            return findControlById(controls, "cmbResponsible");
        }
        if (msgLower.contains("proyecto")) {
            return findControlById(controls, "cmbProject");
        }
        if (msgLower.contains("mes")) {
            return findControlById(controls, "txtActivityMonth");
        }
        if (msgLower.contains("inicio")) {
            return findControlById(controls, "txtActivityStartWeek");
        }
        if (msgLower.contains("fin")) {
            return findControlById(controls, "txtActivityEndWeek");
        }
        if (msgLower.contains("contraseña") || msgLower.contains("password")) {
            javafx.scene.control.Control c = findControlById(controls, "pwdPassword");
            if (c == null || !c.isVisible()) c = findControlById(controls, "txtVisiblePassword");
            return c;
        }

        return null;
    }

    private static javafx.scene.control.Control findControlById(List<javafx.scene.control.Control> controls, String id) {
        return controls.stream()
            .filter(c -> id.equalsIgnoreCase(c.getId()))
            .findFirst()
            .orElse(null);
    }

    private static Window getActiveWindow() {
        return Window.getWindows().stream()
            .filter(Window::isFocused)
            .findFirst()
            .orElse(Window.getWindows().isEmpty() ? null : Window.getWindows().get(0));
    }

    private static VBox findFormContainer(Node node) {
        if (node instanceof BorderPane borderPane) {
            Node center = borderPane.getCenter();
            if (center instanceof VBox vBox) {
                return vBox;
            }
            if (center instanceof ScrollPane scrollPane) {
                if (scrollPane.getContent() instanceof VBox contentVBox) {
                    return contentVBox;
                }
            }
            if (center != null) {
                VBox found = findFormContainer(center);
                if (found != null) return found;
            }
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                if (child instanceof VBox vBox) {
                    if (vBox.isVisible()) {
                        return vBox;
                    }
                }
                VBox found = findFormContainer(child);
                if (found != null) return found;
            }
        }
        return null;
    }
}
