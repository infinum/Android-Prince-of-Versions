package co.infinum.princeofversions.helpers;

import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.interfaces.VersionRepository;
import co.infinum.princeofversions.interfaces.VersionVerifierFactory;
import co.infinum.princeofversions.mvp.interactor.impl.POVInteractorImpl;
import co.infinum.princeofversions.mvp.presenter.POVPresenter;
import co.infinum.princeofversions.mvp.presenter.impl.POVPresenterImpl;
import co.infinum.princeofversions.mvp.view.POVView;

/**
 * Created by stefano on 08/07/16.
 */
public class POVFactoryHelper {

    private static POVFactoryHelper instance;

    public static POVFactoryHelper getInstance() {
        if (instance == null) {
            instance = new POVFactoryHelper();
        }
        return instance;
    }

    private POVFactoryHelper() {}

    /**
     * Creates POVPresenter for given view, loader, version factory and version repository.
     * @param view View associated with presenter.
     * @param loader Loader used for loading update configuration resource.
     * @param factory Factory for creating class for verifying versions.
     * @param repository Repository for persisting library data.
     * @return New instance of POVPresenter.
     */
    public POVPresenter getPresenter(POVView view, UpdateConfigLoader loader, VersionVerifierFactory factory,
                                     VersionRepository repository) {
        return new POVPresenterImpl(view, new POVInteractorImpl(factory.newInstance(), loader), repository);
    }

}
