package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateResult;
import javax.annotation.Nullable;

public interface OnUpdateNotAllowed {

    boolean onImmediateUpdateNotAllowed(QueenOfVersionsInAppUpdateInfo updateInfo, @Nullable UpdateResult updateResult);

    boolean onFlexibleUpdateNotAllowed(QueenOfVersionsInAppUpdateInfo updateInfo, @Nullable UpdateResult updateResult);
}
