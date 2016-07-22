package co.infinum.princeofversions;

import android.content.Context;
import android.content.pm.PackageManager;

import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.helpers.ContextHelper;
import co.infinum.princeofversions.helpers.POVFactoryHelper;
import co.infinum.princeofversions.helpers.PrefsVersionRepository;
import co.infinum.princeofversions.helpers.parsers.JSONVersionConfigParser;
import co.infinum.princeofversions.interfaces.IVersionVerifier;
import co.infinum.princeofversions.interfaces.UpdateChecker;
import co.infinum.princeofversions.interfaces.VersionRepository;
import co.infinum.princeofversions.mvp.presenter.POVPresenter;
import co.infinum.princeofversions.mvp.view.POVView;
import co.infinum.princeofversions.threading.ThreadVersionVerifier;

/**
 * Created by stefano on 08/07/16.
 */
public class DefaultUpdater implements UpdateChecker, POVView {

    private POVPresenter presenter;

    private UpdaterCallback callback;

    private POVFactoryHelper.VersionVerifierProvider factory;

    private VersionRepository repository;

    public DefaultUpdater(final Context context, final UpdaterCallback callback) {
        this(context, callback, new POVFactoryHelper.VersionVerifierProvider() {
            @Override
            public IVersionVerifier get() {
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
            public IVersionVerifier get() {
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
    }

    @Override
    public void checkForUpdates(LoaderFactory loaderFactory) {
        UpdateConfigLoader loader = loaderFactory.newInstance();
        try {
            loader.validate();
        } catch (UpdateConfigLoader.ValidationException e) {
            throw new IllegalArgumentException(e);
        }
        presenter = POVFactoryHelper.getInstance().getPresenter(this, loader, factory, repository);
        presenter.checkForUpdates();
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
    public void notifyError(String error) {
        callback.onError(error);
    }
}
