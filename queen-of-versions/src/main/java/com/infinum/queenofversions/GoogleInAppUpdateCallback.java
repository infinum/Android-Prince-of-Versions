package com.infinum.queenofversions;

import android.app.Activity;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.InstallErrorCode;
import com.google.android.play.core.install.model.InstallStatus;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import co.infinum.princeofversions.UpdaterCallback;

public class GoogleInAppUpdateCallback implements UpdaterCallback, InstallStateUpdatedListener, GoogleInAppUpdateFlexibleHandler {

    private int requestCode;
    private AppUpdateManager appUpdateManager;
    private Activity activity;
    private UpdaterStateCallback flexibleStateListener;

    public GoogleInAppUpdateCallback(int requestCode, Activity activity, UpdaterStateCallback listener) {
        this.requestCode = requestCode;
        this.activity = activity;
        this.appUpdateManager = AppUpdateManagerFactory.create(activity);
        this.flexibleStateListener = listener;
    }

    @Override
    public void onNewUpdate(@NotNull String version, final boolean isMandatory, @NotNull Map<String, String> metadata) {
        String appVersionCode = metadata.get("version-code");
        checkWithGoogleForAnUpdate(isMandatory,appVersionCode);

    }

    @Override
    public void onNoUpdate(@NotNull Map<String, String> metadata) {
        String appVersionCode = metadata.get("version-code");
        checkWithGoogleForAnUpdate(false,appVersionCode);
    }

    @Override
    public void onError(@NotNull Throwable error) {
        flexibleStateListener.onFailed(new GoogleInAppUpdateException(error));
    }

    @Override
    public void onStateUpdate(InstallState installState) {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            flexibleStateListener.onDownloaded(this);
        } else if (installState.installStatus() == InstallStatus.CANCELED) {
            flexibleStateListener.onCanceled();
        } else if (installState.installStatus() == InstallStatus.INSTALLING) {
            flexibleStateListener.onInstalling();
        } else if (installState.installStatus() == InstallStatus.DOWNLOADING) {
            flexibleStateListener.onDownloading();
        } else if (installState.installStatus() == InstallStatus.REQUIRES_UI_INTENT) {
            flexibleStateListener.onRequiresUI();
        } else if (installState.installStatus() == InstallStatus.INSTALLED) {
            flexibleStateListener.onInstalled();
        } else if (installState.installStatus() == InstallStatus.PENDING) {
            flexibleStateListener.onPending();
        } else if (installState.installStatus() == InstallStatus.UNKNOWN) {
            flexibleStateListener.onUnknown();
        } else if (installState.installStatus() == InstallStatus.FAILED) {
            checkErrorStates(installState);
        }
    }

    @Override
    public void completeUpdate() {
        appUpdateManager.completeUpdate();
        appUpdateManager.unregisterListener(this);
    }

    private void checkErrorStates(InstallState installState) {
        if (installState.installErrorCode() == InstallErrorCode.ERROR_API_NOT_AVAILABLE) {
            flexibleStateListener.onFailed(new GoogleInAppUpdateException(GoogleException.API_NOT_AVAILABLE));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_DOWNLOAD_NOT_PRESENT) {
            flexibleStateListener.onFailed(new GoogleInAppUpdateException(GoogleException.DOWNLOAD_NOT_PRESENT));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INSTALL_NOT_ALLOWED) {
            flexibleStateListener.onFailed(new GoogleInAppUpdateException(GoogleException.INSTALL_NOT_ALLOWED));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INSTALL_UNAVAILABLE) {
            flexibleStateListener.onFailed(new GoogleInAppUpdateException(GoogleException.INSTALL_UNAVAILABLE));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INTERNAL_ERROR) {
            flexibleStateListener.onFailed(new GoogleInAppUpdateException(GoogleException.INTERNAL_ERROR));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INVALID_REQUEST) {
            flexibleStateListener.onFailed(new GoogleInAppUpdateException(GoogleException.INVALID_REQUEST));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_UNKNOWN) {
            flexibleStateListener.onFailed(new GoogleInAppUpdateException(GoogleException.ERROR_UNKNOWN));
        }
    }

    private void checkWithGoogleForAnUpdate(boolean isMandatory, String appVersionCode){

        appUpdateManager.getAppUpdateInfo()
            .addOnSuccessListener(
                new GoogleInAppUpdateSuccessListener(
                    requestCode,
                    activity,
                    isMandatory,
                    appUpdateManager,
                    flexibleStateListener,
                    appVersionCode,
                    this,
                    this,
                    this
                )
            )
            .addOnFailureListener(new GoogleInAppUpdateFailureListener(this));
    }
}
