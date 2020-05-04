package co.infinum.queenofversions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.common.IntentSenderForResultStarter;
import com.google.android.play.core.install.model.AppUpdateType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nullable;

public final class QueenOfVersionsFragment extends Fragment {

    public static final String TAG = QueenOfVersionsFragment.class.getName();

    private static final int REQUEST_CODE = 29;

    @Nullable
    private AppUpdateManager appUpdateManager;

    @Nullable
    private UpdateResult updateResult;

    private Collection<QueenOfVersions.Callback> callbacks = new HashSet<>();

    @Nullable
    private AppUpdateInfo updateInfo;

    @AppUpdateType
    private int updateType;

    private boolean hasUpdateBeenConsumed = false;

    public QueenOfVersionsFragment() {
        setRetainInstance(true);
    }

    static QueenOfVersionsFragment get(FragmentActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        if (fm != null) {
            Fragment fragmentByTag = fm.findFragmentByTag(QueenOfVersionsFragment.TAG);
            if (fragmentByTag != null && !(fragmentByTag instanceof QueenOfVersionsFragment)) {
                throw new IllegalStateException(QueenOfVersionsFragment.TAG + " does not hold the QueenOfVersions fragment.");
            }
            QueenOfVersionsFragment fragment = (QueenOfVersionsFragment) fragmentByTag;
            if (fragment != null) {
                return fragment;
            } else {
                fragment = new QueenOfVersionsFragment();
                fm.beginTransaction()
                        .add(fragment, QueenOfVersionsFragment.TAG)
                        .commitNowAllowingStateLoss();
                return fragment;
            }
        } else {
            throw new IllegalStateException("Fragment manager is null.");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appUpdateManager = AppUpdateManagerFactory.create(context);

        if (updateInfo != null && !hasUpdateBeenConsumed) {
            internalStartUpdateFlow(updateInfo, updateType);
        }
    }

    @Override
    public void onDetach() {
        appUpdateManager = null;
        super.onDetach();
    }

    void startUpdateFlow(
            AppUpdateInfo updateInfo,
            @AppUpdateType int updateType,
            @Nullable UpdateResult updateResult,
            QueenOfVersions.Callback callback
    ) {
        callbacks.add(callback);
        this.updateResult = updateResult;

        internalStartUpdateFlow(updateInfo, updateType);
    }

    void detachCallback(QueenOfVersions.Callback callback) {
        callbacks.remove(callback);
    }

    private void internalStartUpdateFlow(
            AppUpdateInfo updateInfo,
            @AppUpdateType int updateType
    ) {
        AppUpdateManager updateManager = appUpdateManager;
        if (updateManager != null) {
            try {
                updateManager.startUpdateFlowForResult(
                        updateInfo,
                        updateType,
                        new QueenOfVersionsIntentSenderForResultStarter(updateType == AppUpdateType.IMMEDIATE),
                        REQUEST_CODE
                );
                hasUpdateBeenConsumed = true;
            } catch (IntentSender.SendIntentException e) {
                for (QueenOfVersions.Callback c : callbacks) {
                    c.onError(e);
                }
            }
        } else {
            this.updateInfo = updateInfo;
            this.updateType = updateType;
            hasUpdateBeenConsumed = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE == requestCode) {
            UpdateStatus updateStatus;
            if (AppUpdateType.IMMEDIATE == updateType) {
                updateStatus = UpdateStatus.REQUIRED_UPDATE_NEEDED;
            } else {
                updateStatus = UpdateStatus.NEW_UPDATE_AVAILABLE;
            }
            hasUpdateBeenConsumed = true;

            List<QueenOfVersions.Callback> copy = new ArrayList<>(callbacks);
            callbacks.clear();
            AppUpdateInfo localUpdateInfo = this.updateInfo;
            this.updateInfo = null;

            for (QueenOfVersions.Callback callback : copy) {
                if (Activity.RESULT_OK == resultCode) {
                    callback.onUpdateAccepted(QueenOfVersionsInAppUpdateInfo.from(localUpdateInfo), updateStatus, updateResult);
                } else {
                    callback.onUpdateDeclined(QueenOfVersionsInAppUpdateInfo.from(localUpdateInfo), updateStatus, updateResult);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private final class QueenOfVersionsIntentSenderForResultStarter implements IntentSenderForResultStarter {

        private final boolean isImmediateFlow;

        QueenOfVersionsIntentSenderForResultStarter(final boolean isImmediateFlow) {
            this.isImmediateFlow = isImmediateFlow;
        }

        @Override
        public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues,
                int extraFlags, Bundle options) throws IntentSender.SendIntentException {

            if (!isImmediateFlow) {
                // don't do this for immediate flow because once installed it closes play services activity too late
                // we could potentially start another check in-between installation done and activity closes
                // that would cause the same activity reorder to front and play services would close it because of the done update
                // for that reason second update wouldn't see an update activity

                // this is okay solution if immediate update activity isn't translucent
                // - otherwise it could create more activities on rotation
                flagsMask = flagsMask | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
                flagsValues = flagsValues | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
            }

            QueenOfVersionsFragment.this.startIntentSenderForResult(
                    intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        }
    }
}
