package com.infinum.queenofversions;

class GoogleInAppUpdateException extends Exception {

    private final GoogleException error;

    GoogleInAppUpdateException(GoogleException exception) {
        super(exception.toString());
        this.error = exception;
    }

    GoogleInAppUpdateException(Throwable error){
        super(error.getMessage());
        this.error = GoogleException.ERROR_UNKNOWN;
    }

    GoogleException getError() {
        return error;
    }
}
