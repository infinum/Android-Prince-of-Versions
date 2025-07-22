package co.infinum.princeofversions

import co.infinum.princeofversions.mocks.MockApplicationConfiguration
import co.infinum.princeofversions.mocks.MockStorage
import co.infinum.princeofversions.mocks.ResourceFileLoader
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.IOException
import java.util.concurrent.Executor

@RunWith(MockitoJUnitRunner::class)
class PrinceOfVersionsTest {

    @Mock
    private lateinit var callback: UpdaterCallback

    @Test
    fun testMandatoryUpdate() {
        val storage = MockStorage()
        val appConfig = MockApplicationConfiguration(100, 16)
        val pov = PrinceOfVersions(storage, MainThreadExecutor(), appConfig)
        val loader = ResourceFileLoader("valid_update_full.json")

        val result = pov.checkForUpdates(loader)

        assertThat(result.status).isEqualTo(UpdateStatus.REQUIRED_UPDATE_NEEDED)
        assertThat(result.updateVersion).isEqualTo(245)
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245)
    }

    @Test
    fun testNoUpdate() {
        val storage = MockStorage()
        val appConfig = MockApplicationConfiguration(300, 16)
        val pov = PrinceOfVersions(storage, MainThreadExecutor(), appConfig)
        val loader = ResourceFileLoader("valid_update_full.json")

        val result = pov.checkForUpdates(loader)

        assertThat(result.status).isEqualTo(UpdateStatus.NO_UPDATE_AVAILABLE)
        assertThat(result.updateVersion).isEqualTo(300)
        assertThat(storage.lastNotifiedVersion(null)).isNull()
    }

    @Test
    fun testOptionalUpdate() {
        val storage = MockStorage()
        val appConfig = MockApplicationConfiguration(200, 16)
        val pov = PrinceOfVersions(storage, MainThreadExecutor(), appConfig)
        val loader = ResourceFileLoader("valid_update_full.json")

        val result = pov.checkForUpdates(loader)

        assertThat(result.status).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE)
        assertThat(result.updateVersion).isEqualTo(245)
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245)
    }

    @Test
    fun testOptionalUpdateAlreadyNotifiedOnce() {
        val storage = MockStorage(245)
        val appConfig = MockApplicationConfiguration(200, 16)
        val pov = PrinceOfVersions(storage, MainThreadExecutor(), appConfig)
        val loader = ResourceFileLoader("valid_update_full.json") // This file has notification type ONCE

        val result = pov.checkForUpdates(loader)

        assertThat(result.status).isEqualTo(UpdateStatus.NO_UPDATE_AVAILABLE)
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245) // Unchanged
    }

    @Test
    fun testOptionalUpdateAlreadyNotifiedAlways() {
        val storage = MockStorage(245)
        val appConfig = MockApplicationConfiguration(200, 16)
        val pov = PrinceOfVersions(storage, MainThreadExecutor(), appConfig)
        val loader = ResourceFileLoader("valid_update_notification_always.json")

        val result = pov.checkForUpdates(loader)

        assertThat(result.status).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE)
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245) // Updated again
    }

    @Test
    fun testAsyncSuccess() {
        val storage = MockStorage()
        val appConfig = MockApplicationConfiguration(100, 16)
        val pov = PrinceOfVersions(storage, MainThreadExecutor(), appConfig)
        val loader = ResourceFileLoader("valid_update_full.json")

        pov.checkForUpdates(loader, callback)

        verify(callback, timeout(1000)).onSuccess(any())
        verify(callback, never()).onError(any())
    }

    @Test
    fun testAsyncError() {
        val storage = MockStorage()
        val appConfig = MockApplicationConfiguration(100, 16)
        val pov = PrinceOfVersions(storage, MainThreadExecutor(), appConfig)
        val loader = mock<Loader>()
        whenever(loader.load()).thenThrow(IOException("Network error"))

        pov.checkForUpdates(loader, callback)

        verify(callback, timeout(1000)).onError(any<IOException>())
        verify(callback, never()).onSuccess(any())
    }

    @Test
    fun testAsyncCancellation() {
        val storage = MockStorage()
        val appConfig = MockApplicationConfiguration(100, 16)
        // Use an executor that doesn't run immediately
        val pov = PrinceOfVersions(storage, Executor { /* no-op */ }, appConfig)
        val loader = ResourceFileLoader("valid_update_full.json")

        val cancelable = pov.checkForUpdates(loader, callback)
        cancelable.cancel()

        verify(callback, never()).onSuccess(any())
        verify(callback, never()).onError(any())
    }

    @Test
    fun testBuilderThrowsErrorWhenNoContextAndNoStorage() {
        assertThatThrownBy {
            PrinceOfVersions.Builder()
                .withAppConfig(MockApplicationConfiguration(1, 1))
                .build()
        }
            .isInstanceOf(UnsupportedOperationException::class.java)
            .hasMessage("You must define storage and application configuration if you don't provide Context.")
    }

    @Test
    fun testBuilderThrowsErrorWhenNoContextAndNoAppConfig() {
        assertThatThrownBy {
            PrinceOfVersions.Builder()
                .withStorage(MockStorage())
                .build()
        }
            .isInstanceOf(UnsupportedOperationException::class.java)
            .hasMessage("You must define storage and application configuration if you don't provide Context.")
    }

    @Test
    fun testBuilderWithCustomComponents() {
        val storage = MockStorage()
        val appConfig = MockApplicationConfiguration(1, 1)
        val parser = mock<ConfigurationParser>()
        whenever(parser.parse(any())).thenReturn(PrinceOfVersionsConfig.Builder().withMandatoryVersion(2).build())

        val pov = PrinceOfVersions.Builder()
            .withStorage(storage)
            .withAppConfig(appConfig)
            .withParser(parser)
            .withCallbackExecutor(MainThreadExecutor())
            .build()

        pov.checkForUpdates(ResourceFileLoader("valid_update_full.json"))

        // Verify our custom parser was used
        verify(parser).parse(any())
    }

    private class MainThreadExecutor : Executor {
        override fun execute(command: Runnable) {
            command.run()
        }
    }
}
