package co.infinum.princeofversions.tests;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Build;

import co.infinum.princeofversions.BuildConfig;
import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.interfaces.SdkVersionProvider;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.VersionVerifierListener;
import co.infinum.princeofversions.mvp.interactor.PovInteractor;
import co.infinum.princeofversions.mvp.interactor.impl.PovInteractorImpl;
import co.infinum.princeofversions.mvp.interactor.listeners.PovInteractorListener;
import co.infinum.princeofversions.util.SdkVersionProviderMock;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class InteractorTest {

    private VersionVerifier versionVerifier;

    private UpdateConfigLoader loader;

    private SdkVersionProvider sdkVersionProvider;

    @Before
    public void setUp() throws JSONException {
        versionVerifier = Mockito.mock(VersionVerifier.class);
        loader = Mockito.mock(UpdateConfigLoader.class);
        sdkVersionProvider = new SdkVersionProviderMock(16);
    }

    @Test
    public void testMandatoryUpdate() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("2.0.0"),
                new VersionContext.Version("3.0.0"), true,
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
        PovInteractorListener listener = Mockito.mock(PovInteractorListener.class);
        PovInteractor interactor = new PovInteractorImpl(versionVerifier, loader, sdkVersionProvider);
        interactor.checkForUpdates(listener);
        Mockito.verify(listener, Mockito.times(1)).onMandatoryUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onNoUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
    }

    @Test
    public void testOptionalUpdate() {
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
        PovInteractorListener listener = Mockito.mock(PovInteractorListener.class);
        PovInteractor interactor = new PovInteractorImpl(versionVerifier, loader, sdkVersionProvider);
        interactor.checkForUpdates(listener);
        Mockito.verify(listener, Mockito.times(0)).onMandatoryUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(1)).onUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onNoUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
    }

    @Test
    public void testNoUpdate() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("4.0.0"),
                new VersionContext.Version("2.0.0"), false,
                new VersionContext.UpdateContext(new VersionContext.Version("3.0.1"), "ONCE"), false, 24);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        PovInteractorListener listener = Mockito.mock(PovInteractorListener.class);
        PovInteractor interactor = new PovInteractorImpl(versionVerifier, loader, sdkVersionProvider);
        interactor.checkForUpdates(listener);
        Mockito.verify(listener, Mockito.times(0)).onMandatoryUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(1)).onNoUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
    }

    @Test
    public void testError() {
        final VersionContext versionContext = new VersionContext(
                new VersionContext.Version("2.0.0"),
                new VersionContext.Version("3.0.0"), true,
                new VersionContext.UpdateContext(new VersionContext.Version("3.0.1"), "ONCE"), true, 15);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionUnavailable(ErrorCode.LOAD_ERROR);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        PovInteractorListener listener = Mockito.mock(PovInteractorListener.class);
        PovInteractor interactor = new PovInteractorImpl(versionVerifier, loader, sdkVersionProvider);
        interactor.checkForUpdates(listener);
        Mockito.verify(listener, Mockito.times(0)).onMandatoryUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onNoUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(1)).onError(ErrorCode.LOAD_ERROR);
    }

}
