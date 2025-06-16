package aragon.game.input;

public class InputLoadingException extends Exception {
    public InputLoadingException(String message) {
        super(message);
    }

    public InputLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
