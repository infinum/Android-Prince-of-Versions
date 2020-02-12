package co.infinum.queenofversions;

import android.app.Activity;

import com.google.android.play.core.appupdate.AppUpdateInfo;

public interface GoogleAppUpdater {

    void initGoogleUpdate(boolean isMandatory, String version);

    void startUpdate(int updateType);

    void noUpdate();

    void startFlexibleFlow(AppUpdateInfo appUpdateInfo);

    void startImmediateFlow(AppUpdateInfo appUpdateInfo);

    void registerImmediateFlow();

    void completeUpdate();

    void resumeUpdate(Activity activity);

    void restartUpdate();

    void notifyUser();

    void cancel();
}
