package co.infinum.queenofversions;

import android.app.Activity;
import android.content.IntentSender;
import co.infinum.princeofversions.UpdateInfo;
import co.infinum.princeofversions.UpdateResult;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.tasks.OnSuccessListener;
import java.util.Map;
import javax.annotation.Nullable;

class QueenOfVersionsAppUpdater implements GoogleAppUpdater {

    private final Activity activity;

    private final AppUpdateManager appUpdateManager;

    private final int requestCode;

    private final QueenOfVersionsCancelableCallback flexibleStateListener;

    private final QueenOfVersionsUpdaterCallback callback;

    @Nullable
    private AppUpdateInfo appUpdateInfo;

    QueenOfVersionsAppUpdater(Activity activity, AppUpdateManager appUpdateManager, int requestCode,
            QueenOfVersionsCancelableCallback flexibleStateListener, QueenOfVersionsUpdaterCallback callback) {
        this.activity = activity;
        this.appUpdateManager = appUpdateManager;
        this.requestCode = requestCode;
        this.flexibleStateListener = flexibleStateListener;
        this.callback = callback;
    }

    @Override
    public void initGoogleUpdate(
            final boolean isMandatory,
            @Nullable final Integer versionCode,
            @Nullable final UpdateResult updateResult
    ) {
        appUpdateManager.getAppUpdateInfo()
                .addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                    @Override
                    public void onSuccess(AppUpdateInfo appUpdateInfo) {
                        QueenOfVersionsAppUpdater.this.appUpdateInfo = appUpdateInfo;
                        callback.handleSuccess(
                                appUpdateInfo.updateAvailability(),
                                versionCode,
                                appUpdateInfo.availableVersionCode(),
                                isMandatory,
                                updateResult
                        );
                    }
                }
        ).addOnFailureListener(new GoogleInAppUpdateFailureListener(flexibleStateListener));
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
    public void noUpdate(Map<String, String> metadata, UpdateInfo updateInfo) {
        flexibleStateListener.onNoUpdate(metadata, updateInfo);
    }

    @Override
    public void mandatoryUpdateNotAvailable(
            int mandatoryVersion,
            int availableVersion,
            Map<String, String> metadata,
            UpdateInfo updateInfo
    ) {
        flexibleStateListener.onMandatoryUpdateNotAvailable(mandatoryVersion, availableVersion, metadata, updateInfo);
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
            flexibleStateListener.onError(e);
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
            flexibleStateListener.onError(e);
        }
    }

    @Override
    public void registerImmediateFlow() {
        activity.getApplication().registerActivityLifecycleCallbacks(
                new QueenOfVersionsActivityLifecycleCallback(this)
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
                                    QueenOfVersionsAppUpdater.this.appUpdateInfo = appUpdateInfo;
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
            flexibleStateListener.onError(e);
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
