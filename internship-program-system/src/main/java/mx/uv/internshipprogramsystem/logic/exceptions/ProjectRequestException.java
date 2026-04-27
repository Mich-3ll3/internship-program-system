package mx.uv.internshipprogramsystem.logic.exceptions;

public class ProjectRequestException extends Exception {
    public ProjectRequestException(String message) {
        super(message);
    }

    public ProjectRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
