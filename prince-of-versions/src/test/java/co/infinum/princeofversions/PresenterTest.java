package co.infinum.princeofversions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import co.infinum.princeofversions.mocks.SingleThreadExecutor;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
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
        CheckResult checkResult = CheckResult.mandatoryUpdate(10, DEFAULT_METADATA);
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        Result result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new Result(
            UpdateStatus.REQUIRED_UPDATE_NEEDED,
            checkResult.getUpdateVersion(),
            checkResult.metadata()
        ));

        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.getUpdateVersion());
    }

    @Test
    public void testNoUpdate() throws Throwable {
        CheckResult checkResult = CheckResult.noUpdate(10, DEFAULT_METADATA);
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        Result result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new Result(
            UpdateStatus.NO_UPDATE_AVAILABLE,
            checkResult.getUpdateVersion(),
            checkResult.metadata()
        ));

        verify(storage, times(0)).rememberLastNotifiedVersion(anyInt());
    }

    @Test
    public void testOptionalUpdateWhenNotNotifiedUpdate() throws Throwable {
        CheckResult checkResult = CheckResult.optionalUpdate(12, NotificationType.ONCE, DEFAULT_METADATA);
        when(storage.lastNotifiedVersion(null)).thenReturn(11);
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        Result result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new Result(
            UpdateStatus.NEW_UPDATE_AVAILABLE,
            checkResult.getUpdateVersion(),
            checkResult.metadata()
        ));

        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.getUpdateVersion());
    }

    @Test
    public void testMandatoryUpdateAlreadyNotifiedUpdateWithAlways() throws Throwable {
        CheckResult checkResult = CheckResult.optionalUpdate(12, NotificationType.ALWAYS, DEFAULT_METADATA);
        when(storage.lastNotifiedVersion(null)).thenReturn(12);
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        Result result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new Result(
            UpdateStatus.NEW_UPDATE_AVAILABLE,
            checkResult.getUpdateVersion(),
            checkResult.metadata()
        ));

        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.getUpdateVersion());
    }

    @Test
    public void testMandatoryUpdateAlreadyNotifiedUpdateWithOnce() throws Throwable {
        CheckResult checkResult = CheckResult.optionalUpdate(12, NotificationType.ONCE, DEFAULT_METADATA);
        when(storage.lastNotifiedVersion(null)).thenReturn(12);
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        Result result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new Result(
            UpdateStatus.NO_UPDATE_AVAILABLE,
            checkResult.getUpdateVersion(),
            checkResult.metadata()
        ));

        verify(storage, times(0)).rememberLastNotifiedVersion(anyInt());
    }

    @Test
    public void testSyncCheckMandatory() throws Throwable {
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.REQUIRED_UPDATE_NEEDED, 10, new HashMap<String, String>());
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(ApplicationConfiguration.class))).thenCallRealMethod();

        Result actual = mock.check(loader, appConfig);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testSyncCheckOptional() throws Throwable {
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.NEW_UPDATE_AVAILABLE, 10, new HashMap<String, String>());
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(ApplicationConfiguration.class))).thenCallRealMethod();

        Result actual = mock.check(loader, appConfig);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testSyncCheckNoUpdate() throws Throwable {
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.NO_UPDATE_AVAILABLE, 10, new HashMap<String, String>());
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(ApplicationConfiguration.class))).thenCallRealMethod();

        Result actual = mock.check(loader, appConfig);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testAsyncCheckMandatory() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.REQUIRED_UPDATE_NEEDED, 10, new HashMap<String, String>());
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
        Result expected = new Result(UpdateStatus.NEW_UPDATE_AVAILABLE, 10, new HashMap<String, String>());
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
        Result expected = new Result(UpdateStatus.NO_UPDATE_AVAILABLE, 10, new HashMap<String, String>());
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(Executor.class), any(UpdaterCallback.class), any(ApplicationConfiguration.class)))
            .thenCallRealMethod();
        when(mock.createCall()).thenCallRealMethod();

        mock.check(loader, new SingleThreadExecutor(), callback, appConfig);

        verify(callback, times(0)).onNewUpdate(anyInt(), anyBoolean(), ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(1)).onNoUpdate(expected.getMetadata());
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckNoUpdateCancel() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.NO_UPDATE_AVAILABLE, 10, new HashMap<String, String>());
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

        verify(callback, times(0)).onNewUpdate(anyInt(), anyBoolean(), ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckMandatoryUpdateCancel() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.REQUIRED_UPDATE_NEEDED, 10, new HashMap<String, String>());
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

        verify(callback, times(0)).onNewUpdate(anyInt(), anyBoolean(), ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckOptionalUpdateCancel() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        Result expected = new Result(UpdateStatus.NEW_UPDATE_AVAILABLE, 10, new HashMap<String, String>());
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

        verify(callback, times(0)).onNewUpdate(anyInt(), anyBoolean(), ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckError() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        Throwable throwable = new IllegalStateException();
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenThrow(throwable);
        when(mock.check(any(Loader.class), any(Executor.class), any(UpdaterCallback.class), any(ApplicationConfiguration.class)))
            .thenCallRealMethod();
        when(mock.createCall()).thenCallRealMethod();

        mock.check(loader, new SingleThreadExecutor(), callback, appConfig);

        verify(callback, times(0)).onNewUpdate(anyInt(), anyBoolean(), ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
        verify(callback, times(1)).onError(throwable);
    }

    @Test(expected = IllegalStateException.class)
    public void testSyncCheckError() throws Throwable {
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenThrow(new IllegalStateException());
        presenter.check(loader, appConfig);
    }
}
