package co.infinum.princeofversions;

/**
 * This class loads resource, parse it and determine whether there is an update or not
 */
public interface Interactor {

    /**
     * Check if there is an update by loading update resource, parsing it and comparing data to application configuration.
     *
     * @param loader    Loader instance which will be used for loading resource
     * @param appConfig Application configuration
     * @return Result data
     * @throws Throwable if some error happens during check
     */
    CheckResult check(Loader loader, ApplicationConfiguration appConfig) throws Throwable;

}
