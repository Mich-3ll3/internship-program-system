package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class NumericTextFieldLengthLimiterListener implements ChangeListener<String> {
    private final TextField textField;
    private final int maxLength;

    public NumericTextFieldLengthLimiterListener(TextField textField, int maxLength) {
        this.textField = textField;
        this.maxLength = maxLength;
    }

    @Override
    public void changed(
            ObservableValue<? extends String> observable,
            String oldValue,
            String newValue
    ) {
        if (newValue == null) {
            return;
        }
        String filteredValue = newValue.replaceAll("[^0-9]", "");
        if (filteredValue.length() > maxLength) {
            filteredValue = filteredValue.substring(0, maxLength);
        }
        if (!newValue.equals(filteredValue)) {
            textField.setText(filteredValue);
            textField.positionCaret(filteredValue.length());
        }
    }
}
