package co.infinum.queenofversions;

public interface QueenOfVersionsCallback {

    void onDownloaded(QueenOfVersionFlexibleUpdateHandler handler);

    void onCanceled();

    void onInstalled();

    void onPending();

    void onUnknown();

    void onFailed(Exception exception);

    void onNoUpdate();

    void onDownloading();

    void onInstalling();

    void onRequiresUI();

    void onMandatoryUpdateNotAvailable();
}
