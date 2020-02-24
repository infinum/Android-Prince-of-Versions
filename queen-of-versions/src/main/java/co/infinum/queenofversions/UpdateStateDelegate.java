package co.infinum.queenofversions;

class UpdateStateDelegate implements QueenOfVersions.Callback {

    private boolean isCanceled;
    private QueenOfVersions.Callback listener;

    UpdateStateDelegate(boolean isCanceled, QueenOfVersions.Callback listener) {
        this.isCanceled = isCanceled;
        this.listener = listener;
    }

    @Override
    public void onDownloaded(QueenOfVersionsFlexibleUpdateHandler handler) {
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
    public void onUnknown() {
        if (!isCanceled) {
            listener.onUnknown();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (!isCanceled) {
            listener.onError(throwable);
        }
    }

    @Override
    public void onNoUpdate() {
        if (!isCanceled) {
            listener.onNoUpdate();
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
    public void onRequiresUI() {
        if (!isCanceled) {
            listener.onRequiresUI();
        }
    }

    @Override
    public void onMandatoryUpdateNotAvailable(int mandatoryVersion, int availableVersion) {
        if (!isCanceled) {
            listener.onMandatoryUpdateNotAvailable(mandatoryVersion, availableVersion);
        }
    }

    void cancel() {
        isCanceled = true;
    }
}
