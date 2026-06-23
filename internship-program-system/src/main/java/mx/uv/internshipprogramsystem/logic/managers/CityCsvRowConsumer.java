package mx.uv.internshipprogramsystem.logic.managers;

import java.util.List;

public class CityCsvRowConsumer implements CsvRowConsumer {
    private final LocationCatalogManager manager;

    public CityCsvRowConsumer(LocationCatalogManager manager) {
        this.manager = manager;
    }

    @Override
    public void accept(List<String> values) {
        manager.addCity(values);
    }
}
