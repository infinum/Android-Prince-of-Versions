package co.infinum.princeofversions;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Nonnull;

/**
 * Implementation of {@link Executor} which runs check on background thread of default priority.
 */
final class PrinceOfVersionsDefaultExecutor implements Executor {

    private static final ExecutorService SERVICE = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(@Nonnull Runnable r) {
            Thread t = new Thread(r, "PrinceOfVersions Thread");
            t.setDaemon(true);
            return t;
        }
    });

    @Override
    public void execute(@Nonnull Runnable runnable) {
        SERVICE.submit(runnable);
    }
}