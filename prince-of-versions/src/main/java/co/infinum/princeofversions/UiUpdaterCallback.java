package co.infinum.princeofversions;

import android.os.Handler;
import android.os.Looper;

import java.util.Map;

public class UiUpdaterCallback implements UpdaterCallback {

    private Handler handler = new Handler(Looper.getMainLooper());

    private UpdaterCallback callback;

    public UiUpdaterCallback(UpdaterCallback callback) {
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
