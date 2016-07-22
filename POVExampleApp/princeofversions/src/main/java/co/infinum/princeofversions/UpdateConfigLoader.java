package co.infinum.princeofversions;

import java.io.IOException;

public interface UpdateConfigLoader {

    String load() throws IOException, InterruptedException;

    void cancel();

    void validate() throws ValidationException;

    class ValidationException extends Exception {
        public ValidationException() {
        }

        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }

        public ValidationException(Throwable cause) {
            super(cause);
        }
    }

}
