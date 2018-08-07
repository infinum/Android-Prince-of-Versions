package co.infinum.princeofversions.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import co.infinum.princeofversions.ApplicationConfiguration;
import co.infinum.princeofversions.CheckResult;
import co.infinum.princeofversions.Exceptions;
import co.infinum.princeofversions.Executor;
import co.infinum.princeofversions.Interactor;
import co.infinum.princeofversions.Loader;
import co.infinum.princeofversions.NotificationType;
import co.infinum.princeofversions.PresenterImpl;
import co.infinum.princeofversions.Result;
import co.infinum.princeofversions.Storage;
import co.infinum.princeofversions.UpdateStatus;
import co.infinum.princeofversions.UpdaterCancelable;
import co.infinum.princeofversions.UpdaterCallback;
import co.infinum.princeofversions.mocks.SingleThreadExecutor;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PresenterTest {

    private static final Map<String, String> DEFAULT_METADATA = new HashMap<>();

    @Mock
    Interactor interactor;

    @Mock
    Storage storage;

    @Mock
    Loader loader;

    @Mock
    ApplicationConfiguration appConfig;

    private PresenterImpl presenter;

    @Before
    public void setUp() {
        presenter = new PresenterImpl(interactor, storage);
    }

    @After
    public void tearDown() {
        presenter = null;
    }

    @Test
    public void testMandatoryUpdate() throws Throwable {
        CheckResult checkResult = CheckResult.mandatoryUpdate("1.0.0", DEFAULT_METADATA);
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        Result result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new Result(
                UpdateStatus.MANDATORY,
                checkResult.getUpdateVersion(),
                checkResult.metadata()
        ));

        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.getUpdateVersion());
    }

    @Test
    public void testNoUpdate() throws Throwable {
        CheckResult checkResult = CheckResult.noUpdate("1.0.0", DEFAULT_METADATA);
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        Result result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new Result(
                UpdateStatus.NO_UPDATE,
                checkResult.getUpdateVersion(),
                checkResult.metadata()
        ));

        verify(storage, times(0)).rememberLastNotifiedVersion(anyString());
    }

    @Test
    public void testOptionalUpdateWhenNotNotifiedUpdate() throws Throwable {
        CheckResult checkResult = CheckResult.optionalUpdate("1.0.2", NotificationType.ONCE, DEFAULT_METADATA);
        when(storage.lastNotifiedVersion(null)).thenReturn("1.0.1");
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        Result result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new Result(
                UpdateStatus.OPTIONAL,
                checkResult.getUpdateVersion(),
                checkResult.metadata()
        ));

        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.getUpdateVersion());
    }

    @Test
    public void testMandatoryUpdateAlreadyNotifiedUpdateWithAlways() throws Throwable {
        CheckResult checkResult = CheckResult.optionalUpdate("1.0.2", NotificationType.ALWAYS, DEFAULT_METADATA);
        when(storage.lastNotifiedVersion(null)).thenReturn("1.0.2");
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        Result result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new Result(
                UpdateStatus.OPTIONAL,
                checkResult.getUpdateVersion(),
                checkResult.metadata()
        ));

        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.getUpdateVersion());
    }

    @Test
    public void testMandatoryUpdateAlreadyNotifiedUpdateWithOnce() throws Throwable {
        CheckResult checkResult = CheckResult.optionalUpdate("1.0.2", NotificationType.ONCE, DEFAULT_METADATA);
        when(storage.lastNotifiedVersion(null)).thenReturn("1.0.2");
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        Result result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new Result(
                UpdateStatus.NO_UPDATE,
                checkResult.getUpdateVersion(),
                checkResult.metadata()
        ));

        verify(storage, times(0)).rememberLastNotifiedVersion(anyString());
    }

    @Test
    public void testSyncCheckMandatory() throws Throwable {
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.MANDATORY, "1.0.0", new HashMap<String, String>());
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(ApplicationConfiguration.class))).thenCallRealMethod();

        Result actual = mock.check(loader, appConfig);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testSyncCheckOptional() throws Throwable {
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.OPTIONAL, "1.0.0", new HashMap<String, String>());
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(ApplicationConfiguration.class))).thenCallRealMethod();

        Result actual = mock.check(loader, appConfig);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testSyncCheckNoUpdate() throws Throwable {
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.NO_UPDATE, "1.0.0", new HashMap<String, String>());
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(ApplicationConfiguration.class))).thenCallRealMethod();

        Result actual = mock.check(loader, appConfig);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testAsyncCheckMandatory() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.MANDATORY, "1.0.0", new HashMap<String, String>());
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(Executor.class), any(UpdaterCallback.class), any(ApplicationConfiguration.class)))
                .thenCallRealMethod();
        when(mock.createCall()).thenCallRealMethod();

        mock.check(loader, new SingleThreadExecutor(), callback, appConfig);

        verify(callback, times(1)).onNewUpdate(expected.getVersion(), true, expected.getMetadata());
        verify(callback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckOptional() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.OPTIONAL, "1.0.0", new HashMap<String, String>());
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(Executor.class), any(UpdaterCallback.class), any(ApplicationConfiguration.class)))
                .thenCallRealMethod();
        when(mock.createCall()).thenCallRealMethod();

        mock.check(loader, new SingleThreadExecutor(), callback, appConfig);

        verify(callback, times(1)).onNewUpdate(expected.getVersion(), false, expected.getMetadata());
        verify(callback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckNoUpdate() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.NO_UPDATE, "1.0.0", new HashMap<String, String>());
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(Executor.class), any(UpdaterCallback.class), any(ApplicationConfiguration.class)))
                .thenCallRealMethod();
        when(mock.createCall()).thenCallRealMethod();

        mock.check(loader, new SingleThreadExecutor(), callback, appConfig);

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(1)).onNoUpdate(expected.getMetadata());
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckNoUpdateCancel() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.NO_UPDATE, "1.0.0", new HashMap<String, String>());
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(Executor.class), any(UpdaterCallback.class), any(ApplicationConfiguration.class)))
                .thenCallRealMethod();
        when(mock.createCall()).thenReturn(new UpdaterCancelable() {
            @Override
            public boolean isCanceled() {
                return true;
            }
        });

        mock.check(loader, new SingleThreadExecutor(), callback, appConfig);

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckMandatoryUpdateCancel() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.MANDATORY, "1.0.0", new HashMap<String, String>());
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(Executor.class), any(UpdaterCallback.class), any(ApplicationConfiguration.class)))
                .thenCallRealMethod();
        when(mock.createCall()).thenReturn(new UpdaterCancelable() {
            @Override
            public boolean isCanceled() {
                return true;
            }
        });

        mock.check(loader, new SingleThreadExecutor(), callback, appConfig);

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckOptionalUpdateCancel() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.OPTIONAL, "1.0.0", new HashMap<String, String>());
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(Executor.class), any(UpdaterCallback.class), any(ApplicationConfiguration.class)))
                .thenCallRealMethod();
        when(mock.createCall()).thenReturn(new UpdaterCancelable() {
            @Override
            public boolean isCanceled() {
                return true;
            }
        });

        mock.check(loader, new SingleThreadExecutor(), callback, appConfig);

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckError() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        Throwable throwable = new Exceptions.PrinceOfVersionsException();
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenThrow(throwable);
        when(mock.check(any(Loader.class), any(Executor.class), any(UpdaterCallback.class), any(ApplicationConfiguration.class)))
                .thenCallRealMethod();
        when(mock.createCall()).thenCallRealMethod();

        mock.check(loader, new SingleThreadExecutor(), callback, appConfig);

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(1)).onError(throwable);
    }

    @Test(expected = Exceptions.PrinceOfVersionsException.class)
    public void testSyncCheckError() throws Throwable {
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenThrow(new Exceptions.PrinceOfVersionsException());
        presenter.check(loader, appConfig);
    }

}
