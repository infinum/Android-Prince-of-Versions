package co.infinum.princeofversions.helpers;

import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.interfaces.SdkVersionProvider;
import co.infinum.princeofversions.interfaces.VersionRepository;
import co.infinum.princeofversions.interfaces.VersionVerifierFactory;
import co.infinum.princeofversions.mvp.interactor.impl.PovInteractorImpl;
import co.infinum.princeofversions.mvp.presenter.PovPresenter;
import co.infinum.princeofversions.mvp.presenter.impl.PovPresenterImpl;
import co.infinum.princeofversions.mvp.view.PovView;

/**
 * Created by stefano on 08/07/16.
 */
public class PovFactoryHelper {

    private static PovFactoryHelper instance;

    private PovFactoryHelper() {
    }

    public static PovFactoryHelper getInstance() {
        if (instance == null) {
            instance = new PovFactoryHelper();
        }
        return instance;
    }

    /**
     * Creates POVPresenter for given view, loader, version factory and version repository.
     *
     * @param view       View associated with presenter.
     * @param loader     Loader used for loading update configuration resource.
     * @param factory    Factory for creating class for verifying versions.
     * @param repository Repository for persisting library data.
     * @param sdkVersionProvider
     * @return New instance of POVPresenter.
     */
    public PovPresenter getPresenter(PovView view, UpdateConfigLoader loader, VersionVerifierFactory factory,
            VersionRepository repository, SdkVersionProvider sdkVersionProvider) {
        return new PovPresenterImpl(view, new PovInteractorImpl(factory.newInstance(), loader, sdkVersionProvider), repository);
    }

}
