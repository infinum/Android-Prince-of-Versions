package co.infinum.princeofversions

/**
 * This class represents a started check for update request which can be canceled.
 */
internal class UpdaterCancelable : PrinceOfVersionsCancelable {

    /**
     * Flag.
     * `true` if call is canceled, `false` otherwise
     */
    override var isCanceled: Boolean = false
        private set

    override fun cancel() {
        isCanceled = true
    }
}