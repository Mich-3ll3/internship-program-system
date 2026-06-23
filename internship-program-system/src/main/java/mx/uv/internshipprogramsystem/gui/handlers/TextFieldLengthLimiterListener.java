package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class TextFieldLengthLimiterListener implements ChangeListener<String> {
    private final TextField textField;
    private final int maxLength;

    public TextFieldLengthLimiterListener(TextField textField, int maxLength) {
        this.textField = textField;
        this.maxLength = maxLength;
    }

    @Override
    public void changed(
            ObservableValue<? extends String> observable,
            String oldValue,
            String newValue
    ) {
        if (newValue != null && newValue.length() > maxLength) {
            textField.setText(oldValue);
        }
    }
}
