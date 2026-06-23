package mx.uv.internshipprogramsystem.gui.controllers;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.gui.handlers.AlwaysTruePredicate;
import mx.uv.internshipprogramsystem.gui.handlers.OrganizationSearchListener;
import mx.uv.internshipprogramsystem.gui.handlers.OrganizationPredicate;
import mx.uv.internshipprogramsystem.gui.util.FormAlertSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkedOrganizationManagementController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(LinkedOrganizationManagementController.class);

    @FXML
    private TextField txtSearchName;

    @FXML
    private TableView<LinkedOrganizationDTO> tblOrganizations;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> colName;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> colEmail;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> colPhoneNumber;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> colCountry;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> colState;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> colCity;

    @FXML
    private TableColumn<LinkedOrganizationDTO, String> colSector;

    private final LinkedOrganizationDAO organizationDAO =
        new LinkedOrganizationDAO();

    private final ObservableList<LinkedOrganizationDTO> masterData =
        FXCollections.observableArrayList();

    private FilteredList<LinkedOrganizationDTO> filteredOrganizations;

    @FXML
    public void initialize() {
        configureTable();
        configureSearch();
        loadOrganizations();
    }

    private void configureTable() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhoneNumber.setCellValueFactory(
            new PropertyValueFactory<>("phoneNumber")
        );
        colCountry.setCellValueFactory(new PropertyValueFactory<>("country"));
        colState.setCellValueFactory(new PropertyValueFactory<>("state"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colSector.setCellValueFactory(new PropertyValueFactory<>("sector"));

        filteredOrganizations =
            new FilteredList<>(masterData, new AlwaysTruePredicate<>());
        tblOrganizations.setItems(filteredOrganizations);
    }

    private void configureSearch() {
        txtSearchName
            .textProperty()
            .addListener(new OrganizationSearchListener(this));
    }

    private void loadOrganizations() {
        try {
            List<LinkedOrganizationDTO> organizations =
                organizationDAO.findAll();
            masterData.setAll(organizations);
        } catch (BusinessException exception) {
            LOGGER.error("Error al cargar organizaciones", exception);
            FormAlertSupport.showError(
                "Error de carga",
                "No se pudo cargar la lista de organizaciones."
            );
        } catch (DataAccessException exception) {
            LOGGER.error("Error de conexion al cargar organizaciones", exception);
            FormAlertSupport.showError(
                "Error de conexion",
                "No se pudo conectar con la base de datos para cargar las organizaciones. Por favor intente mas tarde."
            );
        }
    }

    @FXML
    private void validateSearchOrganization() {
        applyOrganizationFilter(txtSearchName.getText());
    }

    public void applyOrganizationFilter(String rawQuery) {
        String query =
            rawQuery == null
                ? ""
                : rawQuery.trim().toLowerCase();

        filteredOrganizations.setPredicate(new OrganizationPredicate(query));
    }

    @FXML
    private void goRegisterOrganization(ActionEvent event) {
        WindowManagerController.changeView(
            "RegisterLinkedOrganizationDashboard.fxml"
        );
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView(
            "CoordinatorProfessorHomeDashboard.fxml"
        );
    }

    @FXML
    private void logOut(ActionEvent event) {
        UserSessionManager.clearSession();
        LOGGER.info("Cierre de sesion realizado correctamente.");
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

    @FXML
    private void goLinkedOrganizationModule(ActionEvent event) {
        WindowManagerController.changeView(
            "LinkedOrganizationManagementGUI.fxml"
        );
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        WindowManagerController.changeView("InternModuleDashboard.fxml");
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        // Modulo pendiente de implementar.
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        WindowManagerController.changeView("ProjectModuleDashboard.fxml");
    }
}
