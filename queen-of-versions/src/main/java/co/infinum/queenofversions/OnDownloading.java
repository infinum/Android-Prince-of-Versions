package co.infinum.queenofversions;

public interface OnDownloading {

    void onDownloading(
            QueenOfVersionsInAppUpdateInfo inAppUpdate,
            long bytesDownloadedSoFar,
            long totalBytesToDownload
    );
}
