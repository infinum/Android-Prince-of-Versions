package co.infinum.queenofversions;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import javax.annotation.Nullable;

/**
 * Information about update received from Google Play.
 */
public final class QueenOfVersionsInAppUpdateInfo {

    static QueenOfVersionsInAppUpdateInfo from(@Nullable AppUpdateInfo appUpdateInfo) {
        return new QueenOfVersionsInAppUpdateInfo(
                appUpdateInfo != null ? appUpdateInfo.availableVersionCode() : null,
                appUpdateInfo != null ? appUpdateInfo.clientVersionStalenessDays() : null,
                appUpdateInfo != null ? appUpdateInfo.updatePriority() : null
        );
    }

    static QueenOfVersionsInAppUpdateInfo from(@Nullable InAppUpdateData inAppUpdateData) {
        return new QueenOfVersionsInAppUpdateInfo(
                inAppUpdateData != null ? inAppUpdateData.availableVersionCode() : null,
                inAppUpdateData != null ? inAppUpdateData.clientStalenessDays() : null,
                inAppUpdateData != null ? inAppUpdateData.priority() : null
        );
    }

    @Nullable
    private final Integer versionCode;

    @Nullable
    private final Integer clientVersionStalenessDays;

    @Nullable
    private final Integer updatePriority;

    QueenOfVersionsInAppUpdateInfo(
            @Nullable final Integer versionCode,
            @Nullable final Integer clientVersionStalenessDays,
            @Nullable final Integer updatePriority
    ) {
        this.versionCode = versionCode;
        this.clientVersionStalenessDays = clientVersionStalenessDays;
        this.updatePriority = updatePriority;
    }

    /**
     * Version code of available update.  If the update is not available returns <code>null</code>.
     * @return version code of available update or <code>null</code> if either update or this property isn't available.
     */
    @Nullable
    public Integer versionCode() {
        return versionCode;
    }

    /**
     * Days of staleness of the update. If the update is not available returns <code>null</code>.
     * @return days of staleness or <code>null</code> if either update or this property isn't available.
     */
    @Nullable
    public Integer clientVersionStalenessDays() {
        return clientVersionStalenessDays;
    }

    /**
     * Update priority level. If the update is not available returns <code>null</code>.
     * @return update priority or <code>null</code> if either update or this property isn't available.
     */
    @Nullable
    public Integer updatePriority() {
        return updatePriority;
    }
}
