package co.infinum.princeofversions.interfaces;

import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.common.VersionContext;

public interface VersionVerifierListener {

    public void versionAvailable(VersionContext version);

    public void versionUnavailable(@ErrorCode int error);

}
