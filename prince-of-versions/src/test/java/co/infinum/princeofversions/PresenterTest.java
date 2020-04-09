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

    @Mock
    UpdateInfo updateInfo;

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
        CheckResult checkResult = CheckResult.mandatoryUpdate(10, DEFAULT_METADATA, updateInfo);
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        UpdateResult result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new UpdateResult(
            updateInfo,
            DEFAULT_METADATA,
            UpdateStatus.REQUIRED_UPDATE_NEEDED,
            10
        ));

        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.getUpdateVersion());
    }

    @Test
    public void testNoUpdate() throws Throwable {
        CheckResult checkResult = CheckResult.noUpdate(10, DEFAULT_METADATA, updateInfo);
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        UpdateResult result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new UpdateResult(
            updateInfo,
            DEFAULT_METADATA,
            UpdateStatus.NO_UPDATE_AVAILABLE,
            checkResult.getUpdateVersion()
        ));

        verify(storage, times(0)).rememberLastNotifiedVersion(anyInt());
    }

    @Test
    public void testOptionalUpdateWhenNotNotifiedUpdate() throws Throwable {
        CheckResult checkResult = CheckResult.optionalUpdate(12, NotificationType.ONCE, DEFAULT_METADATA, updateInfo);
        when(storage.lastNotifiedVersion(null)).thenReturn(11);
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        UpdateResult result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new UpdateResult(
            updateInfo,
            DEFAULT_METADATA,
            UpdateStatus.NEW_UPDATE_AVAILABLE,
            checkResult.getUpdateVersion()
        ));

        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.getUpdateVersion());
    }

    @Test
    public void testMandatoryUpdateAlreadyNotifiedUpdateWithAlways() throws Throwable {
        CheckResult checkResult = CheckResult.optionalUpdate(12, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo);
        when(storage.lastNotifiedVersion(null)).thenReturn(12);
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        UpdateResult result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new UpdateResult(
            updateInfo,
            DEFAULT_METADATA,
            UpdateStatus.NEW_UPDATE_AVAILABLE,
            checkResult.getUpdateVersion()
        ));

        verify(storage, times(1)).rememberLastNotifiedVersion(checkResult.getUpdateVersion());
    }

    @Test
    public void testMandatoryUpdateAlreadyNotifiedUpdateWithOnce() throws Throwable {
        CheckResult checkResult = CheckResult.optionalUpdate(12, NotificationType.ONCE, DEFAULT_METADATA, updateInfo);
        when(storage.lastNotifiedVersion(null)).thenReturn(12);
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(checkResult);

        UpdateResult result = presenter.run(loader, appConfig);

        assertThat(result).isEqualTo(new UpdateResult(
            updateInfo,
            DEFAULT_METADATA,
            UpdateStatus.NO_UPDATE_AVAILABLE,
            checkResult.getUpdateVersion()
        ));

        verify(storage, times(0)).rememberLastNotifiedVersion(anyInt());
    }

    @Test
    public void testSyncCheckMandatory() throws Throwable {
        PresenterImpl mock = mock(PresenterImpl.class);
        UpdateResult expected = new UpdateResult(updateInfo, DEFAULT_METADATA, UpdateStatus.REQUIRED_UPDATE_NEEDED, 10);
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(ApplicationConfiguration.class))).thenCallRealMethod();

        UpdateResult actual = mock.check(loader, appConfig);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testSyncCheckOptional() throws Throwable {
        PresenterImpl mock = mock(PresenterImpl.class);
        UpdateResult expected = new UpdateResult(updateInfo, DEFAULT_METADATA, UpdateStatus.NEW_UPDATE_AVAILABLE, 10);
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(ApplicationConfiguration.class))).thenCallRealMethod();

        UpdateResult actual = mock.check(loader, appConfig);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testSyncCheckNoUpdate() throws Throwable {
        PresenterImpl mock = mock(PresenterImpl.class);
        UpdateResult expected = new UpdateResult(updateInfo, DEFAULT_METADATA, UpdateStatus.NO_UPDATE_AVAILABLE, 10);
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(ApplicationConfiguration.class))).thenCallRealMethod();

        UpdateResult actual = mock.check(loader, appConfig);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testAsyncCheckMandatory() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        UpdateResult expected =
            new UpdateResult(updateInfo, DEFAULT_METADATA, UpdateStatus.REQUIRED_UPDATE_NEEDED, 10);
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(Executor.class), any(UpdaterCallback.class), any(ApplicationConfiguration.class)))
            .thenCallRealMethod();
        when(mock.createCall()).thenCallRealMethod();

        mock.check(loader, new SingleThreadExecutor(), callback, appConfig);

        verify(callback, times(1)).onSuccess(expected);
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckOptional() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        UpdateResult expected = new UpdateResult(updateInfo, DEFAULT_METADATA, UpdateStatus.NEW_UPDATE_AVAILABLE, 10);
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(Executor.class), any(UpdaterCallback.class), any(ApplicationConfiguration.class)))
            .thenCallRealMethod();
        when(mock.createCall()).thenCallRealMethod();

        mock.check(loader, new SingleThreadExecutor(), callback, appConfig);

        verify(callback, times(1)).onSuccess(expected);
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckNoUpdate() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        UpdateResult expected = new UpdateResult(updateInfo, DEFAULT_METADATA, UpdateStatus.NO_UPDATE_AVAILABLE, 10);
        when(mock.run(any(Loader.class), any(ApplicationConfiguration.class))).thenReturn(expected);
        when(mock.check(any(Loader.class), any(Executor.class), any(UpdaterCallback.class), any(ApplicationConfiguration.class)))
            .thenCallRealMethod();
        when(mock.createCall()).thenCallRealMethod();

        mock.check(loader, new SingleThreadExecutor(), callback, appConfig);

        verify(callback, times(1)).onSuccess(expected);
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckNoUpdateCancel() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        UpdateResult expected = new UpdateResult(updateInfo, DEFAULT_METADATA, UpdateStatus.NO_UPDATE_AVAILABLE, 10);
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

        verify(callback, times(0)).onSuccess(expected);
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckMandatoryUpdateCancel() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        UpdateResult expected = new UpdateResult(updateInfo, DEFAULT_METADATA, UpdateStatus.REQUIRED_UPDATE_NEEDED, 10);
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

        verify(callback, times(0)).onSuccess(expected);
        verify(callback, times(0)).onError(any(Throwable.class));
    }

    @Test
    public void testAsyncCheckOptionalUpdateCancel() throws Throwable {
        UpdaterCallback callback = mock(UpdaterCallback.class);
        PresenterImpl mock = mock(PresenterImpl.class);
        UpdateResult expected = new UpdateResult(updateInfo, DEFAULT_METADATA, UpdateStatus.NEW_UPDATE_AVAILABLE, 10);
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

        verify(callback, times(0)).onSuccess(expected);
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

        verify(callback, times(0)).onSuccess(any(UpdateResult.class));
        verify(callback, times(1)).onError(any(Throwable.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testSyncCheckError() throws Throwable {
        when(interactor.check(any(Loader.class), any(ApplicationConfiguration.class))).thenThrow(new IllegalStateException());
        presenter.check(loader, appConfig);
    }
}
