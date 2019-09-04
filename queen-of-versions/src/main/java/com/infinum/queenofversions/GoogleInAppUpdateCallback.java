package com.infinum.queenofversions;

import android.app.Activity;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.InstallErrorCode;
import com.google.android.play.core.install.model.InstallStatus;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.IllegalCharsetNameException;
import java.util.Map;

import co.infinum.princeofversions.UpdaterCallback;

public class GoogleInAppUpdateCallback implements UpdaterCallback, InstallStateUpdatedListener, GoogleInAppUpdateFlexibleHandler {

    private int requestCode;
    private AppUpdateManager appUpdateManager;
    private Activity activity;
    private UpdaterFlexibleStateCallback flexibleStateListener;
    private PrinceOfVersionsStateCallback princeOfVersionsStateListener;

    public GoogleInAppUpdateCallback(int requestCode, Activity activity,
        PrinceOfVersionsStateCallback stateCallback, UpdaterFlexibleStateCallback listener) {
        this.requestCode = requestCode;
        this.activity = activity;
        this.appUpdateManager = AppUpdateManagerFactory.create(activity);
        this.princeOfVersionsStateListener = stateCallback;
        this.flexibleStateListener = listener;
    }

    public GoogleInAppUpdateCallback(int requestCode, Activity activity, PrinceOfVersionsStateCallback stateCallback) {
        this.requestCode = requestCode;
        this.activity = activity;
        this.appUpdateManager = AppUpdateManagerFactory.create(activity);
        this.princeOfVersionsStateListener = stateCallback;
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
        princeOfVersionsStateListener.onNoUpdate();
    }

    @Override
    public void onError(@NotNull Throwable error) {
        princeOfVersionsStateListener.onError(error);
    }

    @Override
    public void onStateUpdate(InstallState installState) {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            flexibleStateListener.onDownloaded(this);
        } else if (installState.installStatus() == InstallStatus.CANCELED) {
            flexibleStateListener.onCanceled();
        } else if (installState.installStatus() == InstallStatus.INSTALLED) {
            flexibleStateListener.onInstalled();
        } else if (installState.installStatus() == InstallStatus.PENDING) {
            flexibleStateListener.onPending();
        } else if (installState.installStatus() == InstallStatus.UNKNOWN) {
            flexibleStateListener.onUnknown();
        } else if (installState.installStatus() == InstallStatus.FAILED) {

            if (installState.installErrorCode() == InstallErrorCode.ERROR_API_NOT_AVAILABLE) {
                flexibleStateListener.onFailed(new GoogleInAppUpdateException("API NOT AVAILABLE"));
            } else if (installState.installErrorCode() == InstallErrorCode.ERROR_DOWNLOAD_NOT_PRESENT) {
                flexibleStateListener.onFailed(new GoogleInAppUpdateException("DOWNLOAD NOT PRESENT"));
            } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INSTALL_NOT_ALLOWED) {
                flexibleStateListener.onFailed(new GoogleInAppUpdateException("INSTALL NOT ALLOWED"));
            } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INSTALL_UNAVAILABLE) {
                flexibleStateListener.onFailed(new GoogleInAppUpdateException("INSTALL UNAVAILABLE"));
            } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INTERNAL_ERROR) {
                flexibleStateListener.onFailed(new GoogleInAppUpdateException("INTERNAL ERROR"));
            } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INVALID_REQUEST) {
                flexibleStateListener.onFailed(new GoogleInAppUpdateException("INVALID REQUEST"));
            } else if (installState.installErrorCode() == InstallErrorCode.ERROR_UNKNOWN) {
                flexibleStateListener.onFailed(new GoogleInAppUpdateException("UNKNOWN ERROR"));
            }
        }
    }

    @Override
    public void completeUpdate() {
        appUpdateManager.completeUpdate();
        appUpdateManager.unregisterListener(this);
    }
}
