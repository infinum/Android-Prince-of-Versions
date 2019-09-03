package com.example.queen_of_versions;

import android.app.Activity;
import android.content.Context;

import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.UpdaterCallback;

public class Playground {

    public Playground(){

        UpdaterCallback googleCallback = new GoogleInAppUpdateCallback(new Activity());
        PrinceOfVersions princeOfVersions = new PrinceOfVersions.Builder().build(new Activity());

        princeOfVersions.checkForUpdates("paste.com",googleCallback);
    }
}
