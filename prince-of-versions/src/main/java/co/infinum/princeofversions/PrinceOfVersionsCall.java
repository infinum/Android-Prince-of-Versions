package co.infinum.princeofversions;

/**
 * A call represents a started check for update request which can be canceled.
 */
public interface PrinceOfVersionsCall {

    /**
     * Cancel the call. Callback won't be notified after invocation of this method.
     */
    void cancel();

    /**
     * Returns true if call is canceled, false otherwise.
     *
     * @return true if call is canceled, false otherwise.
     */
    boolean isCanceled();

}
