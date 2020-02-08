package co.infinum.queenofversions;

class GoogleInAppUpdateException extends Exception {

    GoogleInAppUpdateException(InAppUpdateError exception) {
        super(exception.name());
    }

    GoogleInAppUpdateException(Throwable error) {
        super(error.getMessage());
    }
}
