package co.infinum.princeofversions.interfaces;

import co.infinum.princeofversions.common.VersionContext;

/**
 * Represents callback for result provided after verifying for updates.
 */
public interface VersionVerifierListener {

    /**
     * Method is called when versions are loaded and computed.
     *
     * @param version Computed set of versions. Current, minimum and optionally optional update.
     */
    void versionAvailable(VersionContext version);

    /**
     * Method is called when error occurred on loading or computing versions.
     *
     * @param throwable describing error occurred.
     */
    void versionUnavailable(Throwable throwable);

}
