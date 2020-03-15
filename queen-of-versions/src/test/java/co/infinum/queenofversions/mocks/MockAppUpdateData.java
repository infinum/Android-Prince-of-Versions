package co.infinum.queenofversions.mocks;

import co.infinum.queenofversions.InAppUpdateData;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

public class MockAppUpdateData implements InAppUpdateData {

    private final int versionCode;

    @UpdateAvailability
    private final int updateAvailability;

    @InstallStatus
    private final int installStatus;

    private final boolean isImmediateUpdateAllowed;

    private final boolean isFlexibleUpdateAllowed;

    private MockAppUpdateData(
            final int versionCode,
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

    public static InAppUpdateData createImmediateAvailble(
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

    public static InAppUpdateData createImmediateUnavailble() {
        return new MockAppUpdateData(
                0,
                UpdateAvailability.UPDATE_AVAILABLE,
                InstallStatus.UNKNOWN,
                true,
                false
        );
    }

    public static InAppUpdateData createImmediateAvailble(
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

    public static InAppUpdateData createFlexibleAvailble(
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

    public static InAppUpdateData createFlexibleUnavailble() {
        return new MockAppUpdateData(
                0,
                UpdateAvailability.UPDATE_AVAILABLE,
                InstallStatus.UNKNOWN,
                false,
                true
        );
    }

    public static InAppUpdateData createFlexibleAvailble(
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
                0,
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
    public int availableVersionCode() {
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

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public int clientStalenessDays() {
        return 0;
    }
}
