package gui;

/**
 * Custom exception used in dialog windows for parameter checking.
 */
public class ValidationException extends RuntimeException {

    // the value of exception
    private final String value;

    public ValidationException(String message) {
        this(message, "");
    }

    public ValidationException(String message, String value) {
        super(message);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
