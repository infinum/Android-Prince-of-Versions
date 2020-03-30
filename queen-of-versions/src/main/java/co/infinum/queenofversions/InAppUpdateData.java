package co.infinum.queenofversions;

import androidx.annotation.VisibleForTesting;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import javax.annotation.Nullable;

@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
public interface InAppUpdateData {

    @Nullable
    Integer availableVersionCode();

    @UpdateAvailability
    int updateAvailability();

    @InstallStatus
    int installStatus();

    boolean isUpdateTypeAllowed(@AppUpdateType int updateType);

    @Nullable
    Integer priority();

    @Nullable
    Integer clientStalenessDays();
}
