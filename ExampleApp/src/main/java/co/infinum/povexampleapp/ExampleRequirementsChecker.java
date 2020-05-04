package co.infinum.povexampleapp;

import co.infinum.princeofversions.RequirementChecker;
import javax.annotation.Nonnull;

/**
 * Custom Requirements checker used to demonstrate checking of custom requirements in JSON file
 */
public class ExampleRequirementsChecker implements RequirementChecker {

    @Override
    public boolean checkRequirements(@Nonnull String value) {
        int min = Integer.parseInt(value);
        return min == 5;
    }
}
