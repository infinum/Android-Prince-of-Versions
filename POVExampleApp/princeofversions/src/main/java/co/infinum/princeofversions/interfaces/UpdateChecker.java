package co.infinum.princeofversions.interfaces;

import co.infinum.princeofversions.LoaderFactory;
import co.infinum.princeofversions.PrinceOfVersionsContext;
import co.infinum.princeofversions.callbacks.UpdaterCallback;

/**
 *  Implementation of this interface defines logic for checking for updates.
 *  Interface provides two methods for updates checking. Both methods use UpdaterCallback interface for providing results back to user.
 *  <p>
 *      More generic one accepting LoaderFactory as argument and using it for loading update configuration file resource. Second one
 *      using String representation of resource locator for loading it.
 *  </p>
 */
public interface UpdateChecker {

    /**
     * Method checks for updates from resource provided by given LoaderFactory and notifies UpdaterCallback if there is some update
     * available or not. Object returned from method represents calling context through is available to check if update check was
     * notified or cancel update checking if not.
     * <p>
     *     After creating new loader from LoaderFactory its validate method is called which throws exception if loader is invalid.
     * </p>
     * @param factory Representation of custom resource loader.
     * @param callback Callback for notifying update check result.
     * @return Calling context representing this concrete update check.
     * @throws IllegalArgumentException if newly created loader is invalid.
     */
    PrinceOfVersionsContext checkForUpdates(LoaderFactory factory, UpdaterCallback callback);

    /**
     * Method checks for updates from resource specified by given resource locator and notifies UpdaterCallback if there is some update
     * available or not. Object returned from method represents calling context through is available to check if update check was
     * notified or cancel update checking if not.
     * @param url Resource locator.
     * @param callback Callback for notifying update check result.
     * @return Calling context representing this concrete update check.
     * @throws IllegalArgumentException if resource locator is invalid.
     */
    PrinceOfVersionsContext checkForUpdates(String url, UpdaterCallback callback);

}
