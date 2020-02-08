package co.infinum.queenofversions;

import android.app.Activity;
import android.content.IntentSender;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import co.infinum.princeofversions.UpdaterCallback;

public class GoogleInAppUpdateSuccessListener implements OnSuccessListener<AppUpdateInfo> {

    private final int requestCode;
    private final Activity activity;
    private final boolean isMandatory;
    private final AppUpdateManager appUpdateManager;
    private final InstallStateUpdatedListener installStateUpdatedListener;
    private final UpdaterCallback updaterCallback;
    private final InAppUpdateProcessCallback flexibleStateListener;
    private final GoogleInAppUpdateFlexibleHandler handler;
    private final String appVersionCode;

    GoogleInAppUpdateSuccessListener(int requestCode, Activity activity, boolean isMandatory, AppUpdateManager appUpdateManager,
        InAppUpdateProcessCallback flexibleListener, String versionCode, InstallStateUpdatedListener installStateUpdatedListener,
        UpdaterCallback updaterCallback,
        GoogleInAppUpdateFlexibleHandler handler) {
        this.requestCode = requestCode;
        this.activity = activity;
        this.isMandatory = isMandatory;
        this.appUpdateManager = appUpdateManager;
        this.flexibleStateListener = flexibleListener;
        this.installStateUpdatedListener = installStateUpdatedListener;
        this.updaterCallback = updaterCallback;
        this.handler = handler;
        this.appVersionCode = versionCode;
    }

    //TODO we have to agree on what to do in specific version cases
    @Override
    public void onSuccess(AppUpdateInfo appUpdateInfo) {
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
            if (checkVersionCode(appUpdateInfo)) {
                startUpdate(appUpdateInfo, isMandatory);
            } else {
                startUpdate(appUpdateInfo, false);
            }
        } else {
            noUpdate();
        }
    }

    private void startUpdate(AppUpdateInfo appUpdateInfo, boolean isMandatory) {
        appUpdateManager.registerListener(installStateUpdatedListener);

        if (isMandatory) {
            startImmediateFlow(appUpdateInfo);
        } else {
            startFlexibleFlow(appUpdateInfo);
        }
    }

    private void noUpdate() {
        flexibleStateListener.onNoUpdate();
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
            registerImmediateResumeFlow();
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

    private void registerImmediateResumeFlow() {
        activity.getApplication().registerActivityLifecycleCallbacks(
            new AppCallbacks(requestCode, activity, appUpdateManager, updaterCallback, flexibleStateListener, handler)
        );
    }

    private boolean checkVersionCode(AppUpdateInfo appUpdateInfo) {
        int versionCode;
        if (appVersionCode == null) {
            return false;
        } else {
            try {
                versionCode = Integer.parseInt(appVersionCode);
            } catch (NumberFormatException e) {
                flexibleStateListener.onFailed(e);
                e.printStackTrace();
                return false;
            }
        }

        return versionCode == appUpdateInfo.availableVersionCode();
    }
}
