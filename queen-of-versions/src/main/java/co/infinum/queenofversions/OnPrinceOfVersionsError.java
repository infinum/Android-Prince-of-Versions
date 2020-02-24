package co.infinum.queenofversions;

import co.infinum.princeofversions.UpdateStatus;

public interface OnPrinceOfVersionsError {

    UpdateStatus continueUpdateCheckAsStatus(Throwable error) throws Throwable;
}
