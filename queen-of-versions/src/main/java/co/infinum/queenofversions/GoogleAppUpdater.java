package co.infinum.queenofversions;

import android.app.Activity;
import co.infinum.princeofversions.UpdateInfo;
import co.infinum.princeofversions.UpdateResult;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import java.util.Map;
import javax.annotation.Nullable;

public interface GoogleAppUpdater {

    void initGoogleUpdate(boolean isMandatory, @Nullable Integer version, @Nullable UpdateResult updateResult);

    void startUpdate(int updateType);

    void noUpdate(@Nullable Map<String, String> metadata, @Nullable UpdateInfo updateInfo);

    void startFlexibleFlow(AppUpdateInfo appUpdateInfo);

    void startImmediateFlow(AppUpdateInfo appUpdateInfo);

    void registerImmediateFlow();

    void completeUpdate();

    void resumeUpdate(Activity activity);

    void restartUpdate();

    void notifyUser();

    void mandatoryUpdateNotAvailable(int mandatoryVersion, int availableVersion, Map<String, String> metadata, UpdateInfo updateInfo);

    void cancel();
}
