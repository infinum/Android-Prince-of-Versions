package co.infinum.princeofversions.mvp.interactor.listeners;

import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.common.VersionContext;

public interface PovInteractorListener {

    void onUpdateAvailable(VersionContext version);

    void onMandatoryUpdateAvailable(VersionContext version);

    void onNoUpdateAvailable(VersionContext version);

    void onError(@ErrorCode int error);

}
