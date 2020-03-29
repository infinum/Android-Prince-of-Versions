package co.infinum.queenofversions;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import javax.annotation.Nullable;

/**
 * Information about update received from Google Play.
 */
public final class QueenOfVersionsInAppUpdateInfo {

    static QueenOfVersionsInAppUpdateInfo from(@Nullable AppUpdateInfo appUpdateInfo) {
        return new QueenOfVersionsInAppUpdateInfo(
                appUpdateInfo != null ? appUpdateInfo.availableVersionCode() : 0,
                appUpdateInfo != null ? appUpdateInfo.clientVersionStalenessDays() : 0,
                appUpdateInfo != null ? appUpdateInfo.updatePriority() : 0
        );
    }

    static QueenOfVersionsInAppUpdateInfo from(@Nullable InAppUpdateData inAppUpdateData) {
        return new QueenOfVersionsInAppUpdateInfo(
                inAppUpdateData != null ? inAppUpdateData.availableVersionCode() : 0,
                inAppUpdateData != null ? inAppUpdateData.clientStalenessDays() : 0,
                inAppUpdateData != null ? inAppUpdateData.priority() : 0
        );
    }

    private final int versionCode;

    private final int clientVersionStalenessDays;

    private final int updatePriority;

    QueenOfVersionsInAppUpdateInfo(
            final int versionCode,
            final int clientVersionStalenessDays,
            final int updatePriority
    ) {
        this.versionCode = versionCode;
        this.clientVersionStalenessDays = clientVersionStalenessDays;
        this.updatePriority = updatePriority;
    }

    /**
     * Version code of available update.
     * @return version code of available update.
     */
    public int versionCode() {
        return versionCode;
    }

    /**
     * Days of staleness of the update.
     * @return days of staleness.
     */
    public int clientVersionStalenessDays() {
        return clientVersionStalenessDays;
    }

    /**
     * Update priority level.
     * @return update priority.
     */
    public int updatePriority() {
        return updatePriority;
    }
}
