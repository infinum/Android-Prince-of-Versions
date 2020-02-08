package co.infinum.povexampleapp;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import co.infinum.princeofversions.RequirementChecker;

/**
 * Example of custom requirements checker
 */

public class PrinceRequirementsChecker implements RequirementChecker {

    /**
     * Minimum SDK for mandatory version
     */
    private static final String REQUIRED_ANDROID_VERSION = "requiredAndroidVersion";
    private static final String REQUIRED_LETTER_LENGTH = "requiredNumberOfLetters";

    private int version;

    PrinceRequirementsChecker() {
        this.version = Build.VERSION.SDK_INT;
    }

    @Override
    public boolean checkRequirements(JSONObject data) throws JSONException {
        if (data.has(REQUIRED_ANDROID_VERSION)) {
            int min = data.getInt(REQUIRED_ANDROID_VERSION);
            if (version < min - 5) {
                return false;
            }
        }
        if (data.has(REQUIRED_LETTER_LENGTH)) {
            int min = data.getInt(REQUIRED_LETTER_LENGTH);
            return min < 10;
        }
        return true;
    }
}
