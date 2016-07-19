package co.infinum.princeofversions.interfaces;

import co.infinum.princeofversions.callbacks.UpdaterCallback;

/**
 * Created by stefano on 08/07/16.
 */
public interface UpdateChecker {

    void checkForUpdates(String url, UpdaterCallback uc);
}
