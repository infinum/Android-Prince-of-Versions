package co.infinum.queenofversions;

/**
 * Called when {@link com.google.android.play.core.install.InstallStateUpdatedListener} produces {@link
 * com.google.android.play.core.install.model.InstallStatus} with value DOWNLOADING.
 */
public interface OnDownloading {
    /**
     * Called when {@link com.google.android.play.core.install.InstallStateUpdatedListener} produces {@link
     * com.google.android.play.core.install.model.InstallStatus} with value DOWNLOADING.
     *
     * @param inAppUpdate information about the update read from Google Play
     * @param bytesDownloadedSoFar number of bytes downloaded so far for this update
     * @param totalBytesToDownload total number of bytes that needs to be downloaded for this update
     */
    void onDownloading(
            QueenOfVersionsInAppUpdateInfo inAppUpdate,
            long bytesDownloadedSoFar,
            long totalBytesToDownload
    );
}
