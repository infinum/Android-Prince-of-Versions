package co.infinum.queenofversions.mocks;

import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import javax.annotation.Nullable;

import co.infinum.queenofversions.InAppUpdateData;

public class MockAppUpdateData implements InAppUpdateData {

    @Nullable
    private final Integer versionCode;

    @UpdateAvailability
    private final int updateAvailability;

    @InstallStatus
    private final int installStatus;

    private final boolean isImmediateUpdateAllowed;

    private final boolean isFlexibleUpdateAllowed;

    private MockAppUpdateData(
            @Nullable final Integer versionCode,
            @UpdateAvailability final int updateAvailability,
            @InstallStatus final int installStatus,
            final boolean isImmediateUpdateAllowed,
            final boolean isFlexibleUpdateAllowed
    ) {
        this.versionCode = versionCode;
        this.updateAvailability = updateAvailability;
        this.installStatus = installStatus;
        this.isImmediateUpdateAllowed = isImmediateUpdateAllowed;
        this.isFlexibleUpdateAllowed = isFlexibleUpdateAllowed;
    }

    public static InAppUpdateData createImmediate(
            @UpdateAvailability int updateAvailability,
            @InstallStatus int installStatus,
            int versionCode
    ) {
        return new MockAppUpdateData(
                versionCode,
                updateAvailability,
                installStatus,
                true,
                false
        );
    }

    public static InAppUpdateData createFlexible(
            @UpdateAvailability int updateAvailability,
            @InstallStatus int installStatus,
            int versionCode
    ) {
        return new MockAppUpdateData(
                versionCode,
                updateAvailability,
                installStatus,
                false,
                true
        );
    }

    public static InAppUpdateData createImmediateAvailable(
            @InstallStatus int installStatus,
            int versionCode
    ) {
        return new MockAppUpdateData(
                versionCode,
                UpdateAvailability.UPDATE_AVAILABLE,
                installStatus,
                true,
                false
        );
    }

    public static InAppUpdateData createImmediateUnavailable() {
        return new MockAppUpdateData(
                null,
                UpdateAvailability.UPDATE_AVAILABLE,
                InstallStatus.UNKNOWN,
                true,
                false
        );
    }

    public static InAppUpdateData createImmediateAvailable(
            int versionCode
    ) {
        return new MockAppUpdateData(
                versionCode,
                UpdateAvailability.UPDATE_AVAILABLE,
                InstallStatus.UNKNOWN,
                true,
                false
        );
    }

    public static InAppUpdateData createFlexibleAvailable(
            @InstallStatus int installStatus,
            int versionCode
    ) {
        return new MockAppUpdateData(
                versionCode,
                UpdateAvailability.UPDATE_AVAILABLE,
                installStatus,
                false,
                true
        );
    }

    public static InAppUpdateData createFlexibleUnavailable() {
        return new MockAppUpdateData(
                null,
                UpdateAvailability.UPDATE_AVAILABLE,
                InstallStatus.UNKNOWN,
                false,
                true
        );
    }

    public static InAppUpdateData createFlexibleAvailable(
            int versionCode
    ) {
        return new MockAppUpdateData(
                versionCode,
                UpdateAvailability.UPDATE_AVAILABLE,
                InstallStatus.UNKNOWN,
                false,
                true
        );
    }

    public static InAppUpdateData create(
            @UpdateAvailability int updateAvailability,
            @InstallStatus int installStatus,
            int versionCode
    ) {
        return new MockAppUpdateData(
                versionCode,
                updateAvailability,
                installStatus,
                true,
                true
        );
    }

    public static InAppUpdateData createAvailable(
            @InstallStatus int installStatus,
            int versionCode
    ) {
        return new MockAppUpdateData(
                versionCode,
                UpdateAvailability.UPDATE_AVAILABLE,
                installStatus,
                true,
                true
        );
    }

    public static InAppUpdateData createUnavailable() {
        return new MockAppUpdateData(
                null,
                UpdateAvailability.UPDATE_NOT_AVAILABLE,
                InstallStatus.UNKNOWN,
                true,
                true
        );
    }

    public static InAppUpdateData createAvailable(
            int versionCode
    ) {
        return new MockAppUpdateData(
                versionCode,
                UpdateAvailability.UPDATE_AVAILABLE,
                InstallStatus.UNKNOWN,
                true,
                true
        );
    }

    @Override
    public Integer availableVersionCode() {
        return versionCode;
    }

    @Override
    public int updateAvailability() {
        return updateAvailability;
    }

    @Override
    public int installStatus() {
        return installStatus;
    }

    @Override
    public boolean isUpdateTypeAllowed(@AppUpdateType int updateType) {
        if (updateType == AppUpdateType.IMMEDIATE) {
            return isImmediateUpdateAllowed;
        } else {
            return isFlexibleUpdateAllowed;
        }
    }

    @Nullable
    @Override
    public Integer priority() {
        return null;
    }

    @Nullable
    @Override
    public Integer clientStalenessDays() {
        return null;
    }
}
