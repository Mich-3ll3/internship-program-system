package mx.uv.internshipprogramsystem.gui.navigation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.gui.controllers.UpdateProfessorDashboardController;
import mx.uv.internshipprogramsystem.gui.controllers.UpdateInternDashboardController;
import mx.uv.internshipprogramsystem.gui.controllers.ProjectUpdateDashboardController;

public class NavigationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavigationManager.class);
    private static final String FXML_BASE_PATH = "/mx/uv/internshipprogramsystem/gui/fxml/";
    
    private static BorderPane staticMainLayout;
    private static final Deque<String> viewHistory = new ArrayDeque<>();
    private static final Map<String, Parent> viewCache = new HashMap<>();
    private static final Map<Parent, javafx.scene.Node> viewSidebarMap = new HashMap<>();
    private static String currentView;

    @FXML
    private BorderPane mainLayout;

    @FXML
    public void initialize() {
        staticMainLayout = mainLayout;
    }

    public static void logout() {
        UserSessionManager.clearSession();
        changeView("LoginDashboard.fxml");
    }

    public static void changeView(String fxmlName) {
        boolean useCache = isCacheable(fxmlName);
        changeView(fxmlName, useCache);
    }

    public static void changeView(String fxmlName, boolean useCache) {
        try {
            Parent view;
            if (useCache && viewCache.containsKey(fxmlName)) {
                view = viewCache.get(fxmlName);
                LOGGER.info("Vista recuperada de la caché: {}", fxmlName);
            } else {
                FXMLLoader loader = createLoader(fxmlName);
                view = loader.load();
                if (useCache) {
                    viewCache.put(fxmlName, view);
                    LOGGER.info("Vista guardada en la caché: {}", fxmlName);
                }
            }
            showView(fxmlName, view);
        } catch (IOException ioException) {
            LOGGER.error("Fallo crítico al cargar la vista {}.", fxmlName, ioException);
            throw new RuntimeException("Fallo al cargar la vista " + fxmlName, ioException);
        }
    }

    public static void changeViewToUpdateProfessor(String fxmlName, ProfessorDTO professor) {
        try {
            FXMLLoader loader = createLoader(fxmlName);
            Parent view = loader.load();
            setProfessorData(loader, professor);
            showView(fxmlName, view);
        } catch (IOException ioException) {
            LOGGER.error("Error al cargar vista de edición de profesor.", ioException);
            throw new RuntimeException("Error al cargar vista de edición de profesor.", ioException);
        }
    }

    public static void changeViewToUpdateIntern(String fxmlName, InternDTO intern) {
        try {
            FXMLLoader loader = createLoader(fxmlName);
            Parent view = loader.load();
            setInternData(loader, intern);
            showView(fxmlName, view);
        } catch (IOException ioException) {
            LOGGER.error("Error al cargar vista de edición de estudiante.", ioException);
            throw new RuntimeException("Error al cargar vista de edición de estudiante.", ioException);
        }
    }

    public static void changeViewToUpdateProject(String fxmlName, ProjectDTO project) {
        try {
            FXMLLoader loader = createLoader(fxmlName);
            Parent view = loader.load();
            setProjectData(loader, project);
            showView(fxmlName, view);
        } catch (IOException ioException) {
            LOGGER.error("Error al cargar vista de edición de proyecto.", ioException);
            throw new RuntimeException("Error al cargar vista de edición de proyecto.", ioException);
        }
    }

    public static void goBack() {
        if (!viewHistory.isEmpty()) {
            String previousView = viewHistory.pop();
            changeViewWithoutHistory(previousView);
        } else {
            LOGGER.info("No hay vista anterior en el historial.");
        }
    }

    private static boolean isListView(String fxmlName) {
        return fxmlName.contains("ModuleDashboard")
                || fxmlName.contains("Management")
                || fxmlName.contains("GUI")
                || fxmlName.contains("ReportHomeDashboard")
                || fxmlName.contains("SelfAssessmentHomeDashboard");
    }

    private static boolean isCacheable(String fxmlName) {
        if (fxmlName.equals("LoginDashboard.fxml")
                || fxmlName.equals("ActivateAccountDashboard.fxml")
                || fxmlName.equals("ForgotPasswordDashboard.fxml")
                || fxmlName.equals("ResendActivationTokenDashboard.fxml")
                || fxmlName.contains("Register")
                || fxmlName.contains("Update")) {
            return false;
        }
        return !isListView(fxmlName);
    }

    private static FXMLLoader createLoader(String fxmlName) throws IOException {
        URL fxmlUrl = NavigationManager.class.getResource(FXML_BASE_PATH + fxmlName);
        if (fxmlUrl == null) {
            throw new IOException("No se encontró el archivo FXML: " + FXML_BASE_PATH + fxmlName);
        }
        return new FXMLLoader(fxmlUrl);
    }

    private static void setProfessorData(FXMLLoader loader, ProfessorDTO professor) {
        Object controller = loader.getController();
        if (controller instanceof UpdateProfessorDashboardController) {
            UpdateProfessorDashboardController updateController = (UpdateProfessorDashboardController) controller;
            updateController.setProfessorData(professor);
        }
    }

    private static void setInternData(FXMLLoader loader, InternDTO intern) {
        Object controller = loader.getController();
        if (controller instanceof UpdateInternDashboardController) {
            UpdateInternDashboardController updateController = (UpdateInternDashboardController) controller;
            updateController.setInternData(intern);
        }
    }

    private static void setProjectData(FXMLLoader loader, ProjectDTO project) {
        Object controller = loader.getController();
        if (controller instanceof ProjectUpdateDashboardController) {
            ProjectUpdateDashboardController updateController = (ProjectUpdateDashboardController) controller;
            updateController.setProjectData(project);
        }
    }

    private static void showView(String fxmlName, Parent view) {
        if (staticMainLayout != null) {
            saveCurrentViewInHistory();
            currentView = fxmlName;
            
            boolean isLoginOrRecovery = fxmlName.equals("LoginDashboard.fxml")
                    || fxmlName.equals("ActivateAccountDashboard.fxml")
                    || fxmlName.equals("ForgotPasswordDashboard.fxml")
                    || fxmlName.equals("ResendActivationTokenDashboard.fxml");

            if (isLoginOrRecovery) {
                staticMainLayout.setLeft(null);
            }

            if (view instanceof BorderPane) {
                BorderPane borderPane = (BorderPane) view;
                javafx.scene.Node leftPane;
                if (viewSidebarMap.containsKey(borderPane)) {
                    leftPane = viewSidebarMap.get(borderPane);
                } else {
                    leftPane = borderPane.getLeft();
                    if (leftPane != null) {
                        viewSidebarMap.put(borderPane, leftPane);
                    }
                }
                borderPane.setLeft(null);
                if (leftPane != null) {
                    staticMainLayout.setLeft(leftPane);
                }
                staticMainLayout.setCenter(borderPane);
            } else {
                staticMainLayout.setCenter(view);
            }
        } else {
            LOGGER.error("No se pudo cambiar la vista porque staticMainLayout es null.");
        }
    }

    private static void changeViewWithoutHistory(String fxmlName) {
        try {
            Parent view;
            boolean useCache = isCacheable(fxmlName);
            if (useCache && viewCache.containsKey(fxmlName)) {
                view = viewCache.get(fxmlName);
                LOGGER.info("Vista recuperada de la caché al regresar: {}", fxmlName);
            } else {
                FXMLLoader loader = createLoader(fxmlName);
                view = loader.load();
                if (useCache) {
                    viewCache.put(fxmlName, view);
                    LOGGER.info("Vista guardada en la caché al regresar: {}", fxmlName);
                }
            }
            currentView = fxmlName;
            if (staticMainLayout != null) {
                boolean isLoginOrRecovery = fxmlName.equals("LoginDashboard.fxml")
                        || fxmlName.equals("ActivateAccountDashboard.fxml")
                        || fxmlName.equals("ForgotPasswordDashboard.fxml")
                        || fxmlName.equals("ResendActivationTokenDashboard.fxml");

                if (isLoginOrRecovery) {
                    staticMainLayout.setLeft(null);
                }

                if (view instanceof BorderPane) {
                    BorderPane borderPane = (BorderPane) view;
                    javafx.scene.Node leftPane;
                    if (viewSidebarMap.containsKey(borderPane)) {
                        leftPane = viewSidebarMap.get(borderPane);
                    } else {
                        leftPane = borderPane.getLeft();
                        if (leftPane != null) {
                            viewSidebarMap.put(borderPane, leftPane);
                        }
                    }
                    borderPane.setLeft(null);
                    if (leftPane != null) {
                        staticMainLayout.setLeft(leftPane);
                    }
                    staticMainLayout.setCenter(borderPane);
                } else {
                    staticMainLayout.setCenter(view);
                }
            } else {
                LOGGER.error("No se pudo regresar porque staticMainLayout es null.");
            }
        } catch (IOException ioException) {
            LOGGER.error("Fallo crítico al regresar a la vista {}.", fxmlName, ioException);
            throw new RuntimeException("Fallo al regresar a la vista " + fxmlName, ioException);
        }
    }


    private static void saveCurrentViewInHistory() {
        if (currentView != null) {
            viewHistory.push(currentView);
        }
    }
}
