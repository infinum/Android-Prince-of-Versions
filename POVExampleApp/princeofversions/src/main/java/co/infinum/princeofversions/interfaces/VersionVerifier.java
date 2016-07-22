package co.infinum.princeofversions.interfaces;

import co.infinum.princeofversions.UpdateConfigLoader;

public interface VersionVerifier {

    void verify(UpdateConfigLoader loader, VersionVerifierListener listener);

    void cancel();

}
