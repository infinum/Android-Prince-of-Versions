package com.example.queen_of_versions;

import android.app.Activity;
import android.app.Application;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import co.infinum.princeofversions.UpdaterCallback;

public class GoogleInAppUpdateCallback implements UpdaterCallback,InstallStateUpdatedListener, QueenOfVersionsUpdateHandler {

    private static final int REQUEST_CODE = 420;

    private AppUpdateManager appUpdateManager;
    private Activity activity;
    private OnDownloadedListener listener;

    GoogleInAppUpdateCallback(Activity activity, OnDownloadedListener listener) {
        this.activity = activity;
        this.appUpdateManager = AppUpdateManagerFactory.create(activity);
        this.listener = listener;
    }

    @Override
    public void onNewUpdate(@NotNull String version, final boolean isMandatory, @NotNull Map<String, String> metadata) {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(
            new OnSuccessListener<AppUpdateInfo>() {
                @Override
                public void onSuccess(AppUpdateInfo appUpdateInfo) {
                    if( appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE){
                        if (isMandatory) {
                            try {
                                appUpdateManager.startUpdateFlowForResult(
                                    appUpdateManager.getAppUpdateInfo().getResult(),
                                    AppUpdateType.IMMEDIATE,
                                    activity,
                                    REQUEST_CODE
                                );
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                appUpdateManager.startUpdateFlowForResult(
                                    appUpdateManager.getAppUpdateInfo().getResult(),
                                    AppUpdateType.FLEXIBLE,
                                    activity,
                                    REQUEST_CODE
                                );
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }

                        appUpdateManager.registerListener(GoogleInAppUpdateCallback.this);
                        activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                            @Override
                            public void onActivityCreated(Activity activity, Bundle bundle) {
                            }

                            @Override
                            public void onActivityStarted(Activity activity) {
                            }

                            @Override
                            public void onActivityResumed(final Activity activity) {
                                appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                                    @Override
                                    public void onSuccess(AppUpdateInfo appUpdateInfo) {
                                        if(appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                                            try {
                                                appUpdateManager.startUpdateFlowForResult(
                                                    appUpdateInfo,
                                                    AppUpdateType.IMMEDIATE,
                                                    activity,
                                                    REQUEST_CODE
                                                );
                                            } catch (IntentSender.SendIntentException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onActivityPaused(Activity activity) {
                            }

                            @Override
                            public void onActivityStopped(Activity activity) {
                            }

                            @Override
                            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                            }

                            @Override
                            public void onActivityDestroyed(Activity activity) {
                            }
                        });
                    }
                }
            }
        );

        //TODO check if somewhere this callback is hanging
    }

    @Override
    public void onNoUpdate(@NotNull Map<String, String> metadata) {
        // Use metadata somehow? Ask for help here
    }

    @Override
    public void onError(@NotNull Throwable error) {
        error.printStackTrace();
    }

    @Override
    public void onStateUpdate(InstallState installState) {
        if( installState.installStatus() == InstallStatus.DOWNLOADED){
            listener.notifyUser(this);
        }
    }

    @Override
    public void proceed() {
        appUpdateManager.completeUpdate();
        appUpdateManager.unregisterListener(this);
    }
}
