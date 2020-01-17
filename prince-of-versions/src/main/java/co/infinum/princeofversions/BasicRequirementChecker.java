package co.infinum.princeofversions;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represent a concrete implementation of {@link RequirementChecker} that will be used by a default.
 */
public class BasicRequirementChecker implements RequirementChecker {

    /**
     * Minimum SDK for mandatory version
     */
    private static final String MANDATORY_MIN_SDK = "requiredAndroidVersion";
    private int sdkVersionCode = Build.VERSION.SDK_INT;

    /**
     * Basic implementation of this method that is going to be used by default.
     * By default the only requirements we are checking, is if is required android lower than required.
     *
     * @param data Json data that contains all requirements for new update
     * @return true or false depending if are required requirements matched
     */
    @Override
    public boolean checkRequirements(JSONObject data) throws JSONException {
        if (data.has(MANDATORY_MIN_SDK)) {
            int minSdk = data.getInt(MANDATORY_MIN_SDK);
            return minSdk <= sdkVersionCode;
        } else {
            return false;
        }
    }
}
