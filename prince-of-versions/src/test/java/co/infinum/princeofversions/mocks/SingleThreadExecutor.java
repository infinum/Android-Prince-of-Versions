package co.infinum.princeofversions.mocks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class SingleThreadExecutor implements Executor {

    @Override
    public void execute(@NonNull Runnable runnable) {
        runnable.run();
    }
}
