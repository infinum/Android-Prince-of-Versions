package co.infinum.princeofversions;

/**
 * Represents a status of an update check
 */
public enum UpdateStatus {
    /**
     * Update is available and type of update is mandatory
     */
    REQUIRED_UPDATE_NEEDED,

    /**
     * Update is available and type of update is optional
     */
    NEW_UPDATE_AVAILABLE,

    /**
     * Update is not available
     */
    NO_UPDATE_AVAILABLE
}
