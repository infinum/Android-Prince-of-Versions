package co.infinum.queenofversions;

import co.infinum.princeofversions.PrinceOfVersionsCancelable;
import javax.annotation.Nullable;

class QueenOfVersionsCancelable implements PrinceOfVersionsCancelable {

    private final QueenOfVersionsUpdaterCallback callback;

    @Nullable
    private final PrinceOfVersionsCancelable delegate;

    private boolean isCanceled = false;

    QueenOfVersionsCancelable(QueenOfVersionsUpdaterCallback callback, @Nullable PrinceOfVersionsCancelable delegate) {
        this.callback = callback;
        this.delegate = delegate;
    }

    QueenOfVersionsCancelable(QueenOfVersionsUpdaterCallback callback) {
        this(callback, null);
    }

    @Override
    public void cancel() {
        isCanceled = true;
        callback.cancel();
        if (delegate != null) {
            delegate.cancel();
        }
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }
}
