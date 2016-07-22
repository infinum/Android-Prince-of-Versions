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
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.mvp.interactor.POVInteractor;
import co.infinum.princeofversions.mvp.interactor.impl.POVInteractorImpl;
import co.infinum.princeofversions.mvp.interactor.listeners.POVInteractorListener;
import co.infinum.princeofversions.interfaces.VersionVerifierListener;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class InteractorTest {

    private VersionVerifier versionVerifier;
    private UpdateConfigLoader loader;

    @Before
    public void setUp() throws JSONException {
        versionVerifier = Mockito.mock(VersionVerifier.class);
        loader = Mockito.mock(UpdateConfigLoader.class);
    }

    @Test
    public void testMandatoryUpdate() {
        final VersionContext versionContext = new VersionContext("2.0.0", new VersionContext.Version("3.0.0", "1"), true, new VersionContext
                .UpdateContext(new VersionContext.Version("3.0.1", "2"), "ONCE"), true);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        POVInteractorListener listener = Mockito.mock(POVInteractorListener.class);
        POVInteractor interactor = new POVInteractorImpl(versionVerifier, loader);
        interactor.checkForUpdates(listener);
        Mockito.verify(listener, Mockito.times(1)).onMandatoryUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onNoUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
    }

    @Test
    public void testOptionalUpdate() {
        final VersionContext versionContext = new VersionContext("3.0.0", new VersionContext.Version("2.0.0", "1"), false, new
                VersionContext
                        .UpdateContext(new VersionContext.Version("3.0.1", "2"), "ONCE"), true);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        POVInteractorListener listener = Mockito.mock(POVInteractorListener.class);
        POVInteractor interactor = new POVInteractorImpl(versionVerifier, loader);
        interactor.checkForUpdates(listener);
        Mockito.verify(listener, Mockito.times(0)).onMandatoryUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(1)).onUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onNoUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
    }

    @Test
    public void testNoUpdate() {
        final VersionContext versionContext = new VersionContext("4.0.0", new VersionContext.Version("2.0.0", "1"), false, new
                VersionContext
                        .UpdateContext(new VersionContext.Version("3.0.1", "2"), "ONCE"), false);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionAvailable(versionContext);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        POVInteractorListener listener = Mockito.mock(POVInteractorListener.class);
        POVInteractor interactor = new POVInteractorImpl(versionVerifier, loader);
        interactor.checkForUpdates(listener);
        Mockito.verify(listener, Mockito.times(0)).onMandatoryUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(1)).onNoUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
    }

    @Test
    public void testError() {
        final VersionContext versionContext = new VersionContext("2.0.0", new VersionContext.Version("3.0.0", "1"), true, new VersionContext
                .UpdateContext(new VersionContext.Version("3.0.1", "2"), "ONCE"), true);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionUnavailable(ErrorCode.LOAD_ERROR);
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        POVInteractorListener listener = Mockito.mock(POVInteractorListener.class);
        POVInteractor interactor = new POVInteractorImpl(versionVerifier, loader);
        interactor.checkForUpdates(listener);
        Mockito.verify(listener, Mockito.times(0)).onMandatoryUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(0)).onNoUpdateAvailable(versionContext);
        Mockito.verify(listener, Mockito.times(1)).onError(ErrorCode.LOAD_ERROR);
    }

}
