package co.infinum.princeofversions;

/**
 * This class represents a started check for update request which can be canceled.
 */
public class UpdaterCall implements PrinceOfVersionsCall {

    /**
     * Flag.
     * {@code true} if call is canceled, {@code false} otherwise
     */
    private boolean flag;

    @Override
    public void cancel() {
        this.flag = true;
    }

    @Override
    public boolean isCanceled() {
        return flag;
    }
}
