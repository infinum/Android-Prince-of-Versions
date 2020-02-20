package co.infinum.princeofversions;

import android.os.Build;

/**
 * Represent a concrete implementation of {@link RequirementChecker} that will be used by a default.
 */
class PrinceOfVersionsDefaultRequirementsChecker implements RequirementChecker {

    /**
     * Minimum SDK for mandatory version
     */
    private int sdkVersionCode = Build.VERSION.SDK_INT;

    /**
     * Basic implementation of this method that is going to be used by default.
     * By default the only requirements we are checking, is if is required android lower than required.
     *
     * @param value Json data that contains all requirements for new update
     * @return true or false depending if are required requirements matched
     */

    @Override
    public boolean checkRequirements(String value) {
        int minSdk = Integer.parseInt(value);
        return minSdk <= sdkVersionCode;
    }
}
