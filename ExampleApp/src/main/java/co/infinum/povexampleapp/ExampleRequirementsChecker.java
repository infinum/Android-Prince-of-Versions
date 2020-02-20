package co.infinum.povexampleapp;

import android.support.annotation.NonNull;

import co.infinum.princeofversions.RequirementChecker;

/**
 * Custom Requirements checker used to demonstrate checking of custom requirements in JSON file
 */
public class ExampleRequirementsChecker implements RequirementChecker {

    @Override
    public boolean checkRequirements(@NonNull String value) {
        int min = Integer.parseInt(value);
        return min == 5;
    }
}
