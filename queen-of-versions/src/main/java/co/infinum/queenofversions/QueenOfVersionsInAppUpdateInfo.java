package co.infinum.queenofversions;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import javax.annotation.Nullable;

import static co.infinum.queenofversions.InAppUpdateData.INVALID_VALUE;

/**
 * Information about update received from Google Play.
 */
public final class QueenOfVersionsInAppUpdateInfo {

    static QueenOfVersionsInAppUpdateInfo from(@Nullable AppUpdateInfo appUpdateInfo) {
        return new QueenOfVersionsInAppUpdateInfo(
                appUpdateInfo != null ? getOrDefault(appUpdateInfo.availableVersionCode(), INVALID_VALUE) : INVALID_VALUE,
                appUpdateInfo != null ? getOrDefault(appUpdateInfo.clientVersionStalenessDays(), INVALID_VALUE) : INVALID_VALUE,
                appUpdateInfo != null ? getOrDefault(appUpdateInfo.updatePriority(), INVALID_VALUE) : INVALID_VALUE
        );
    }

    static QueenOfVersionsInAppUpdateInfo from(@Nullable InAppUpdateData inAppUpdateData) {
        return new QueenOfVersionsInAppUpdateInfo(
                inAppUpdateData != null ? inAppUpdateData.availableVersionCode() : 0,
                inAppUpdateData != null ? inAppUpdateData.clientStalenessDays() : 0,
                inAppUpdateData != null ? inAppUpdateData.priority() : 0
        );
    }

    private static int getOrDefault(@Nullable Integer value, int defaultValue) {
        return value != null ? value : defaultValue;
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
     * Version code of available update.  If the update is not available returns -1.
     * @return version code of available update.
     */
    public int versionCode() {
        return versionCode;
    }

    /**
     * Days of staleness of the update. If the update is not available returns -1.
     * @return days of staleness.
     */
    public int clientVersionStalenessDays() {
        return clientVersionStalenessDays;
    }

    /**
     * Update priority level. If the update is not available returns -1.
     * @return update priority.
     */
    public int updatePriority() {
        return updatePriority;
    }
}
