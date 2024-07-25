package co.infinum.queenofversions;

import com.google.android.gms.tasks.OnFailureListener;

final class GoogleInAppUpdateFailureListener implements OnFailureListener {

    private final QueenOfVersions.Callback flexibleListener;

    GoogleInAppUpdateFailureListener(QueenOfVersions.Callback flexibleListener) {
        this.flexibleListener = flexibleListener;
    }

    @Override
    public void onFailure(Exception e) {
        flexibleListener.onError(e);
    }
}
