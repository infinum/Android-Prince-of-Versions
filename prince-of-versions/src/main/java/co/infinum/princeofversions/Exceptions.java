package co.infinum.princeofversions;

public interface Exceptions {

    class PrinceOfVersionsException extends RuntimeException {

        public PrinceOfVersionsException() {
        }

        public PrinceOfVersionsException(String message) {
            super(message);
        }

        public PrinceOfVersionsException(String message, Throwable cause) {
            super(message, cause);
        }

        public PrinceOfVersionsException(Throwable cause) {
            super(cause);
        }

    }

}
