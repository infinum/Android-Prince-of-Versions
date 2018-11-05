package co.infinum.princeofversions;

import android.os.Handler;
import android.os.Looper;

import java.util.Map;

/**
 * This class wraps {@link UpdaterCallback} instance to run all methods on main thread.
 */
class UiUpdaterCallback implements UpdaterCallback {

    /**
     * Handler on UI thread
     */
    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Instance to which calls will be delegated
     */
    private UpdaterCallback callback;

    UiUpdaterCallback(UpdaterCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onNewUpdate(final String version, final boolean isMandatory, final Map<String, String> metadata) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onNewUpdate(version, isMandatory, metadata);
            }
        });
    }

    @Override
    public void onNoUpdate(final Map<String, String> metadata) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onNoUpdate(metadata);
            }
        });
    }

    @Override
    public void onError(final Throwable error) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(error);
            }
        });
    }
}
