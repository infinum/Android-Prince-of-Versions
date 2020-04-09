package co.infinum.princeofversions;

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
     * @param result result of the update check. Read <code>status</code> to find out if there is an update,
     *              and if there is read <code>updateVersion</code> to find out what is the version of the update
     */
    void onSuccess(UpdateResult result);

    /**
     * Method is called when there was some error while computing update check.
     *
     * @param error Throwable that describes error occurred.
     */
    void onError(Throwable error);
}
