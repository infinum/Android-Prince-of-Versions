package co.infinum.princeofversions.mvp.presenter.impl;

import com.github.zafarkhaja.semver.Version;

import android.os.Build;

import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.interfaces.VersionRepository;
import co.infinum.princeofversions.mvp.interactor.PovInteractor;
import co.infinum.princeofversions.mvp.interactor.listeners.PovInteractorListener;
import co.infinum.princeofversions.mvp.presenter.PovPresenter;
import co.infinum.princeofversions.mvp.view.PovView;

public class PovPresenterImpl implements PovPresenter {

    private static final int STANDARD_VERSION_LENGTH = 3;

    private static final int FIRST_POSITION_IN_ARRAY = 0;

    private static final int SECOND_POSITION_IN_ARRAY = 1;

    private static final int THIRD_POSITION_IN_ARRAY = 2;

    private static final int SINGLE_NUMBER_LENGTH = 1;

    private static final String DASH_REGEX = "-";

    private static final String DOT_REGEX = "\\.";

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
                if (checkIfUpdateShouldBeMade(version)) {
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
            public void onError(@ErrorCode int error) {
                view.notifyError(error);
            }
        });
    }

    private boolean checkIfUpdateShouldBeMade(VersionContext version) {

        if (version.getOptionalUpdate().getNewMinSdk() <= Build.VERSION.SDK_INT) {
            return true;
        }

        //Splitting version 1.2.3 into three separate Strings - split by "."
        String[] currentVersionPieces = version.getCurrentVersion().getVersionString().split(DOT_REGEX);
        String[] updateVersionPieces = version.getOptionalUpdate().getVersion().getVersionString().split(DOT_REGEX);

        //Checking if after the split we got what we wanted, the array of 3 elements - major, minor and patch
        if (updateVersionPieces.length == STANDARD_VERSION_LENGTH && currentVersionPieces.length == STANDARD_VERSION_LENGTH) {

            //If we did, we parse them to integers and compare them afterwards
            int currentMajor = Integer.parseInt(currentVersionPieces[FIRST_POSITION_IN_ARRAY]);
            int updateMajor = Integer.parseInt(updateVersionPieces[FIRST_POSITION_IN_ARRAY]);
            int currentMinor = Integer.parseInt(currentVersionPieces[SECOND_POSITION_IN_ARRAY]);
            int updateMinor = Integer.parseInt(updateVersionPieces[SECOND_POSITION_IN_ARRAY]);

            if (currentMajor == updateMajor && currentMinor == updateMinor) {
                return comparePatches(currentVersionPieces[THIRD_POSITION_IN_ARRAY], updateVersionPieces[THIRD_POSITION_IN_ARRAY],
                        version);
            }
        }
        return false;
    }

    private boolean comparePatches(String currentVersionPiece, String updateVersionPiece, VersionContext version) {
        //If the patch part of version is a single number
        if (currentVersionPiece.length() == SINGLE_NUMBER_LENGTH) {
            //Parse the number into an int.
            int currentPatch = Integer.parseInt(currentVersionPiece);
            //Check if update's patch is a number
            if (updateVersionPiece.length() == SINGLE_NUMBER_LENGTH) {
                //If it is, try parsing it.
                int updatePatch = Integer.parseInt(updateVersionPiece);

                //Check if patches are consecutive, e.g. 1.0.2 and 1.0.3
                if (currentPatch + 1 == updatePatch) {
                    //Finally check if user actually supports the new minSdk
                    return version.getOptionalUpdate().getNewMinSdk() <= Build.VERSION.SDK_INT;
                }
                //If update's patch is not a single number, e.g. 1.0.2-rc12 (2-rc12 in this case)
            } else {
                //Check if it contains a "-" (dash) (2-rc12, 2-b5, 3-beta4) all of those variants contain a dash
                if (updateVersionPiece.contains(DASH_REGEX)) {
                    //If it does, split patch by dash - getting 2 and rc12 separately
                    String[] updatePatchPieces = updateVersionPiece.split(DASH_REGEX);
                    //If current + 1 is equal to 2 check if user can support the new minSdk on his phone
                    if (currentPatch + 1 == Integer.parseInt(updatePatchPieces[FIRST_POSITION_IN_ARRAY])) {
                        return version.getOptionalUpdate().getNewMinSdk() <= Build.VERSION.SDK_INT;
                    }
                }
            }
            //If current's patch is not a single number, e.g. 1.0.2-rc12 (2-rc12 in this case)
        } else {
            //Check if current version's patch contains "-" (dash)
            if (currentVersionPiece.contains(DASH_REGEX)) {
                //Split the current version's patch by dash "-"
                String[] currentPatchPieces = currentVersionPiece.split(DASH_REGEX);
                //If update's patch is a single number
                if (updateVersionPiece.length() == SINGLE_NUMBER_LENGTH) {
                    //Compare if they are the same after you add 1 to the current patch
                    if (Integer.parseInt(currentPatchPieces[FIRST_POSITION_IN_ARRAY]) + 1 == Integer.parseInt(updateVersionPiece)) {
                        //If they are, check if user supports the new minSdk
                        return version.getOptionalUpdate().getNewMinSdk() <= Build.VERSION.SDK_INT;
                    }
                    //If update is not a single number
                } else {
                    //Check if there's a dash "-"
                    if (updateVersionPiece.contains(DASH_REGEX)) {
                        //If so, split by dash "-"
                        String[] updatePatchPieces = updateVersionPiece.split(DASH_REGEX);
                        //Check current and update if they are the same after adding 1 to the current
                        if (Integer.parseInt(updatePatchPieces[FIRST_POSITION_IN_ARRAY]) == Integer
                                .parseInt(currentPatchPieces[FIRST_POSITION_IN_ARRAY]) + 1) {
                            //If so, check if user supports the new minSdk
                            return version.getOptionalUpdate().getNewMinSdk() <= Build.VERSION.SDK_INT;
                        }
                    }
                }
            }
        }
        return false;
    }
}
