package com.infinum.queenofversions;

public interface PrinceOfVersionsStateCallback {

    void onNoUpdate();

    void onError(Throwable error);
}
