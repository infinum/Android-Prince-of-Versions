package co.infinum.princeofversions

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

/**
 * An implementation of [Executor] that runs tasks on the Android main thread.
 */
class PrinceOfVersionsCallbackExecutor : Executor {

    private val handler = Handler(Looper.getMainLooper())

    override fun execute(command: Runnable) {
        handler.post(command)
    }
}