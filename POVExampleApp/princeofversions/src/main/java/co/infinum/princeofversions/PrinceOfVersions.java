package co.infinum.princeofversions;

import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.exceptions.UrlNotSetException;
import co.infinum.princeofversions.interfaces.UpdateChecker;
import co.infinum.princeofversions.mvp.view.POVView;

/**
 * Created by stefano on 08/07/16.
 */
public class PrinceOfVersions implements UpdateChecker, POVView {

    private static String url;

    public void setup(String url) {
        this.url = url;
    }

    @Override
    public void checkForUpdates(UpdaterCallback uc, String url) {
        if (url == null) {
            try {
                throw new UrlNotSetException("Url that points to a remote server isnt set");
            } catch (UrlNotSetException e) {
                e.printStackTrace();
            }
        } else {
            //presenter
        }
    }

    @Override
    public void onUpdatesChecked() {

    }
}
