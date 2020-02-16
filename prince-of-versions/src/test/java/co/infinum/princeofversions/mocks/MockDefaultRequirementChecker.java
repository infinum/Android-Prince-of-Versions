package co.infinum.princeofversions.mocks;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import co.infinum.princeofversions.ApplicationConfiguration;
import co.infinum.princeofversions.RequirementChecker;

/**
 *  Mocked default requirement checker that represents a implementation {@link RequirementChecker} that will be used for Unit tests
 */
public class MockDefaultRequirementChecker implements RequirementChecker {

    /**
     * Minimum SDK for mandatory version
     */
    private static final String REQUIRED_ANDROID_VERSION = "requiredAndroidVersion";
    private int sdkVersionCode;

    public MockDefaultRequirementChecker(ApplicationConfiguration appConfig){
        this.sdkVersionCode = appConfig.sdkVersionCode();
    }

    @Override
    public boolean checkRequirements(JSONObject data) throws JSONException {
        if (data.has(REQUIRED_ANDROID_VERSION)) {
            int minSdk = data.getInt(REQUIRED_ANDROID_VERSION);
            return minSdk <= this.sdkVersionCode;
        } else {
            return false;
        }
    }
}
