package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateInfo;
import java.util.Map;

public interface OnMandatoryUpdateNotAvailable {

    void onMandatoryUpdateNotAvailable(
            int mandatoryVersion,
            QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
            Map<String, String> metadata,
            UpdateInfo updateInfo
    );
}
