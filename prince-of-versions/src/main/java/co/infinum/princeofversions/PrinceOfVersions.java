package co.infinum.princeofversions;

import android.content.Context;

/**
 * This class represents main entry point for using library.
 * <p>
 * Most common way to create instance of this class should be using constructor with two arguments, providing application context
 * Context and callback class UpdaterCallback with methods for accepting result of update check.
 * </p>
 * <p>
 * There are two forms of checkForUpdates method: default one with one argument of type String and more powerful one which accepts
 * LoaderFactory factory interface for creating loader of type UpdateConfigLoader. That way it is possible to use library with custom
 * implementation of loader, for configuring library to load update resource from file, string or on some other way. Default method
 * use NetworkLoaderFactory, eg. String argument represents url from which update resource will be downloaded.
 * </p>
 * <p>
 * Also, library has cancel option for stopping loading and check process. If checking is cancelled no result will be returned
 * to callback.
 * </p>
 *
 * There is code for most common usage of this library
 * <pre>
 *         UpdateChecker updater = new DefaultUpdater(context, callback);
 *         LoaderFactory loaderFactory = new NetworkLoaderFactory("http://example.com/some/update.json");
 *         updater.checkForUpdates(loaderFactory); // starts checking for updates using NetworkLoader
 *     </pre>
 *
 * Example of using library with custom loader follows bellow.
 * <pre>
 *         UpdateChecker updater = new DefaultUpdater(context, callback);
 *         LoaderFactory loaderFactory = new FileLoaderFactory("path/to/file");
 *         updater.checkForUpdates(loaderFactory); // starts checking for updates using custom loader
 *     </pre>
 *
 * <p>
 * <b>Be aware, when implementing custom loader factory always return new instance of custom loader in newInstance method!</b>
 * This is important because of cancel functionality. There is no way once cancelled loader became uncancelled, so to support correct
 * cancel functionality always provide new instance of loader.
 * </p>
 */
public class PrinceOfVersions {

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

    private Presenter presenter;

    private ApplicationConfiguration appConfig;

    public PrinceOfVersions(Context context) {
        this(createDefaultParser(), createDefaultVersionParser(), createDefaultStorage(context), createAppConfig(context));
    }

    private PrinceOfVersions(Parser parser, VersionParser versionParser, Storage storage, ApplicationConfiguration appConfig) {
        this.presenter = new PresenterImpl(
                new InteractorImpl(parser, versionParser),
                storage
        );
        this.appConfig = appConfig;
    }

    public PrinceOfVersionsCall checkForUpdates(String url, UpdaterCallback callback) {
        return checkForUpdates(new PrinceOfVersionsDefaultExecutor(), new NetworkLoader(url), callback);
    }

    public PrinceOfVersionsCall checkForUpdates(Loader loader, UpdaterCallback callback) {
        return checkForUpdates(new PrinceOfVersionsDefaultExecutor(), loader, callback);
    }

    public PrinceOfVersionsCall checkForUpdates(Executor executor, String url, UpdaterCallback callback) {
        return checkForUpdates(executor, new NetworkLoader(url), callback);
    }

    public PrinceOfVersionsCall checkForUpdates(Executor executor, Loader loader, UpdaterCallback callback) {
        return presenter.check(loader, executor, new UiUpdaterCallback(callback), appConfig);
    }

    public Result checkForUpdates(String url) throws Throwable {
        return checkForUpdates(new NetworkLoader(url));
    }

    public Result checkForUpdates(Loader loader) throws Throwable {
        return presenter.check(loader, appConfig);
    }

    public static class Builder {

        private Parser parser;

        private Storage storage;

        private VersionParser versionParser;

        public Parser getParser() {
            return parser;
        }

        public Builder withParser(Parser parser) {
            this.parser = parser;
            return this;
        }

        public Storage getStorage() {
            return storage;
        }

        public Builder withStorage(Storage storage) {
            this.storage = storage;
            return this;
        }

        public VersionParser getVersionParser() {
            return versionParser;
        }

        public Builder withVersionParser(VersionParser versionParser) {
            this.versionParser = versionParser;
            return this;
        }

        public PrinceOfVersions build(Context context) {
            return new PrinceOfVersions(
                    parser != null ? parser : createDefaultParser(),
                    versionParser != null ? versionParser : createDefaultVersionParser(),
                    storage != null ? storage : createDefaultStorage(context),
                    createAppConfig(context)
            );
        }
    }

}
