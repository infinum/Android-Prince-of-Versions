package com.infinum.queenofversions;

class GoogleInAppUpdateException extends Exception {

    GoogleInAppUpdateException(GoogleException exception){
        super(exception.toString());
    }

    GoogleInAppUpdateException(Throwable error){
        super(error.getMessage());
    }
}
