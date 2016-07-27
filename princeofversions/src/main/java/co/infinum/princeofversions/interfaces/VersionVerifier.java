package co.infinum.princeofversions.interfaces;

import co.infinum.princeofversions.UpdateConfigLoader;

/**
 * Interface implements verifying for updates and cancellation of verifying.
 */
public interface VersionVerifier {

    /**
     * Method verify for updates from resource provided by given loader. After verification is computed result is notified through
     * VersionVerifierListener.
     *
     * @param loader   Loading update configuration resource.
     * @param listener For notifying result.
     */
    void verify(UpdateConfigLoader loader, VersionVerifierListener listener);

    /**
     * Method cancel verification and loading from loader.
     */
    void cancel();

}
