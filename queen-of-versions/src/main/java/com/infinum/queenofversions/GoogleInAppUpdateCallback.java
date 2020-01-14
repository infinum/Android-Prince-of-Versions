package com.infinum.queenofversions;

import android.app.Activity;
import android.support.annotation.VisibleForTesting;

import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallErrorCode;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import co.infinum.princeofversions.UpdaterCallback;

public class GoogleInAppUpdateCallback implements UpdaterCallback, InstallStateUpdatedListener, GoogleInAppUpdateFlexibleHandler {

    public static final int UNSUPPORTED_VERSION = 3;

    private UpdateStateDelegate flexibleStateListener;
    private GoogleAppUpdater googleAppUpdater;
    private final int appVersionCode;

    public GoogleInAppUpdateCallback(int requestCode, Activity activity, UpdaterStateCallback listener, int appVersionCode) {
        this.flexibleStateListener = new UpdateStateDelegate(false, listener);
        this.appVersionCode = appVersionCode;
        this.googleAppUpdater = new AppUpdater(activity, AppUpdateManagerFactory.create(activity), requestCode,
            flexibleStateListener,
            this);
    }

    @VisibleForTesting
    GoogleInAppUpdateCallback(int requestCode, GoogleAppUpdater appUpdater, UpdaterStateCallback flexibleStateListener,
        int appVersionCode) {
        this.flexibleStateListener = new UpdateStateDelegate(false, flexibleStateListener);
        this.appVersionCode = appVersionCode;
        this.googleAppUpdater = appUpdater;
    }

    @Override
    public void onNewUpdate(@NotNull String version, final boolean isMandatory, @NotNull Map<String, String> metadata) {
        String princeVersionCode = metadata.get("version-code");
        checkWithGoogleForAnUpdate(isMandatory, princeVersionCode);
    }

    @Override
    public void onNoUpdate(@NotNull Map<String, String> metadata) {
        String princeVersionCode = metadata.get("version-code");
        checkWithGoogleForAnUpdate(false, princeVersionCode);
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
        googleAppUpdater.completeUpdate();
    }

    void handleSuccess(@UpdateAvailability int updateAvailability, String princeVersionCode, int googleUpdateVersionCode,
        boolean isMandatory) {
        int updateType = checkVersionCode(princeVersionCode, googleUpdateVersionCode, isMandatory);
        if (updateType != UNSUPPORTED_VERSION) {
            if (updateAvailability == UpdateAvailability.UPDATE_AVAILABLE && (updateType == AppUpdateType.FLEXIBLE
                || updateType == AppUpdateType.IMMEDIATE)) {
                googleAppUpdater.startUpdate(updateType);
            } else {
                googleAppUpdater.noUpdate();
            }
        } else {
            googleAppUpdater.wrongVersion();
        }
    }

    //This method is called when you leave app during an immediate update, but also it checks if user has left app during flexible update
    //In case of flexible update we notify user about downloaded update so he can do install it or whatever

    void handleResumeSuccess(@UpdateAvailability int updateAvailability, @InstallStatus int installStatus, boolean isFlexible) {
        if (updateAvailability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
            googleAppUpdater.restartUpdate();
        } else if (installStatus == InstallStatus.DOWNLOADED && isFlexible) {
            googleAppUpdater.notifyUser();
        }
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

    private void checkWithGoogleForAnUpdate(boolean isMandatory, String princeVersionCode) {
        googleAppUpdater.initGoogleUpdate(isMandatory, princeVersionCode);
    }

    private int checkVersionCode(String princeVersionCode, int googleVersionCode, boolean isMandatory) {
        int princeOfVersionsCode;
        if (princeVersionCode == null) {
            return AppUpdateType.FLEXIBLE;
        } else {
            princeOfVersionsCode = Integer.parseInt(princeVersionCode);
        }

        if (princeOfVersionsCode <= googleVersionCode && isMandatory) {
            if (appVersionCode > princeOfVersionsCode) {
                return AppUpdateType.FLEXIBLE;
            } else {
                return AppUpdateType.IMMEDIATE;
            }
        } else if (princeOfVersionsCode > googleVersionCode) {
            if (appVersionCode
                > googleVersionCode) { //This if shouldn't be possible in production. App version can't be lower than Google Play version.
                return UNSUPPORTED_VERSION;
            } else if (appVersionCode == googleVersionCode && isMandatory) {
                return UNSUPPORTED_VERSION;
            } else if (appVersionCode < googleVersionCode && isMandatory) {
                //TODO still have to figure out how to handle this kind of update
                return AppUpdateType.FLEXIBLE;
            } else {
                //TODO for now we should offer flexible,but in future we will add mandatory list that will check if google update is
                // mandatory
                return AppUpdateType.FLEXIBLE;
            }
        } else {
            return AppUpdateType.FLEXIBLE;
        }
    }

    public void cancel() {
        flexibleStateListener.cancel();
    }
}
