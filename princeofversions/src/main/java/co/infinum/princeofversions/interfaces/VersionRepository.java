package co.infinum.princeofversions.interfaces;

/**
 * Represents repository for persisting library data.
 */
public interface VersionRepository {

    /**
     * Method returns last notified optional update version.
     *
     * @return Last notified optional update version string.
     */
    String getLastVersionName();

    /**
     * Method sets last notified optional update version string.
     *
     * @param version Last notified optional update version string.
     */
    void setLastVersionName(String version);

    /**
     * Method returns last notified optional update version, if not exists returns default value.
     *
     * @param defaultValue Default value used if real value not exists.
     * @return Last notified optional update version if exists, default otherwise.
     */
    String getLastVersionName(String defaultValue);

}
