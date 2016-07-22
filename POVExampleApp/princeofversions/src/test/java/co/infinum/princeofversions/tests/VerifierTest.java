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
import android.test.mock.MockContext;

import java.io.IOException;

import co.infinum.princeofversions.BuildConfig;
import co.infinum.princeofversions.DefaultUpdater;
import co.infinum.princeofversions.LoaderFactory;
import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.helpers.POVFactoryHelper;
import co.infinum.princeofversions.interfaces.IVersionVerifier;
import co.infinum.princeofversions.interfaces.VersionRepository;
import co.infinum.princeofversions.network.VersionVerifierListener;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class VerifierTest {

    private UpdaterCallback callback;

    private LoaderFactory loaderFactory;

    private UpdateConfigLoader loader;

    private IVersionVerifier versionVerifier;

    private POVFactoryHelper.VersionVerifierProvider provider;

    private VersionRepository repository;

    @Before
    public void setUp() throws UpdateConfigLoader.ValidationException, IOException, InterruptedException, JSONException {
        callback = Mockito.mock(UpdaterCallback.class);
        loaderFactory = Mockito.mock(LoaderFactory.class);
        loader = Mockito.mock(UpdateConfigLoader.class);
        versionVerifier = Mockito.mock(IVersionVerifier.class);
        provider = Mockito.mock(POVFactoryHelper.VersionVerifierProvider.class);
        repository = Mockito.mock(VersionRepository.class);
        Mockito.when(repository.getLastVersionName(Mockito.anyString())).thenReturn(null);
        Mockito.doNothing().when(repository).setLastVersionName(Mockito.anyString());
        Mockito.doNothing().when(loader).validate();
        Mockito.when(loader.load()).thenReturn("");
        Mockito.when(loaderFactory.newInstance()).thenReturn(loader);
        Mockito.when(provider.get()).thenReturn(versionVerifier);
    }

    @Test
    public void testMandatoryUpdateAvailable() throws UpdateConfigLoader.ValidationException, InterruptedException, IOException {
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

        DefaultUpdater updater = new DefaultUpdater(new MockContext(), callback, provider, repository);
        updater.checkForUpdates(loaderFactory);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("3.0.0", true);
        Mockito.verify(callback, Mockito.times(0)).onError(Mockito.anyString());
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

    @Test
    public void testOptionalUpdateAvailable() {
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

        DefaultUpdater updater = new DefaultUpdater(new MockContext(), callback, provider, repository);
        updater.checkForUpdates(loaderFactory);
        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("3.0.1", false);
        Mockito.verify(callback, Mockito.times(0)).onError(Mockito.anyString());
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

    @Test
    public void testNoUpdateAvailable() {
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

        DefaultUpdater updater = new DefaultUpdater(new MockContext(), callback, provider, repository);
        updater.checkForUpdates(loaderFactory);
        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(callback, Mockito.times(0)).onError(Mockito.anyString());
        Mockito.verify(callback, Mockito.times(1)).onNoUpdate();
    }

    @Test
    public void testErrorOnUpdate() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                VersionVerifierListener listener = (VersionVerifierListener) args[1];
                listener.versionUnavailable("Some serious error occurred.");
                return null;
            }
        }).when(versionVerifier).verify(Mockito.any(UpdateConfigLoader.class), Mockito.any(VersionVerifierListener.class));
        DefaultUpdater updater = new DefaultUpdater(new MockContext(), callback, provider, repository);
        updater.checkForUpdates(loaderFactory);
        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(callback, Mockito.times(1)).onError("Some serious error occurred.");
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

}
