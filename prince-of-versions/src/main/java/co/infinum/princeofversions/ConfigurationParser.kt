package co.infinum.princeofversions

/**
 * This class parses update resource text into [PrinceOfVersionsConfig].
 */
interface ConfigurationParser {
    /**
     * Parses update resource into [PrinceOfVersionsConfig].
     *
     * @param value text representation of update resource.
     * @return Class which holds all relevant data.
     * @throws Throwable if error happens during parsing.
     */
    @Throws(Throwable::class)
    fun parse(value: String): PrinceOfVersionsConfig
}
