package co.infinum.queenofversions;

import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

public interface InAppUpdateData {

    int availableVersionCode();

    @UpdateAvailability
    int updateAvailability();

    @InstallStatus
    int installStatus();

    boolean isUpdateTypeAllowed(@AppUpdateType int updateType);

    int priority();

    int clientStalenessDays();
}
