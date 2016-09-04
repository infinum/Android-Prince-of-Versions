package co.infinum.princeofversions.mvp.view;

import co.infinum.princeofversions.common.ErrorCode;

/**
 * Created by stefano on 08/07/16.
 */
public interface PovView {

    void notifyMandatoryUpdate(String version);

    void notifyOptionalUpdate(String version);

    void notifyNoUpdate();

    void notifyError(@ErrorCode int error);
}
