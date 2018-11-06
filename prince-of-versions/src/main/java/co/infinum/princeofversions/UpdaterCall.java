package co.infinum.princeofversions;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

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
    public Result execute() throws Throwable {
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
