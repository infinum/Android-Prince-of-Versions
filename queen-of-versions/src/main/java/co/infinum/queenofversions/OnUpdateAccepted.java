package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;
import javax.annotation.Nullable;

public interface OnUpdateAccepted {

    void onUpdateAccepted(
            QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
            UpdateStatus updateStatus,
            @Nullable UpdateResult updateResult
    );
}
