package co.infinum.princeofversions.mvp.presenter.impl;

import com.github.zafarkhaja.semver.Version;

import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.interfaces.VersionRepository;
import co.infinum.princeofversions.mvp.interactor.PovInteractor;
import co.infinum.princeofversions.mvp.interactor.listeners.PovInteractorListener;
import co.infinum.princeofversions.mvp.presenter.PovPresenter;
import co.infinum.princeofversions.mvp.view.PovView;

public class PovPresenterImpl implements PovPresenter {

    private PovView view;

    private PovInteractor interactor;

    private VersionRepository repository;

    public PovPresenterImpl(PovView view, PovInteractor interactor, VersionRepository repository) {
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
        interactor.checkForUpdates(new PovInteractorListener() {
            @Override
            public void onUpdateAvailable(VersionContext version) {
                // notify if there is no notification type or there was no notification before, or current version is not equal to
                // last one.
                String notificationType = version.getOptionalUpdate().getNotificationType();
                String lastNotifiedVersion = repository.getLastVersionName(null);

                boolean notNotifiedUpdateAvailable = lastNotifiedVersion == null || !lastNotifiedVersion
                        .equals(version.getOptionalUpdate().getVersion()
                                .getVersionString());
                boolean alreadyNotifiedUpdateAvailable = lastNotifiedVersion != null && lastNotifiedVersion
                        .equals(version.getOptionalUpdate().getVersion()
                                .getVersionString());
                if (notNotifiedUpdateAvailable || (alreadyNotifiedUpdateAvailable && notificationType != null && notificationType
                        .equalsIgnoreCase("ALWAYS"))) {
                    repository.setLastVersionName(version.getOptionalUpdate().getVersion().getVersionString());
                    view.notifyOptionalUpdate(version.getOptionalUpdate().getVersion().getVersionString(), version.getMetadata());
                } else {
                    view.notifyNoUpdate(version.getMetadata());
                }
            }

            @Override
            public void onMandatoryUpdateAvailable(VersionContext version) {

                String minimumVersion;

                try {
                    Version mandatoryVersion = Version.valueOf(version.getMinimumVersion().getVersionString());
                    Version optionalUpdate = Version.valueOf(version.getOptionalUpdate().getVersion().getVersionString());
                    //This covers a specific scenario
                    //1. User has 1.0.0. installed
                    //2. Two new versions are published: 1.1.0 (mandatory) and 1.1.1 (optional)
                    //3. The library should display mandatory update with optional version (1.1.1)
                    minimumVersion = optionalUpdate.greaterThan(mandatoryVersion)
                            ? version.getOptionalUpdate().getVersion().getVersionString()
                            : version.getMinimumVersion().getVersionString();

                } catch (Exception e) {
                    minimumVersion = version.getMinimumVersion().getVersionString();
                    e.printStackTrace();
                }

                repository.setLastVersionName(minimumVersion);
                view.notifyMandatoryUpdate(minimumVersion, version.getMetadata());
            }

            @Override
            public void onNoUpdateAvailable(VersionContext version) {
                view.notifyNoUpdate(version.getMetadata());
            }

            @Override
            public void onError(Throwable throwable) {
                view.notifyError(throwable);
            }
        });
    }
}
