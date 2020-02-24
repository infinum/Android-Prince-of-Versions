package co.infinum.queenofversions;

import android.app.Activity;

import com.google.android.play.core.appupdate.AppUpdateInfo;

import co.infinum.princeofversions.UpdateInfo;
import javax.annotation.Nullable;

public interface GoogleAppUpdater {

    void initGoogleUpdate(boolean isMandatory, @Nullable Integer version, @Nullable UpdateInfo updateInfo);

    void startUpdate(int updateType);

    void noUpdate();

    void startFlexibleFlow(AppUpdateInfo appUpdateInfo);

    void startImmediateFlow(AppUpdateInfo appUpdateInfo);

    void registerImmediateFlow();

    void completeUpdate();

    void resumeUpdate(Activity activity);

    void restartUpdate();

    void notifyUser();

    void mandatoryUpdateNotAvailable(int mandatoryVersion, int availableVersion);

    void cancel();
}
