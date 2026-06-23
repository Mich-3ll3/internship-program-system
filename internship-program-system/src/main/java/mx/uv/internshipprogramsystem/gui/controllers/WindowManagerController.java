package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.charset.StandardCharsets;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.gui.util.ChangeViewToUpdateProjectRunnable;

public class WindowManagerController {

    private static BorderPane mainLayout;
    private static final Logger LOGGER = Logger.getLogger(WindowManagerController.class.getName());
    private static final Deque<String> viewHistory = new ArrayDeque<>();
    private static String currentView;

    private static Parent dashboardLayoutRoot;
    private static MainDashboardLayoutController dashboardLayoutController;

    @FXML
    private StackPane containerArea;

    public static void setMainLayout(BorderPane layout) {
        mainLayout = layout;
    }

    private static boolean isLoginView(String fxmlName) {
        return "LoginDashboard.fxml".equalsIgnoreCase(fxmlName)
            || "ForgotPasswordDashboard.fxml".equalsIgnoreCase(fxmlName)
            || "ActivateAccountDashboard.fxml".equalsIgnoreCase(fxmlName)
            || "ResendActivationTokenDashboard.fxml".equalsIgnoreCase(fxmlName)
            || "LoginGUI.fxml".equalsIgnoreCase(fxmlName);
    }

    private static Parent getOrCreateDashboardLayout() throws IOException {
        if (dashboardLayoutRoot == null) {
            URL fxmlUrl = WindowManagerController.class.getResource(
                "/mx/uv/internshipprogramsystem/gui/fxml/MainDashboardLayout.fxml"
            );
            if (fxmlUrl == null) {
                throw new IOException("No se encontró MainDashboardLayout.fxml");
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setCharset(StandardCharsets.UTF_8);
            dashboardLayoutRoot = loader.load();
            dashboardLayoutController = loader.getController();
        }
        return dashboardLayoutRoot;
    }

    public static Pane getNavigationTarget() {
        if (dashboardLayoutController != null) {
            return dashboardLayoutController.getPneViewContainer();
        }
        return mainLayout;
    }

    public static void changeView(String fxmlName) {
        Platform.runLater(new mx.uv.internshipprogramsystem.gui.util.ChangeViewRunnable(fxmlName));
    }

    public static void executeChangeView(String fxmlName) {
        try {
            if (isLoginView(fxmlName)) {
                dashboardLayoutRoot = null;
                dashboardLayoutController = null;
                mx.uv.internshipprogramsystem.gui.util.NavigationDashboardManager.clearCache();

                URL fxmlUrl = WindowManagerController.class.getResource(
                    "/mx/uv/internshipprogramsystem/gui/fxml/" + fxmlName
                );
                if (fxmlUrl == null) {
                    throw new IOException("No se encontró FXML en: " + fxmlName);
                }
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                loader.setCharset(StandardCharsets.UTF_8);
                Parent view = loader.load();

                if (mainLayout != null) {
                    if (currentView != null) {
                        viewHistory.push(currentView);
                    }
                    currentView = fxmlName;
                    mainLayout.setCenter(view);

                    if (mainLayout.getScene() != null && mainLayout.getScene().getWindow() instanceof javafx.stage.Stage stage) {
                        stage.sizeToScene();
                        stage.centerOnScreen();
                    }
                }
            } else {
                Parent layout = getOrCreateDashboardLayout();
                boolean layoutChanged = false;
                if (mainLayout != null && mainLayout.getCenter() != layout) {
                    mainLayout.setCenter(layout);
                    layoutChanged = true;
                }

                mx.uv.internshipprogramsystem.gui.util.NavigationDashboardManager.loadCenterView(
                    dashboardLayoutController.getPneViewContainer(),
                    "/mx/uv/internshipprogramsystem/gui/fxml/" + fxmlName
                );

                if (dashboardLayoutController != null) {
                    boolean isHome = "AdminHomeDashboard.fxml".equalsIgnoreCase(fxmlName)
                        || "CoordinatorProfessorHomeDashboard.fxml".equalsIgnoreCase(fxmlName)
                        || "ProfessorHomeDashboard.fxml".equalsIgnoreCase(fxmlName)
                        || "InternHomeDashboard.fxml".equalsIgnoreCase(fxmlName);
                    dashboardLayoutController.updateHeaderVisibility(isHome);
                }

                if (currentView != null) {
                    viewHistory.push(currentView);
                }
                currentView = fxmlName;

                if (layoutChanged && mainLayout != null && mainLayout.getScene() != null && mainLayout.getScene().getWindow() instanceof javafx.stage.Stage stage) {
                    stage.sizeToScene();
                    stage.centerOnScreen();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fallo crítico al cargar vista: " + fxmlName, e);
        }
    }

    public static void changeViewToUpdateProfessor(String fxmlName, ProfessorDTO professor) {
        Platform.runLater(new mx.uv.internshipprogramsystem.gui.util.ChangeViewToUpdateProfessorRunnable(fxmlName, professor));
    }

    public static void executeChangeViewToUpdateProfessor(String fxmlName, ProfessorDTO professor) {
        try {
            Parent layout = getOrCreateDashboardLayout();
            if (mainLayout != null && mainLayout.getCenter() != layout) {
                mainLayout.setCenter(layout);
            }

            String path = "/mx/uv/internshipprogramsystem/gui/fxml/" + fxmlName;
            URL fxmlUrl = WindowManagerController.class.getResource(path);
            if (fxmlUrl == null) {
                throw new IOException("No se encontró FXML en: " + fxmlName);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setCharset(StandardCharsets.UTF_8);
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UpdateProfessorDashboardController updateController) {
                updateController.setProfessorData(professor);
            }

            mx.uv.internshipprogramsystem.gui.util.NavigationDashboardManager.displayView(
                dashboardLayoutController.getPneViewContainer(),
                view
            );
            if (dashboardLayoutController != null) {
                dashboardLayoutController.updateHeaderVisibility(false);
            }
            if (currentView != null) {
                viewHistory.push(currentView);
            }
            currentView = fxmlName;
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Error al cargar vista de edición", exception);
        }
    }

    public static void changeViewToUpdateIntern(String fxmlName, InternDTO intern) {
        Platform.runLater(new mx.uv.internshipprogramsystem.gui.util.ChangeViewToUpdateInternRunnable(fxmlName, intern));
    }

    public static void executeChangeViewToUpdateIntern(String fxmlName, InternDTO intern) {
        try {
            Parent layout = getOrCreateDashboardLayout();
            if (mainLayout != null && mainLayout.getCenter() != layout) {
                mainLayout.setCenter(layout);
            }

            String path = "/mx/uv/internshipprogramsystem/gui/fxml/" + fxmlName;
            URL fxmlUrl = WindowManagerController.class.getResource(path);
            if (fxmlUrl == null) {
                throw new IOException("No se encontró FXML en: " + fxmlName);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setCharset(StandardCharsets.UTF_8);
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UpdateInternDashboardController updateController) {
                updateController.setInternData(intern);
            }

            mx.uv.internshipprogramsystem.gui.util.NavigationDashboardManager.displayView(
                dashboardLayoutController.getPneViewContainer(),
                view
            );
            if (dashboardLayoutController != null) {
                dashboardLayoutController.updateHeaderVisibility(false);
            }
            if (currentView != null) {
                viewHistory.push(currentView);
            }
            currentView = fxmlName;
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Error al cargar vista de edición", exception);
        }
    }



    public static void changeViewToUpdateProject(String fxmlName, ProjectDTO project) {
        Platform.runLater(new ChangeViewToUpdateProjectRunnable(fxmlName, project));
    }

    public static void executeChangeViewToUpdateProject(String fxmlName, ProjectDTO project) {
        try {
            Parent layout = getOrCreateDashboardLayout();
            if (mainLayout != null && mainLayout.getCenter() != layout) {
                mainLayout.setCenter(layout);
            }

            String path = "/mx/uv/internshipprogramsystem/gui/fxml/" + fxmlName;
            URL fxmlUrl = WindowManagerController.class.getResource(path);
            if (fxmlUrl == null) {
                throw new IOException("No se encontró FXML en: " + fxmlName);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setCharset(StandardCharsets.UTF_8);
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ProjectUpdateDashboardController updateController) {
                updateController.setProjectData(project);
            }

            mx.uv.internshipprogramsystem.gui.util.NavigationDashboardManager.displayView(
                dashboardLayoutController.getPneViewContainer(),
                view
            );
            if (dashboardLayoutController != null) {
                dashboardLayoutController.updateHeaderVisibility(false);
            }
            if (currentView != null) {
                viewHistory.push(currentView);
            }
            currentView = fxmlName;
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Error al cargar vista de edición de proyecto", exception);
        }
    }

    public static void goBack() {
        Platform.runLater(new mx.uv.internshipprogramsystem.gui.util.GoBackRunnable());
    }

    public static void executeGoBack() {
        if (!viewHistory.isEmpty()) {
            String previousView = viewHistory.pop();
            changeView(previousView);
        } else {
            LOGGER.log(Level.INFO, "No hay vista anterior en el historial.");
        }
    }
}
