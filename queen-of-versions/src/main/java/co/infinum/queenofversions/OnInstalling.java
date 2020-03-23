package co.infinum.queenofversions;

/**
 * Called when {@link com.google.android.play.core.install.InstallStateUpdatedListener} produces {@link
 * com.google.android.play.core.install.model.InstallStatus} with value INSTALLING.
 */
public interface OnInstalling {

    /**
     * Called when {@link com.google.android.play.core.install.InstallStateUpdatedListener} produces {@link
     * com.google.android.play.core.install.model.InstallStatus} with value INSTALLING.
     *
     * @param inAppUpdateInfo information about the update read from Google Play
     */
    void onInstalling(QueenOfVersionsInAppUpdateInfo inAppUpdateInfo);
}
