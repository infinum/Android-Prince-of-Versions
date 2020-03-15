package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateInfo;
import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;
import java.util.Map;
import javax.annotation.Nullable;

class QueenOfVersionsCancelableCallback implements QueenOfVersions.Callback {

    private boolean isCanceled;

    private QueenOfVersions.Callback delegate;

    QueenOfVersionsCancelableCallback(boolean isCanceled, QueenOfVersions.Callback delegate) {
        this.isCanceled = isCanceled;
        this.delegate = delegate;
    }

    @Override
    public void onCanceled() {
        if (!isCanceled) {
            delegate.onCanceled();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (!isCanceled) {
            delegate.onError(throwable);
        }
    }

    @Override
    public void onNoUpdate(@Nullable Map<String, String> metadata, @Nullable UpdateInfo updateInfo) {
        if (!isCanceled) {
            delegate.onNoUpdate(metadata, updateInfo);
        }
    }

    @Override
    public void onDownloaded(QueenOfVersions.UpdateHandler handler, QueenOfVersionsInAppUpdateInfo inAppUpdate) {
        if (!isCanceled) {
            delegate.onDownloaded(handler, inAppUpdate);
        }
    }

    @Override
    public void onDownloading(QueenOfVersionsInAppUpdateInfo inAppUpdate, long bytesDownloadedSoFar, long totalBytesToDownload) {
        if (!isCanceled) {
            delegate.onDownloading(inAppUpdate, bytesDownloadedSoFar, totalBytesToDownload);
        }
    }

    @Override
    public void onInstalled(QueenOfVersionsInAppUpdateInfo appUpdateInfo) {
        if (!isCanceled) {
            delegate.onInstalled(appUpdateInfo);
        }
    }

    @Override
    public void onInstalling(QueenOfVersionsInAppUpdateInfo inAppUpdateInfo) {
        if (!isCanceled) {
            delegate.onInstalling(inAppUpdateInfo);
        }
    }

    @Override
    public void onMandatoryUpdateNotAvailable(
            int mandatoryVersion,
            QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
            Map<String, String> metadata,
            UpdateInfo updateInfo
    ) {
        if (!isCanceled) {
            delegate.onMandatoryUpdateNotAvailable(mandatoryVersion, inAppUpdateInfo, metadata, updateInfo);
        }
    }

    @Override
    public void onPending(QueenOfVersionsInAppUpdateInfo inAppUpdateInfo) {
        if (!isCanceled) {
            delegate.onPending(inAppUpdateInfo);
        }
    }

    @Override
    public void onUpdateAccepted(
            QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
            UpdateStatus updateStatus,
            @Nullable UpdateResult updateResult
    ) {
        if (!isCanceled) {
            delegate.onUpdateAccepted(inAppUpdateInfo, updateStatus, updateResult);
        }
    }

    @Override
    public void onUpdateDeclined(
            QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
            UpdateStatus updateStatus,
            @Nullable UpdateResult updateResult
    ) {
        if (!isCanceled) {
            delegate.onUpdateDeclined(inAppUpdateInfo, updateStatus, updateResult);
        }
    }

    void cancel() {
        isCanceled = true;
    }

    boolean isCanceled() {
        return isCanceled;
    }
}
