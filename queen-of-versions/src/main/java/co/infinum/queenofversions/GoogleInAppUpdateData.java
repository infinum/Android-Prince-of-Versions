package co.infinum.queenofversions;

import com.google.android.play.core.appupdate.AppUpdateInfo;

final class GoogleInAppUpdateData implements InAppUpdateData {

    private final AppUpdateInfo appUpdateInfo;

    GoogleInAppUpdateData(final AppUpdateInfo appUpdateInfo) {
        this.appUpdateInfo = appUpdateInfo;
    }

    @Override
    public Integer availableVersionCode() {
        try {
            return appUpdateInfo.availableVersionCode();
        } catch (Throwable error) {
            return null;
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
    public Integer priority() {
        try {
            return appUpdateInfo.updatePriority();
        } catch (Throwable error) {
            return null;
        }
    }

    @Override
    public Integer clientStalenessDays() {
        try {
            return appUpdateInfo.clientVersionStalenessDays();
        } catch (Throwable error) {
            return null;
        }
    }
}
