package mx.uv.internshipprogramsystem.logic.managers;

import java.util.List;

public class StateCsvRowConsumer implements CsvRowConsumer {
    private final LocationCatalogManager manager;

    public StateCsvRowConsumer(LocationCatalogManager manager) {
        this.manager = manager;
    }

    @Override
    public void accept(List<String> values) {
        manager.addState(values);
    }
}
