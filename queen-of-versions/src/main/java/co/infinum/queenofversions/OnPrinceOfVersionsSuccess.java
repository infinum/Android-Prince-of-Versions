package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;

/**
 * Called after successfully parsing update configuration and checking for update availability in PrinceOfVersions.
 * Implement this to handle result of {@link co.infinum.princeofversions.PrinceOfVersions} check as a specific update type.
 */
public interface OnPrinceOfVersionsSuccess {

    /**
     * Called after successfully parsing update configuration and checking for update availability in PrinceOfVersions.
     * Implement this to handle result of {@link co.infinum.princeofversions.PrinceOfVersions} check as a specific update type.
     * @param result result of {@link co.infinum.princeofversions.PrinceOfVersions} update check.
     * @return  REQUIRED_UPDATE_NEEDED for starting IMMEDIATE update flow,
     *          NEW_UPDATE_AVAILABLE for starting FLEXIBLE update flow,
     *          NO_UPDATE_AVAILABLE for skipping this update
     */
    UpdateStatus handleUpdateResultAsStatus(UpdateResult result);
}
