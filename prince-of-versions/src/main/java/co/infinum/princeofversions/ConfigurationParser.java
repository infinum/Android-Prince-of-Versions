package co.infinum.princeofversions;

/**
 * This class parses update resource text into {@link PrinceOfVersionsConfig}.
 */
public interface ConfigurationParser {

    /**
     * Parses update resource into {@link PrinceOfVersionsConfig}.
     *
     * @param value text representation of update resource.
     * @return Class which holds all relevant data.
     * @throws Throwable if error happens during parsing.
     */
    PrinceOfVersionsConfig parse(String value) throws Throwable;

}
