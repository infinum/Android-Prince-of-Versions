package co.infinum.queenofversions;

/**
 * Called when {@link com.google.android.play.core.install.InstallStateUpdatedListener} produces {@link
 * com.google.android.play.core.install.model.InstallStatus} with value INSTALLING.
 */
public interface OnInstalling {

    void onInstalling(QueenOfVersionsInAppUpdateInfo inAppUpdateInfo);
}
