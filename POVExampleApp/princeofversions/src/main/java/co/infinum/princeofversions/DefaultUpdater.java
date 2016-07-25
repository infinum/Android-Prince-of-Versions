package co.infinum.princeofversions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.helpers.ContextHelper;
import co.infinum.princeofversions.helpers.POVFactoryHelper;
import co.infinum.princeofversions.helpers.PrefsVersionRepository;
import co.infinum.princeofversions.helpers.parsers.JSONVersionConfigParser;
import co.infinum.princeofversions.interfaces.UpdateChecker;
import co.infinum.princeofversions.interfaces.VersionRepository;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.VersionVerifierFactory;
import co.infinum.princeofversions.mvp.presenter.POVPresenter;
import co.infinum.princeofversions.network.NetworkLoaderFactory;
import co.infinum.princeofversions.threading.ThreadVersionVerifier;

/**
 * This class represents main entry point for using library.
 * <p>
 *     Most common way to create instance of this class should be using constructor with two arguments, providing application context
 *     Context and callback class UpdaterCallback with methods for accepting result of update check.
 * </p>
 * <p>
 *     There are two forms of checkForUpdates method: default one with one argument of type String and more powerful one which accepts
 *     LoaderFactory factory interface for creating loader of type UpdateConfigLoader. That way it is possible to use library with custom
 *     implementation of loader, for configuring library to load update resource from file, string or on some other way. Default method
 *     use NetworkLoaderFactory, eg. String argument represents url from which update resource will be downloaded.
 * </p>
 * <p>
 *     Also, library has cancel option for stopping loading and check process. If checking is cancelled no result will be returned
 *     to callback.
 * </p>
 *
 * <p>
 *     There is code for most common usage of this library
 *     <pre>
 *         UpdateChecker updater = new DefaultUpdater(context, callback);
 *         LoaderFactory loaderFactory = new NetworkLoaderFactory("http://example.com/some/update.json");
 *         updater.checkForUpdates(loaderFactory); // starts checking for updates using NetworkLoader
 *     </pre>
 * </p>
 *
 * <p>
 *     Example of using library with custom loader follows bellow.
 *     <pre>
 *         UpdateChecker updater = new DefaultUpdater(context, callback);
 *         LoaderFactory loaderFactory = new FileLoaderFactory("path/to/file");
 *         updater.checkForUpdates(loaderFactory); // starts checking for updates using custom loader
 *     </pre>
 * </p>
 *
 * <p>
 *     <b>Be aware, when implementing custom loader factory always return new instance of custom loader in newInstance method!</b>
 *     This is important because of cancel functionality. There is no way once cancelled loader became uncancelled, so to support correct
 *     cancel functionality always provide new instance of loader.
 * </p>
 */
public class DefaultUpdater implements UpdateChecker {

    /**
     * Factory for creating VersionVerifier instance.
     */
    private VersionVerifierFactory factory;

    /**
     * Repository for persisting library data.
     */
    private VersionRepository repository;

    /**
     * Creates a new instance of updater for application associated with provided context.
     * @param context Context of associated application.
     */
    public DefaultUpdater(@NonNull final Context context) {
        this(context, new VersionVerifierFactory() {
            @Override
            public VersionVerifier newInstance() {
                try {
                    return new ThreadVersionVerifier(new JSONVersionConfigParser(ContextHelper.getAppVersion(context)));
                } catch (PackageManager.NameNotFoundException e) {
                    throw new IllegalArgumentException("Current version not available.");
                }
            }
        });
    }

    /**
     * Creates a new instance of updater for application associated with provided context using custom implementation of VersionVerifier.
     * <p>
     *     VersionVerifierFactory must create new VersionVerifier instance to support cancel functionality. VersionVerifier should provide
     *     implementation of loading data using in-method provided loader, transforming it to VersionContext representation and firing
     *     right event of in-method provided listener.
     * </p>
     * @param context Context of associated application.
     * @param factory Custom factory for creating VersionVerifier instances.
     */
    public DefaultUpdater(@NonNull final Context context, VersionVerifierFactory factory) {
        this(context, factory, new PrefsVersionRepository(context));
    }

    /**
     * Creates a new instance of updater for application associated with provided context using custom implementation of
     * VersionRepository for persisting library data.
     * @param context Context of associated application.
     * @param repository Custom implementation of repository for persisting library data.
     */
    public DefaultUpdater(@NonNull final Context context, VersionRepository repository) {
        this(context, new VersionVerifierFactory() {
            @Override
            public VersionVerifier newInstance() {
                try {
                    return new ThreadVersionVerifier(new JSONVersionConfigParser(ContextHelper.getAppVersion(context)));
                } catch (PackageManager.NameNotFoundException e) {
                    throw new IllegalArgumentException("Current version not available.");
                }
            }
        }, repository);
    }

    /**
     * Creates a new instance of updater for application associated with provided context using custom implementation of
     * VersionVerifierFactory and VersionRepository.
     * <p>
     *     VersionVerifierFactory must create new VersionVerifier instance to support cancel functionality. VersionVerifier should
     *     provide implementation of loading data using in-method provided loader, transforming it to VersionContext representation and
     *     firing right event of in-method provided listener.
     * </p>
     * <p>VersionRepository is custom implementation of storage for persisting library data.</p>
     * @param context Context of associated application.
     * @param factory Custom factory for creating VersionVerifier instances.
     * @param repository Custom implementation of repository for persisting library data.
     */
    public DefaultUpdater(@NonNull final Context context, VersionVerifierFactory factory,
                          VersionRepository repository) {
        ContextHelper.setContext(context);
        this.factory = factory;
        this.repository = repository;
        validateDependenciesAndThrowIllegalArgumentIfNotValid();
    }

    /**
     * Validating dependency injected through constructors.
     * @throws IllegalArgumentException if some of dependencies is not valid.
     */
    private void validateDependenciesAndThrowIllegalArgumentIfNotValid() {
        if (this.factory == null) {
            throw new IllegalArgumentException("Factory is null.");
        } else if (this.repository == null) {
            throw new IllegalArgumentException("Repository is null.");
        }
    }

    @Override
    public PrinceOfVersionsContext checkForUpdates(LoaderFactory loaderFactory, UpdaterCallback callback) {
        UpdateConfigLoader loader = loaderFactory.newInstance();
        try {
            loader.validate();
        } catch (LoaderValidationException e) {
            throw new IllegalArgumentException(e);
        }
        PrinceOfVersionsContext povContext = new PrinceOfVersionsContext(callback);
        POVPresenter presenter = POVFactoryHelper.getInstance().getPresenter(povContext, loader, factory, repository);
        povContext.setPresenter(presenter);
        presenter.checkForUpdates();
        return povContext;
    }

    /**
     * Method checks for updates from resource specified by given resource locator and notifies UpdaterCallback if there is some update
     * available or not. Object returned from method represents calling context through is available to check if update check was
     * notified or cancel update checking if not.
     * <p>Note: currently only network resources are supported.</p>
     * @param url Resource locator.
     * @param callback Callback for notifying update check result.
     * @return Calling context representing this concrete update check.
     * @throws IllegalArgumentException if resource locator is invalid.
     */
    @Override
    public PrinceOfVersionsContext checkForUpdates(String url, UpdaterCallback callback) {
        return checkForUpdates(new NetworkLoaderFactory(url), callback);
    }

}
