package co.infinum.queenofversions;

import android.app.Activity;
import co.infinum.princeofversions.UpdateInfo;
import co.infinum.princeofversions.UpdateResult;
import java.util.Map;
import javax.annotation.Nullable;

interface GoogleAppUpdater {

    @Nullable
    InAppUpdateData createInAppUpdateData();

    void initGoogleUpdate(boolean isMandatory, @Nullable Integer version, @Nullable UpdateResult updateResult);

    void startUpdate(int updateType, @Nullable UpdateResult updateResult);

    void noUpdate(@Nullable Map<String, String> metadata, @Nullable UpdateInfo updateInfo);

    void completeUpdate();

    void resumeUpdate(Activity activity);

    void restartUpdate();

    void onUpdateDownloaded();

    void mandatoryUpdateNotAvailable(
            int mandatoryVersion,
            QueenOfVersionsInAppUpdateInfo appUpdateInfo,
            Map<String, String> metadata,
            UpdateInfo updateInfo
    );

    void cancel();
}
