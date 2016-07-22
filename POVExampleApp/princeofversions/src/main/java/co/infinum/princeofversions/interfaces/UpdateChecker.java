package co.infinum.princeofversions.interfaces;

import co.infinum.princeofversions.LoaderFactory;

/**
 * Created by stefano on 08/07/16.
 */
public interface UpdateChecker {

    void checkForUpdates(LoaderFactory factory);

    void cancel();
}
