package co.infinum.princeofversions;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Implementation of {@link Executor} which runs task on main thread.
 */
public class PrinceOfVersionsCallbackExecutor implements Executor {

    private final Handler handler = new Handler(Looper.myLooper());

    @Override
    public void execute(@NonNull final Runnable command) {
        handler.post(command);
    }
}
