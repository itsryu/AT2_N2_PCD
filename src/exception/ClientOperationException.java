package exception;

public class ClientOperationException extends Exception {
    public ClientOperationException(String message) {
        super(message);
    }

    public ClientOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
