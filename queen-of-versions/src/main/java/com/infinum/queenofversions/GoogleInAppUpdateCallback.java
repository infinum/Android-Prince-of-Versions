package com.infinum.queenofversions;

import android.app.Activity;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.InstallStatus;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import co.infinum.princeofversions.UpdaterCallback;

public class GoogleInAppUpdateCallback implements UpdaterCallback, InstallStateUpdatedListener, GoogleInAppUpdateFlexibleHandler {

    private final int requestCode;
    private AppUpdateManager appUpdateManager;
    private Activity activity;
    private UpdaterStateCallback listener;

    public GoogleInAppUpdateCallback(int requestCode, Activity activity, UpdaterStateCallback listener) {
        this.requestCode = requestCode;
        this.activity = activity;
        this.appUpdateManager = AppUpdateManagerFactory.create(activity);
        this.listener = listener;
    }

    @Override
    public void onNewUpdate(@NotNull String version, final boolean isMandatory, @NotNull Map<String, String> metadata) {
        appUpdateManager.getAppUpdateInfo()
            .addOnSuccessListener(
                new GoogleInAppUpdateSuccessListener(
                    requestCode,
                    activity,
                    isMandatory,
                    appUpdateManager,
                    this,
                    this
                )
            )
            .addOnFailureListener(new GoogleInAppUpdateFailureListener(this));
    }

    @Override
    public void onNoUpdate(@NotNull Map<String, String> metadata) {
        listener.onNoUpdate();
    }

    @Override
    public void onError(@NotNull Throwable error) {
        listener.onError(error);
    }

    @Override
    public void onStateUpdate(InstallState installState) {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            listener.onDownloaded(this);
        }
    }

    @Override
    public void completeUpdate() {
        appUpdateManager.completeUpdate();
        appUpdateManager.unregisterListener(this);
    }
}
