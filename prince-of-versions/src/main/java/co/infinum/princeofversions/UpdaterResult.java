package co.infinum.princeofversions;

import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.mvp.presenter.POVPresenter;
import co.infinum.princeofversions.mvp.view.POVView;

/**
 * This class represents calling context for specific update check.
 * <p>
 * Through instance of this class it is available to check if update check associated with this calling context finished and cancel
 * it if not.
 * If update check finished, eg. if this context is marked as consumed there is no way to cancel notifying result anymore.
 * Context is marked as consumed if result is notified or is just being notified. Cancel option is available until class set its
 * consumed flag or until cancel is called. Class will set consumed flag when result is computed and ready for notify. Class will set
 * isCancelled flag after cancel is called.
 * </p>
 */
public class UpdaterResult implements POVView {

    /**
     * Presenter instance associated with this calling context.
     */
    private POVPresenter presenter;

    /**
     * User provided callback for notifying result.
     */
    private UpdaterCallback callback;

    /**
     * Consumed flag.
     * true if result is computed, false otherwise.
     */
    private boolean isConsumed = false;

    /**
     * Cancel flag.
     * true if cancel is called, false otherwise.
     */
    private boolean isCancelled = false;

    /**
     * Creates a new calling context with user defined callback for notifying result.
     *
     * @param callback User defined callback for notifying result.
     */
    UpdaterResult(UpdaterCallback callback) {
        this.callback = callback;
    }

    /**
     * Method sets presenter instance.
     *
     * @param presenter Presenter.
     * @return this.
     */
    UpdaterResult setPresenter(POVPresenter presenter) {
        this.presenter = presenter;
        return this;
    }

    /**
     * Provides cancel functionality of calling context.
     * <p>
     * Computation can be cancelled anytime until it is computed.
     * After computation is done and result is ready cancel option is no longer available, eg. this method has no effect then.
     * </p>
     */
    public void cancel() {
        if (presenter != null && !isConsumed()) {
            presenter.onCancel();
            isCancelled = true;
        }
    }

    /**
     * Provides consumed flag for this calling context.
     * <p>
     * Context is consumed when all computation is done and result is ready for notifying.
     * If context is consumed cancel option has no effect anymore and result is provided through callback.
     * </p>
     *
     * @return true if context is already consumed, false otherwise.
     */
    public boolean isConsumed() {
        return isConsumed;
    }

    /**
     * Provides isCancelled flag for this calling context.
     * <p>
     * Context is marked as cancelled when cancel method is called.
     * </p>
     *
     * @return true if context is cancelled, false otherwise.
     */
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void notifyMandatoryUpdate(String version) {
        isConsumed = true;
        callback.onNewUpdate(version, true);
    }

    @Override
    public void notifyOptionalUpdate(String version) {
        isConsumed = true;
        callback.onNewUpdate(version, false);
    }

    @Override
    public void notifyNoUpdate() {
        isConsumed = true;
        callback.onNoUpdate();
    }

    @Override
    public void notifyError(@ErrorCode int error) {
        isConsumed = true;
        callback.onError(error);
    }

}
