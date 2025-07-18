package co.infinum.princeofversions

/**
 * This class handle's persisting of last notified version.
 */
interface Storage {
    /**
     * Get previously saved last notified version.
     *
     * @param defaultValue Default version to return if there is no version saved.
     * @return Previously saved last notified version if exists or defaultValue if does not exist.
     */
    fun lastNotifiedVersion(defaultValue: Int?): Int?

    /**
     * Save last notified version.
     *
     * @param version Version to be saved.
     */
    fun rememberLastNotifiedVersion(version: Int?)
}
