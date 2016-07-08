package co.infinum.princeofversions.mvp.presenter.impl;

import co.infinum.princeofversions.mvp.interactor.POVInteractor;
import co.infinum.princeofversions.mvp.presenter.POVPresenter;
import co.infinum.princeofversions.mvp.view.POVView;

/**
 * Created by stefano on 08/07/16.
 */
public class POVPresenterImpl implements POVPresenter {

    private POVView view;
    private POVInteractor interactor;

    public POVPresenterImpl(POVView view, POVInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void checkForUpdates() {
        interactor.checkForUpdates();
    }
}
