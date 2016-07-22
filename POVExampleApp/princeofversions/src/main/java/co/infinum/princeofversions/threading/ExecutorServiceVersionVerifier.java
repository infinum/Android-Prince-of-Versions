package co.infinum.princeofversions.threading;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.helpers.parsers.VersionConfigParser;
import co.infinum.princeofversions.interfaces.IVersionVerifier;
import co.infinum.princeofversions.network.VersionVerifierListener;

public class ExecutorServiceVersionVerifier implements IVersionVerifier {

    private static final String TAG = "threadexec";
    public static final long DEFAULT_TIMEOUT_SECONDS = 60;

    private VersionConfigParser parser;

    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private Future<Void> future;

    public ExecutorServiceVersionVerifier(VersionConfigParser parser) {
        this.parser = parser;
    }

    private void getVersion(UpdateConfigLoader loader, VersionVerifierListener listener) {
        InputStream response = null;
        try {
            String content = loader.load();

            Log.e(TAG, content);

            ifTaskIsCancelledThrowInterrupt();
            VersionContext version = parser.parse(content);

            ifTaskIsCancelledThrowInterrupt();
            listener.versionAvailable(version);
        } catch (IOException | VersionConfigParser.ParseException e) {
            e.printStackTrace();
            listener.versionUnavailable(e.getMessage());
        } catch (CancellationException | InterruptedException e) {
            // someone cancelled the task
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception ignorable) {}
            }
        }
    }

    @Override
    public void verify(final UpdateConfigLoader loader, final VersionVerifierListener listener) {
        future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws IOException {
                getVersion(loader, listener);
                return null;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    future.get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                } catch (Exception ignore) {
                    // future is cancelled or timed out or thread is interrupted => anyway, just return
                }
            }
        }).start();
    }

    @Override
    public void cancel() {
        try {
            future.cancel(true);
        } catch (CancellationException ignorable) {
            ignorable.printStackTrace();
        }
    }

    private void ifTaskIsCancelledThrowInterrupt() {
        if (future.isCancelled()) {
            throw new CancellationException();
        }
    }

}
