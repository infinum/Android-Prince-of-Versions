package co.infinum.princeofversions

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import java.io.IOException
import java.util.concurrent.Executor

@RunWith(MockitoJUnitRunner::class)
class UpdaterCallTest {

    @Mock
    private lateinit var core: PrinceOfVersions

    @Mock
    private lateinit var loader: Loader

    @Mock
    private lateinit var callback: UpdaterCallback

    @Mock
    private lateinit var cancelable: PrinceOfVersionsCancelable

    @Mock
    private lateinit var executor: Executor

    @Mock
    private lateinit var updateResult: UpdateResult

    @Before
    fun setUp() {
        `when`(core.checkForUpdates(loader)).thenReturn(updateResult)
    }

    @Test
    fun testExecuteReturnsUpdateResult() {
        `when`(core.checkForUpdates(loader)).thenReturn(updateResult)
        val call: PrinceOfVersionsCall = UpdaterCall(core, loader)
        val result = call.execute()
        assertThat(result).isEqualTo(updateResult)
    }

    @Test
    fun testExecuteTwiceThrows() {
        val call: PrinceOfVersionsCall = UpdaterCall(core, loader)
        call.execute()
        assertThatThrownBy { call.execute() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Already executed!")
    }

    @Test
    fun testExecuteWhenCanceledThrows() {
        val call: PrinceOfVersionsCall = UpdaterCall(core, loader)
        call.cancel()
        assertThatThrownBy { call.execute() }
            .isInstanceOf(IOException::class.java)
            .hasMessage("Canceled!")
    }

    @Test
    fun testEnqueueCallsCore() {
        `when`(core.checkForUpdates(loader, callback)).thenReturn(cancelable)
        val call: PrinceOfVersionsCall = UpdaterCall(core, loader)
        call.enqueue(callback)
        verify(core).checkForUpdates(loader, callback)
    }

    @Test
    fun testEnqueueTwiceThrows() {
        val call: PrinceOfVersionsCall = UpdaterCall(core, loader)
        call.enqueue(callback)
        assertThatThrownBy { call.enqueue(callback) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Already executed!")
    }

    @Test
    fun testEnqueueWhenCanceledCallsOnError() {
        val call: PrinceOfVersionsCall = UpdaterCall(core, loader)
        call.cancel()
        call.enqueue(callback)
        verify(callback).onError(any())
        verify(core, never()).checkForUpdates(any<Loader>(), any<UpdaterCallback>())
    }

    @Test
    fun testEnqueueWithExecutorCallsCore() {
        `when`(core.checkForUpdates(executor, loader, callback)).thenReturn(cancelable)
        val call: PrinceOfVersionsCall = UpdaterCall(core, loader)
        call.enqueue(executor, callback)
        verify(core).checkForUpdates(executor, loader, callback)
    }

    @Test
    fun testEnqueueWithExecutorWhenCanceledCallsOnError() {
        val call: PrinceOfVersionsCall = UpdaterCall(core, loader)
        call.cancel()
        call.enqueue(executor, callback)
        verify(callback).onError(any())
        verify(core, never()).checkForUpdates(any<Executor>(), any<Loader>(), any<UpdaterCallback>())
    }

    @Test
    fun testCancelAfterEnqueue() {
        `when`(core.checkForUpdates(loader, callback)).thenReturn(cancelable)
        val call: PrinceOfVersionsCall = UpdaterCall(core, loader)
        call.enqueue(callback)
        call.cancel()
        assertThat(call.isCanceled).isTrue()
        verify(cancelable).cancel()
    }

    @Test
    fun testCancelBeforeEnqueue() {
        val call: PrinceOfVersionsCall = UpdaterCall(core, loader)
        call.cancel()
        assertThat(call.isCanceled).isTrue()
        // No interaction with a cancelable object, as it hasn't been created yet
    }

    @Test
    fun testIsCanceledReturnsFalseInitially() {
        val call: PrinceOfVersionsCall = UpdaterCall(core, loader)
        assertThat(call.isCanceled).isFalse()
    }

    @Test
    fun testExecuteAfterEnqueueThrows() {
        val call: PrinceOfVersionsCall = UpdaterCall(core, loader)
        call.enqueue(callback)
        assertThatThrownBy { call.execute() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Already executed!")
    }

    @Test
    fun testEnqueueAfterExecuteThrows() {
        val call: PrinceOfVersionsCall = UpdaterCall(core, loader)
        call.execute()
        assertThatThrownBy { call.enqueue(callback) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Already executed!")
    }
}
