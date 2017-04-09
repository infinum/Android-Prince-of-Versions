package co.infinum.princeofversions.verifiers;

import co.infinum.princeofversions.Executor;

public class SingleThreadExecutor implements Executor {

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
