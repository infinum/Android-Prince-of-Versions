package co.infinum.queenofversions;

import com.google.android.play.core.appupdate.AppUpdateInfo;

class GoogleInAppUpdateData implements InAppUpdateData {

    private final AppUpdateInfo appUpdateInfo;

    GoogleInAppUpdateData(final AppUpdateInfo appUpdateInfo) {
        this.appUpdateInfo = appUpdateInfo;
    }

    @Override
    public int availableVersionCode() {
        return appUpdateInfo.availableVersionCode();
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
        return appUpdateInfo.updatePriority();
    }

    @Override
    public int clientStalenessDays() {
        return appUpdateInfo.clientVersionStalenessDays();
    }
}
