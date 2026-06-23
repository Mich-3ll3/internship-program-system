package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;

public class StaffNumberTextFormatterFilter
    implements UnaryOperator<TextFormatter.Change> {

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        String text = change.getControlNewText();
        return text.matches("^\\d{0,6}$") ? change : null;
    }
}
