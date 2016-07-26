package co.infinum.princeofversions.callbacks;

import co.infinum.princeofversions.common.ErrorCode;

/**
 * Callback for notifying result after version checking computation is done.
 * <p>
 *     After result is computed, it is provided through one of defined methods.
 * </p>
 */
public interface UpdaterCallback {

    /**
     * Method is called when there is new update available for current application.
     * @param version Version string of available update.
     * @param isMandatory Determines if update is mandatory or just optional, true if update is mandatory, false if it is optional.
     */
    void onNewUpdate(String version, boolean isMandatory);

    /**
     * Method is called when update check is finished successfully, but there is no new update available.
     */
    void onNoUpdate();

    /**
     * Method is called when there was some error while computing update check.
     * @param error ErrorCode describing error occurred.
     */
    void onError(@ErrorCode int error);
}
