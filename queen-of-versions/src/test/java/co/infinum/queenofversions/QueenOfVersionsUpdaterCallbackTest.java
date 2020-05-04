package co.infinum.queenofversions;

import co.infinum.princeofversions.NotificationType;
import co.infinum.princeofversions.Storage;
import co.infinum.princeofversions.UpdateInfo;
import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;
import co.infinum.queenofversions.mocks.MockAppUpdateData;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QueenOfVersionsUpdaterCallbackTest {

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
    OnInAppUpdateAvailable onInAppUpdateAvailable;

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
                onInAppUpdateAvailable,
                storage
        );

        UpdateResult result = new UpdateResult(
                new UpdateInfo(null, null, Collections.<String, String>emptyMap(), 0, NotificationType.ONCE),
                Collections.<String, String>emptyMap(),
                UpdateStatus.NEW_UPDATE_AVAILABLE,
                1
        );

        testing.onSuccess(result);

        verify(onSuccess).handleUpdateResultAsStatus(result);
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
                onInAppUpdateAvailable,
                storage
        );

        Throwable error = new Throwable();

        testing.onError(error);

        verify(onError).continueUpdateCheckAsStatus(error);
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
                onInAppUpdateAvailable,
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

        verify(onSuccess).handleUpdateResultAsStatus(result);
        verify(callback).onError(error);
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
                onInAppUpdateAvailable,
                storage
        );

        Throwable error = new Throwable();
        Throwable thrown = new RuntimeException();

        Mockito.when(onError.continueUpdateCheckAsStatus(Mockito.eq(error))).thenThrow(thrown);

        testing.onError(error);

        verify(onError).continueUpdateCheckAsStatus(error);
        verify(callback).onError(thrown);
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
                onInAppUpdateAvailable,
                storage
        );

        UpdateResult result = new UpdateResult(
                new UpdateInfo(null, null, Collections.<String, String>emptyMap(), 0, NotificationType.ONCE),
                Collections.<String, String>emptyMap(),
                UpdateStatus.NEW_UPDATE_AVAILABLE,
                1
        );

        testing.continueUpdateCheckBasedOnStatus(UpdateStatus.NO_UPDATE_AVAILABLE, result);

        verify(callback).onNoUpdate(ArgumentMatchers.<String, String>anyMap(), any(UpdateInfo.class));
    }

    public void successWithNoUpdateAndMandatoryRequiredNotifiesNoMandatoryUpdate() {
        QueenOfVersionsUpdaterCallback testing = new QueenOfVersionsUpdaterCallback(
                googleAppUpdater,
                callback,
                10,
                onSuccess,
                onError,
                onUpdateNotAllowed,
                onInAppUpdateAvailable,
                storage
        );

        testing.handleSuccess(
                MockAppUpdateData.createUnavailable(),
                11,
                true,
                null
        );

        verify(callback).onMandatoryUpdateNotAvailable(
                11,
                any(QueenOfVersionsInAppUpdateInfo.class),
                ArgumentMatchers.<String, String>anyMap(),
                any(UpdateInfo.class)
        );
    }
}
