package co.infinum.queenofversions;

public class GoogleInAppUpdateException extends Exception {

    GoogleInAppUpdateException(InAppUpdateError error) {
        super(error.name());
    }

    GoogleInAppUpdateException(Throwable exception) {
        super(exception);
    }
}
