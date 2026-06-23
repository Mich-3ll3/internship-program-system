package mx.uv.internshipprogramsystem.logic.managers;

import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public class LocationCatalogManager {

    private static final String COUNTRIES_CATALOG_PATH =
        "/catalogs/countries.csv";
    private static final String STATES_CATALOG_PATH =
        "/catalogs/states.csv";
    private static final String CITIES_CATALOG_PATH =
        "/catalogs/cities.csv";

    private final Map<String, String> countryIdByName =
        new LinkedHashMap<>();
    private final Map<String, String> countryNameById =
        new LinkedHashMap<>();
    private final Map<String, String> stateIdByKey =
        new LinkedHashMap<>();
    private final Map<String, String> stateNameById =
        new LinkedHashMap<>();
    private final Map<String, List<String>> statesByCountryId =
        new LinkedHashMap<>();
    private final Map<String, List<String>> citiesByStateId =
        new LinkedHashMap<>();

    public static LocationCatalogManager loadDefault()
            throws BusinessException, DataAccessException {
        LocationCatalogManager manager =
            new LocationCatalogManager();
        manager.loadCatalogs();
        return manager;
    }

    public List<String> getCountries() {
        List<String> countries =
            new ArrayList<>(
                countryIdByName.keySet()
            );
        Collections.sort(countries);
        return countries;
    }

    public List<String> getStatesByCountry(String country) {
        List<String> states = List.of();
        String countryId = countryIdByName.get(country);

        if (countryId != null) {
            states =
                sortedCopy(
                    statesByCountryId.getOrDefault(
                        countryId,
                        List.of()
                    )
                );
        }
        return states;
    }

    public List<String> getCitiesByCountryAndState(
            String country,
            String state
    ) {
        List<String> cities = List.of();
        String countryId = countryIdByName.get(country);

        if (countryId != null && state != null) {
            String stateId =
                stateIdByKey.get(
                    buildStateKey(
                        countryId,
                        state
                    )
                );

            if (stateId != null) {
                cities =
                    sortedCopy(
                        citiesByStateId.getOrDefault(
                            stateId,
                            List.of()
                        )
                    );
            }
        }
        return cities;
    }

    private void loadCatalogs()
            throws BusinessException, DataAccessException {
        loadCountries();
        loadStates();
        loadCities();
    }

    private void loadCountries()
            throws BusinessException, DataAccessException {
        readCsv(
            COUNTRIES_CATALOG_PATH,
            new CountryCsvRowConsumer(this)
        );
    }

    private void loadStates()
            throws BusinessException, DataAccessException {
        readCsv(
            STATES_CATALOG_PATH,
            new StateCsvRowConsumer(this)
        );
    }

    private void loadCities()
            throws BusinessException, DataAccessException {
        readCsv(
            CITIES_CATALOG_PATH,
            new CityCsvRowConsumer(this)
        );
    }

    public void addCountry(List<String> values) {
        String id = getValue(values, 0);
        String name = getValue(values, 1);

        if (!id.isBlank() && !name.isBlank()) {
            countryIdByName.put(name, id);
            countryNameById.put(id, name);
        }
    }

    public void addState(List<String> values) {
        String id = getValue(values, 0);
        String name = getValue(values, 1);
        String countryId = getValue(values, 2);

        if (!id.isBlank()
                && !name.isBlank()
                && countryNameById.containsKey(countryId)) {
            stateNameById.put(id, name);
            stateIdByKey.put(buildStateKey(countryId, name), id);

            List<String> states = statesByCountryId.get(countryId);
            if (states == null) {
                states = new ArrayList<>();
                statesByCountryId.put(countryId, states);
            }
            states.add(name);
        }
    }

    public void addCity(List<String> values) {
        String name = getValue(values, 1);
        String stateId = getValue(values, 2);

        if (!name.isBlank() && stateNameById.containsKey(stateId)) {
            List<String> cities = citiesByStateId.get(stateId);
            if (cities == null) {
                cities = new ArrayList<>();
                citiesByStateId.put(stateId, cities);
            }
            cities.add(name);
        }
    }

    private void readCsv(
            String catalogPath,
            CsvRowConsumer rowConsumer
    ) throws BusinessException, DataAccessException {
        try (InputStream inputStream =
                getClass().getResourceAsStream(catalogPath)) {
            if (inputStream == null) {
                throw new BusinessException(
                    "No se encontro el catalogo: "
                    + catalogPath
                );
            }

            try (BufferedReader reader =
                    new BufferedReader(
                        new InputStreamReader(
                            inputStream,
                            StandardCharsets.UTF_8
                        )
                    )) {
                readRows(reader, rowConsumer);
            }
        } catch (BusinessException businessException) {
            throw businessException;
        } catch (IOException ioException) {
            throw new BusinessException(
                "No se pudo leer el catalogo: "
                + catalogPath,
                ioException
            );
        }
    }

    private void readRows(
            BufferedReader reader,
            CsvRowConsumer rowConsumer
    ) throws IOException {
        String line = reader.readLine();

        while ((line = reader.readLine()) != null) {
            List<String> values = parseCsvLine(line);
            rowConsumer.accept(values);
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean insideQuotes = false;

        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);

            if (character == '"') {
                if (insideQuotes
                        && index + 1 < line.length()
                        && line.charAt(index + 1) == '"') {
                    value.append(character);
                    index++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (character == ',' && !insideQuotes) {
                values.add(value.toString());
                value.setLength(0);
            } else {
                value.append(character);
            }
        }
        values.add(value.toString());
        return values;
    }

    private String getValue(List<String> values, int index) {
        String value = "";
        if (index < values.size() && values.get(index) != null) {
            value = values.get(index).trim();
        }
        return value;
    }

    private String buildStateKey(String countryId, String state) {
        return countryId + "|" + state;
    }

    private List<String> sortedCopy(List<String> values) {
        Set<String> uniqueValues = new LinkedHashSet<>(values);
        List<String> sortedValues = new ArrayList<>(uniqueValues);
        Collections.sort(sortedValues);
        return sortedValues;
    }
}