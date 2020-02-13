package co.infinum.queenofversions;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.google.android.play.core.appupdate.AppUpdateManager;

import co.infinum.princeofversions.UpdaterCallback;

public class AppCallbacks implements Application.ActivityLifecycleCallbacks {

    private final int requestCode;
    private final Activity activity;
    private final AppUpdateManager appUpdateManager;
    private final UpdaterCallback updaterCallback;
    private final InAppUpdateProcessCallback flexibleListener;
    private final GoogleInAppUpdateFlexibleHandler handler;

    AppCallbacks(int requestCode, Activity activity, AppUpdateManager appUpdateManager,
        UpdaterCallback updaterCallback, InAppUpdateProcessCallback flexibleListener, GoogleInAppUpdateFlexibleHandler handler) {
        this.requestCode = requestCode;
        this.activity = activity;
        this.appUpdateManager = appUpdateManager;
        this.updaterCallback = updaterCallback;
        this.flexibleListener = flexibleListener;
        this.handler = handler;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        resumeAppUpdate(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    private void resumeAppUpdate(Activity activity) {
        if (this.activity.equals(activity)) {
            appUpdateManager.getAppUpdateInfo()
                .addOnSuccessListener(
                    new GoogleInAppUpdateResumeSuccessListener(requestCode, activity, appUpdateManager, updaterCallback, flexibleListener,
                        handler)
                )
                .addOnFailureListener(new GoogleInAppUpdateFailureListener(updaterCallback));
        }
    }
}