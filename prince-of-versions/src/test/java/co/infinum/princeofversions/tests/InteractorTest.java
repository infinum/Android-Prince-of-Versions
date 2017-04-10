package co.infinum.princeofversions.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import co.infinum.princeofversions.ApplicationConfiguration;
import co.infinum.princeofversions.CheckResult;
import co.infinum.princeofversions.Interactor;
import co.infinum.princeofversions.InteractorImpl;
import co.infinum.princeofversions.Loader;
import co.infinum.princeofversions.MockApplicationConfiguration;
import co.infinum.princeofversions.Parser;
import co.infinum.princeofversions.PrinceOfVersionsConfig;
import co.infinum.princeofversions.PrinceOfVersionsDefaultVersionParser;

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
    Parser parser;

    private Interactor interactor;

    @Before
    public void setUp() throws Throwable {
        interactor = new InteractorImpl(parser, new PrinceOfVersionsDefaultVersionParser());
        when(loader.load()).thenReturn(DEFAULT_LOADER_RESULT);
    }

    @After
    public void tearDown() {
        interactor = null;
    }

    @Test
    public void checkMandatoryUpdateWithNoSdk() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withOptionalVersion("1.0.0")
                .build();
        when(parser.parse(anyString())).thenReturn(config);

        CheckResult result = interactor.check(loader, new MockApplicationConfiguration("1.0.0", 1));

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getMandatoryVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenOnlyMandatoryAvailableWithNoSdk() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .build();

        when(parser.parse(anyString())).thenReturn(config);

        CheckResult result = interactor.check(loader, new MockApplicationConfiguration("1.0.0", 1));

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getMandatoryVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenNoUpdateAvailableWithNoSdk() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.0")
                .build();

        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.noUpdate(appConfig.version(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenNoMandatoryOrOptionalUpdateAvailableWithNoSdk() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.0")
                .withOptionalVersion("1.0.0")
                .build();

        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.noUpdate(appConfig.version(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWithOptionalVersionNoSdk() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withOptionalVersion("1.0.2")
                .build();
        when(parser.parse(anyString())).thenReturn(config);

        CheckResult result = interactor.check(loader, new MockApplicationConfiguration("1.0.0", 1));

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getOptionalVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenMandatoryAndOptionalAreEqualWithNoSdk() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withOptionalVersion("1.0.1")
                .build();
        when(parser.parse(anyString())).thenReturn(config);

        CheckResult result = interactor.check(loader, new MockApplicationConfiguration("1.0.0", 1));

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getMandatoryVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWithWithSdkGreaterThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(2)
                .withOptionalVersion("1.0.0")
                .withOptionalMinSdk(2)
                .build();
        when(parser.parse(anyString())).thenReturn(config);

        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);
        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.noUpdate(appConfig.version(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWithWithSdkEqualToCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(2)
                .withOptionalVersion("1.0.0")
                .withOptionalMinSdk(2)
                .build();
        when(parser.parse(anyString())).thenReturn(config);

        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);
        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getMandatoryVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWithWithSdkLessThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(2)
                .withOptionalVersion("1.0.0")
                .withOptionalMinSdk(2)
                .build();
        when(parser.parse(anyString())).thenReturn(config);

        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 3);
        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getMandatoryVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenOnlyMandatoryAvailableWithSdkGreaterThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(2)
                .build();

        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

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

        when(parser.parse(anyString())).thenReturn(config);

        CheckResult result = interactor.check(loader, new MockApplicationConfiguration("1.0.0", 1));

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getMandatoryVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenOnlyMandatoryAvailableWithSdkGLessThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.1")
                .withMandatoryMinSdk(1)
                .build();

        when(parser.parse(anyString())).thenReturn(config);

        CheckResult result = interactor.check(loader, new MockApplicationConfiguration("1.0.0", 2));

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getMandatoryVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkMandatoryUpdateWhenNoUpdateAvailableWithSdkGreaterThanCurrent() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.0")
                .withMandatoryMinSdk(2)
                .build();

        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

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

        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

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

        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

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

        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

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

        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

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

        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

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
        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

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
        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getOptionalVersion(), config.getMetadata())
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
        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getOptionalVersion(), config.getMetadata())
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
        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 1);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

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
        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getMandatoryVersion(), config.getMetadata())
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
        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.mandatoryUpdate(config.getMandatoryVersion(), config.getMetadata())
        );
    }

    @Test
    public void checkOptionalUpdateWhenNoMandatoryNoSdk() throws Throwable {
        PrinceOfVersionsConfig config = new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion("1.0.0")
                .withOptionalVersion("1.0.1")
                .build();
        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.optionalUpdate(config.getOptionalVersion(), config.getOptionalNotificationType(), config.getMetadata())
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
        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

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
        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.optionalUpdate(config.getOptionalVersion(), config.getOptionalNotificationType(), config.getMetadata())
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
        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.optionalUpdate(config.getOptionalVersion(), config.getOptionalNotificationType(), config.getMetadata())
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
        when(parser.parse(anyString())).thenReturn(config);
        ApplicationConfiguration appConfig = new MockApplicationConfiguration("1.0.0", 2);

        CheckResult result = interactor.check(loader, appConfig);

        verify(loader, times(1)).load();
        verify(parser, times(1)).parse(DEFAULT_LOADER_RESULT);

        assertThat(result).isEqualTo(
                CheckResult.optionalUpdate(config.getOptionalVersion(), config.getOptionalNotificationType(), config.getMetadata())
        );
    }

}
