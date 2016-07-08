package co.infinum.princeofversions.helpers;

import co.infinum.princeofversions.mvp.interactor.impl.POVInteractorImpl;
import co.infinum.princeofversions.mvp.presenter.POVPresenter;
import co.infinum.princeofversions.mvp.presenter.impl.POVPresenterImpl;
import co.infinum.princeofversions.mvp.view.POVView;

/**
 * Created by stefano on 08/07/16.
 */
public class POVFactoryHelper {

    public static POVPresenter getPresenter(POVView view) {
        return new POVPresenterImpl(view, new POVInteractorImpl());
    }

}
