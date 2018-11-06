package co.infinum.princeofversions.mocks;

import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

public class SingleThreadExecutor implements Executor {

    @Override
    public void execute(@Nonnull Runnable runnable) {
        runnable.run();
    }
}
