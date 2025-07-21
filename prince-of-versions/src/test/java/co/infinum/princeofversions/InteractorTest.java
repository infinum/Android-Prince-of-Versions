package co.infinum.princeofversions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import co.infinum.princeofversions.mocks.MockApplicationConfiguration;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InteractorTest {

    private static final String DEFAULT_LOADER_RESULT = "";

    @Mock
    Loader loader;

    @Mock
    ConfigurationParser configurationParser;

    private Interactor interactor;

    @Before
    public void setUp() throws Throwable {
        interactor = new InteractorImpl(configurationParser);
        when(loader.load()).thenReturn(DEFAULT_LOADER_RESULT);
    }

    @After
    public void tearDown() {
        interactor = null;
    }

    @Test
    public void checkMandatoryUpdate() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(2)
            .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration(1, 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        UpdateInfo expectedUpdateInfo = new UpdateInfo(
            config.getMandatoryVersion(),
            config.getOptionalVersion(),
            config.getRequirements(),
            appConfig.getVersion(),
            config.getOptionalNotificationType()
        );

        assertThat(result).isEqualTo(
            CheckResult.Companion.mandatoryUpdate(config.getMandatoryVersion(), config.getMetadata(), expectedUpdateInfo)
        );
    }

    @Test
    public void checkMandatoryUpdateWhenNoUpdateAvailable() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(1)
            .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration(1, 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        UpdateInfo expectedUpdateInfo = new UpdateInfo(
            config.getMandatoryVersion(),
            config.getOptionalVersion(),
            config.getRequirements(),
            appConfig.getVersion(),
            config.getOptionalNotificationType()
        );

        assertThat(result).isEqualTo(
            CheckResult.Companion.noUpdate(appConfig.getVersion(), config.getMetadata(), expectedUpdateInfo)
        );
    }

    @Test
    public void checkMandatoryUpdateWhenNoMandatoryOrOptionalUpdateAvailable() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(1)
            .withOptionalVersion(1)
            .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration(1, 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        UpdateInfo expectedUpdateInfo = new UpdateInfo(
            config.getMandatoryVersion(),
            config.getOptionalVersion(),
            config.getRequirements(),
            appConfig.getVersion(),
            config.getOptionalNotificationType()
        );

        assertThat(result).isEqualTo(
            CheckResult.Companion.noUpdate(appConfig.getVersion(), config.getMetadata(), expectedUpdateInfo)
        );
    }

    @Test
    public void checkMandatoryUpdateWithOptionalVersion() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(2)
            .withOptionalVersion(3)
            .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration(1, 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        UpdateInfo expectedUpdateInfo = new UpdateInfo(
            config.getMandatoryVersion(),
            config.getOptionalVersion(),
            config.getRequirements(),
            appConfig.getVersion(),
            config.getOptionalNotificationType()
        );

        assertThat(result).isEqualTo(
            CheckResult.Companion.mandatoryUpdate(config.getOptionalVersion(), config.getMetadata(), expectedUpdateInfo)
        );
    }

    @Test
    public void checkMandatoryUpdateWhenMandatoryAndOptionalAreEqual() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(2)
            .withOptionalVersion(2)
            .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration(1, 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        UpdateInfo expectedUpdateInfo = new UpdateInfo(
            config.getMandatoryVersion(),
            config.getOptionalVersion(),
            config.getRequirements(),
            appConfig.getVersion(),
            config.getOptionalNotificationType()
        );

        assertThat(result).isEqualTo(
            CheckResult.Companion.mandatoryUpdate(config.getMandatoryVersion(), config.getMetadata(), expectedUpdateInfo)
        );
    }

    @Test
    public void checkOptionalUpdate() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
            .withOptionalVersion(2)
            .withOptionalNotificationType(NotificationType.ONCE)
            .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration(1, 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        UpdateInfo expectedUpdateInfo = new UpdateInfo(
            config.getMandatoryVersion(),
            config.getOptionalVersion(),
            config.getRequirements(),
            appConfig.getVersion(),
            config.getOptionalNotificationType()
        );

        assertThat(result).isEqualTo(
            CheckResult.Companion.optionalUpdate(config.getOptionalVersion(), NotificationType.ONCE, config.getMetadata(), expectedUpdateInfo)
        );
    }

    @Test
    public void checkOptionalUpdateWhenNoUpdateAvailable() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
            .withOptionalVersion(1)
            .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration(1, 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        UpdateInfo expectedUpdateInfo = new UpdateInfo(
            config.getMandatoryVersion(),
            config.getOptionalVersion(),
            config.getRequirements(),
            appConfig.getVersion(),
            config.getOptionalNotificationType()
        );

        assertThat(result).isEqualTo(
            CheckResult.Companion.noUpdate(appConfig.getVersion(), config.getMetadata(), expectedUpdateInfo)
        );
    }
}