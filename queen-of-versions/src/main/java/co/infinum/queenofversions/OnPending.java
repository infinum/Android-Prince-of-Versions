package co.infinum.queenofversions;

/**
 * Called when {@link com.google.android.play.core.install.InstallStateUpdatedListener} produces {@link
 * com.google.android.play.core.install.model.InstallStatus} with value PENDING.
 */
public interface OnPending {

    /**
     * Called when {@link com.google.android.play.core.install.InstallStateUpdatedListener} produces {@link
     * com.google.android.play.core.install.model.InstallStatus} with value PENDING.
     *
     * @param inAppUpdateInfo information about the update read from Google Play
     */
    void onPending(QueenOfVersionsInAppUpdateInfo inAppUpdateInfo);
}
