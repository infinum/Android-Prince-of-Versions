package co.infinum.queenofversions;

public interface OnDownloaded {

    void onDownloaded(
            QueenOfVersions.UpdateHandler handler,
            QueenOfVersionsInAppUpdateInfo inAppUpdate
    );
}
