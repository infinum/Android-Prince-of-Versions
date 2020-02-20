package co.infinum.princeofversions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Represent a concrete implementation of {@link RequirementChecker} that will be used by a default.
 */
class PrinceOfVersionsCompositeRequirementsChecker implements RequirementChecker {

    private final Map<RequirementChecker, String> checkers;

    PrinceOfVersionsCompositeRequirementsChecker(final Map<RequirementChecker, String> checkers) {
        this.checkers = checkers;
    }

    @Override
    public boolean checkRequirements(String value) throws JSONException {
        JSONObject data = new JSONObject(value);
        for (final RequirementChecker checker : checkers.keySet()) {
            try {
                String key = checkers.get(checker);
                if (data.has(key)) {
                    if (!checker.checkRequirements(data.getString(key))) {
                        return false;
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
