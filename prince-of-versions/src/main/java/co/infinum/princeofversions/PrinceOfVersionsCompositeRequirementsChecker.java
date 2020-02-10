package co.infinum.princeofversions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Represent a concrete implementation of {@link RequirementChecker} that will be used by a default.
 */
class PrinceOfVersionsCompositeRequirementsChecker implements RequirementChecker {

    private final List<RequirementChecker> checkers;

    PrinceOfVersionsCompositeRequirementsChecker(final List<RequirementChecker> checkers) {
        this.checkers = checkers;
    }

    @Override
    public boolean checkRequirements(JSONObject data) throws JSONException {
        for (final RequirementChecker checker : checkers) {
            if (!checker.checkRequirements(data)) {
                return false;
            }
        }
        return true;
    }
}
