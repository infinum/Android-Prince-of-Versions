package co.infinum.princeofversions.mvp.interactor;

import co.infinum.princeofversions.mvp.interactor.listeners.POVInteractorListener;

/**
 * Created by stefano on 08/07/16.
 */
public interface POVInteractor {

    void checkForUpdates(POVInteractorListener listener);

    void cancel();
}
