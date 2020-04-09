package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateResult;
import javax.annotation.Nullable;

/**
 * Implement to handle the case when update type is available, but not allowed.
 */
public interface OnUpdateNotAllowed {

    /**
     * Called if update type {@link com.google.android.play.core.install.model.AppUpdateType} is IMMEDIATE, but update type is not allowed
     * (checked by {@link com.google.android.play.core.install.model.AppUpdateType} {@code isUpdateTypeAllowed}.
     * This method will be called only if IMMEDIATE update type is not allowed and FLEXIBLE update type is allowed.
     *
     * @param updateInfo information about the update read from Google Play.
     * @param updateResult information about the update read from PrinceOfVersions configuration.
     * @return {@code true} to continue as update type FLEXIBLE, {@code false} to continue as there is no update available.
     */
    boolean onImmediateUpdateNotAllowed(QueenOfVersionsInAppUpdateInfo updateInfo, @Nullable UpdateResult updateResult);

    /**
     * Called if update type {@link com.google.android.play.core.install.model.AppUpdateType} is FLEXIBLE, but update type is not allowed
     * (checked by {@link com.google.android.play.core.install.model.AppUpdateType} {@code isUpdateTypeAllowed}.
     * This method will be called only if FLEXIBLE update type is not allowed and IMMEDIATE update type is allowed.
     *
     * @param updateInfo information about the update read from Google Play.
     * @param updateResult information about the update read from PrinceOfVersions configuration.
     * @return {@code true} to continue as update type IMMEDIATE, {@code false} to continue as there is no update available.
     */
    boolean onFlexibleUpdateNotAllowed(QueenOfVersionsInAppUpdateInfo updateInfo, @Nullable UpdateResult updateResult);
}
