package com.infinum.queenofversions;

public interface UpdaterFlexibleStateCallback {

    void onDownloaded(GoogleInAppUpdateFlexibleHandler handler);

    void onCanceled();

    void onInstalled();

    void onPending();

    void onUnknown();

    void onFailed(Exception exception);
}
