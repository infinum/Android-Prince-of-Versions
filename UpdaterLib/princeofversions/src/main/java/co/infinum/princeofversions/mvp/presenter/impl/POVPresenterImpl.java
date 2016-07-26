package co.infinum.princeofversions.mvp.presenter.impl;

import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.interfaces.VersionRepository;
import co.infinum.princeofversions.mvp.interactor.POVInteractor;
import co.infinum.princeofversions.mvp.interactor.listeners.POVInteractorListener;
import co.infinum.princeofversions.mvp.presenter.POVPresenter;
import co.infinum.princeofversions.mvp.view.POVView;

/**
 * Created by stefano on 08/07/16.
 */
public class POVPresenterImpl implements POVPresenter {

    private POVView view;
    private POVInteractor interactor;
    private VersionRepository repository;

    public POVPresenterImpl(POVView view, POVInteractor interactor, VersionRepository repository) {
        this.view = view;
        this.interactor = interactor;
        this.repository = repository;
    }

    @Override
    public void onCancel() {
        interactor.cancel();
    }

    @Override
    public void checkForUpdates() {
        interactor.checkForUpdates(new POVInteractorListener() {
            @Override
            public void onUpdateAvailable(VersionContext version) {
                // notify if there is no notification type or there was no notification before, or current version is not equal to last one.
                String notificationType = version.getOptionalUpdate().getNotificationType();
                String lastVersion = repository.getLastVersionName(null);
                if (notificationType == null || lastVersion == null || !lastVersion.equals(version.getOptionalUpdate().getVersion()
                        .getVersionString())) {
                    repository.setLastVersionName(version.getOptionalUpdate().getVersion().getVersionString());
                    view.notifyOptionalUpdate(version.getOptionalUpdate().getVersion().getVersionString());
                } else {
                    view.notifyNoUpdate();
                }
            }

            @Override
            public void onMandatoryUpdateAvailable(VersionContext version) {
                repository.setLastVersionName(version.getMinimumVersion().getVersionString());
                view.notifyMandatoryUpdate(version.getMinimumVersion().getVersionString());
            }

            @Override
            public void onNoUpdateAvailable(VersionContext version) {
                view.notifyNoUpdate();
            }

            @Override
            public void onError(@ErrorCode int error) {
                view.notifyError(error);
            }
        });
    }
}
