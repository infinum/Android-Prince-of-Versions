package co.infinum.princeofversions

/**
 * A call represents a started check for update request which can be canceled.
 */
interface PrinceOfVersionsCancelable {
    /**
     * Cancel the call. Callback won't be notified after invocation of this method.
     */
    fun cancel()

    /**
     * Returns true if call is canceled, false otherwise.
     *
     * @return true if call is canceled, false otherwise.
     */
    val isCanceled: Boolean
}
