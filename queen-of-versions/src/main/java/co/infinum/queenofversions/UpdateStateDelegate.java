package co.infinum.queenofversions;

class UpdateStateDelegate implements QueenOfVersionsCallback {

    private boolean isCanceled;
    private QueenOfVersionsCallback listener;

    UpdateStateDelegate(boolean isCanceled, QueenOfVersionsCallback listener) {
        this.isCanceled = isCanceled;
        this.listener = listener;
    }

    @Override
    public void onDownloaded(QueenOfVersionFlexibleUpdateHandler handler) {
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
    public void onFailed(Exception exception) {
        if (!isCanceled) {
            listener.onFailed(exception);
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
    public void onMandatoryUpdateNotAvailable() {
        if (!isCanceled) {
            listener.onMandatoryUpdateNotAvailable();
        }
    }

    void cancel() {
        isCanceled = true;
    }
}
