package co.infinum.princeofversions;

/**
 * Represents parser for used for parsing textual representation of version into {@link Version} object.
 */
public interface VersionParser {

    /**
     * Parses text representation of version into {@link Version} object.
     *
     * @param value text which represents version
     * @return parsed version as {@link Version} object
     */
    Version parse(String value);

}
