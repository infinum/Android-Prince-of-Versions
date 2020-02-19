package co.infinum.princeofversions;

import java.util.Map;

/**
 * Callback for notifying result after version checking computation is done.
 * <p>
 * After result is computed, it is provided through one of defined methods.
 * </p>
 */
public interface UpdaterCallback {

    /**
     * Method is called when there is new update available for current application.
     *
     * @param version     Version code of available update.
     * @param isMandatory Determines if update is mandatory or just optional, true if update is mandatory, false if it is optional.
     * @param metadata    Metadata accompanying the update
     */
    void onNewUpdate(int version, boolean isMandatory, Map<String, String> metadata);

    /**
     * Method is called when update check is finished successfully, but there is no new update available.
     * @param metadata   Metadata accompanying no update message
     */
    void onNoUpdate(Map<String, String> metadata);

    /**
     * Method is called when there was some error while computing update check.
     *
     * @param error Throwable that describes error occurred.
     */
    void onError(Throwable error);
}
