package co.infinum.princeofversions.mvp.interactor.impl;

import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.interfaces.IVersionVerifier;
import co.infinum.princeofversions.mvp.interactor.POVInteractor;
import co.infinum.princeofversions.mvp.interactor.listeners.POVInteractorListener;
import co.infinum.princeofversions.network.VersionVerifierListener;

/**
 * Created by stefano on 08/07/16.
 */
public class POVInteractorImpl implements POVInteractor {

    private IVersionVerifier versionVerifier;
    private UpdateConfigLoader loader;

    public POVInteractorImpl(IVersionVerifier versionVerifier, UpdateConfigLoader loader) {
        this.versionVerifier = versionVerifier;
        this.loader = loader;
    }

    @Override
    public void checkForUpdates(final POVInteractorListener listener) {
        versionVerifier.verify(loader, new VersionVerifierListener() {
            @Override
            public void versionAvailable(VersionContext version) {
                if (version.isCurrentLessThanMinimum()) {
                    listener.onMandatoryUpdateAvailable(version);
                } else if (version.hasOptionalUpdate() && version.isCurrentLessThanOptional()) {
                    listener.onUpdateAvailable(version);
                } else {
                    listener.onNoUpdateAvailable(version);
                }
            }

            @Override
            public void versionUnavailable(String error) {
                listener.onError(error);
            }
        });
    }

    @Override
    public void cancel() {
        loader.cancel();
        versionVerifier.cancel();
    }
}
