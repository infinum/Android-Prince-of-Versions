package co.infinum.princeofversions

import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import java.util.concurrent.Executor

class ExecutorUpdaterCallbackTest {

    @Test
    fun testOnSuccessIsCalledOnExecutor() {
        val mockCallback = mock(UpdaterCallback::class.java)
        val syncExecutor = Executor { it.run() }
        val executorCallback = ExecutorUpdaterCallback(mockCallback, syncExecutor)
        val mockResult = mock(UpdateResult::class.java)

        executorCallback.onSuccess(mockResult)

        verify(mockCallback).onSuccess(mockResult)
        verifyNoMoreInteractions(mockCallback)
    }

    @Test
    fun testOnErrorIsCalledOnExecutor() {
        val mockCallback = mock(UpdaterCallback::class.java)
        val syncExecutor = Executor { it.run() }
        val executorCallback = ExecutorUpdaterCallback(mockCallback, syncExecutor)
        val mockError = mock(Throwable::class.java)

        executorCallback.onError(mockError)

        verify(mockCallback).onError(mockError)
        verifyNoMoreInteractions(mockCallback)
    }
}
