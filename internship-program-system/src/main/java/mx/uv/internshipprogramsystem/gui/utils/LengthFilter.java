
package mx.uv.internshipprogramsystem.gui.utils;

import javafx.scene.control.TextFormatter;

public class LengthFilter implements java.util.function.UnaryOperator<TextFormatter.Change> {
    
    private int maxLength;

    public LengthFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        TextFormatter.Change validChange = null;
        String newText = change.getControlNewText();
        
        if (newText.length() <= this.maxLength) {
            validChange = change;
        }
        
        return validChange;
    }
}