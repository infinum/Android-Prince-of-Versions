package co.infinum.princeofversions.helpers;

import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.VersionRepository;
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

    public POVPresenter getPresenter(POVView view, UpdateConfigLoader loader, VersionVerifierProvider provider,
                                     VersionRepository repository) {
        return new POVPresenterImpl(view, new POVInteractorImpl(provider.get(), loader), repository);
    }

    public interface VersionVerifierProvider {
        VersionVerifier get();
    }

}
