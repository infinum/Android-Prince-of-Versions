package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateInfo;
import java.util.Map;

/**
 * Called in case {@link co.infinum.princeofversions.PrinceOfVersions} check
 * results in {@link co.infinum.princeofversions.UpdateStatus} REQUIRED_UPDATE_NEEDED
 * but Google Play does not have that update version available.
 */
public interface OnMandatoryUpdateNotAvailable {

    /**
     * Called in case {@link co.infinum.princeofversions.PrinceOfVersions} check
     * results in {@link co.infinum.princeofversions.UpdateStatus} REQUIRED_UPDATE_NEEDED
     * but Google Play does not have that update version available.
     *
     * @param mandatoryVersion  version code of the update for which {@link co.infinum.princeofversions.PrinceOfVersions} claims it
     *                          is required update and Google Play does not have information about that update yet.
     * @param inAppUpdateInfo   information received from Google Play update check.
     * @param metadata          metadata received from {@link co.infinum.princeofversions.PrinceOfVersions} update check.
     * @param updateInfo        information received from {@link co.infinum.princeofversions.PrinceOfVersions} update check.
     */
    void onMandatoryUpdateNotAvailable(
            int mandatoryVersion,
            QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
            Map<String, String> metadata,
            UpdateInfo updateInfo
    );
}
