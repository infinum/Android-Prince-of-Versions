package co.infinum.princeofversions.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Build;
import android.test.mock.MockContext;

import java.io.IOException;

import co.infinum.princeofversions.BuildConfig;
import co.infinum.princeofversions.LoaderFactory;
import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.exceptions.LoaderValidationException;
import co.infinum.princeofversions.interfaces.VersionRepository;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.VersionVerifierFactory;
import co.infinum.princeofversions.interfaces.VersionVerifierListener;
import co.infinum.princeofversions.util.SdkVersionProviderMock;

import static org.mockito.Matchers.eq;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class VerifierTest {

    private UpdaterCallback callback;

    private LoaderFactory loaderFactory;

    private UpdateConfigLoader loader;

    private VersionVerifier versionVerifier;

    private VersionVerifierFactory provider;

    private VersionRepository repository;

    @Before
    public void setUp() throws LoaderValidationException, IOException, InterruptedException {
        callback = Mockito.mock(UpdaterCallback.class);
        loaderFactory = Mockito.mock(LoaderFactory.class);
        loader = Mockito.mock(UpdateConfigLoader.class);
        versionVerifier = Mockito.mock(VersionVerifier.class);
        provider = Mockito.mock(VersionVerifierFactory.class);
        repository = Mockito.mock(VersionRepository.class);
        Mockito.when(repository.getLastVersionName(Mockito.anyString())).thenReturn(null);
        Mockito.doNothing().when(repository).setLastVersionName(Mockito.anyString());
        Mockito.doNothing().when(loader).validate();
        Mockito.when(loader.load()).thenReturn("");
        Mockito.when(loaderFactory.newInstance()).thenReturn(loader);
        Mockito.when(provider.newInstance()).thenReturn(versionVerifier);
    }

    @Test
    public void testMandatoryUpdateAvailable() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("2.0.0"),
                new VersionContext.Version("3.0.0"), true,
                new VersionContext.UpdateContext(new VersionContext.Version("1.0.0"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("3.0.0"), eq(true), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(null);
    }

    @Test
    public void testMandatoryUpdateAvailableShortShort() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("2.0"),
                new VersionContext.Version("3.0"), true,
                new VersionContext.UpdateContext(new VersionContext.Version("3.0"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("3.0"), eq(true), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testMandatoryUpdateAvailableShortLong() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("2.0"),
                new VersionContext.Version("3.0.0"), true,
                new VersionContext.UpdateContext(new VersionContext.Version("3.0.0"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("3.0.0"), eq(true), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testMandatoryUpdateAvailableLongShort() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("2.0.0"),
                new VersionContext.Version("3.0"), true,
                new VersionContext.UpdateContext(new VersionContext.Version("3.0"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);

        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("3.0"), eq(true), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testMandatoryUpdateAvailableBetaBeta() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("2.0.0-beta1"),
                new VersionContext.Version("2.0.0-beta2"), true,
                new VersionContext.UpdateContext(new VersionContext.Version("1.0.0"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("2.0.0-beta2"), eq(true), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testMandatoryUpdateAvailableBetaNormal() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("2.0.0-beta3"),
                new VersionContext.Version("3.0.0"), true,
                new VersionContext.UpdateContext(new VersionContext.Version("3.0.0"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("3.0.0"), eq(true), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testMandatoryUpdateAvailableBetaRc() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("2.0.0-beta1"),
                new VersionContext.Version("2.0.0-rc1"), true,
                new VersionContext.UpdateContext(new VersionContext.Version("1.0.0"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("2.0.0-rc1"), eq(true), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testMandatoryUpdateAvailableBB() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("2.0.0-b12"),
                new VersionContext.Version("2.0.0-b45"), true,
                new VersionContext.UpdateContext(new VersionContext.Version("1.0.1"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("2.0.0-b45"), eq(true), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testOptionalUpdateAvailable() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("3.0.0"),
                new VersionContext.Version("2.0.0"), false,
                new VersionContext.UpdateContext(new VersionContext.Version("3.0.1"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("3.0.1"), eq(false), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(null);
    }


    @Test
    public void testOptionalUpdateAvailableShortShort() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("3.0"),
                new VersionContext.Version("2.0"), false,
                new VersionContext.UpdateContext(new VersionContext.Version("3.1"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("3.1"), eq(false), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testOptionalUpdateAvailableShortLong() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("3.0"),
                new VersionContext.Version("2.0.0"), false,
                new VersionContext.UpdateContext(new VersionContext.Version("3.0.1"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("3.0.1"), eq(false), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testOptionalUpdateAvailableLongShort() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("3.0.0"),
                new VersionContext.Version("2.0"), false,
                new VersionContext.UpdateContext(new VersionContext.Version("3.1"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("3.1"), eq(false), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testOptionalUpdateAvailableBetaBeta() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("2.0.0-beta2"),
                new VersionContext.Version("2.0.0-beta1"), false,
                new VersionContext.UpdateContext(new VersionContext.Version("2.0.0-beta3"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("2.0.0-beta3"), eq(false), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testOptionalUpdateAvailableBetaNormal() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("3.0.0-beta3"),
                new VersionContext.Version("2.0.0"), false,
                new VersionContext.UpdateContext(new VersionContext.Version("3.0.1"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("3.0.1"), eq(false), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testOptionalUpdateAvailableBetaRc() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("3.0.0-beta1"),
                new VersionContext.Version("2.0.0"), false,
                new VersionContext.UpdateContext(new VersionContext.Version("3.0.0-rc1"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("3.0.0-rc1"), eq(false), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testOptionalUpdateAvailableBB() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("3.0.0-b12"),
                new VersionContext.Version("2.0.0-b45"), false,
                new VersionContext.UpdateContext(new VersionContext.Version("3.0.0-b45"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("3.0.0-b45"), eq(false), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testNoUpdateAvailable() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("4.0.0"),
                new VersionContext.Version("2.0.0"), false,
                new VersionContext.UpdateContext(new VersionContext.Version("3.0.1"), "ONCE"), false, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(0))
                .onNewUpdate(Mockito.anyString(), Mockito.anyBoolean(), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(1)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testErrorOnUpdate() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionUnavailable(ErrorCode.WRONG_VERSION, new Exception());
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(0))
                .onNewUpdate(Mockito.anyString(), Mockito.anyBoolean(), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(1)).onError(ArgumentMatchers.eq(ErrorCode.WRONG_VERSION), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }


    @Test
    public void testMandatoryAndOptionalUpdate() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("1.0.0"),
                new VersionContext.Version("1.1.0"), true,
                new VersionContext.UpdateContext(new VersionContext.Version("1.1.1"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("1.1.1"), eq(true), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testMandatoryAndOptionalUpdateTheSame() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("1.0.0"),
                new VersionContext.Version("1.1.0"), true,
                new VersionContext.UpdateContext(new VersionContext.Version("1.1.0"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("1.1.0"), eq(true), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

    @Test
    public void testMandatoryAndOptionalUpdateParseException() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("1.0.0"),
                new VersionContext.Version("1.1.0"), true,
                new VersionContext.UpdateContext(new VersionContext.Version("1.1.little_prince"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        SdkVersionProviderMock sdkVersionProviderMock = new SdkVersionProviderMock(15);
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository, sdkVersionProviderMock);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate(eq("1.1.0"), eq(true), ArgumentMatchers.<String, String>anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }

}
