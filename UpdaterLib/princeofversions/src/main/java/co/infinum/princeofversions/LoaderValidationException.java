package co.infinum.princeofversions;

/**
 * Represents exception thrown if validation error occurred while validating loader.
 */
public class LoaderValidationException extends Exception {

    public LoaderValidationException() {}

    public LoaderValidationException(String message) {
        super(message);
    }

    public LoaderValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoaderValidationException(Throwable cause) {
        super(cause);
    }

}
