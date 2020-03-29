package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;
import javax.annotation.Nullable;

/**
 * Called if after successfully parsing update data from Google Play there is an update available.
 */
public interface OnInAppUpdateAvailable {

    /**
     * Called if after successfully parsing update data from Google Play there is an update available.
     * Implement this to handle In-App update data as a specific update type.
     *
     * @param currentStatus status of the update resolution
     * @param inAppUpdateInfo information about the update available in Google Play
     * @param updateResult optional result of the update check from {@link co.infinum.princeofversions.PrinceOfVersions}
     * @return  REQUIRED_UPDATE_NEEDED for starting IMMEDIATE update flow,
     *          NEW_UPDATE_AVAILABLE for starting FLEXIBLE update flow,
     *          NO_UPDATE_AVAILABLE for skipping this update
     */
    UpdateStatus handleInAppUpdateAsStatus(
            UpdateStatus currentStatus,
            QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
            @Nullable UpdateResult updateResult
    );
}
