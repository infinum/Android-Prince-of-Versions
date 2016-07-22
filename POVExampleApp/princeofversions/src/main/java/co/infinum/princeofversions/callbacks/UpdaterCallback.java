package co.infinum.princeofversions.callbacks;

import co.infinum.princeofversions.common.ErrorCode;

/**
 * Created by stefano on 08/07/16.
 */
public interface UpdaterCallback {

    void onNewUpdate(String version, boolean isMandatory);

    void onNoUpdate();

    void onError(@ErrorCode int error);
}
