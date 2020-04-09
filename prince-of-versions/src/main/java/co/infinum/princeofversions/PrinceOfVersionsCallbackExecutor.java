package co.infinum.princeofversions;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import javax.annotation.Nonnull;

/**
 * Implementation of {@link Executor} which runs task on main thread.
 */
public class PrinceOfVersionsCallbackExecutor implements Executor {

    private final Handler handler = new Handler(Looper.myLooper());

    @Override
    public void execute(@Nonnull final Runnable command) {
        handler.post(command);
    }
}
