package co.infinum.princeofversions.mvp.interactor;

import co.infinum.princeofversions.mvp.interactor.listeners.PovInteractorListener;

/**
 * Created by stefano on 08/07/16.
 */
public interface PovInteractor {

    void checkForUpdates(PovInteractorListener listener);

    void cancel();
}
