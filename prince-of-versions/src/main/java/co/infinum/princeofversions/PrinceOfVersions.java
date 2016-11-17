package co.infinum.princeofversions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.exceptions.LoaderValidationException;
import co.infinum.princeofversions.helpers.ContextHelper;
import co.infinum.princeofversions.helpers.PovFactoryHelper;
import co.infinum.princeofversions.helpers.PrefsVersionRepository;
import co.infinum.princeofversions.helpers.parsers.JsonVersionConfigParser;
import co.infinum.princeofversions.helpers.parsers.ParserFactory;
import co.infinum.princeofversions.helpers.parsers.VersionConfigParser;
import co.infinum.princeofversions.interfaces.VersionRepository;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.VersionVerifierFactory;
import co.infinum.princeofversions.loaders.factories.NetworkLoaderFactory;
import co.infinum.princeofversions.mvp.presenter.PovPresenter;
import co.infinum.princeofversions.threading.ExecutorServiceVersionVerifier;

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
     *
     * @param context Context of associated application.
     */
    public PrinceOfVersions(@NonNull final Context context) {
        this(context.getApplicationContext(), createDefaultVersionVerifierFactory(new ParserFactory() {
            @Override
            public VersionConfigParser newInstance() {
                try {
                    return new JsonVersionConfigParser(ContextHelper.getAppVersion(context.getApplicationContext()));
                } catch (PackageManager.NameNotFoundException e) {
                    throw new IllegalArgumentException("Current version not available.");
                }
            }
        }));
    }

    /**
     * Creates a new instance of updater for application associated with provided context using custom parser implementation.
     *
     * @param context       Context of associated application.
     * @param parserFactory Factory for creating custom parser for parsing loaded content.
     */
    public PrinceOfVersions(@NonNull final Context context, ParserFactory parserFactory) {
        this(context.getApplicationContext(), createDefaultVersionVerifierFactory(parserFactory));
    }

    /**
     * Creates a new instance of updater for application associated with provided context using custom implementation of VersionVerifier.
     * <p>
     * VersionVerifierFactory must create new VersionVerifier instance to support cancel functionality. VersionVerifier should provide
     * implementation of loading data using in-method provided loader, transforming it to VersionContext representation and firing
     * right event of in-method provided listener.
     * </p>
     *
     * @param context Context of associated application.
     * @param factory Custom factory for creating VersionVerifier instances.
     */
    public PrinceOfVersions(@NonNull final Context context, VersionVerifierFactory factory) {
        this(context.getApplicationContext(), factory, new PrefsVersionRepository(context.getApplicationContext()));
    }

    /**
     * Creates a new instance of updater for application associated with provided context using custom implementation of
     * VersionRepository for persisting library data.
     *
     * @param context    Context of associated application.
     * @param repository Custom implementation of repository for persisting library data.
     */
    public PrinceOfVersions(@NonNull final Context context, VersionRepository repository) {
        this(context.getApplicationContext(), createDefaultVersionVerifierFactory(new ParserFactory() {
            @Override
            public VersionConfigParser newInstance() {
                try {
                    return new JsonVersionConfigParser(ContextHelper.getAppVersion(context.getApplicationContext()));
                } catch (PackageManager.NameNotFoundException e) {
                    throw new IllegalArgumentException("Current version not available.");
                }
            }
        }), repository);
    }

    /**
     * Creates a new instance of updater for application associated with provided context using custom implementation of parser and custom
     * implementation of VersionRepository for persisting library data.
     *
     * @param context       Context of associated application.
     * @param parserFactory Factory for creating custom parser for parsing loaded content.
     * @param repository    Custom implementation of repository for persisting library data.
     */
    public PrinceOfVersions(@NonNull final Context context, ParserFactory parserFactory, VersionRepository repository) {
        this(context.getApplicationContext(), createDefaultVersionVerifierFactory(parserFactory), repository);
    }

    /**
     * Creates a new instance of updater for application associated with provided context using custom implementation of
     * VersionVerifierFactory and VersionRepository.
     * <p>
     * VersionVerifierFactory must create new VersionVerifier instance to support cancel functionality. VersionVerifier should
     * provide implementation of loading data using in-method provided loader, transforming it to VersionContext representation and
     * firing right event of in-method provided listener.
     * </p>
     * <p>VersionRepository is custom implementation of storage for persisting library data.</p>
     *
     * @param context    Context of associated application.
     * @param factory    Custom factory for creating VersionVerifier instances.
     * @param repository Custom implementation of repository for persisting library data.
     */
    public PrinceOfVersions(@NonNull final Context context, VersionVerifierFactory factory,
            VersionRepository repository) {
        this.factory = factory;
        this.repository = repository;
        validateDependencies();
    }

    /**
     * Utility method for creating default version verifier using given factory for creating concrete parser.
     *
     * @param factory Factory for creating concrete parser.
     * @return New instance of VersionVerifier class.
     */
    public static VersionVerifier createDefaultVersionVerifier(ParserFactory factory) {
        return new ExecutorServiceVersionVerifier(factory.newInstance());
    }

    /**
     * Utility method for creating default version verifier factory using given parser factory.
     *
     * @param factory Factory for creating concrete parser.
     * @return New instance of VersionVerifierFactory class.
     */
    public static VersionVerifierFactory createDefaultVersionVerifierFactory(final ParserFactory factory) {
        return new VersionVerifierFactory() {
            @Override
            public VersionVerifier newInstance() {
                return createDefaultVersionVerifier(factory);
            }
        };
    }

    /**
     * Validating dependency injected through constructors.
     *
     * @throws IllegalArgumentException if some of dependencies is not valid.
     */
    private void validateDependencies() {
        if (this.factory == null) {
            throw new IllegalArgumentException("Factory is null.");
        } else if (this.repository == null) {
            throw new IllegalArgumentException("Repository is null.");
        }
    }

    /**
     * Method checks for updates from resource provided by given LoaderFactory and notifies UpdaterCallback if there is some update
     * available or not. Object returned from method represents calling context through is available to check if update check was
     * notified or cancel update checking if not.
     * <p>
     * After creating new loader from LoaderFactory its validate method is called which throws exception if loader is invalid.
     * </p>
     *
     * @param loaderFactory Representation of custom resource loader.
     * @param callback      Callback for notifying update check result.
     * @return Calling context representing this concrete update check.
     * @throws IllegalArgumentException if newly created loader is invalid.
     */
    public UpdaterResult checkForUpdates(LoaderFactory loaderFactory, UpdaterCallback callback) {
        UpdateConfigLoader loader = loaderFactory.newInstance();
        try {
            loader.validate();
        } catch (LoaderValidationException e) {
            throw new IllegalArgumentException(e);
        }
        UpdaterResult povContext = new UpdaterResult(callback);
        PovPresenter presenter = PovFactoryHelper.getInstance().getPresenter(povContext, loader, factory, repository);
        povContext.setPresenter(presenter);
        presenter.checkForUpdates();
        return povContext;
    }

    /**
     * Method checks for updates from resource specified by given resource locator and notifies UpdaterCallback if there is some update
     * available or not. Object returned from method represents calling context through is available to check if update check was
     * notified or cancel update checking if not.
     * <p>Note: currently only network resources are supported.</p>
     *
     * @param url      Resource locator.
     * @param callback Callback for notifying update check result.
     * @return Calling context representing this concrete update check.
     * @throws IllegalArgumentException if resource locator is invalid.
     */
    public UpdaterResult checkForUpdates(String url, UpdaterCallback callback) {
        return checkForUpdates(new NetworkLoaderFactory(url), callback);
    }

}
