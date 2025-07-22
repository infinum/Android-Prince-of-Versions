package co.infinum.princeofversions

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.concurrent.Executor

@RunWith(MockitoJUnitRunner::class)
class PresenterTest {

    @Mock
    private lateinit var interactor: Interactor

    @Mock
    private lateinit var storage: Storage

    @Mock
    private lateinit var loader: Loader

    @Mock
    private lateinit var appConfig: ApplicationConfiguration

    @Mock
    private lateinit var updateInfo: UpdateInfo

    private lateinit var presenter: PresenterImpl

    private val defaultMetadata: Map<String, String> = emptyMap()

    @Before
    fun setUp() {
        presenter = PresenterImpl(interactor, storage)
    }

    @Test
    fun testMandatoryUpdate() {
        val checkResult = CheckResult.mandatoryUpdate(10, defaultMetadata, updateInfo)
        whenever(interactor.check(any(), any())).thenReturn(checkResult)

        val result = presenter.run(loader, appConfig)

        val expected = UpdateResult(updateInfo, defaultMetadata, UpdateStatus.REQUIRED_UPDATE_NEEDED, 10)
        assertThat(result).isEqualTo(expected)
        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.updateVersion)
    }

    @Test
    fun testNoUpdate() {
        val checkResult = CheckResult.noUpdate(10, defaultMetadata, updateInfo)
        whenever(interactor.check(any(), any())).thenReturn(checkResult)

        val result = presenter.run(loader, appConfig)

        val expected = UpdateResult(updateInfo, defaultMetadata, UpdateStatus.NO_UPDATE_AVAILABLE, checkResult.updateVersion)
        assertThat(result).isEqualTo(expected)
        verify(storage, never()).rememberLastNotifiedVersion(anyInt())
    }

    @Test
    fun testOptionalUpdateFirstTime() {
        val checkResult = CheckResult.optionalUpdate(12, NotificationType.ONCE, defaultMetadata, updateInfo)
        whenever(storage.lastNotifiedVersion(null)).thenReturn(null) // First time seeing any update
        whenever(interactor.check(any(), any())).thenReturn(checkResult)

        val result = presenter.run(loader, appConfig)

        val expected = UpdateResult(updateInfo, defaultMetadata, UpdateStatus.NEW_UPDATE_AVAILABLE, checkResult.updateVersion)
        assertThat(result).isEqualTo(expected)
        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.updateVersion)
    }

    @Test
    fun testOptionalUpdateWhenNotNotified() {
        val checkResult = CheckResult.optionalUpdate(12, NotificationType.ONCE, defaultMetadata, updateInfo)
        whenever(storage.lastNotifiedVersion(null)).thenReturn(11) // Old version notified
        whenever(interactor.check(any(), any())).thenReturn(checkResult)

        val result = presenter.run(loader, appConfig)

        val expected = UpdateResult(updateInfo, defaultMetadata, UpdateStatus.NEW_UPDATE_AVAILABLE, checkResult.updateVersion)
        assertThat(result).isEqualTo(expected)
        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.updateVersion)
    }

    @Test
    fun testOptionalUpdateNotifiedAlways() {
        val checkResult = CheckResult.optionalUpdate(12, NotificationType.ALWAYS, defaultMetadata, updateInfo)
        whenever(storage.lastNotifiedVersion(null)).thenReturn(12) // Same version notified
        whenever(interactor.check(any(), any())).thenReturn(checkResult)

        val result = presenter.run(loader, appConfig)

        val expected = UpdateResult(updateInfo, defaultMetadata, UpdateStatus.NEW_UPDATE_AVAILABLE, checkResult.updateVersion)
        assertThat(result).isEqualTo(expected)
        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.updateVersion)
    }

    @Test
    fun testOptionalUpdateNotifiedOnce() {
        val checkResult = CheckResult.optionalUpdate(12, NotificationType.ONCE, defaultMetadata, updateInfo)
        whenever(storage.lastNotifiedVersion(null)).thenReturn(12) // Same version notified
        whenever(interactor.check(any(), any())).thenReturn(checkResult)

        val result = presenter.run(loader, appConfig)

        val expected = UpdateResult(updateInfo, defaultMetadata, UpdateStatus.NO_UPDATE_AVAILABLE, checkResult.updateVersion)
        assertThat(result).isEqualTo(expected)
        verify(storage, never()).rememberLastNotifiedVersion(anyInt())
    }

    @Test
    fun testSyncCheckError() {
        whenever(interactor.check(any(), any())).thenThrow(IllegalStateException())
        assertThatThrownBy { presenter.check(loader, appConfig) }
            .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun testAsyncCheckSuccess() {
        val callback: UpdaterCallback = mock()
        val executor = Executor { it.run() }
        val checkResult = CheckResult.mandatoryUpdate(10, defaultMetadata, updateInfo)
        val expected = UpdateResult(updateInfo, defaultMetadata, UpdateStatus.REQUIRED_UPDATE_NEEDED, 10)
        whenever(interactor.check(loader, appConfig)).thenReturn(checkResult)

        presenter.check(loader, executor, callback, appConfig)

        verify(callback, times(1)).onSuccess(expected)
        verify(callback, never()).onError(any())
    }

    @Test
    fun testAsyncCheckError() {
        val callback: UpdaterCallback = mock()
        val executor = Executor { it.run() }
        val throwable = IllegalStateException()
        whenever(interactor.check(loader, appConfig)).thenThrow(throwable)

        presenter.check(loader, executor, callback, appConfig)

        verify(callback, never()).onSuccess(any())
        verify(callback, times(1)).onError(throwable)
    }

    @Test
    fun testAsyncCheckSuccessIsIgnoredWhenCanceled() {
        val callback: UpdaterCallback = mock()
        val executor = Executor { /* Don't run immediately */ }

        val cancelable = presenter.check(loader, executor, callback, appConfig)
        cancelable.cancel()

        verify(callback, never()).onSuccess(any())
        verify(callback, never()).onError(any())
    }

    @Test
    fun testAsyncCheckErrorIsIgnoredWhenCanceled() {
        val callback: UpdaterCallback = mock()
        val executor = Executor { /* Don't run immediately */ }

        val cancelable = presenter.check(loader, executor, callback, appConfig)
        cancelable.cancel()

        verify(callback, never()).onSuccess(any())
        verify(callback, never()).onError(any())
    }
}
