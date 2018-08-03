package co.infinum.princeofversions;

/**
 * This class handles update checks.
 */
public interface Presenter {

    /**
     * Start synchronous update check.
     *
     * @param loader    Object for loading data.
     * @param appConfig Configuration of application.
     * @return Update result.
     * @throws Throwable if error happens during check.
     */
    Result check(Loader loader, ApplicationConfiguration appConfig) throws Throwable;

    /**
     * Start asynchronous update check.
     *
     * @param loader    Object for loading data.
     * @param executor  Object for executing update check.
     * @param callback  Callback to which result will be notified.
     * @param appConfig Application configuration.
     * @return Call object which offers method for cancelling update check.
     */
    PrinceOfVersionsCancelable check(Loader loader, Executor executor, UpdaterCallback callback, ApplicationConfiguration appConfig);

}
