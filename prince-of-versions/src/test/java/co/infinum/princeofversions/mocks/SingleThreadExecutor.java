package co.infinum.princeofversions.mocks;

import java.util.concurrent.Executor;

public class SingleThreadExecutor implements Executor {

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
