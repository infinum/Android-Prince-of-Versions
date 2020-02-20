package co.infinum.princeofversions;

import org.json.JSONException;

/**
 * This class handles requirement checking while JSON parsing
 */
public interface RequirementChecker {

    /**
     * This method is used to check if passed data requires specified requirements
     *
     * @param value Value of the requirement we are checking
     * @return true or false depending if the data matched requirements
     */
    boolean checkRequirements(String value) throws JSONException;
}
