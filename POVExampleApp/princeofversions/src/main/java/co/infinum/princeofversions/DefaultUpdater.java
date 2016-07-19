package co.infinum.princeofversions;

import android.content.Context;

import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.exceptions.UrlNotSetException;
import co.infinum.princeofversions.helpers.ContextHelper;
import co.infinum.princeofversions.helpers.POVFactoryHelper;
import co.infinum.princeofversions.interfaces.UpdateChecker;
import co.infinum.princeofversions.mvp.presenter.POVPresenter;
import co.infinum.princeofversions.mvp.view.POVView;

/**
 * Created by stefano on 08/07/16.
 */
public class DefaultUpdater implements UpdateChecker, POVView {

    private String url;

    private POVPresenter presenter;

    public DefaultUpdater(final Context context) {
        ContextHelper.setContext(context);
    }

    @Override
    public void checkForUpdates(String url, UpdaterCallback uc) {
        if (url == null) {
            try {
                throw new UrlNotSetException("Url that points to a remote server isn't set");
            } catch (UrlNotSetException e) {
                e.printStackTrace();
            }
        } else {
            this.url = url;
            presenter = POVFactoryHelper.getPresenter(this);
            presenter.checkForUpdates();
        }
    }

    @Override
    public void onUpdatesChecked() {

    }
}
