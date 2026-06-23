package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import mx.uv.internshipprogramsystem.gui.controllers.LoginDashboardController;

public class LoginInputValidationListener implements ChangeListener<String> {
    private final LoginDashboardController controller;

    public LoginInputValidationListener(LoginDashboardController controller) {
        this.controller = controller;
    }

    @Override
    public void changed(
            ObservableValue<? extends String> observable,
            String oldValue,
            String newValue
    ) {
        controller.handleValidateInputs();
    }
}
