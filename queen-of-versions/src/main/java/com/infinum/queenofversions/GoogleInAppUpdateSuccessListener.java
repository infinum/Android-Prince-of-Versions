package com.infinum.queenofversions;

import android.app.Activity;
import android.content.IntentSender;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import java.util.HashMap;

import co.infinum.princeofversions.UpdaterCallback;

public class GoogleInAppUpdateSuccessListener implements OnSuccessListener<AppUpdateInfo> {

    private final int requestCode;
    private final Activity activity;
    private final boolean isMandatory;
    private final AppUpdateManager appUpdateManager;
    private final InstallStateUpdatedListener installStateUpdatedListener;
    private final UpdaterCallback updaterCallback;
    private final UpdaterStateCallback flexiableStateListener;
    private final GoogleInAppUpdateFlexibleHandler handler;
    private final String appVersionCode;

    GoogleInAppUpdateSuccessListener(int requestCode, Activity activity, boolean isMandatory, AppUpdateManager appUpdateManager,
        UpdaterStateCallback flexiableListener, String versionCode, InstallStateUpdatedListener installStateUpdatedListener,
        UpdaterCallback updaterCallback,
        GoogleInAppUpdateFlexibleHandler handler) {
        this.requestCode = requestCode;
        this.activity = activity;
        this.isMandatory = isMandatory;
        this.appUpdateManager = appUpdateManager;
        this.flexiableStateListener = flexiableListener;
        this.installStateUpdatedListener = installStateUpdatedListener;
        this.updaterCallback = updaterCallback;
        this.handler = handler;
        this.appVersionCode = versionCode;
    }

    @Override
    public void onSuccess(AppUpdateInfo appUpdateInfo) {
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && checkVersionCode(appUpdateInfo)) {
            updateAvailable(appUpdateInfo);
        } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE) {
            noUpdate();
        }
    }

    private void updateAvailable(AppUpdateInfo appUpdateInfo) {
        if (isMandatory) {
            startImmediateFlow(appUpdateInfo);
        } else {
            startFlexibleFlow(appUpdateInfo);
        }
        appUpdateManager.registerListener(installStateUpdatedListener);
    }

    private void noUpdate() {
        updaterCallback.onNoUpdate(new HashMap<String, String>());
    }

    private void startFlexibleFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,
                activity,
                requestCode
            );
        } catch (IntentSender.SendIntentException e) {
            updaterCallback.onError(e);
        }
    }

    private void startImmediateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,
                activity,
                requestCode
            );
            registerImmediateResumeFlow();
        } catch (IntentSender.SendIntentException e) {
            updaterCallback.onError(e);
        }
    }

    private void registerImmediateResumeFlow() {
        activity.getApplication().registerActivityLifecycleCallbacks(
            new AppCallbacks(requestCode, activity, appUpdateManager, updaterCallback, flexiableStateListener, handler)
        );
    }

    private boolean checkVersionCode(AppUpdateInfo appUpdateInfo){
        int versionCode;
        if(appVersionCode == null){
            return false;
        }else{
            versionCode = Integer.parseInt(appVersionCode);
        }

        return versionCode == appUpdateInfo.availableVersionCode();
    }
}
