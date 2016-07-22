package co.infinum.princeofversions.interfaces;

import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.network.VersionVerifierListener;

public interface IVersionVerifier {

    void verify(UpdateConfigLoader loader, VersionVerifierListener listener);

    void cancel();

}
