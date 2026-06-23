package mx.uv.internshipprogramsystem.gui.controllers;

import mx.uv.internshipprogramsystem.gui.navigation.NavigationManager;
import mx.uv.internshipprogramsystem.gui.util.FormAlertSupport;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.LocationCatalogManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;
import mx.uv.internshipprogramsystem.logic.validations.InputCleaner;
import mx.uv.internshipprogramsystem.logic.validations.LinkedOrganizationValidator;

public class RegisterLinkedOrganizationDashboardController {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            RegisterLinkedOrganizationDashboardController.class
        );

    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int MAX_PHONE_LENGTH = 10;
    private static final int MAX_ADDRESS_LENGTH = 150;
    private static final int MAX_COUNTRY_LENGTH = 100;
    private static final int MAX_CITY_LENGTH = 60;
    private static final int MAX_STATE_LENGTH = 60;
    private static final int MAX_SECTOR_LENGTH = 80;
    private static final int MAX_USER_COUNT_LENGTH = 6;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private TextField txtAddress;

    @FXML
    private ComboBox<String> cmbCountry;

    @FXML
    private ComboBox<String> cmbState;

    @FXML
    private ComboBox<String> cmbCity;

    @FXML
    private TextField txtSector;

    @FXML
    private TextField txtDirectUserCount;

    @FXML
    private TextField txtIndirectUserCount;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnIntern;

    @FXML
    private Button btnReports;

    @FXML
    private Button btnProjects;

    @FXML
    private Button btnLinkedOrganization;

    @FXML
    private Button btnExit;

    private final LinkedOrganizationDAO organizationDAO =
        new LinkedOrganizationDAO();

    private LocationCatalogManager locationCatalogManager;

    private List<String> countryOptions =
        new ArrayList<>();

    private List<String> stateOptions =
        new ArrayList<>();

    private List<String> cityOptions =
        new ArrayList<>();

    private boolean updatingCountryOptions;

    private boolean updatingStateOptions;

    private boolean updatingCityOptions;

    @FXML
    private void initialize() {
        btnHome.setOnAction(new mx.uv.internshipprogramsystem.gui.handlers.NavigationActionHandler("goBack"));
        btnIntern.setOnAction(new mx.uv.internshipprogramsystem.gui.handlers.NavigationActionHandler("InternModuleDashboard.fxml", Permission.CONSULT_INTERN));
        btnReports.setOnAction(new mx.uv.internshipprogramsystem.gui.handlers.NavigationActionHandler("ReportHomeDashboard.fxml", Permission.CONSULT_REPORT));
        btnProjects.setOnAction(new mx.uv.internshipprogramsystem.gui.handlers.NavigationActionHandler("ProjectsModuleDashboard.fxml", Permission.CONSULT_PROJECT));
        btnLinkedOrganization.setOnAction(new mx.uv.internshipprogramsystem.gui.handlers.NavigationActionHandler("LinkedOrganizationManagementGUI.fxml", Permission.CONSULT_ORGANIZATION));
        btnExit.setOnAction(new mx.uv.internshipprogramsystem.gui.handlers.LogoutActionHandler());

        try {
            configureInputFields();

            loadLocationCatalog();
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al cargar catalogo de ubicaciones.",
                businessException
            );

            FormAlertSupport.showError(
                "Error de catalogo",
                businessException.getMessage()
            );
        } catch (DataAccessException dataAccessException) {
            LOGGER.error(
                "Error de conexion al cargar catalogo de ubicaciones.",
                dataAccessException
            );

            FormAlertSupport.showError(
                "Error de conexion",
                "No se pudo conectar con la base de datos para cargar las ubicaciones. Por favor intente mas tarde."
            );
        }
    }

    private void configureInputFields() {
        limitTextField(
            txtName,
            MAX_NAME_LENGTH
        );

        limitTextField(
            txtEmail,
            MAX_EMAIL_LENGTH
        );

        limitNumericTextField(
            txtPhoneNumber,
            MAX_PHONE_LENGTH
        );

        limitTextField(
            txtAddress,
            MAX_ADDRESS_LENGTH
        );

        limitTextField(
            txtSector,
            MAX_SECTOR_LENGTH
        );

        limitNumericTextField(
            txtDirectUserCount,
            MAX_USER_COUNT_LENGTH
        );

        limitNumericTextField(
            txtIndirectUserCount,
            MAX_USER_COUNT_LENGTH
        );
    }

    private void loadLocationCatalog()
            throws BusinessException, DataAccessException {
        locationCatalogManager =
            LocationCatalogManager.loadDefault();

        countryOptions =
            locationCatalogManager.getCountries();

        cmbCountry.setEditable(
            true
        );

        cmbState.setEditable(
            true
        );

        cmbCity.setEditable(
            true
        );

        limitTextField(
            cmbCountry.getEditor(),
            MAX_COUNTRY_LENGTH
        );

        limitTextField(
            cmbState.getEditor(),
            MAX_STATE_LENGTH
        );

        limitTextField(
            cmbCity.getEditor(),
            MAX_CITY_LENGTH
        );

        cmbCountry.getItems().setAll(
            countryOptions
        );

        cmbState.setDisable(
            false
        );

        cmbCity.setDisable(
            false
        );

        configureLocationListeners();
    }

    public void handleCountryTextChangedExternal(String selectedCountry) {
        if (!updatingCountryOptions) {
            handleCountryTextChanged(selectedCountry);
        }
    }

    public void handleStateTextChangedExternal(String selectedState) {
        if (!updatingStateOptions) {
            handleStateTextChanged(selectedState);
        }
    }

    public void handleCityTextChangedExternal(String selectedCity) {
        if (!updatingCityOptions) {
            filterComboBoxOptions(
                cmbCity,
                cityOptions,
                selectedCity,
                LocationField.CITY
            );
        }
    }

    private void configureLocationListeners() {
        cmbCountry
            .getEditor()
            .textProperty()
            .addListener(
                new mx.uv.internshipprogramsystem.gui.handlers.CountryTextChangedListener(this)
            );

        cmbState
            .getEditor()
            .textProperty()
            .addListener(
                new mx.uv.internshipprogramsystem.gui.handlers.StateTextChangedListener(this)
            );

        cmbCity
            .getEditor()
            .textProperty()
            .addListener(
                new mx.uv.internshipprogramsystem.gui.handlers.CityTextChangedListener(this)
            );
    }

    private void handleCountryTextChanged(
            String countryText
    ) {
        filterComboBoxOptions(
            cmbCountry,
            countryOptions,
            countryText,
            LocationField.COUNTRY
        );

        String matchedCountry =
            findExactMatch(
                countryOptions,
                countryText
            );

        loadStatesByCountry(
            matchedCountry,
            countryText
        );
    }

    private void handleStateTextChanged(
            String stateText
    ) {
        filterComboBoxOptions(
            cmbState,
            stateOptions,
            stateText,
            LocationField.STATE
        );

        String countryText =
            getComboBoxText(
                cmbCountry
            );

        String matchedCountry =
            findExactMatch(
                countryOptions,
                countryText
            );

        String matchedState =
            findExactMatch(
                stateOptions,
                stateText
            );

        loadCitiesByState(
            matchedCountry,
            matchedState,
            stateText
        );
    }

    private void loadStatesByCountry(
            String matchedCountry,
            String countryText
    ) {
        stateOptions =
            new ArrayList<>();

        if (matchedCountry != null) {
            stateOptions =
                new ArrayList<>(
                    locationCatalogManager.getStatesByCountry(
                        matchedCountry
                    )
                );
        }

        updateComboBoxItems(
            cmbState,
            stateOptions,
            "",
            LocationField.STATE
        );

        updateComboBoxItems(
            cmbCity,
            List.of(),
            "",
            LocationField.CITY
        );

        cityOptions =
            new ArrayList<>();

        cmbState.setDisable(
            countryText == null || countryText.isBlank()
        );

        cmbCity.setDisable(
            countryText == null || countryText.isBlank()
        );
    }

    private void loadCitiesByState(
            String matchedCountry,
            String matchedState,
            String stateText
    ) {
        cityOptions =
            new ArrayList<>();

        if (matchedCountry != null && matchedState != null) {
            cityOptions =
                new ArrayList<>(
                    locationCatalogManager.getCitiesByCountryAndState(
                        matchedCountry,
                        matchedState
                    )
                );
        }

        updateComboBoxItems(
            cmbCity,
            cityOptions,
            "",
            LocationField.CITY
        );

        cmbCity.setDisable(
            stateText == null || stateText.isBlank()
        );
    }

    private void filterComboBoxOptions(
            ComboBox<String> comboBox,
            List<String> options,
            String typedText,
            LocationField locationField
    ) {
        String normalizedText =
            normalizeText(
                typedText
            );

        List<String> filteredOptions =
            new ArrayList<>();

        for (String option : options) {
            if (normalizeText(option).contains(normalizedText)) {
                filteredOptions.add(
                    option
                );
            }
        }

        updateComboBoxItems(
            comboBox,
            filteredOptions,
            typedText,
            locationField
        );
    }

    private void updateComboBoxItems(
            ComboBox<String> comboBox,
            List<String> options,
            String editorText,
            LocationField locationField
    ) {
        setUpdatingFlag(
            locationField,
            true
        );

        comboBox.getItems().setAll(
            options
        );

        comboBox
            .getEditor()
            .setText(
                editorText == null ? "" : editorText
            );

        comboBox
            .getEditor()
            .positionCaret(
                comboBox.getEditor().getText().length()
            );

        if (!options.isEmpty()
                && editorText != null
                && !editorText.isBlank()
                && comboBox.isFocused()) {
            comboBox.show();
        }

        setUpdatingFlag(
            locationField,
            false
        );
    }

    private void setUpdatingFlag(
            LocationField locationField,
            boolean value
    ) {
        switch (locationField) {
            case COUNTRY:
                updatingCountryOptions =
                    value;
                break;
            case STATE:
                updatingStateOptions =
                    value;
                break;
            case CITY:
                updatingCityOptions =
                    value;
                break;
            default:
                break;
        }
    }

    private String findExactMatch(
            List<String> options,
            String value
    ) {
        String exactMatch =
            null;

        String normalizedValue =
            normalizeText(
                value
            );

        for (String option : options) {
            if (normalizeText(option).equals(normalizedValue)) {
                exactMatch =
                    option;
                break;
            }
        }

        return exactMatch;
    }

    private String normalizeText(
            String text
    ) {
        String normalizedText =
            "";

        if (text != null) {
            normalizedText =
                text
                    .trim()
                    .toLowerCase();
        }

        return normalizedText;
    }

    @FXML
    private void validateOrganizationForm() {
        try {
            validatePermission(
                Permission.REGISTER_ORGANIZATION
            );

            LinkedOrganizationDTO organization =
                buildLinkedOrganization();

            LinkedOrganizationValidator validator =
                new LinkedOrganizationValidator();

            validator.validateFullOrganization(
                organization
            );

            registerOrganization(
                organization
            );
        } catch (NumberFormatException numberFormatException) {
            LOGGER.warn(
                "Formato invalido en campos numericos.",
                numberFormatException
            );

            FormAlertSupport.showError(
                "Error de formato",
                "Los usuarios directos e indirectos deben ser numeros."
            );
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al registrar organizacion.",
                businessException
            );

            FormAlertSupport.showError(
                "Error",
                businessException.getMessage()
            );
        } catch (DataAccessException dataAccessException) {
            LOGGER.error(
                "Error de conexion al registrar organizacion.",
                dataAccessException
            );

            FormAlertSupport.showError(
                "Error de conexion",
                "No se pudo conectar con la base de datos. Por favor intente mas tarde."
            );
        }
    }

    private void registerOrganization(
            LinkedOrganizationDTO organization
    ) throws BusinessException, DataAccessException {
        boolean wasCreated =
            organizationDAO.createLinkedOrganization(
                organization
            );

        if (wasCreated) {
            FormAlertSupport.showInformation(
                "Registro exitoso",
                "Organizacion registrada correctamente."
            );

            clearForm();
        }
    }

    private LinkedOrganizationDTO buildLinkedOrganization()
            throws BusinessException {
        LinkedOrganizationDTO organization =
            new LinkedOrganizationDTO(
                cleanText(
                    txtName
                ),
                cleanText(
                    txtAddress
                ),
                getComboBoxText(
                    cmbCountry
                ),
                getComboBoxText(
                    cmbCity
                ),
                getComboBoxText(
                    cmbState
                ),
                cleanText(
                    txtEmail
                ).toLowerCase(),
                cleanText(
                    txtPhoneNumber
                ),
                cleanText(
                    txtSector
                ),
                parseUserCount(
                    txtIndirectUserCount
                ),
                parseUserCount(
                    txtDirectUserCount
                )
            );

        return organization;
    }

    private String getComboBoxText(
            ComboBox<String> comboBox
    ) {
        String value =
            "";

        if (comboBox.getEditor() != null) {
            value =
                comboBox
                    .getEditor()
                    .getText();
        } else if (comboBox.getSelectionModel().getSelectedItem() != null) {
            value =
                comboBox
                    .getSelectionModel()
                    .getSelectedItem();
        }

        return InputCleaner.sanitizeText(
            value
        );
    }

    private String cleanText(
            TextField textField
    ) {
        String cleanValue =
            InputCleaner.sanitizeText(
                textField.getText()
            );

        return cleanValue;
    }

    private int parseUserCount(
            TextField textField
    ) {
        String value =
            textField
                .getText()
                .trim();

        if (value.isBlank()) {
            value =
                "0";
        }

        return Integer.parseInt(
            value
        );
    }

    @FXML
    private void clearForm() {
        txtName.clear();

        txtEmail.clear();

        txtPhoneNumber.clear();

        txtAddress.clear();

        cmbCountry
            .getSelectionModel()
            .clearSelection();
        cmbCountry
            .getEditor()
            .clear();

        cmbState.getItems().clear();

        cmbState
            .getSelectionModel()
            .clearSelection();
        cmbState
            .getEditor()
            .clear();

        cmbState.setDisable(
            false
        );

        cmbCity.getItems().clear();

        cmbCity
            .getSelectionModel()
            .clearSelection();
        cmbCity
            .getEditor()
            .clear();

        cmbCity.setDisable(
            false
        );

        txtSector.clear();

        txtDirectUserCount.clear();

        txtIndirectUserCount.clear();
    }

    private void limitTextField(
            TextField textField,
            int maxLength
    ) {
        textField.textProperty().addListener(
            new mx.uv.internshipprogramsystem.gui.handlers.TextFieldLengthLimiterListener(textField, maxLength)
        );
    }

    private void limitNumericTextField(
            TextField textField,
            int maxLength
    ) {
        textField.textProperty().addListener(
            new mx.uv.internshipprogramsystem.gui.handlers.NumericTextFieldLengthLimiterListener(textField, maxLength)
        );
    }

    private void validatePermission(
            Permission permission
    ) throws BusinessException, DataAccessException {
        AccessControlManager accessControlManager =
            new AccessControlManager();

        accessControlManager.validatePermission(
            UserSessionManager.getCurrentUser(),
            permission
        );
    }

    private enum LocationField {
        COUNTRY,
        STATE,
        CITY
    }
}


