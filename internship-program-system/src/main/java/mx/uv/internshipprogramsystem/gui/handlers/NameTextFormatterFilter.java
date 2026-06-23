package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;

public class NameTextFormatterFilter
    implements UnaryOperator<TextFormatter.Change> {

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        String text = change.getControlNewText();
        String regex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]*$";
        return (text.matches(regex) && text.length() <= 60) ? change : null;
    }
}
