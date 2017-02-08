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
                            //If its a mandatory update, we check if the update has some optional parameters (sdk values in this case)
                            if (!version.hasOptionalUpdate()) {
                                //If it does not it means the update has no sdk restrictions set, to be more precise the restrictions
                                //haven't changed which means the user's phone supports the minimum version of the app and thus we tell
                                //listener to notify the user there's a new version of the app available
                                listener.onMandatoryUpdateAvailable(version);
                            } else {
                                //If there are some optional parts we check if the user's phone supports the minimal version of the app
                                //and its requirements
                                if (version.getOptionalUpdate().getLastMinSdk() <= Build.VERSION.SDK_INT) {
                                    //If it does -> notify the user there's a new version of the app available
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
