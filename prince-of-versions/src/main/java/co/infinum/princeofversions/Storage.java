package co.infinum.princeofversions;

import javax.annotation.Nullable;

/**
 * This class handle's persisting of last notified version.
 */
public interface Storage {

    /**
     * Get previously saved last notified version.
     *
     * @param defaultValue Default version to return if there is no version saved.
     * @return Previously saved last notified version if exists or defaultValue if does not exist.
     */
    @Nullable
    Integer lastNotifiedVersion(@Nullable Integer defaultValue);

    /**
     * Save last notified version.
     *
     * @param version Version to be saved.
     */
    void rememberLastNotifiedVersion(@Nullable Integer version);

}
