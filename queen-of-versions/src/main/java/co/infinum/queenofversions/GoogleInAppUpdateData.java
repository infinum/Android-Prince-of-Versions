package co.infinum.queenofversions;

import com.google.android.play.core.appupdate.AppUpdateInfo;

final class GoogleInAppUpdateData implements InAppUpdateData {

    private final AppUpdateInfo appUpdateInfo;

    GoogleInAppUpdateData(final AppUpdateInfo appUpdateInfo) {
        this.appUpdateInfo = appUpdateInfo;
    }

    @Override
    public int availableVersionCode() {
        try {
            return appUpdateInfo.availableVersionCode();
        } catch (Throwable error) {
            return INVALID_VALUE;
        }
    }

    @Override
    public int updateAvailability() {
        return appUpdateInfo.updateAvailability();
    }

    @Override
    public int installStatus() {
        return appUpdateInfo.installStatus();
    }

    @Override
    public boolean isUpdateTypeAllowed(int updateType) {
        return appUpdateInfo.isUpdateTypeAllowed(updateType);
    }

    @Override
    public int priority() {
        try {
            return appUpdateInfo.updatePriority();
        } catch (Throwable error) {
            return INVALID_VALUE;
        }
    }

    @Override
    public int clientStalenessDays() {
        try {
            Integer value = appUpdateInfo.clientVersionStalenessDays();
            return value != null ? value : INVALID_VALUE;
        } catch (Throwable error) {
            return INVALID_VALUE;
        }
    }
}
