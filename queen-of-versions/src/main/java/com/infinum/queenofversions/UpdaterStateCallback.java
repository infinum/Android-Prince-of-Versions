package com.infinum.queenofversions;

public interface UpdaterStateCallback {

    void onDownloaded(GoogleInAppUpdateFlexibleHandler handler);

    void onNoUpdate();

    void onError(Throwable error);
}
