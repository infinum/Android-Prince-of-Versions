package com.infinum.queenofversions;

import android.app.Activity;
import android.content.IntentSender;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import co.infinum.princeofversions.UpdaterCallback;

public class GoogleInAppUpdateResumeSuccessListener implements OnSuccessListener<AppUpdateInfo> {

    private final int requestCode;
    private final Activity activity;
    private final AppUpdateManager appUpdateManager;
    private final UpdaterCallback updaterCallback;

    GoogleInAppUpdateResumeSuccessListener(int requestCode, Activity activity, AppUpdateManager appUpdateManager,
        UpdaterCallback updaterCallback) {
        this.requestCode = requestCode;
        this.activity = activity;
        this.appUpdateManager = appUpdateManager;
        this.updaterCallback = updaterCallback;
    }

    @Override
    public void onSuccess(AppUpdateInfo appUpdateInfo) {
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
            updateInProgress(appUpdateInfo);
        }
    }

    private void updateInProgress(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,
                activity,
                requestCode
            );
        } catch (IntentSender.SendIntentException e) {
            updaterCallback.onError(e);
        }
    }
}
