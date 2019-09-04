package com.infinum.queenofversions;

import com.google.android.play.core.tasks.OnFailureListener;

import co.infinum.princeofversions.UpdaterCallback;

public class GoogleInAppUpdateFailureListener implements OnFailureListener {

    private final UpdaterCallback updaterCallback;

    GoogleInAppUpdateFailureListener(UpdaterCallback updaterCallback) {
        this.updaterCallback = updaterCallback;
    }

    @Override
    public void onFailure(Exception e) {
        updaterCallback.onError(e);
    }
}
