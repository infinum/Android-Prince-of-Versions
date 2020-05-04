package co.infinum.queenofversions;

/**
 * Called when {@link com.google.android.play.core.install.InstallStateUpdatedListener} produces {@link
 * com.google.android.play.core.install.model.InstallStatus} with value DOWNLOADED.
 */
public interface OnDownloaded {
    /**
     * Called when {@link com.google.android.play.core.install.InstallStateUpdatedListener} produces {@link
     * com.google.android.play.core.install.model.InstallStatus} with value DOWNLOADED.
     * If implementing this method {@code handler.completeUpdate()} has to be called in order to proceed with installation.
     *
     * @param handler handler used to resume with update installation.
     * @param inAppUpdate information about the update read from Google Play
     */
    void onDownloaded(
            QueenOfVersions.UpdateHandler handler,
            QueenOfVersionsInAppUpdateInfo inAppUpdate
    );
}
