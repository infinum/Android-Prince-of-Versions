package co.infinum.povexampleapp;

import org.json.JSONException;
import org.json.JSONObject;

import co.infinum.princeofversions.RequirementChecker;

/**
 * Example of custom requirements checker
 */

public class ExampleRequirementsChecker implements RequirementChecker {

    @Override
    public boolean checkRequirements(String value) {
        int min = Integer.parseInt(value);
        return min == 5;
    }
}
