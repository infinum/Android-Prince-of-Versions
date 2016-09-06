package co.infinum.princeofversions.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
                new VersionContext.UpdateContext(new VersionContext.Version("3.0.1"), "ONCE"), true);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));

        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("3.0.0", true, null);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(null);
    }

    @Test
    public void testOptionalUpdateAvailable() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("3.0.0"),
                new VersionContext.Version("2.0.0"), false,
                new VersionContext.UpdateContext(new VersionContext.Version("3.0.1"), "ONCE"), true);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));

        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("3.0.1", false, null);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(null);
    }

    @Test
    public void testNoUpdateAvailable() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("4.0.0"),
                new VersionContext.Version("2.0.0"), false,
                new VersionContext.UpdateContext(new VersionContext.Version("3.0.1"), "ONCE"), false);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));

        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyMap());
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(1)).onNoUpdate(null);
    }

    @Test
    public void testErrorOnUpdate() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionUnavailable(ErrorCode.WRONG_VERSION);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        PrinceOfVersions updater = new PrinceOfVersions(new MockContext(), provider, repository);
        updater.checkForUpdates(loaderFactory, callback);
        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyMap());
        Mockito.verify(callback, Mockito.times(1)).onError(ErrorCode.WRONG_VERSION);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(null);
    }

}
