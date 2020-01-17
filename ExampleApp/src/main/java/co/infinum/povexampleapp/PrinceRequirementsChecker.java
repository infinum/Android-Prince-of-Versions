package co.infinum.povexampleapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import co.infinum.princeofversions.RequirementChecker;

/**
 *  Example of custom requirements checker
 */

public class PrinceRequirementsChecker implements RequirementChecker {

    /**
     * Minimum SDK for mandatory version
     */
    private static final String MANDATORY_MIN_SDK = "requiredAndroidVersion";
    private static final String REQUIRED_LETTER_LENGHT = "requiredNumberOfLetters";

    private int version;

    PrinceRequirementsChecker() {
        this.version = Build.VERSION.SDK_INT;
    }

    @Override
    public boolean checkRequirements(JSONObject data) throws JSONException {
        if(data.has(MANDATORY_MIN_SDK)){
            int min = data.getInt(MANDATORY_MIN_SDK);
            if(version < min-5){
                return false;
            }
        }
        if(data.has(REQUIRED_LETTER_LENGHT)){
            int min = data.getInt(REQUIRED_LETTER_LENGHT);
            return min < 10;
        }
        return true;
    }
}
