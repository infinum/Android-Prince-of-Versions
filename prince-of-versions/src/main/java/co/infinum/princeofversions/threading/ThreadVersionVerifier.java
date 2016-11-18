package co.infinum.princeofversions.threading;

import java.io.IOException;
import java.util.concurrent.CancellationException;

import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.exceptions.ParseException;
import co.infinum.princeofversions.helpers.parsers.VersionConfigParser;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.VersionVerifierListener;

/**
 * Implements checking for updates using single thread per check.
 * <p>On every check new thread is created for computing result.</p>
 *
 * <pre>
 *     1 request computation in same time = 1 thread for computing result.
 *     10 requests computations in same time = 1 thread for computing result.
 * </pre>
 */
public class ThreadVersionVerifier implements VersionVerifier {

    private static final String TAG = "threadVerifier";

    /**
     * Cancellation flag. True if cancelled, false otherwise.
     */
    private volatile boolean cancelled = false;

    /**
     * Parser used for parsing loaded update configuration resource.
     */
    private VersionConfigParser parser;

    /**
     * Creates a new instance of verifier with parser provided through argument.
     *
     * @param parser Update configuration resource parser.
     */
    public ThreadVersionVerifier(VersionConfigParser parser) {
        this.parser = parser;
    }

    /**
     * Method loads version using given loader and notify result of version parsing and computation to given callback.
     *
     * @param loader   Loads update configuration.
     * @param listener Callback for notifying results.
     */
    protected void getVersion(UpdateConfigLoader loader, VersionVerifierListener listener) {
        try {
            String content = loader.load();

            ifTaskIsCancelledThrowInterrupt(); // if cancelled here no need to parse response
            VersionContext version = parser.parse(content);

            ifTaskIsCancelledThrowInterrupt(); // if cancelled here no need to fire event
            listener.versionAvailable(version);
        } catch (IOException e) {
            listener.versionUnavailable(ErrorCode.LOAD_ERROR);
        } catch (ParseException e) {
            listener.versionUnavailable(ErrorCode.WRONG_VERSION);
        } catch (CancellationException | InterruptedException e) { // NOPMD
            // someone cancelled the task
        } catch (Throwable e) {
            listener.versionUnavailable(ErrorCode.UNKNOWN_ERROR);
        }
    }

    @Override
    public void verify(final UpdateConfigLoader loader, final VersionVerifierListener listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getVersion(loader, listener);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    /**
     * Checks if loading is cancelled and throwing interrupt if it is.
     *
     * @throws InterruptedException if loading is cancelled.
     */
    private void ifTaskIsCancelledThrowInterrupt() throws InterruptedException {
        if (cancelled) {
            throw new InterruptedException();
        }
    }
}
