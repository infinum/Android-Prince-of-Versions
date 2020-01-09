package com.infinum.queenofversions;

import android.app.Activity;
import android.content.IntentSender;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.tasks.OnSuccessListener;

public class AppUpdater implements GoogleAppUpdater {

    private final Activity activity;
    private final AppUpdateManager appUpdateManager;
    private final int requestCode;
    private final UpdaterStateCallback flexibleStateListener;
    private final GoogleInAppUpdateCallback callback;

    private AppUpdateInfo appUpdateInfo;

    AppUpdater(Activity activity, AppUpdateManager appUpdateManager, int requestCode,
        UpdaterStateCallback flexibleStateListener, GoogleInAppUpdateCallback callback) {
        this.activity = activity;
        this.appUpdateManager = appUpdateManager;
        this.requestCode = requestCode;
        this.flexibleStateListener = flexibleStateListener;
        this.callback = callback;
    }

    @Override
    public void initGoogleUpdate(final boolean isMandatory, final String versionCode) {
        appUpdateManager.getAppUpdateInfo()
            .addOnSuccessListener(
                new OnSuccessListener<AppUpdateInfo>() {
                    @Override
                    public void onSuccess(AppUpdateInfo appUpdateInfo) {
                        AppUpdater.this.appUpdateInfo = appUpdateInfo;
                        callback.handleSuccess(appUpdateInfo.updateAvailability(), versionCode, appUpdateInfo.availableVersionCode(),
                            isMandatory);
                    }
                }
            )
            .addOnFailureListener(new GoogleInAppUpdateFailureListener(flexibleStateListener));
    }

    @Override
    public void startUpdate(int updateType) {
        if (updateType == AppUpdateType.IMMEDIATE) {
            startImmediateFlow(appUpdateInfo);
        } else {
            startFlexibleFlow(appUpdateInfo);
        }
        appUpdateManager.registerListener(callback);
    }

    @Override
    public void noUpdate() {
        flexibleStateListener.onNoUpdate();
    }

    @Override
    public void startFlexibleFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,
                activity,
                requestCode
            );
        } catch (IntentSender.SendIntentException e) {
            flexibleStateListener.onFailed(e);
        }
    }

    @Override
    public void startImmediateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,
                activity,
                requestCode
            );
            registerImmediateFlow();
        } catch (IntentSender.SendIntentException e) {
            flexibleStateListener.onFailed(e);
        }
    }

    @Override
    public void registerImmediateFlow() {
        activity.getApplication().registerActivityLifecycleCallbacks(
            new AppCallbacks(this)
        );
    }

    @Override
    public void completeUpdate() {
        appUpdateManager.completeUpdate();
        appUpdateManager.unregisterListener(callback);
    }

    @Override
    public void resumeUpdate(Activity activity) {
        if (this.activity.equals(activity)) {
            appUpdateManager.getAppUpdateInfo()
                .addOnSuccessListener(
                    new OnSuccessListener<AppUpdateInfo>() {
                        @Override
                        public void onSuccess(AppUpdateInfo appUpdateInfo) {
                            AppUpdater.this.appUpdateInfo = appUpdateInfo;
                            callback.handleResumeSuccess(appUpdateInfo.updateAvailability(), appUpdateInfo.installStatus(),
                                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE));
                        }
                    }
                )
                .addOnFailureListener(new GoogleInAppUpdateFailureListener(flexibleStateListener));
        }
    }

    @Override
    public void restartUpdate() {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,
                activity,
                requestCode
            );
        } catch (IntentSender.SendIntentException e) {
            flexibleStateListener.onFailed(e);
        }
    }

    @Override
    public void notifyUser() {
        flexibleStateListener.onDownloaded(callback);
    }

    @Override
    public void cancel() {
        appUpdateManager.unregisterListener(callback);
    }
}
