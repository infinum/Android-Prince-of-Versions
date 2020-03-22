package co.infinum.queenofversions;

/**
 * Called in case of any error during update check.
 * In case {@link com.google.android.play.core.install.InstallStateUpdatedListener} produces {@link
 * com.google.android.play.core.install.model.InstallStatus} with value ERROR,
 * error returned in this callback is of type {@link GoogleInAppUpdateException}.
 *
 */
public interface OnError {

    /**
     * Called in case of any error during update check.
     *
     * In case {@link com.google.android.play.core.install.InstallStateUpdatedListener} produces {@link
     * com.google.android.play.core.install.model.InstallStatus} with value ERROR,
     * error returned in this callback is of type {@link GoogleInAppUpdateException}.
     *
     * @param error error occured during update check.
     */
    void onError(Throwable error);
}
