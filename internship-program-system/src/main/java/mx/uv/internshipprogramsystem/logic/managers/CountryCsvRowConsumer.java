package mx.uv.internshipprogramsystem.logic.managers;

import java.util.List;

public class CountryCsvRowConsumer implements CsvRowConsumer {
    private final LocationCatalogManager manager;

    public CountryCsvRowConsumer(LocationCatalogManager manager) {
        this.manager = manager;
    }

    @Override
    public void accept(List<String> values) {
        manager.addCountry(values);
    }
}
