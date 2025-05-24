package exception;

/**
 * Authentication exception, thrown when authentication or authorization errors occur
 */
public class AuthException extends RuntimeException {
    
    /**
     * Create an authentication exception with the specified message
     * @param message Error message
     */
    public AuthException(String message) {
        super(message);
    }
    
    /**
     * Create an authentication exception with the specified message and cause
     * @param message Error message
     * @param cause Exception cause
     */
    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
} 