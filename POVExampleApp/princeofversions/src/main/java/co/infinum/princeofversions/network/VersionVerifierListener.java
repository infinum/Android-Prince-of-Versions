package co.infinum.princeofversions.network;

import co.infinum.princeofversions.common.VersionContext;

public interface VersionVerifierListener {

    public void versionAvailable(VersionContext version);

    public void versionUnavailable(String error);

}
