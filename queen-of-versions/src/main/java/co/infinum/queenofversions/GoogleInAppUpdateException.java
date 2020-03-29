package co.infinum.queenofversions;

public class GoogleInAppUpdateException extends Exception {

    private final InAppUpdateError error;

    GoogleInAppUpdateException(InAppUpdateError error) {
        super("Error occurred during update check. Error code is: " + error.name());
        this.error = error;
    }

    public InAppUpdateError error() {
        return error;
    }
}
