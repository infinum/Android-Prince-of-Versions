package co.infinum.princeofversions.threading;

import android.os.Handler;
import android.os.Looper;

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
import co.infinum.princeofversions.exceptions.ParseException;
import co.infinum.princeofversions.helpers.parsers.VersionConfigParser;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.VersionVerifierListener;

/**
 * Implements checking for updates using a single cached thread.
 * <p>On every check new thread is created for waiting for result (eg. thread is blocked until result is ready). Class still use one
 * thread for computing result, but if more instances are running just one thread computing results.</p>
 *
 * <pre>
 *     1 request computation in same time = 1 thread for computing and 1 blocked thread waiting for result.
 *     10 requests computations in same time = 1 thread for computing and 10 blocked threads (not using processor time) waiting for
 *     result.
 * </pre>
 */
public class ExecutorServiceVersionVerifier implements VersionVerifier {

    /**
     * Default timeout for computing result.
     */
    public static final long DEFAULT_TIMEOUT_SECONDS = 60;

    private static final String TAG = "threadexec";

    /**
     * Thread pool, contains only one thread.
     */
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Handler used to return callbacks on the main thread
     */
    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Parser used for parsing loaded update configuration resource.
     */
    private VersionConfigParser parser;

    /**
     * This instance associated task for computation.
     */
    private Future<Void> future;

    /**
     * Creates a new instance of verifier with parser provided through argument.
     *
     * @param parser Update configuration resource parser.
     */
    public ExecutorServiceVersionVerifier(VersionConfigParser parser) {
        this.parser = parser;
    }

    /**
     * Method loads version using given loader and notify result of version parsing and computation to given callback.
     *
     * @param loader   Loads update configuration.
     * @param listener Callback for notifying results.
     */
    protected void getVersion(UpdateConfigLoader loader, final VersionVerifierListener listener) {
        InputStream response = null;
        try {
            String content = loader.load();

            ifTaskIsCancelledThrowInterrupt();
            final VersionContext version = parser.parse(content);

            ifTaskIsCancelledThrowInterrupt();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.versionAvailable(version);
                }
            });
        } catch (IOException e) {
            onError(e, listener);
        } catch (ParseException e) {
            onError(e, listener);
        } catch (CancellationException | InterruptedException intentionalEmpty) { // NOPMD
            // someone cancelled the task
        } catch (Throwable e) {
            e.printStackTrace();
            onError(e, listener);
        } finally {
            try {
                response.close();
            } catch (Exception ignorable) { // NOPMD
                // ignorable exception
            }
        }
    }

    private void onError(final Throwable throwable, final VersionVerifierListener listener) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.versionUnavailable(throwable);
            }
        });
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

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    future.get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    future = null;
                } catch (Exception ignorable) { // NOPMD
                    // future is cancelled or timed out or thread is interrupted => anyway, just return
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void cancel() {
        try {
            if (future != null) {
                future.cancel(true);
            }
        } catch (CancellationException ignorable) { // NOPMD
            // ignorable exception
        }
    }

    /**
     * Checks if loading is cancelled and throwing interrupt if it is.
     *
     * @throws InterruptedException if loading is cancelled.
     */
    private void ifTaskIsCancelledThrowInterrupt() {
        if (future != null && future.isCancelled()) {
            throw new CancellationException();
        }
    }

}
