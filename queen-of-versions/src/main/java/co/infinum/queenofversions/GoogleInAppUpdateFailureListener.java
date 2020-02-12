package co.infinum.queenofversions;

import com.google.android.play.core.tasks.OnFailureListener;

public class GoogleInAppUpdateFailureListener implements OnFailureListener {

    private final InAppUpdateProcessCallback flexibleListener;

    GoogleInAppUpdateFailureListener(InAppUpdateProcessCallback flexibleListener) {
        this.flexibleListener = flexibleListener;
    }

    @Override
    public void onFailure(Exception e) {
        flexibleListener.onFailed(e);
    }
}
