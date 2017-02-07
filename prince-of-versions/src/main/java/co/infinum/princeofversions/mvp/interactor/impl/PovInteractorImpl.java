package co.infinum.princeofversions.mvp.interactor.impl;

import android.os.Build;

import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.VersionVerifierListener;
import co.infinum.princeofversions.mvp.interactor.PovInteractor;
import co.infinum.princeofversions.mvp.interactor.listeners.PovInteractorListener;

public class PovInteractorImpl implements PovInteractor {

    private VersionVerifier versionVerifier;

    private UpdateConfigLoader loader;

    public PovInteractorImpl(VersionVerifier versionVerifier, UpdateConfigLoader loader) {
        this.versionVerifier = versionVerifier;
        this.loader = loader;
    }

    @Override
    public void checkForUpdates(final PovInteractorListener listener) {
        versionVerifier.verify(loader, new VersionVerifierListener() {
                    @Override
                    public void versionAvailable(VersionContext version) {
                        if (version.isCurrentLessThanMinimum()) {
                            if (!version.hasOptionalUpdate()) {
                                listener.onMandatoryUpdateAvailable(version);
                            } else {
                                if (version.getOptionalUpdate().getLastMinSdk() <= Build.VERSION.SDK_INT) {
                                    listener.onMandatoryUpdateAvailable(version);
                                } else {
                                    listener.onNoUpdateAvailable(version);
                                }
                            }
                        } else if (version.hasOptionalUpdate() && version.isCurrentLessThanOptional()) {
                            listener.onUpdateAvailable(version);
                        } else {
                            listener.onNoUpdateAvailable(version);
                        }
                    }

                    @Override
                    public void versionUnavailable(@ErrorCode int error) {
                        listener.onError(error);
                    }
                }
        );
    }

    @Override
    public void cancel() {
        loader.cancel();
        versionVerifier.cancel();
    }
}
