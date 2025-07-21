package co.infinum.princeofversions

import android.content.Context
import androidx.annotation.VisibleForTesting
import java.util.concurrent.Executor

/**
 * This class represents main entry point for using library.
 *
 * Most common way to create instance of this class should be using [Builder] or constructor with [Context] argument.
 *
 * To check if update exists you can use two different approaches: synchronous and asynchronous.
 *
 * Depending on used approach there are several versions of checkForUpdates method.
 * Synchronous execution is possible using checkForUpdates methods with one parameter ([Loader] or [String] as URL from which
 * update configuration will be downloaded.
 * Asynchronous execution expects one more parameter: [UpdaterCallback] callback through which results will be notified. Also,
 * there you can specify custom [Executor], class which should run process, usually on background thread.
 * With this approach checkForUpdates method returns [PrinceOfVersionsCancelable] object which you can use to cancel request.
 *
 * Here is code for most common usage of this library
 * ```
 * PrinceOfVersions updater = new PrinceOfVersions(context);
 * PrinceOfVersionsCancelable call = updater.checkForUpdates("[http://example.com/some/update.json](http://example.com/some/update.json)", callback); // starts
 * checking
 * for update
 * ```
 */
class PrinceOfVersions private constructor(
    private val presenter: Presenter,
    private val appConfig: ApplicationConfiguration,
    private val callbackExecutor: Executor
) {

    /**
     * Creates a [PrinceOfVersions] instance using the provided [Context] and default dependencies.
     *
     * @param context The context used for checking the application version and for default storage.
     */
    constructor(context: Context) : this(
        presenter = PresenterImpl(
            InteractorImpl(createDefaultParser()),
            createDefaultStorage(context)
        ),
        appConfig = createAppConfig(context),
        callbackExecutor = createDefaultCallbackExecutor()
    )

    @VisibleForTesting
    internal constructor(storage: Storage, callbackExecutor: Executor, appConfig: ApplicationConfiguration) : this(
        presenter = PresenterImpl(
            InteractorImpl(createDefaultParser()),
            storage
        ),
        appConfig = appConfig,
        callbackExecutor = callbackExecutor
    )

    @VisibleForTesting
    internal constructor(
        storage: Storage,
        callbackExecutor: Executor,
        appConfig: ApplicationConfiguration,
        checkers: Map<String, RequirementChecker>
    ) : this(
        presenter = PresenterImpl(
            InteractorImpl(createMockedParser(checkers)),
            storage
        ),
        appConfig = appConfig,
        callbackExecutor = callbackExecutor
    )

    /**
     * Starts an asynchronous check for an update from a URL.
     *
     * @param url The URL from which the update configuration will be loaded.
     * @param callback The callback to notify with the result.
     * @return An instance through which the call can be canceled.
     */
    fun checkForUpdates(url: String, callback: UpdaterCallback): PrinceOfVersionsCancelable {
        return checkForUpdates(PrinceOfVersionsDefaultExecutor(), NetworkLoader(url), callback)
    }

    /**
     * Starts an asynchronous check for an update using a custom [Loader].
     *
     * @param loader The instance for loading the update configuration resource.
     * @param callback The callback to notify with the result.
     * @return An instance through which the call can be canceled.
     */
    fun checkForUpdates(loader: Loader, callback: UpdaterCallback): PrinceOfVersionsCancelable {
        return checkForUpdates(PrinceOfVersionsDefaultExecutor(), loader, callback)
    }

    /**
     * Starts an asynchronous check for an update from a URL on a custom [Executor].
     *
     * @param executor The instance for running the check call.
     * @param url The URL from which the update configuration will be loaded.
     * @param callback The callback to notify with the result.
     * @return An instance through which the call can be canceled.
     */
    fun checkForUpdates(executor: Executor, url: String, callback: UpdaterCallback): PrinceOfVersionsCancelable {
        return checkForUpdates(executor, NetworkLoader(url), callback)
    }

    /**
     * Starts an asynchronous check for an update using a custom [Loader] on a custom [Executor].
     *
     * @param executor The instance for running the check call.
     * @param loader The instance for loading the update configuration resource.
     * @param callback The callback to notify with the result.
     * @return An instance through which the call can be canceled.
     */
    fun checkForUpdates(executor: Executor, loader: Loader, callback: UpdaterCallback): PrinceOfVersionsCancelable {
        return checkForUpdatesInternal(executor, loader, ExecutorUpdaterCallback(callback, callbackExecutor))
    }

    @VisibleForTesting
    internal fun checkForUpdatesInternal(executor: Executor, loader: Loader, callback: UpdaterCallback): PrinceOfVersionsCancelable {
        return presenter.check(loader, executor, callback, appConfig)
    }

    /**
     * Starts a synchronous check for an update from a URL.
     *
     * @param url The URL from which the update configuration will be loaded.
     * @return The result of the update check.
     * @throws Throwable if an error occurs.
     */
    @Throws(Throwable::class)
    fun checkForUpdates(url: String): UpdateResult {
        return checkForUpdates(NetworkLoader(url))
    }

    /**
     * Starts a synchronous check for an update using a custom [Loader].
     *
     * @param loader The instance for loading the update configuration resource.
     * @return The result of the update check.
     * @throws Throwable if an error occurs.
     */
    @Throws(Throwable::class)
    fun checkForUpdates(loader: Loader): UpdateResult {
        return presenter.check(loader, appConfig)
    }

    /**
     * Creates a new call object that will load the configuration from the specified URL.
     *
     * @param url The URL from which the update configuration will be loaded.
     * @return A call with the ability to execute or enqueue the check.
     */
    fun newCall(url: String): PrinceOfVersionsCall {
        return newCall(NetworkLoader(url))
    }

    /**
     * Creates a new call object that will load the configuration using the specified [Loader].
     *
     * @param loader The instance for loading the update configuration resource.
     * @return A call with the ability to execute or enqueue the check.
     */
    fun newCall(loader: Loader): PrinceOfVersionsCall {
        return UpdaterCall(this, loader)
    }

    /**
     * A helper class for building a [PrinceOfVersions] object.
     * All methods are optional.
     */
    class Builder {

        private val requirementCheckers = mutableMapOf<String, RequirementChecker>()
        private var configurationParser: ConfigurationParser? = null
        private var storage: Storage? = null
        private var appConfig: ApplicationConfiguration? = null
        private var callbackExecutor: Executor? = null

        /**
         * Sets a custom configuration parser.
         */
        fun withParser(configurationParser: ConfigurationParser) = apply {
            this.configurationParser = configurationParser
        }

        /**
         * Sets a custom storage implementation.
         */
        fun withStorage(storage: Storage) = apply {
            this.storage = storage
        }

        /**
         * Sets a custom callback executor.
         */
        fun withCallbackExecutor(callbackExecutor: Executor?) = apply {
            this.callbackExecutor = callbackExecutor
        }

        /**
         * Adds a custom requirements checker.
         */
        fun addRequirementsChecker(key: String, requirementsChecker: RequirementChecker) = apply {
            this.requirementCheckers[key] = requirementsChecker
        }

        /**
         * Removes a custom requirements checker.
         */
        fun removeRequirementsChecker(key: String) = apply {
            this.requirementCheckers.remove(key)
        }

        @VisibleForTesting
        internal fun withAppConfig(appConfig: ApplicationConfiguration) = apply {
            this.appConfig = appConfig
        }

        /**
         * Creates the [PrinceOfVersions] instance using the configured values.
         */
        fun build(context: Context): PrinceOfVersions {
            val finalParser = this.configurationParser ?: createDefaultParser(requirementCheckers)
            val finalStorage = this.storage ?: createDefaultStorage(context)
            val finalCallbackExecutor = this.callbackExecutor ?: createDefaultCallbackExecutor()
            val finalAppConfig = this.appConfig ?: createAppConfig(context)

            return PrinceOfVersions(
                presenter = PresenterImpl(InteractorImpl(finalParser), finalStorage),
                appConfig = finalAppConfig,
                callbackExecutor = finalCallbackExecutor
            )
        }

        /**
         * Creates the [PrinceOfVersions] instance using the configured values.
         * Note: Both storage and application configuration must be set to use this method.
         */
        @VisibleForTesting
        internal fun build(): PrinceOfVersions {
            if (storage == null || appConfig == null) {
                throw UnsupportedOperationException(
                    "You must define storage and application configuration if you don't provide Context."
                )
            }
            val finalParser = this.configurationParser ?: createDefaultParser(requirementCheckers)
            val finalCallbackExecutor = this.callbackExecutor ?: createDefaultCallbackExecutor()

            return PrinceOfVersions(
                presenter = PresenterImpl(InteractorImpl(finalParser), storage!!),
                appConfig = appConfig!!,
                callbackExecutor = finalCallbackExecutor
            )
        }
    }

    private companion object {
        private fun createDefaultParser(requirementCheckers: MutableMap<String, RequirementChecker>): ConfigurationParser {
            requirementCheckers[PrinceOfVersionsDefaultRequirementsChecker.KEY] = PrinceOfVersionsDefaultRequirementsChecker()
            return JsonConfigurationParser(PrinceOfVersionsRequirementsProcessor(requirementCheckers))
        }

        private fun createDefaultParser(): ConfigurationParser {
            return createDefaultParser(mutableMapOf())
        }

        private fun createMockedParser(requirementCheckers: Map<String, RequirementChecker>): ConfigurationParser {
            return JsonConfigurationParser(PrinceOfVersionsRequirementsProcessor(requirementCheckers))
        }

        private fun createDefaultStorage(context: Context): Storage {
            return PrinceOfVersionsDefaultNamedPreferenceStorage(context)
        }

        private fun createAppConfig(context: Context): ApplicationConfiguration {
            return ApplicationConfigurationImpl(context)
        }

        private fun createDefaultCallbackExecutor(): Executor {
            return PrinceOfVersionsCallbackExecutor()
        }
    }
}
