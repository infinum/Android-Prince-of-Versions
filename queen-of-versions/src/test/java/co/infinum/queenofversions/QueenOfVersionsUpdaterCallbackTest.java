package co.infinum.queenofversions;

import co.infinum.princeofversions.NotificationType;
import co.infinum.princeofversions.Storage;
import co.infinum.princeofversions.UpdateInfo;
import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QueenOfVersionsUpdaterCallbackTest {

    private static final int REQUEST_CODE = 123;

    @Mock
    GoogleAppUpdater googleAppUpdater;

    @Mock
    QueenOfVersions.Callback callback;

    @Mock
    OnPrinceOfVersionsSuccess onSuccess;

    @Mock
    OnPrinceOfVersionsError onError;

    @Mock
    OnUpdateNotAllowed onUpdateNotAllowed;

    @Mock
    Storage storage;

    @Test
    public void onSuccessCallsAdapter() {
        QueenOfVersionsUpdaterCallback testing = new QueenOfVersionsUpdaterCallback(
                googleAppUpdater,
                callback,
                10,
                onSuccess,
                onError,
                onUpdateNotAllowed,
                storage
        );

        UpdateResult result = new UpdateResult(
                new UpdateInfo(null, null, Collections.<String, String>emptyMap(), 0, NotificationType.ONCE),
                Collections.<String, String>emptyMap(),
                UpdateStatus.NEW_UPDATE_AVAILABLE,
                1
        );

        testing.onSuccess(result);

        Mockito.verify(onSuccess).handleUpdateResultAsStatus(result);
    }

    @Test
    public void onErrorCallsAdapter() throws Throwable {
        QueenOfVersionsUpdaterCallback testing = new QueenOfVersionsUpdaterCallback(
                googleAppUpdater,
                callback,
                10,
                onSuccess,
                onError,
                onUpdateNotAllowed,
                storage
        );

        UpdateResult result = new UpdateResult(
                new UpdateInfo(null, null, Collections.<String, String>emptyMap(), 0, NotificationType.ONCE),
                Collections.<String, String>emptyMap(),
                UpdateStatus.NEW_UPDATE_AVAILABLE,
                1
        );

        Throwable error = new Throwable();

        testing.onError(error);

        Mockito.verify(onError).continueUpdateCheckAsStatus(error);
    }

    @Test
    public void onSuccessHandlesError() {
        QueenOfVersionsUpdaterCallback testing = new QueenOfVersionsUpdaterCallback(
                googleAppUpdater,
                callback,
                10,
                onSuccess,
                onError,
                onUpdateNotAllowed,
                storage
        );

        UpdateResult result = new UpdateResult(
                new UpdateInfo(null, null, Collections.<String, String>emptyMap(), 0, NotificationType.ONCE),
                Collections.<String, String>emptyMap(),
                UpdateStatus.NEW_UPDATE_AVAILABLE,
                1
        );

        Throwable error = new RuntimeException();

        Mockito.when(onSuccess.handleUpdateResultAsStatus(Mockito.eq(result))).thenThrow(error);

        testing.onSuccess(result);

        Mockito.verify(onSuccess).handleUpdateResultAsStatus(result);
        Mockito.verify(callback).onError(error);
    }

    @Test
    public void onErrorHandlesError() throws Throwable {
        QueenOfVersionsUpdaterCallback testing = new QueenOfVersionsUpdaterCallback(
                googleAppUpdater,
                callback,
                10,
                onSuccess,
                onError,
                onUpdateNotAllowed,
                storage
        );

        Throwable error = new Throwable();
        Throwable thrown = new RuntimeException();

        Mockito.when(onError.continueUpdateCheckAsStatus(Mockito.eq(error))).thenThrow(thrown);

        testing.onError(error);

        Mockito.verify(onError).continueUpdateCheckAsStatus(error);
        Mockito.verify(callback).onError(thrown);
    }

    @Test
    public void statusNoUpdateWontContinueUpdateCheck() {
        QueenOfVersionsUpdaterCallback testing = new QueenOfVersionsUpdaterCallback(
                googleAppUpdater,
                callback,
                10,
                onSuccess,
                onError,
                onUpdateNotAllowed,
                storage
        );

        UpdateResult result = new UpdateResult(
                new UpdateInfo(null, null, Collections.<String, String>emptyMap(), 0, NotificationType.ONCE),
                Collections.<String, String>emptyMap(),
                UpdateStatus.NEW_UPDATE_AVAILABLE,
                1
        );

        testing.continueUpdateCheckBasedOnStatus(UpdateStatus.NO_UPDATE_AVAILABLE, result);

        Mockito.verify(callback).onNoUpdate(ArgumentMatchers.<String, String>anyMap(), ArgumentMatchers.any(UpdateInfo.class));
    }
}
