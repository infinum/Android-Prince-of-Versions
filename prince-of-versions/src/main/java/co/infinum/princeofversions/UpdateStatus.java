package co.infinum.princeofversions;

/**
 * Represents a status of an update check
 */
public enum UpdateStatus {

    /**
     * Update is available and type of update is mandatory
     */
    MANDATORY,

    /**
     * Update is available and type of update is optional
     */
    OPTIONAL,

    /**
     * Update is not available
     */
    NO_UPDATE

}
