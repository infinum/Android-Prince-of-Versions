package co.infinum.princeofversions.mocks;

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

    public MockDefaultRequirementChecker(int versionCode) {
        this.sdkVersionCode = versionCode;
    }

    @Override
    public boolean checkRequirements(String value) {
        int minSdk = Integer.parseInt(value);
        return minSdk <= this.sdkVersionCode;
    }
}
