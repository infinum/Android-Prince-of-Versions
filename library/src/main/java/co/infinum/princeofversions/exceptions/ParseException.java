package co.infinum.princeofversions.exceptions;

/**
 * Represents exception thrown while parsing versions.
 * Parse error while be thrown if version does not follow <a href="http://semver.org/">semver</a> specification.
 *
 * @see <a href="http://semver.org/">http://semver.org</a>
 */
public class ParseException extends Exception {

    public ParseException() {
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }
}
