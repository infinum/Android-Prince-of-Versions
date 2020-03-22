package co.infinum.queenofversions;

/**
 * Called when {@link com.google.android.play.core.install.InstallStateUpdatedListener} produces {@link
 * com.google.android.play.core.install.model.InstallStatus} with value DOWNLOADED.
 */
public interface OnDownloaded {

    void onDownloaded(
            QueenOfVersions.UpdateHandler handler,
            QueenOfVersionsInAppUpdateInfo inAppUpdate
    );
}
