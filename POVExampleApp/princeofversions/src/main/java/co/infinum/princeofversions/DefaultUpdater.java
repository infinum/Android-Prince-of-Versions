package co.infinum.princeofversions;

import android.content.Context;
import android.content.pm.PackageManager;

import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.helpers.ContextHelper;
import co.infinum.princeofversions.helpers.POVFactoryHelper;
import co.infinum.princeofversions.helpers.PrefsVersionRepository;
import co.infinum.princeofversions.helpers.parsers.JSONVersionConfigParser;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.UpdateChecker;
import co.infinum.princeofversions.interfaces.VersionRepository;
import co.infinum.princeofversions.mvp.presenter.POVPresenter;
import co.infinum.princeofversions.mvp.view.POVView;
import co.infinum.princeofversions.network.NetworkLoaderFactory;
import co.infinum.princeofversions.threading.ThreadVersionVerifier;

/**
 * <p>This class represents main entry point for using library.</p>
 * <p>Most common way to create instance of this class should be using constructor with two arguments,
 * providing application context Context and callback class UpdaterCallback with methods for accepting result of update check.</p>
 * <p>There are two forms of checkForUpdates method: default one with one argument of type String and more powerful one which accepts
 * LoaderFactory factory interface for creating loader of type UpdateConfigLoader. That way it is possible to use library with custom
 * implementation of loader, for configuring library to load update resource from file, string or on some other way.
 * Default method use NetworkLoaderFactory, eg. String argument represents url from which update resource will be downloaded.</p>
 * <p>Also, library has cancel option for stopping loading and check process. If checking is cancelled no result will be returned
 * to callback.</p>
 *
 * <p>There is code for most common usage of this library
 * <pre>
 *     UpdateChecker updater = new DefaultUpdater(context, callback);
 *     LoaderFactory loaderFactory = new NetworkLoaderFactory("http://example.com/some/update.json");
 *     updater.checkForUpdates(loaderFactory); // starts checking for updates via NetworkLoader
 * </pre></p>
 *
 * <p>Example of using library with custom loader follows bellow:
 * <pre>
 *     UpdateChecker updater = new DefaultUpdater(context, callback);
 *     LoaderFactory loaderFactory = new FileLoaderFactory("path/to/file");
 *     updater.checkForUpdates(loaderFactory); // starts checking for updates via custom loader
 * </pre></p>
 *
 * <p><b>Be aware, when implementing custom loader factory always return new instance of custom loader in newInstance method!</b>
 * This is important because of cancel functionality. There is no way once cancelled loader became uncancelled, so to support
 * correct cancel functionality always provide new instance of loader.</p>
 */
public class DefaultUpdater implements UpdateChecker, POVView {

    private POVPresenter presenter;

    private UpdaterCallback callback;

    private POVFactoryHelper.VersionVerifierProvider factory;

    private VersionRepository repository;

    public DefaultUpdater(final Context context, final UpdaterCallback callback) {
        this(context, callback, new POVFactoryHelper.VersionVerifierProvider() {
            @Override
            public VersionVerifier get() {
                try {
                    return new ThreadVersionVerifier(new JSONVersionConfigParser(ContextHelper.getAppVersion(context)));
                } catch (PackageManager.NameNotFoundException e) {
                    throw new IllegalArgumentException("Current version not available.");
                }
            }
        });
    }

    public DefaultUpdater(final Context context, final UpdaterCallback callback, POVFactoryHelper.VersionVerifierProvider factory) {
        this(context, callback, factory, new PrefsVersionRepository(context));
    }

    public DefaultUpdater(final Context context, final UpdaterCallback callback, VersionRepository repository) {
        this(context, callback, new POVFactoryHelper.VersionVerifierProvider() {
            @Override
            public VersionVerifier get() {
                try {
                    return new ThreadVersionVerifier(new JSONVersionConfigParser(ContextHelper.getAppVersion(context)));
                } catch (PackageManager.NameNotFoundException e) {
                    throw new IllegalArgumentException("Current version not available.");
                }
            }
        }, repository);
    }

    public DefaultUpdater(final Context context, final UpdaterCallback callback, POVFactoryHelper.VersionVerifierProvider factory,
                          VersionRepository repository) {
        ContextHelper.setContext(context);
        this.callback = callback;
        this.factory = factory;
        this.repository = repository;
        validateDependenciesAndThrowIllegalArgumentIfNotValid();
    }

    private void validateDependenciesAndThrowIllegalArgumentIfNotValid() {
        if (this.callback == null) {
            throw new IllegalArgumentException("Callback is null.");
        } else if (this.factory == null) {
            throw new IllegalArgumentException("Factory is null.");
        } else if (this.repository == null) {
            throw new IllegalArgumentException("Repository is null.");
        }
    }

    @Override
    public void checkForUpdates(LoaderFactory loaderFactory) {
        UpdateConfigLoader loader = loaderFactory.newInstance();
        try {
            loader.validate();
        } catch (LoaderValidationException e) {
            throw new IllegalArgumentException(e);
        }
        presenter = POVFactoryHelper.getInstance().getPresenter(this, loader, factory, repository);
        presenter.checkForUpdates();
    }

    @Override
    public void checkForUpdates(String url) {
        checkForUpdates(new NetworkLoaderFactory(url));
    }

    @Override
    public void cancel() {
        if (presenter != null) {
            presenter.onCancel();
        }
    }

    @Override
    public void notifyMandatoryUpdate(String version) {
        callback.onNewUpdate(version, true);
    }

    @Override
    public void notifyOptionalUpdate(String version) {
        callback.onNewUpdate(version, false);
    }

    @Override
    public void notifyNoUpdate() {
        callback.onNoUpdate();
    }

    @Override
    public void notifyError(@ErrorCode int error) {
        callback.onError(error);
    }
}
