package co.infinum.queenofversions;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import co.infinum.princeofversions.UpdateInfo;
import co.infinum.princeofversions.UpdateResult;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.tasks.OnSuccessListener;
import java.util.Map;
import javax.annotation.Nullable;

final class QueenOfVersionsAppUpdater implements GoogleAppUpdater {

    private final FragmentActivity activity;

    private final AppUpdateManager appUpdateManager;

    private final QueenOfVersionsCancelableCallback queenOfVersionsCallback;

    private final QueenOfVersionsUpdaterCallback callback;

    private final QueenOfVersionsActivityLifecycleCallback activityLifecycleCallback = new QueenOfVersionsActivityLifecycleCallback(this);

    @Nullable
    private AppUpdateInfo appUpdateInfo;

    @Nullable
    private UpdateResult updateResult;

    @AppUpdateType
    private int updateType;

    QueenOfVersionsAppUpdater(FragmentActivity activity, AppUpdateManager appUpdateManager,
            QueenOfVersionsCancelableCallback queenOfVersionsCallback, QueenOfVersionsUpdaterCallback callback) {
        this.activity = activity;
        this.appUpdateManager = appUpdateManager;
        this.queenOfVersionsCallback = queenOfVersionsCallback;
        this.callback = callback;
    }

    @Override
    public void initGoogleUpdate(
            final boolean isMandatory,
            @Nullable final Integer versionCode,
            @Nullable final UpdateResult updateResult
    ) {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(
                new OnSuccessListener<AppUpdateInfo>() {
                    @Override
                    public void onSuccess(AppUpdateInfo appUpdateInfo) {
                        QueenOfVersionsAppUpdater.this.updateResult = updateResult;
                        QueenOfVersionsAppUpdater.this.appUpdateInfo = appUpdateInfo;
                        callback.handleSuccess(
                                new GoogleInAppUpdateData(appUpdateInfo),
                                versionCode,
                                isMandatory,
                                updateResult
                        );
                    }
                }
        ).addOnFailureListener(new GoogleInAppUpdateFailureListener(queenOfVersionsCallback));
    }

    @Override
    public void startUpdate(int updateType, @Nullable UpdateResult updateResult) {
        this.updateType = updateType;

        AppUpdateInfo localAppUpdateInfo = this.appUpdateInfo;
        if (localAppUpdateInfo == null) {
            callback.onError(new IllegalStateException("AppUpdateInfo doesn't exist. Report this as an issue."));
            return;
        }

        if (updateType == AppUpdateType.IMMEDIATE) {
            startImmediateFlow(localAppUpdateInfo, updateResult);
        } else {
            startFlexibleFlow(localAppUpdateInfo, updateResult);
        }
        appUpdateManager.registerListener(callback);
    }

    @Override
    public void noUpdate(@Nullable Map<String, String> metadata, @Nullable UpdateInfo updateInfo) {
        queenOfVersionsCallback.onNoUpdate(metadata, updateInfo);
    }

    @Override
    public void mandatoryUpdateNotAvailable(
            int mandatoryVersion,
            QueenOfVersionsInAppUpdateInfo appUpdateInfo,
            Map<String, String> metadata,
            UpdateInfo updateInfo
    ) {
        queenOfVersionsCallback.onMandatoryUpdateNotAvailable(
                mandatoryVersion,
                appUpdateInfo,
                metadata,
                updateInfo
        );
    }

    private void startFlexibleFlow(AppUpdateInfo appUpdateInfo, @Nullable UpdateResult updateResult) {
        registerImmediateFlow();
        QueenOfVersionsFragment fragment = QueenOfVersionsFragment.get(activity);
        fragment.startUpdateFlow(appUpdateInfo, AppUpdateType.FLEXIBLE, updateResult, queenOfVersionsCallback);
    }

    private void startImmediateFlow(AppUpdateInfo appUpdateInfo, @Nullable UpdateResult updateResult) {
        registerImmediateFlow();
        QueenOfVersionsFragment fragment = QueenOfVersionsFragment.get(activity);
        fragment.startUpdateFlow(appUpdateInfo, AppUpdateType.IMMEDIATE, updateResult, queenOfVersionsCallback);
    }

    private void registerImmediateFlow() {
        activity.getApplication().unregisterActivityLifecycleCallbacks(activityLifecycleCallback);
        activity.getApplication().registerActivityLifecycleCallbacks(activityLifecycleCallback);
    }

    @Override
    public void completeUpdate() {
        appUpdateManager.completeUpdate();
        appUpdateManager.unregisterListener(callback);
    }

    @Override
    public void resumeUpdate(Activity activity) {
        if (this.activity.equals(activity) && appUpdateInfo != null) {
            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(
                    new OnSuccessListener<AppUpdateInfo>() {
                        @Override
                        public void onSuccess(AppUpdateInfo appUpdateInfo) {
                            QueenOfVersionsAppUpdater.this.appUpdateInfo = appUpdateInfo;
                            callback.handleResumeSuccess(
                                    appUpdateInfo.updateAvailability(),
                                    appUpdateInfo.installStatus(),
                                    updateType == AppUpdateType.FLEXIBLE
                            );
                        }
                    }
            ).addOnFailureListener(new GoogleInAppUpdateFailureListener(queenOfVersionsCallback));
        }
    }

    @Override
    public void restartUpdate() {
        if (appUpdateInfo != null) {
            registerImmediateFlow();
            QueenOfVersionsFragment fragment = QueenOfVersionsFragment.get(activity);
            fragment.startUpdateFlow(appUpdateInfo, updateType, updateResult, queenOfVersionsCallback);
        }
    }

    @Override
    public void onUpdateDownloaded() {
        queenOfVersionsCallback.onDownloaded(callback, QueenOfVersionsInAppUpdateInfo.from(appUpdateInfo));
    }

    @Override
    public void cancel() {
        activity.getApplication().unregisterActivityLifecycleCallbacks(activityLifecycleCallback);
        QueenOfVersionsFragment.get(activity).detachCallback(queenOfVersionsCallback);
        appUpdateManager.unregisterListener(callback);
    }

    @Nullable
    @Override
    public InAppUpdateData createInAppUpdateData() {
        return appUpdateInfo != null ? new GoogleInAppUpdateData(appUpdateInfo) : null;
    }
}
