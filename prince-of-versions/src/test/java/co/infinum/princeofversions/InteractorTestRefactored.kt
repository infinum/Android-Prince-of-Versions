package co.infinum.princeofversions

import co.infinum.princeofversions.mocks.MockApplicationConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class InteractorTestRefactored {

    private companion object Companion {
        private const val DEFAULT_LOADER_RESULT = ""
    }

    @Mock
    private lateinit var loader: Loader

    @Mock
    private lateinit var configurationParser: ConfigurationParser

    private lateinit var interactor: Interactor

    @Before
    fun setUp() {
        interactor = InteractorImpl(configurationParser)
        `when`(loader.load()).thenReturn(DEFAULT_LOADER_RESULT)
    }

    @After
    fun tearDown() {
        // Not strictly necessary with MockitoJUnitRunner, but good practice
    }

    @Test
    fun checkMandatoryUpdate() {
        val config = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(2)
            .build()
        `when`(configurationParser.parse(anyString())).thenReturn(config)
        val appConfig = MockApplicationConfiguration(1, 1)

        val result = interactor.check(loader, appConfig)

        verify(loader, times(1)).load()
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT)
        val expectedInfo = UpdateInfo(config.mandatoryVersion!!, config.optionalVersion, config.requirements, appConfig.version, config.optionalNotificationType)
        assertThat(result).isEqualTo(CheckResult.mandatoryUpdate(config.mandatoryVersion!!, config.metadata, expectedInfo))
    }

    @Test
    fun checkMandatoryUpdateWhenNoUpdateAvailable() {
        val config = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(1)
            .build()
        `when`(configurationParser.parse(anyString())).thenReturn(config)
        val appConfig = MockApplicationConfiguration(1, 1)

        val result = interactor.check(loader, appConfig)

        verify(loader, times(1)).load()
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT)
        val expectedInfo = UpdateInfo(config.mandatoryVersion!!, config.optionalVersion, config.requirements, appConfig.version, config.optionalNotificationType)
        assertThat(result).isEqualTo(CheckResult.noUpdate(appConfig.version, config.metadata, expectedInfo))
    }

    @Test
    fun checkMandatoryUpdateWhenNoMandatoryOrOptionalUpdateAvailable() {
        val config = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(1)
            .withOptionalVersion(1)
            .build()
        `when`(configurationParser.parse(anyString())).thenReturn(config)
        val appConfig = MockApplicationConfiguration(1, 1)

        val result = interactor.check(loader, appConfig)

        verify(loader, times(1)).load()
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT)
        val expectedInfo = UpdateInfo(config.mandatoryVersion!!, config.optionalVersion!!, config.requirements, appConfig.version, config.optionalNotificationType)
        assertThat(result).isEqualTo(CheckResult.noUpdate(appConfig.version, config.metadata, expectedInfo))
    }

    @Test
    fun checkMandatoryUpdateWithOptionalVersion() {
        val config = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(2)
            .withOptionalVersion(3)
            .build()
        `when`(configurationParser.parse(anyString())).thenReturn(config)
        val appConfig = MockApplicationConfiguration(1, 1)

        val result = interactor.check(loader, appConfig)

        verify(loader, times(1)).load()
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT)
        val expectedInfo = UpdateInfo(config.mandatoryVersion!!, config.optionalVersion!!, config.requirements, appConfig.version, config.optionalNotificationType)
        assertThat(result).isEqualTo(CheckResult.mandatoryUpdate(config.optionalVersion!!, config.metadata, expectedInfo))
    }

    @Test
    fun checkMandatoryUpdateWhenMandatoryAndOptionalAreEqual() {
        val config = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(2)
            .withOptionalVersion(2)
            .build()
        `when`(configurationParser.parse(anyString())).thenReturn(config)
        val appConfig = MockApplicationConfiguration(1, 1)

        val result = interactor.check(loader, appConfig)

        verify(loader, times(1)).load()
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT)
        val expectedInfo = UpdateInfo(config.mandatoryVersion!!, config.optionalVersion!!, config.requirements, appConfig.version, config.optionalNotificationType)
        assertThat(result).isEqualTo(CheckResult.mandatoryUpdate(config.mandatoryVersion!!, config.metadata, expectedInfo))
    }

    @Test
    fun checkOptionalUpdate() {
        val config = PrinceOfVersionsConfig.Builder()
            .withOptionalVersion(2)
            .withOptionalNotificationType(NotificationType.ONCE)
            .build()
        `when`(configurationParser.parse(anyString())).thenReturn(config)
        val appConfig = MockApplicationConfiguration(1, 1)

        val result = interactor.check(loader, appConfig)

        verify(loader, times(1)).load()
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT)
        val expectedInfo = UpdateInfo(config.mandatoryVersion, config.optionalVersion!!, config.requirements, appConfig.version, config.optionalNotificationType)
        assertThat(result).isEqualTo(CheckResult.optionalUpdate(config.optionalVersion!!, NotificationType.ONCE, config.metadata, expectedInfo))
    }

    @Test
    fun checkOptionalUpdateWhenNoUpdateAvailable() {
        val config = PrinceOfVersionsConfig.Builder()
            .withOptionalVersion(1)
            .build()
        `when`(configurationParser.parse(anyString())).thenReturn(config)
        val appConfig = MockApplicationConfiguration(1, 1)

        val result = interactor.check(loader, appConfig)

        verify(loader, times(1)).load()
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT)
        val expectedInfo = UpdateInfo(config.mandatoryVersion, config.optionalVersion!!, config.requirements, appConfig.version, config.optionalNotificationType)
        assertThat(result).isEqualTo(CheckResult.noUpdate(appConfig.version, config.metadata, expectedInfo))
    }

    @Test
    fun checkThrowsExceptionWhenNoVersionAvailable() {
        val config = PrinceOfVersionsConfig.Builder().build() // No versions set
        `when`(configurationParser.parse(anyString())).thenReturn(config)
        val appConfig = MockApplicationConfiguration(1, 1)

        assertThatThrownBy { interactor.check(loader, appConfig) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Both mandatory and optional version are null.")
    }
}