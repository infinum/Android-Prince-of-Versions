package co.infinum.princeofversions.mocks;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import co.infinum.princeofversions.ApplicationConfiguration;
import co.infinum.princeofversions.RequirementChecker;

/**
 * Mocked default requirement checker that represents a implementation {@link RequirementChecker} that will be used for Unit tests
 */
public class MockDefaultRequirementChecker implements RequirementChecker {

    /**
     * Minimum SDK for mandatory version
     */
    private int sdkVersionCode;

    public MockDefaultRequirementChecker(ApplicationConfiguration appConfig) {
        this.sdkVersionCode = appConfig.sdkVersionCode();
    }

    @Override
    public boolean checkRequirements(String value) throws JSONException {
        int minSdk = Integer.parseInt(value);
        return minSdk <= this.sdkVersionCode;
    }
}
