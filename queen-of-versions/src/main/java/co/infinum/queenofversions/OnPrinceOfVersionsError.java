package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateStatus;

/**
 * Called after error in parsing update configuration and checking for update availability in PrinceOfVersions.
 * Implement this to handle error of {@link co.infinum.princeofversions.PrinceOfVersions} check as a specific update type.
 */
public interface OnPrinceOfVersionsError {

    /**
     * Called after error in parsing update configuration and checking for update availability in PrinceOfVersions.
     * Implement this to handle error of {@link co.infinum.princeofversions.PrinceOfVersions} check as a specific update type.
     * @param error error occurred in {@link co.infinum.princeofversions.PrinceOfVersions} update check.
     * @return  REQUIRED_UPDATE_NEEDED for starting IMMEDIATE update flow,
     *          NEW_UPDATE_AVAILABLE for starting FLEXIBLE update flow,
     *          NO_UPDATE_AVAILABLE for skipping this update
     * @throws Throwable to propagate error, {@link OnError} will be called with that error.
     */
    UpdateStatus continueUpdateCheckAsStatus(Throwable error) throws Throwable;
}
