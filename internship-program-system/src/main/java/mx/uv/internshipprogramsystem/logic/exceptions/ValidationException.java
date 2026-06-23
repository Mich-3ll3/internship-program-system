package mx.uv.internshipprogramsystem.logic.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends BusinessException {
    private final List<String> errors;
    
    public ValidationException (String message) {
        super(message);
        this.errors = new ArrayList<>();
        this.errors.add(message);
    }

    public ValidationException (List<String> errors) {
        super(errors != null && !errors.isEmpty() ? String.join(", ", errors) : "");
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
}
