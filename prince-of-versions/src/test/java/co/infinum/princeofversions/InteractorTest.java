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

    @Mock
    UpdateInfo updateInfo;

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

        CheckResult result = interactor.check(loader, new MockApplicationConfiguration(1, 1));

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
            CheckResult.mandatoryUpdate(config.getMandatoryVersion(), config.getMetadata(), updateInfo)
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

        assertThat(result).isEqualTo(
            CheckResult.noUpdate(appConfig.version(), config.getMetadata(), updateInfo)
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

        assertThat(result).isEqualTo(
            CheckResult.noUpdate(appConfig.version(), config.getMetadata(), updateInfo)
        );
    }

    @Test
    public void checkMandatoryUpdateWithOptionalVersion() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(2)
            .withOptionalVersion(3)
            .build();
        when(configurationParser.parse(anyString())).thenReturn(config);

        CheckResult result = interactor.check(loader, new MockApplicationConfiguration(1, 1));

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
            CheckResult.mandatoryUpdate(config.getOptionalVersion(), config.getMetadata(), updateInfo)
        );
    }

    @Test
    public void checkMandatoryUpdateWhenMandatoryAndOptionalAreEqual() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(2)
            .withOptionalVersion(2)
            .build();
        when(configurationParser.parse(anyString())).thenReturn(config);

        CheckResult result = interactor.check(loader, new MockApplicationConfiguration(1, 1));

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
            CheckResult.mandatoryUpdate(config.getMandatoryVersion(), config.getMetadata(), updateInfo)
        );
    }

    @Test
    public void checkOptionalUpdate() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
            .withOptionalVersion(2)
            .build();
        when(configurationParser.parse(anyString())).thenReturn(config);

        CheckResult result = interactor.check(loader, new MockApplicationConfiguration(1, 1));

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
            CheckResult.optionalUpdate(config.getOptionalVersion(), NotificationType.ONCE, config.getMetadata(), updateInfo)
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

        assertThat(result).isEqualTo(
            CheckResult.noUpdate(appConfig.version(), config.getMetadata(), updateInfo)
        );
    }

    /*
    @Test
    public void checkMandatoryUpdateWithWithSdkLessThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(2)
                .withOptionalVersion("1.0.0")
                .withOptionalMinSdk(2)
                .build();
        when(configurationParser.parse(anyString())).thenReturn(config);

        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 3);
        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getMandatoryVersion().getVersion(), config.getMetadata())
        );
    }*/

    /*
    @Test
    public void checkMandatoryUpdateWhenOnlyMandatoryAvailableWithSdkGreaterThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(2)
                .build();

        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.noUpdate(appConfig.version(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenOnlyMandatoryAvailableWithSdkEqualToCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(1)
                .build();

        when(configurationParser.parse(anyString())).thenReturn(config);

        CheckResult result = interactor.check(loader, new MockApplicationConfiguration("1.0.0", 1));

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getMandatoryVersion().getVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenOnlyMandatoryAvailableWithSdkGLessThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(1)
                .build();

        when(configurationParser.parse(anyString())).thenReturn(config);

        CheckResult result = interactor.check(loader, new MockApplicationConfiguration("1.0.0", 2));

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getMandatoryVersion().getVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenNoUpdateAvailableWithSdkGreaterThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.0")
                .withMandatoryMinSdk(2)
                .build();

        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.noUpdate(appConfig.version(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenNoUpdateAvailableWithSdkEqualToCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.0")
                .withMandatoryMinSdk(1)
                .build();

        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.noUpdate(appConfig.version(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenNoUpdateAvailableWithSdkLessThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.0")
                .withMandatoryMinSdk(1)
                .build();

        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.noUpdate(appConfig.version(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenNoMandatoryOrOptionalUpdateAvailableWithSdkGreaterThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.0")
                .withMandatoryMinSdk(2)
                .withOptionalVersion("1.0.0")
                .withOptionalMinSdk(2)
                .build();

        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.noUpdate(appConfig.version(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenNoMandatoryOrOptionalUpdateAvailableWithSdkEqualToCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.0")
                .withMandatoryMinSdk(2)
                .withOptionalVersion("1.0.0")
                .withOptionalMinSdk(2)
                .build();

        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.noUpdate(appConfig.version(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenNoMandatoryOrOptionalUpdateAvailableWithSdkLessThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.0")
                .withMandatoryMinSdk(1)
                .withOptionalVersion("1.0.0")
                .withOptionalMinSdk(1)
                .build();

        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.noUpdate(appConfig.version(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWithOptionalVersionWithSdkGreaterThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(2)
                .withOptionalVersion("1.0.2")
                .withOptionalMinSdk(2)
                .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.noUpdate(appConfig.version(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWithOptionalVersionWithSdkEqualToCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(2)
                .withOptionalVersion("1.0.2")
                .withOptionalMinSdk(2)
                .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getOptionalVersion().getVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWithOptionalVersionWithSdkLessThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(1)
                .withOptionalVersion("1.0.2")
                .withOptionalMinSdk(1)
                .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getOptionalVersion().getVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenMandatoryAndOptionalAreEqualWithSdkGreaterThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(2)
                .withOptionalVersion("1.0.1")
                .withOptionalMinSdk(2)
                .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.noUpdate(appConfig.version(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenMandatoryAndOptionalAreEqualWithSdkEqualToCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(2)
                .withOptionalVersion("1.0.1")
                .withOptionalMinSdk(2)
                .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getMandatoryVersion().getVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenMandatoryAndOptionalAreEqualWithSdkLessThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(1)
                .withOptionalVersion("1.0.1")
                .withOptionalMinSdk(1)
                .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getMandatoryVersion().getVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkOptionalUpdateWhenNoMandatoryNoSdk() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.0")
                .withOptionalVersion("1.0.1")
                .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.optionalUpdate(config.getOptionalVersion().getVersion(), config.getOptionalNotificationType(), config
                .getMetadata())
        );
    }

    @Test
    public void checkOptionalUpdateWhenNoMandatorySdkGreaterThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.0")
                .withMandatoryMinSdk(1)
                .withOptionalVersion("1.0.1")
                .withOptionalMinSdk(3)
                .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.noUpdate(appConfig.version(), config.getMetadata())
        );
    }

    @Test
    public void checkOptionalUpdateWhenNoMandatorySdkEqualToCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.0")
                .withMandatoryMinSdk(1)
                .withOptionalVersion("1.0.1")
                .withOptionalMinSdk(2)
                .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.optionalUpdate(config.getOptionalVersion().getVersion(), config.getOptionalNotificationType(), config
                .getMetadata())
        );
    }

    @Test
    public void checkOptionalUpdateWhenNoMandatorySdkLessThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.0")
                .withMandatoryMinSdk(1)
                .withOptionalVersion("1.0.1")
                .withOptionalMinSdk(1)
                .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.optionalUpdate(config.getOptionalVersion().getVersion(), config.getOptionalNotificationType(), config
                .getMetadata())
        );
    }

    @Test
    public void checkOptionalUpdateWhenHasMandatoryButSdkGreaterThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(3)
                .withOptionalVersion("1.0.2")
                .withOptionalMinSdk(1)
                .build();
        when(configurationParser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(configurationParser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.optionalUpdate(config.getOptionalVersion().getVersion(), config.getOptionalNotificationType(), config
                .getMetadata())
        );
    }*/
}
