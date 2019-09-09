package com.infinum.queenofversions;

import com.google.android.play.core.tasks.OnFailureListener;

import co.infinum.princeofversions.UpdaterCallback;

public class GoogleInAppUpdateFailureListener implements OnFailureListener {

    private final UpdaterStateCallback flexibleListener;

    GoogleInAppUpdateFailureListener(UpdaterStateCallback flexibleListener) {
        this.flexibleListener = flexibleListener;
    }

    @Override
    public void onFailure(Exception e) {
        flexibleListener.onFailed(e);
    }
}
