package co.infinum.princeofversions.mvp.interactor.impl;

import co.infinum.princeofversions.mvp.interactor.POVInteractor;
import co.infinum.princeofversions.network.VersionVerifier;

/**
 * Created by stefano on 08/07/16.
 */
public class POVInteractorImpl implements POVInteractor {

    @Override
    public void checkForUpdates() {
        new VersionVerifier().execute();
    }
}
