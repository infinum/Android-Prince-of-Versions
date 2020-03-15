package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;
import javax.annotation.Nullable;

public interface OnUpdateDeclined {

    void onUpdateDeclined(
            QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
            UpdateStatus updateStatus,
            @Nullable UpdateResult updateResult
    );
}
