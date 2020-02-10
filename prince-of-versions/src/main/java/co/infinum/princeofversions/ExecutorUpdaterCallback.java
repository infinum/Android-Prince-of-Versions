package co.infinum.princeofversions;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * This class wraps {@link UpdaterCallback} instance to run all methods on specific executor.
 */
final class ExecutorUpdaterCallback implements UpdaterCallback {

    /**
     * Instance to which calls will be delegated
     */
    private final UpdaterCallback callback;

    /**
     * Executor which will execute the delegation to the real callback
     */
    private final Executor executor;

    ExecutorUpdaterCallback(UpdaterCallback callback, Executor executor) {
        this.callback = callback;
        this.executor = executor;
    }

    @Override
    public void onNewUpdate(final int version, final boolean isMandatory, final Map<String, String> metadata) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onNewUpdate(version, isMandatory, metadata);
            }
        });
    }

    @Override
    public void onNoUpdate(final Map<String, String> metadata) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onNoUpdate(metadata);
            }
        });
    }

    @Override
    public void onError(final Throwable error) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                callback.onError(error);
            }
        });
    }
}
