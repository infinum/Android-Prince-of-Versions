package co.infinum.princeofversions;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

/**
 * An invocation of a {@link PrinceOfVersions} update check method.
 * The instance can be used only once, if call is already executed or enqueued new instnace should be used instead.
 *
 * <p>Calls may be executed synchronously with {@link #execute}, or asynchronously with {@link #enqueue}.
 * In either case the call can be canceled at any time with {@link #cancel}.
 */
public final class UpdaterCall implements PrinceOfVersionsCall {

    private final PrinceOfVersions core;
    private final Loader loader;

    private final AtomicBoolean executed = new AtomicBoolean(false);
    private final AtomicBoolean canceled = new AtomicBoolean(false);

    @Nullable
    private PrinceOfVersionsCancelable cancelable;

    UpdaterCall(final PrinceOfVersions core, final Loader loader) {
        this.core = core;
        this.loader = loader;
    }

    @Override
    public UpdateResult execute() throws Throwable {
        if (executed.getAndSet(true)) {
            throw new IllegalStateException("Already executed!");
        }
        if (canceled.get()) {
            throw new IOException("Canceled!");
        }
        return core.checkForUpdates(loader);
    }

    @Override
    public void enqueue(final UpdaterCallback callback) {
        if (executed.getAndSet(true)) {
            throw new IllegalStateException("Already executed!");
        }
        if (canceled.get()) {
            callback.onError(new IOException("Canceled"));
        }
        cancelable = core.checkForUpdates(loader, callback);
    }

    @Override
    public void enqueue(final Executor executor, final UpdaterCallback callback) {
        if (executed.getAndSet(true)) {
            throw new IllegalStateException("Already executed!");
        }
        if (canceled.get()) {
            callback.onError(new IOException("Canceled"));
        }
        cancelable = core.checkForUpdates(executor, loader, callback);
    }

    @Override
    public void cancel() {
        canceled.set(true);

        if (cancelable != null) {
            cancelable.cancel();
        }
    }

    @Override
    public boolean isCanceled() {
        return canceled.get();
    }
}
