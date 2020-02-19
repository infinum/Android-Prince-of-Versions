package co.infinum.princeofversions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class handles requirement checking while JSON parsing
 */
public interface RequirementChecker {

    /**
     * This method is used to check if passed data requires specified requirements
     *
     * @param data
     * @return true or false depending if the data matched requirements
     */
    boolean checkRequirements(JSONObject data) throws JSONException;
}
