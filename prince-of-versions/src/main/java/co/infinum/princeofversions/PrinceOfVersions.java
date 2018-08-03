package co.infinum.princeofversions;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

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
 *
 * Here is code for most common usage of this library
 * <pre>
 *         {@link PrinceOfVersions} updater = new {@link PrinceOfVersions}(context);
 *         {@link PrinceOfVersionsCancelable} call = updater.checkForUpdates("http://example.com/some/update.json", callback); // starts
 *         checking
 * for update
 * </pre>
 */
public class PrinceOfVersions {

    private Presenter presenter;
    private ApplicationConfiguration appConfig;

    /**
     * Creates {@link PrinceOfVersions} using provided {@link Context}.
     *
     * @param context context which will be used for checking application version.
     */
    public PrinceOfVersions(Context context) {
        this(createDefaultParser(), createDefaultVersionParser(), createDefaultStorage(context), createAppConfig(context));
    }

    @VisibleForTesting
    public PrinceOfVersions(Storage storage, ApplicationConfiguration appConfig) {
        this(createDefaultParser(), createDefaultVersionParser(), storage, appConfig);
    }

    private PrinceOfVersions(Parser parser, VersionParser versionParser, Storage storage, ApplicationConfiguration appConfig) {
        this.presenter = new PresenterImpl(
            new InteractorImpl(parser, versionParser),
            storage
        );
        this.appConfig = appConfig;
    }

    private static Parser createDefaultParser() {
        return new JsonParser();
    }

    private static Storage createDefaultStorage(Context context) {
        return new PrinceOfVersionsDefaultStorage(context);
    }

    private static VersionParser createDefaultVersionParser() {
        return new PrinceOfVersionsDefaultVersionParser();
    }

    private static ApplicationConfiguration createAppConfig(Context context) {
        return new ApplicationConfigurationImpl(context);
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
        return checkForUpdatesInternal(executor, loader, new UiUpdaterCallback(callback));
    }

    @VisibleForTesting
    public PrinceOfVersionsCancelable checkForUpdatesInternal(Executor executor, Loader loader, UpdaterCallback callback) {
        return presenter.check(loader, executor, callback, appConfig);
    }

    /**
     * Start synchronous check for update using provided URL as {@link String}.
     *
     * @param url Url from where update config will be loaded.
     * @return result of update check.
     * @throws Throwable if error occurred.
     */
    public Result checkForUpdates(String url) throws Throwable {
        return checkForUpdates(new NetworkLoader(url));
    }

    /**
     * Start synchronous check for update using provided URL as {@link String}.
     *
     * @param loader Instance for loading update config resource.
     * @return result of update check.
     * @throws Throwable if error occurred.
     */
    public Result checkForUpdates(Loader loader) throws Throwable {
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
     */
    public static class Builder {

        private Parser parser;

        private Storage storage;

        private VersionParser versionParser;

        private ApplicationConfiguration appConfig;

        public Builder withParser(Parser parser) {
            this.parser = parser;
            return this;
        }

        public Builder withStorage(Storage storage) {
            this.storage = storage;
            return this;
        }

        public Builder withVersionParser(VersionParser versionParser) {
            this.versionParser = versionParser;
            return this;
        }

        @VisibleForTesting
        public Builder withAppConfig(ApplicationConfiguration appConfig) {
            this.appConfig = appConfig;
            return this;
        }

        public PrinceOfVersions build(Context context) {
            return new PrinceOfVersions(
                parser != null ? parser : createDefaultParser(),
                versionParser != null ? versionParser : createDefaultVersionParser(),
                storage != null ? storage : createDefaultStorage(context),
                appConfig != null ? appConfig : createAppConfig(context)
            );
        }

        @VisibleForTesting
        public PrinceOfVersions build() {
            if (storage == null || appConfig == null) {
                throw new UnsupportedOperationException(
                    "You must define storage and application configuration if you not provide Context.");
            }
            return new PrinceOfVersions(
                parser != null ? parser : createDefaultParser(),
                versionParser != null ? versionParser : createDefaultVersionParser(),
                storage,
                appConfig
            );
        }
    }
}
