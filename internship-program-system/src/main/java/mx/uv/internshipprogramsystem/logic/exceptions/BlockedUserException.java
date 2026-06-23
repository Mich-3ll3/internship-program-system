package mx.uv.internshipprogramsystem.logic.exceptions;

public class BlockedUserException extends BusinessException {
    public BlockedUserException(String message) {
        super(message);
    }

    public BlockedUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
