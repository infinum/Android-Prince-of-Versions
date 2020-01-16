package co.infinum.princeofversions;

import org.json.JSONObject;

/**
 *  Represent a concrete implementation of {@link RequirementChecker} that will be used by a default.
 */
public class BasicRequirementChecker implements RequirementChecker {

    /**
     * Basic implementation of this method that is going to be used by default.
     * By default the only requirements we are checking, is if is required android lower than required.
     *
     * @param data
     * @return true or false depending if are required requirements matched
     */
    @Override
    public boolean checkRequirements(JSONObject data) {
        return true;
    }
}
