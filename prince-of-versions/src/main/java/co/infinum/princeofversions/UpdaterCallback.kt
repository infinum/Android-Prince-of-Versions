package co.infinum.princeofversions

/**
 * Callback for notifying result after version checking computation is done.
 *
 * After the result is computed, it is provided through one of the defined methods.
 */
interface UpdaterCallback {

    /**
     * Method is called when there is a new update available for the current application.
     *
     * @param result The result of the update check. Read `status` to find out if there is an update,
     * and if there is, read `updateVersion` to find out the version of the update.
     */
    fun onSuccess(result: UpdateResult)

    /**
     * Method is called when there was some error while computing the update check.
     *
     * @param error A Throwable that describes the error that occurred.
     */
    fun onError(error: Throwable)
}