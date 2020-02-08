package co.infinum.queenofversions;

import android.app.Activity;
import android.content.IntentSender;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import co.infinum.princeofversions.UpdaterCallback;

public class GoogleInAppUpdateResumeSuccessListener implements OnSuccessListener<AppUpdateInfo> {

    private final int requestCode;
    private final Activity activity;
    private final AppUpdateManager appUpdateManager;
    private final UpdaterCallback updaterCallback;
    private final InAppUpdateProcessCallback flexibleListener;
    private final GoogleInAppUpdateFlexibleHandler handler;

    GoogleInAppUpdateResumeSuccessListener(int requestCode, Activity activity, AppUpdateManager appUpdateManager,
        UpdaterCallback updaterCallback, InAppUpdateProcessCallback flexibleListener, GoogleInAppUpdateFlexibleHandler handler) {
        this.requestCode = requestCode;
        this.activity = activity;
        this.appUpdateManager = appUpdateManager;
        this.updaterCallback = updaterCallback;
        this.flexibleListener = flexibleListener;
        this.handler = handler;
    }

    @Override
    public void onSuccess(AppUpdateInfo appUpdateInfo) {
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
            updateInProgress(appUpdateInfo);
        } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
            notifyUser();
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

    private void notifyUser() {
        flexibleListener.onDownloaded(handler);
    }
}
