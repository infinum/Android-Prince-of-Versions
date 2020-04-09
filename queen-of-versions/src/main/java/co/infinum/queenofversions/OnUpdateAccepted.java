package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;
import javax.annotation.Nullable;

/**
 * Implement to handle the case when user accepts the update. This is called when either IMMEDIATE or FLEXIBLE update flow is presented to
 * user and user starts the update by interaction in the UI.
 */
public interface OnUpdateAccepted {

    /**
     * Called when either IMMEDIATE or FLEXIBLE update flow is presented to user and user starts the update by interaction in the UI.
     * @param inAppUpdateInfo information about the update read from Google Play.
     * @param updateStatus determines status by which update has been handled. Can be either {@link UpdateStatus}.REQUIRED_UPDATE_NEEDED
     *                    in case of IMMEDIATE update or {@link UpdateStatus}.NEW_UPDATE_AVAILABLE in case of FLEXIBLE update.
     * @param updateResult information about the update read from PrinceOfVersions configuration.
     */
    void onUpdateAccepted(
            QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
            UpdateStatus updateStatus,
            @Nullable UpdateResult updateResult
    );
}
