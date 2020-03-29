package co.infinum.princeofversions;

import android.content.Context;
import androidx.annotation.VisibleForTesting;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

/**
 * This class represents main entry point for using library.
 * <p>
 * Most common way to create instance of this class should be using {@link Builder} or constructor with {@link Context} argument.
 * </p>
 * <p>
 * To check if update exists you can use two different approaches: synchronous and asynchronous.
 * </p>
 * <p>
 * Depending on used approach there are several versions of checkForUpdates method.
 * Synchronous execution is possible using checkForUpdates methods with one parameter ({@link Loader} or {@link String} as URL from which
 * update configuration will be downloaded.
 * Asynchronous execution expects one more parameter: {@link UpdaterCallback} callback through which results will be notified. Also,
 * there you can specify custom {@link Executor}, class which should run process, usually on background thread.
 * With this approach checkForUpdates method returns {@link PrinceOfVersionsCancelable} object which you can use to cancel request.
 * </p>
 * <p>
 * Here is code for most common usage of this library
 * <pre>
 *         {@link PrinceOfVersions} updater = new {@link PrinceOfVersions}(context);
 *         {@link PrinceOfVersionsCancelable} call = updater.checkForUpdates("http://example.com/some/update.json", callback); // starts
 *         checking
 * for update
 * </pre>
 */
public final class PrinceOfVersions {

    private final Presenter presenter;
    private final ApplicationConfiguration appConfig;
    private final Executor callbackExecutor;

    /**
     * Creates {@link PrinceOfVersions} using provided {@link Context}.
     *
     * @param context context which will be used for checking application version.
     */
    public PrinceOfVersions(Context context) {
        this(createDefaultParser(), createDefaultStorage(context),
            createDefaultCallbackExecutor(),
            createAppConfig(context));
    }

    @VisibleForTesting
    PrinceOfVersions(Storage storage, Executor callbackExecutor, ApplicationConfiguration appConfig) {
        this(createDefaultParser(), storage, callbackExecutor, appConfig);
    }

    @VisibleForTesting
    PrinceOfVersions(Storage storage, Executor callbackExecutor, ApplicationConfiguration appConfig,
        Map<String, RequirementChecker> checkers) {
        this(createMockedParser(checkers), storage, callbackExecutor, appConfig);
    }

    private PrinceOfVersions(ConfigurationParser configurationParser, Storage storage,
        Executor callbackExecutor, ApplicationConfiguration appConfig) {
        this.presenter = new PresenterImpl(
            new InteractorImpl(configurationParser),
            storage
        );
        this.callbackExecutor = callbackExecutor;
        this.appConfig = appConfig;
    }

    private static ConfigurationParser createDefaultParser(Map<String, RequirementChecker> requirementCheckers) {
        requirementCheckers.put(PrinceOfVersionsDefaultRequirementsChecker.KEY, new PrinceOfVersionsDefaultRequirementsChecker());
        return new JsonConfigurationParser(new PrinceOfVersionsRequirementsProcessor(
            requirementCheckers
        ));
    }

    private static ConfigurationParser createDefaultParser() {
        return new JsonConfigurationParser(new PrinceOfVersionsRequirementsProcessor());
    }

    private static ConfigurationParser createMockedParser(Map<String, RequirementChecker> requirementCheckers) {
        return new JsonConfigurationParser(new PrinceOfVersionsRequirementsProcessor(
            requirementCheckers
        ));
    }

    private static Storage createDefaultStorage(Context context) {
        return new PrinceOfVersionsDefaultNamedPreferenceStorage(context);
    }

    private static ApplicationConfiguration createAppConfig(Context context) {
        return new ApplicationConfigurationImpl(context);
    }

    private static Executor createDefaultCallbackExecutor() {
        return new PrinceOfVersionsCallbackExecutor();
    }

    /**
     * Start asynchronous check for update using provided {@link Executor} and {@link String}. Notifies result to provided {@link
     * UpdaterCallback}.
     *
     * @param url      Url from where update config will be loaded.
     * @param callback Callback to notify result.
     * @return instance through which is possible to cancel the call.
     */
    public PrinceOfVersionsCancelable checkForUpdates(String url, UpdaterCallback callback) {
        return checkForUpdates(new PrinceOfVersionsDefaultExecutor(), new NetworkLoader(url), callback);
    }

    /**
     * Start asynchronous check for update using provided {@link Executor} and {@link Loader}. Notifies result to provided {@link
     * UpdaterCallback}.
     *
     * @param loader   Instance for loading update config resource.
     * @param callback Callback to notify result.
     * @return instance through which is possible to cancel the call.
     */
    public PrinceOfVersionsCancelable checkForUpdates(Loader loader, UpdaterCallback callback) {
        return checkForUpdates(new PrinceOfVersionsDefaultExecutor(), loader, callback);
    }

    /**
     * Start asynchronous check for update using provided {@link Executor} and {@link String}. Notifies result to provided {@link
     * UpdaterCallback}.
     *
     * @param executor Instance for running check call.
     * @param url      Url from where update config will be loaded.
     * @param callback Callback to notify result.
     * @return instance through which is possible to cancel the call.
     */
    public PrinceOfVersionsCancelable checkForUpdates(Executor executor, String url, UpdaterCallback callback) {
        return checkForUpdates(executor, new NetworkLoader(url), callback);
    }

    /**
     * Start asynchronous check for update using provided {@link Executor} and {@link Loader}. Notifies result to provided {@link
     * UpdaterCallback}.
     *
     * @param executor Instance for running check call.
     * @param loader   Instance for loading update config resource.
     * @param callback Callback to notify result.
     * @return instance through which is possible to cancel the call.
     */
    public PrinceOfVersionsCancelable checkForUpdates(Executor executor, Loader loader, UpdaterCallback callback) {
        return checkForUpdatesInternal(executor, loader, new ExecutorUpdaterCallback(callback, callbackExecutor));
    }

    @VisibleForTesting
    PrinceOfVersionsCancelable checkForUpdatesInternal(Executor executor, Loader loader, UpdaterCallback callback) {
        return presenter.check(loader, executor, callback, appConfig);
    }

    /**
     * Start synchronous check for update using provided URL as {@link String}.
     *
     * @param url Url from where update config will be loaded.
     * @return result of update check.
     * @throws Throwable if error occurred.
     */
    public UpdateResult checkForUpdates(String url) throws Throwable {
        return checkForUpdates(new NetworkLoader(url));
    }

    /**
     * Start synchronous check for update using provided URL as {@link String}.
     *
     * @param loader Instance for loading update config resource.
     * @return result of update check.
     * @throws Throwable if error occurred.
     */
    public UpdateResult checkForUpdates(Loader loader) throws Throwable {
        return presenter.check(loader, appConfig);
    }

    /**
     * Creates new call object which will load configuration from specified url.
     *
     * @param url Url from where update config will be loaded.
     * @return Call with ability to execute or enqueue the check.
     */
    public PrinceOfVersionsCall newCall(String url) {
        return newCall(new NetworkLoader(url));
    }

    /**
     * Creates new call object which will load configuration from specified url.
     *
     * @param loader Instance for loading update config resource.
     * @return Call with ability to execute or enqueue the check.
     */
    public PrinceOfVersionsCall newCall(Loader loader) {
        return new UpdaterCall(this, loader);
    }

    /**
     * Helper class for building {@link PrinceOfVersions} object.
     * All methods are optional.
     */
    public static class Builder {

        private final Map<String, RequirementChecker> requirementCheckers = new HashMap<>();

        @Nullable
        private ConfigurationParser configurationParser;
        @Nullable
        private Storage storage;
        @Nullable
        private ApplicationConfiguration appConfig;
        @Nullable
        private Executor callbackExecutor;

        /**
         * Set a new configuration parser used to parse configuration file into the model.
         *
         * @param configurationParser Configuration parser
         * @return this builder
         */
        public Builder withParser(ConfigurationParser configurationParser) {
            this.configurationParser = configurationParser;
            return this;
        }

        /**
         * Set a new implementation of the storage used to store internal metadata about update check.
         *
         * @param storage Storage implementation
         * @return this builder
         */
        public Builder withStorage(Storage storage) {
            this.storage = storage;
            return this;
        }

        /**
         * Set a new callback executor which runs callback code on specific thread.
         *
         * @param callbackExecutor Callback executor
         * @return this builder
         */
        public Builder withCallbackExecutor(@Nullable final Executor callbackExecutor) {
            this.callbackExecutor = callbackExecutor;
            return this;
        }

        /**
         * Add a new custom requirements checker that's used in process of parsing JSON
         *
         * @param requirementsChecker Requirements checker
         * @param key                 Key with which we are going to get a wanted requirement from JSON
         * @return this builder
         */
        public Builder addRequirementsChecker(String key, RequirementChecker requirementsChecker) {
            this.requirementCheckers.put(key, requirementsChecker);
            return this;
        }

        /**
         * Remove custom requirements checker that's used in process of parsing JSON
         *
         * @param key                 Key with which we are going to get a wanted requirement from JSON
         * @return this builder
         */
        public Builder removeRequirementsChecker(String key) {
            this.requirementCheckers.remove(key);
            return this;
        }

        /**
         * Set a new application configuration, used to specify data about current application version
         *
         * @param appConfig Currect application configuration
         * @return this builder
         */
        @VisibleForTesting
        Builder withAppConfig(ApplicationConfiguration appConfig) {
            this.appConfig = appConfig;
            return this;
        }

        /**
         * Create the {@link PrinceOfVersions} instance using the configured values.
         *
         * @param context Context used to extract data about current application version
         * @return PrinceOfVersions instance
         */
        public PrinceOfVersions build(Context context) {
            return new PrinceOfVersions(
                configurationParser != null ? configurationParser : createDefaultParser(requirementCheckers),
                storage != null ? storage : createDefaultStorage(context),
                callbackExecutor != null ? callbackExecutor : createDefaultCallbackExecutor(),
                appConfig != null ? appConfig : createAppConfig(context)
            );
        }

        /**
         * Create the {@link PrinceOfVersions} instance using the configured values.
         * Note: both storage and application configuration has to be set to use this method.
         *
         * @return PrinceOfVersions instance
         */
        @VisibleForTesting
        PrinceOfVersions build() {
            if (storage == null || appConfig == null) {
                throw new UnsupportedOperationException(
                    "You must define storage and application configuration if you don't provide Context.");
            }
            return new PrinceOfVersions(
                configurationParser != null ? configurationParser : createDefaultParser(requirementCheckers),
                storage,
                callbackExecutor != null ? callbackExecutor : createDefaultCallbackExecutor(),
                appConfig
            );
        }
    }
}
