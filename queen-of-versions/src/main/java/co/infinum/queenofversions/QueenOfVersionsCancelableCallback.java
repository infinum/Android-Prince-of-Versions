package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateInfo;
import java.util.Map;
import javax.annotation.Nullable;

class QueenOfVersionsCancelableCallback implements QueenOfVersions.Callback {

    private boolean isCanceled;

    private QueenOfVersions.Callback listener;

    QueenOfVersionsCancelableCallback(boolean isCanceled, QueenOfVersions.Callback listener) {
        this.isCanceled = isCanceled;
        this.listener = listener;
    }

    @Override
    public void onDownloaded(QueenOfVersions.UpdateHandler handler) {
        if (!isCanceled) {
            listener.onDownloaded(handler);
        }
    }

    @Override
    public void onCanceled() {
        if (!isCanceled) {
            listener.onCanceled();
        }
    }

    @Override
    public void onInstalled() {
        if (!isCanceled) {
            listener.onInstalled();
        }
    }

    @Override
    public void onPending() {
        if (!isCanceled) {
            listener.onPending();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (!isCanceled) {
            listener.onError(throwable);
        }
    }

    @Override
    public void onNoUpdate(@Nullable Map<String, String> metadata, @Nullable UpdateInfo updateInfo) {
        if (!isCanceled) {
            listener.onNoUpdate(metadata, updateInfo);
        }
    }

    @Override
    public void onDownloading() {
        if (!isCanceled) {
            listener.onDownloading();
        }
    }

    @Override
    public void onInstalling() {
        if (!isCanceled) {
            listener.onInstalling();
        }
    }

    @Override
    public void onMandatoryUpdateNotAvailable(
            int mandatoryVersion,
            int availableVersion,
            Map<String, String> metadata,
            UpdateInfo updateInfo
    ) {
        if (!isCanceled) {
            listener.onMandatoryUpdateNotAvailable(mandatoryVersion, availableVersion, metadata, updateInfo);
        }
    }

    void cancel() {
        isCanceled = true;
    }

    boolean isCanceled() {
        return isCanceled;
    }
}
