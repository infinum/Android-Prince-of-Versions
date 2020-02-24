package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;

public interface OnPrinceOfVersionsSuccess {

    UpdateStatus handleUpdateResultAsStatus(UpdateResult result);
}
