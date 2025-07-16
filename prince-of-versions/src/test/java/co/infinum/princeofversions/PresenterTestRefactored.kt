package co.infinum.princeofversions

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.Executor

@RunWith(MockitoJUnitRunner::class)
class PresenterTestRefactored {

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
        `when`(interactor.check(any(Loader::class.java), any(ApplicationConfiguration::class.java))).thenReturn(checkResult)

        val result = presenter.run(loader, appConfig)

        val expected = UpdateResult(updateInfo, defaultMetadata, UpdateStatus.REQUIRED_UPDATE_NEEDED, 10)
        assertThat(result).isEqualTo(expected)
        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.updateVersion)
    }

    @Test
    fun testNoUpdate() {
        val checkResult = CheckResult.noUpdate(10, defaultMetadata, updateInfo)
        `when`(interactor.check(any(Loader::class.java), any(ApplicationConfiguration::class.java))).thenReturn(checkResult)

        val result = presenter.run(loader, appConfig)

        val expected = UpdateResult(updateInfo, defaultMetadata, UpdateStatus.NO_UPDATE_AVAILABLE, checkResult.updateVersion)
        assertThat(result).isEqualTo(expected)
        verify(storage, never()).rememberLastNotifiedVersion(anyInt())
    }

    @Test
    fun testOptionalUpdateFirstTime() {
        val checkResult = CheckResult.optionalUpdate(12, NotificationType.ONCE, defaultMetadata, updateInfo)
        `when`(storage.lastNotifiedVersion(null)).thenReturn(null) // First time seeing any update
        `when`(interactor.check(any(Loader::class.java), any(ApplicationConfiguration::class.java))).thenReturn(checkResult)

        val result = presenter.run(loader, appConfig)

        val expected = UpdateResult(updateInfo, defaultMetadata, UpdateStatus.NEW_UPDATE_AVAILABLE, checkResult.updateVersion)
        assertThat(result).isEqualTo(expected)
        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.updateVersion)
    }

    @Test
    fun testOptionalUpdateWhenNotNotified() {
        val checkResult = CheckResult.optionalUpdate(12, NotificationType.ONCE, defaultMetadata, updateInfo)
        `when`(storage.lastNotifiedVersion(null)).thenReturn(11) // Old version notified
        `when`(interactor.check(any(Loader::class.java), any(ApplicationConfiguration::class.java))).thenReturn(checkResult)

        val result = presenter.run(loader, appConfig)

        val expected = UpdateResult(updateInfo, defaultMetadata, UpdateStatus.NEW_UPDATE_AVAILABLE, checkResult.updateVersion)
        assertThat(result).isEqualTo(expected)
        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.updateVersion)
    }

    @Test
    fun testOptionalUpdateNotifiedAlways() {
        val checkResult = CheckResult.optionalUpdate(12, NotificationType.ALWAYS, defaultMetadata, updateInfo)
        `when`(storage.lastNotifiedVersion(null)).thenReturn(12) // Same version notified
        `when`(interactor.check(any(Loader::class.java), any(ApplicationConfiguration::class.java))).thenReturn(checkResult)

        val result = presenter.run(loader, appConfig)

        val expected = UpdateResult(updateInfo, defaultMetadata, UpdateStatus.NEW_UPDATE_AVAILABLE, checkResult.updateVersion)
        assertThat(result).isEqualTo(expected)
        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.updateVersion)
    }

    @Test
    fun testOptionalUpdateNotifiedOnce() {
        val checkResult = CheckResult.optionalUpdate(12, NotificationType.ONCE, defaultMetadata, updateInfo)
        `when`(storage.lastNotifiedVersion(null)).thenReturn(12) // Same version notified
        `when`(interactor.check(any(Loader::class.java), any(ApplicationConfiguration::class.java))).thenReturn(checkResult)

        val result = presenter.run(loader, appConfig)

        val expected = UpdateResult(updateInfo, defaultMetadata, UpdateStatus.NO_UPDATE_AVAILABLE, checkResult.updateVersion)
        assertThat(result).isEqualTo(expected)
        verify(storage, never()).rememberLastNotifiedVersion(anyInt())
    }

    @Test
    fun testSyncCheckError() {
        `when`(interactor.check(any(Loader::class.java), any(ApplicationConfiguration::class.java))).thenThrow(IllegalStateException())
        assertThatThrownBy { presenter.check(loader, appConfig) }
            .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun testAsyncCheckSuccess() {
        val callback = mock(UpdaterCallback::class.java)
        val executor = Executor { it.run() }
        val checkResult = CheckResult.mandatoryUpdate(10, defaultMetadata, updateInfo)
        val expected = UpdateResult(updateInfo, defaultMetadata, UpdateStatus.REQUIRED_UPDATE_NEEDED, 10)
        `when`(interactor.check(loader, appConfig)).thenReturn(checkResult)

        presenter.check(loader, executor, callback, appConfig)

        verify(callback, times(1)).onSuccess(expected)
        verify(callback, never()).onError(any(Throwable::class.java))
    }

    @Test
    fun testAsyncCheckError() {
        val callback = mock(UpdaterCallback::class.java)
        val executor = Executor { it.run() }
        val throwable = IllegalStateException()
        `when`(interactor.check(loader, appConfig)).thenThrow(throwable)

        presenter.check(loader, executor, callback, appConfig)

        verify(callback, never()).onSuccess(any(UpdateResult::class.java))
        verify(callback, times(1)).onError(throwable)
    }

    @Test
    fun testAsyncCheckSuccessIsIgnoredWhenCanceled() {
        val callback = mock(UpdaterCallback::class.java)
        val executor = Executor { /* Don't run immediately */ }

        val cancelable = presenter.check(loader, executor, callback, appConfig)
        cancelable.cancel()

        verify(callback, never()).onSuccess(any(UpdateResult::class.java))
        verify(callback, never()).onError(any(Throwable::class.java))
    }

    @Test
    fun testAsyncCheckErrorIsIgnoredWhenCanceled() {
        val callback = mock(UpdaterCallback::class.java)
        val executor = Executor { /* Don't run immediately */ }

        val cancelable = presenter.check(loader, executor, callback, appConfig)
        cancelable.cancel()

        verify(callback, never()).onSuccess(any(UpdateResult::class.java))
        verify(callback, never()).onError(any(Throwable::class.java))
    }
}
