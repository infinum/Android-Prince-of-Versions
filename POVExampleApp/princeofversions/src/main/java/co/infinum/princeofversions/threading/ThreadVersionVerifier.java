package co.infinum.princeofversions.threading;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.CancellationException;

import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.helpers.parsers.VersionConfigParser;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.VersionVerifierListener;

public class ThreadVersionVerifier implements VersionVerifier {

    private static final String TAG = "threadVerifier";
    public static final int DEFAULT_TIMEOUT_SECONDS = 60;

    private Thread thread;
    private volatile boolean cancelled = false;

    private VersionConfigParser parser;

    public ThreadVersionVerifier(VersionConfigParser parser) {
        this.parser = parser;
    }

    private void getVersion(UpdateConfigLoader loader, VersionVerifierListener listener) {
        try {
            String content = loader.load();
            Log.e(TAG, content);

            ifTaskIsCancelledThrowInterrupt(); // if cancelled here no need to parse response
            VersionContext version = parser.parse(content);

            ifTaskIsCancelledThrowInterrupt(); // if cancelled here no need to fire event
            listener.versionAvailable(version);
        } catch (IOException e) {
            e.printStackTrace();
            listener.versionUnavailable(ErrorCode.LOAD_ERROR);
        } catch (VersionConfigParser.ParseException e) {
            e.printStackTrace();
            listener.versionUnavailable(ErrorCode.WRONG_VERSION);
        } catch (CancellationException | InterruptedException e) {
            // someone cancelled the task
        } catch (Throwable e) {
            listener.versionUnavailable(ErrorCode.UNKNOWN_ERROR);
        }
    }

    @Override
    public void verify(final UpdateConfigLoader loader, final VersionVerifierListener listener) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getVersion(loader, listener);
                cancelled = false;
            }
        });
        thread.start();
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    private void ifTaskIsCancelledThrowInterrupt() throws InterruptedException {
        if (cancelled) {
            throw new InterruptedException();
        }
    }
}
