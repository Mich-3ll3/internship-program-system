package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;

public class LengthFilterTextFormatter
        implements UnaryOperator<TextFormatter.Change> {

    private final int maxLength;

    public LengthFilterTextFormatter(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        if (change.getControlNewText().length() <= maxLength) {
            return change;
        }
        return null;
    }
}
