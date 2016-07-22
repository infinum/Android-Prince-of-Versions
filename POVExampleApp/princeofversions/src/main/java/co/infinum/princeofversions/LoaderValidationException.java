package co.infinum.princeofversions;

public class LoaderValidationException extends Exception {

    public LoaderValidationException() {
    }

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
