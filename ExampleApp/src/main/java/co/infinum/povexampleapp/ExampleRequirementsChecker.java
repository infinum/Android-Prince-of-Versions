package co.infinum.povexampleapp;

import org.json.JSONException;
import org.json.JSONObject;

import co.infinum.princeofversions.RequirementChecker;

/**
 * Example of custom requirements checker
 */

public class ExampleRequirementsChecker implements RequirementChecker {

    /**
     * Minimum SDK for mandatory version
     */
    private static final String REQUIRED_LETTER_LENGTH = "requiredNumberOfLetters";

    @Override
    public boolean checkRequirements(JSONObject data) throws JSONException {
        if (data.has(REQUIRED_LETTER_LENGTH)) {
            int min = data.getInt(REQUIRED_LETTER_LENGTH);
            return min == 5;
        }
        return true;
    }
}
