package mx.uv.internshipprogramsystem.gui.controllers;

import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import mx.uv.internshipprogramsystem.gui.util.NavigationDashboardManager;
import mx.uv.internshipprogramsystem.gui.util.ViewLoadException;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.managers.SessionManager;
import mx.uv.internshipprogramsystem.logic.security.MenuAuthorityRegistry;
import mx.uv.internshipprogramsystem.logic.security.MenuOption;
import mx.uv.internshipprogramsystem.logic.security.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SideMenuDashboardController {
    private static final Logger LOGGER = LoggerFactory.getLogger(
        SideMenuDashboardController.class
    );

    @FXML
    private VBox pneSideMenu;

    private static final String ACTIVE_STYLE =
        "-fx-background-color: #ffffff; " +
        "-fx-background-radius: 10; " +
        "-fx-cursor: hand;";

    private static final String INACTIVE_STYLE =
        "-fx-background-color: transparent; " +
        "-fx-background-radius: 10; " +
        "-fx-cursor: hand;";

    private static final String HOVER_STYLE =
        "-fx-background-color: rgba(255, 255, 255, 0.15); " +
        "-fx-background-radius: 10; " +
        "-fx-cursor: hand;";

    @FXML
    private void initialize() {
        SessionManager sessionManager = SessionManager.getInstance();
        UserDTO user = sessionManager.getCurrentUser();
        List<Permission> permissions = sessionManager.getPermissions();

        if (user != null) {
            pneSideMenu.getChildren().clear();
            List<MenuOption> options = MenuAuthorityRegistry.getAvailableOptions(
                user.getRole(),
                permissions
            );
            for (MenuOption option : options) {
                createMenuButton(option);
            }
        }
    }

    private void createMenuButton(MenuOption option) {
        Button btnOption = new Button();
        btnOption.getStyleClass().add("menu-item");
        btnOption.setPrefSize(40.0, 40.0);
        btnOption.setMinSize(40.0, 40.0);
        btnOption.setMaxSize(40.0, 40.0);
        btnOption.setAlignment(Pos.CENTER);

        SVGPath svgIcon = loadIcon(option.getIconPath());
        if (svgIcon != null) {
            btnOption.setGraphic(svgIcon);
        }

        btnOption.setOnAction(new mx.uv.internshipprogramsystem.gui.handlers.SideMenuButtonHandler(this, btnOption, option));

        if ("Inicio".equals(option.getTitle())) {
            btnOption.getStyleClass().add("menu-item-active");
            applyButtonStyle(btnOption, ACTIVE_STYLE, Color.web("#4a90e2"));
        } else {
            applyButtonStyle(btnOption, INACTIVE_STYLE, Color.WHITE);
            setupButtonHover(btnOption);
        }

        pneSideMenu.getChildren().add(btnOption);
    }

    private SVGPath loadIcon(String path) {
        if (path != null && path.startsWith("M")) {
            SVGPath svg = new SVGPath();
            svg.setContent(path);
            svg.setFill(Color.WHITE);
            return svg;
        }
        return null;
    }

    public void handleBtnOptionClick(Button btnSelected, MenuOption option) {
        for (javafx.scene.Node node : pneSideMenu.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.getStyleClass().remove("menu-item-active");
                applyButtonStyle(btn, INACTIVE_STYLE, Color.WHITE);
                setupButtonHover(btn);
            }
        }

        btnSelected.getStyleClass().add("menu-item-active");
        applyButtonStyle(btnSelected, ACTIVE_STYLE, Color.web("#4a90e2"));
        btnSelected.setOnMouseEntered(null);
        btnSelected.setOnMouseExited(null);

        String path = option.getFxmlPath();
        if (path != null && !path.trim().isEmpty()) {
            String fxmlName = path;
            if (fxmlName.contains("/")) {
                fxmlName = fxmlName.substring(fxmlName.lastIndexOf("/") + 1);
            }
            WindowManagerController.changeView(fxmlName);
        }
    }

    public void handleMouseEntered(Button button) {
        if (!button.getStyleClass().contains("menu-item-active")) {
            applyButtonStyle(button, HOVER_STYLE, Color.WHITE);
        }
    }

    public void handleMouseExited(Button button) {
        if (!button.getStyleClass().contains("menu-item-active")) {
            applyButtonStyle(button, INACTIVE_STYLE, Color.WHITE);
        }
    }

    private void setupButtonHover(Button button) {
        button.setOnMouseEntered(new mx.uv.internshipprogramsystem.gui.handlers.SideMenuButtonMouseEnteredHandler(this, button));
        button.setOnMouseExited(new mx.uv.internshipprogramsystem.gui.handlers.SideMenuButtonMouseExitedHandler(this, button));
    }

    private void applyButtonStyle(Button button, String style, Color iconColor) {
        button.setStyle(style);
        if (button.getGraphic() instanceof SVGPath) {
            ((SVGPath) button.getGraphic()).setFill(iconColor);
        }
    }


}
