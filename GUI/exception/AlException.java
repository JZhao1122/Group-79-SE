package exception;

public class AlException extends Exception {
    public AlException(String message) { super(message); }
    public AlException(String message, Throwable cause) { super(message, cause); }
}
