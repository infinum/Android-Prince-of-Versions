package co.infinum.princeofversions

/**
 * This class loads resource, parse it and determine whether there is an update or not.
 */
internal fun interface Interactor {
    /**
     * Check if there is an update by loading update resource, parsing it and comparing data to application configuration.
     *
     * @param loader    Loader instance which will be used for loading resource
     * @param appConfig Application configuration
     * @return Result data
     * @throws Throwable if some error happens during check
     */
    @Throws(Throwable::class)
    fun check(loader: Loader, appConfig: ApplicationConfiguration): CheckResult
}
