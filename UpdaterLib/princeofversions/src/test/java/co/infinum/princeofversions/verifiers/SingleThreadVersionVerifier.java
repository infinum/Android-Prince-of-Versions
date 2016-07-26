package co.infinum.princeofversions.verifiers;

import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.helpers.parsers.VersionConfigParser;
import co.infinum.princeofversions.interfaces.VersionVerifierListener;
import co.infinum.princeofversions.threading.ThreadVersionVerifier;

public class SingleThreadVersionVerifier extends ThreadVersionVerifier {

    public SingleThreadVersionVerifier(VersionConfigParser parser) {
        super(parser);
    }

    @Override
    public void verify(UpdateConfigLoader loader, VersionVerifierListener listener) {
        getVersion(loader, listener);
    }

}
