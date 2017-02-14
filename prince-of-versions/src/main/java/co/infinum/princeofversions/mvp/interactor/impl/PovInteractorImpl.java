package co.infinum.princeofversions.mvp.interactor.impl;

import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.interfaces.SdkVersionProvider;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.VersionVerifierListener;
import co.infinum.princeofversions.mvp.interactor.PovInteractor;
import co.infinum.princeofversions.mvp.interactor.listeners.PovInteractorListener;

public class PovInteractorImpl implements PovInteractor {

    private VersionVerifier versionVerifier;

    private UpdateConfigLoader loader;

    private SdkVersionProvider sdkVersionProvider;

    public PovInteractorImpl(VersionVerifier versionVerifier, UpdateConfigLoader loader, SdkVersionProvider sdkVersionProvider) {
        this.versionVerifier = versionVerifier;
        this.loader = loader;
        this.sdkVersionProvider = sdkVersionProvider;
    }

    @Override
    public void checkForUpdates(final PovInteractorListener listener) {
        versionVerifier.verify(loader, new VersionVerifierListener() {
                    @Override
                    public void versionAvailable(VersionContext version) {
                        if (version.isCurrentLessThanMinimum()) {
                            //If it's a mandatory update, we check if user's phone supports the minSdk
                            if (version.getMinimumVersionMinSdk() <= sdkVersionProvider.getSdkInt()) {
                                //If it does -> notify the user there's a new version of the app available
                                listener.onMandatoryUpdateAvailable(version);
                            } else {

                                //This covers the edge case where user cannot install mandatory update because of minSdk of mandatory
                                //update but he can install the latest update which has lower minSdk needed thus bypassing mandatory
                                // update.
                                if (version.hasOptionalUpdate()
                                        && version.getOptionalUpdate().getNewMinSdk() <= sdkVersionProvider.getSdkInt()
                                        && version.isCurrentLessThanOptional()) {
                                    listener.onUpdateAvailable(version);
                                } else {
                                    listener.onNoUpdateAvailable(version);
                                }
                            }

                        } else if (version.hasOptionalUpdate() && version.isCurrentLessThanOptional()
                                && version.getOptionalUpdate().getNewMinSdk() <= sdkVersionProvider.getSdkInt()) {
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
