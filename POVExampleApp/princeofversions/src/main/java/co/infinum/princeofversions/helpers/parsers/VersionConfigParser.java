package co.infinum.princeofversions.helpers.parsers;

import co.infinum.princeofversions.common.VersionContext;

public interface VersionConfigParser {

    public VersionContext parse(String content) throws ParseException;

    public static class ParseException extends Throwable {
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

}
