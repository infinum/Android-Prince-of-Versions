package co.infinum.princeofversions.mvp.interactor.listeners;

import co.infinum.princeofversions.common.VersionContext;

public interface POVInteractorListener {

    public void onUpdateAvailable(VersionContext version);

    public void onMandatoryUpdateAvailable(VersionContext version);

    public void onNoUpdateAvailable(VersionContext version);

    public void onError(String error);

}
