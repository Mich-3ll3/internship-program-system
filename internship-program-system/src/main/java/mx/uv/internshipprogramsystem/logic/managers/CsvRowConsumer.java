package mx.uv.internshipprogramsystem.logic.managers;

import java.util.List;

public interface CsvRowConsumer {
    void accept(List<String> values);
}
