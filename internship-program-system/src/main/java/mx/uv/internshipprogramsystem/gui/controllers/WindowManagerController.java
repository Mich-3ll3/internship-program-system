package mx.uv.internshipprogramsystem.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;

public class WindowManagerController {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            WindowManagerController.class
        );

    private static final String FXML_BASE_PATH =
        "/mx/uv/internshipprogramsystem/gui/fxml/";

    private static BorderPane mainLayout;

    private static final Deque<String> viewHistory =
        new ArrayDeque<>();

    private static String currentView;

    @FXML
    private StackPane containerArea;

    public static void setMainLayout(
            BorderPane layout
    ) {
        mainLayout =
            layout;
    }

    public static void changeView(
            String fxmlName
    ) {
        try {
            FXMLLoader loader =
                createLoader(
                    fxmlName
                );

            Parent view =
                loader.load();

            showView(
                fxmlName,
                view
            );
        } catch (IOException ioException) {
            LOGGER.error(
                "Fallo crítico al cargar la vista {}.",
                fxmlName,
                ioException
            );
        }
    }

    public static void changeViewToUpdateProfessor(
            String fxmlName,
            ProfessorDTO professor
    ) {
        try {
            FXMLLoader loader =
                createLoader(
                    fxmlName
                );

            Parent view =
                loader.load();

            setProfessorData(
                loader,
                professor
            );

            showView(
                fxmlName,
                view
            );
        } catch (IOException ioException) {
            LOGGER.error(
                "Error al cargar vista de edición de profesor.",
                ioException
            );
        }
    }

    public static void changeViewToUpdateIntern(
            String fxmlName,
            InternDTO intern
    ) {
        try {
            FXMLLoader loader =
                createLoader(
                    fxmlName
                );

            Parent view =
                loader.load();

            setInternData(
                loader,
                intern
            );

            showView(
                fxmlName,
                view
            );
        } catch (IOException ioException) {
            LOGGER.error(
                "Error al cargar vista de edición de estudiante.",
                ioException
            );
        }
    }

    public static void changeViewToUpdateProject(
            String fxmlName,
            ProjectDTO project
    ) {
        try {
            FXMLLoader loader =
                createLoader(
                    fxmlName
                );

            Parent view =
                loader.load();

            setProjectData(
                loader,
                project
            );

            showView(
                fxmlName,
                view
            );
        } catch (IOException ioException) {
            LOGGER.error(
                "Error al cargar vista de edición de proyecto.",
                ioException
            );
        }
    }

    public static void goBack() {
        if (!viewHistory.isEmpty()) {
            String previousView =
                viewHistory.pop();

            changeViewWithoutHistory(
                previousView
            );
        } else {
            LOGGER.info(
                "No hay vista anterior en el historial."
            );
        }
    }

    private static FXMLLoader createLoader(
            String fxmlName
    ) throws IOException {
        URL fxmlUrl =
            WindowManagerController.class.getResource(
                FXML_BASE_PATH + fxmlName
            );

        if (fxmlUrl == null) {
            throw new IOException(
                "No se encontró el archivo FXML: "
                    + FXML_BASE_PATH
                    + fxmlName
            );
        }

        FXMLLoader loader =
            new FXMLLoader(
                fxmlUrl
            );

        return loader;
    }

    private static void setProfessorData(
            FXMLLoader loader,
            ProfessorDTO professor
    ) {
        Object controller =
            loader.getController();

        if (controller instanceof UpdateProfessorDashboardController) {
            UpdateProfessorDashboardController updateController =
                (UpdateProfessorDashboardController) controller;

            updateController.setProfessorData(
                professor
            );
        }
    }

    private static void setInternData(
            FXMLLoader loader,
            InternDTO intern
    ) {
        Object controller =
            loader.getController();

        if (controller instanceof UpdateInternDashboardController) {
            UpdateInternDashboardController updateController =
                (UpdateInternDashboardController) controller;

            updateController.setInternData(
                intern
            );
        }
    }

    private static void setProjectData(
            FXMLLoader loader,
            ProjectDTO project
    ) {
        Object controller =
            loader.getController();

        if (controller instanceof ProjectUpdateDashboardController) {
            ProjectUpdateDashboardController updateController =
                (ProjectUpdateDashboardController) controller;

            updateController.setProjectData(
                project
            );
        }
    }

    private static void showView(
            String fxmlName,
            Parent view
    ) {
        if (mainLayout != null) {
            saveCurrentViewInHistory();

            currentView =
                fxmlName;

            mainLayout.setCenter(
                view
            );
        } else {
            LOGGER.error(
                "No se pudo cambiar la vista porque mainLayout es null."
            );
        }
    }

    private static void changeViewWithoutHistory(
            String fxmlName
    ) {
        try {
            FXMLLoader loader =
                createLoader(
                    fxmlName
                );

            Parent view =
                loader.load();

            currentView =
                fxmlName;

            if (mainLayout != null) {
                mainLayout.setCenter(
                    view
                );
            } else {
                LOGGER.error(
                    "No se pudo regresar porque mainLayout es null."
                );
            }
        } catch (IOException ioException) {
            LOGGER.error(
                "Fallo crítico al regresar a la vista {}.",
                fxmlName,
                ioException
            );
        }
    }

    private static void saveCurrentViewInHistory() {
        if (currentView != null) {
            viewHistory.push(
                currentView
            );
        }
    }
}