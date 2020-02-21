package co.infinum.princeofversions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class PrinceOfVersionsRequirementsProcessor {

    private final Map<String, RequirementChecker> installedCheckers;

    PrinceOfVersionsRequirementsProcessor(final Map<String, RequirementChecker> checkers) {
        this.installedCheckers = new HashMap<>(checkers);
    }

    PrinceOfVersionsRequirementsProcessor() {
        this(Collections.<String, RequirementChecker>emptyMap());
    }

    boolean areRequirementsSatisfied(final Map<String, String> requirements) {
        try {
            for (Map.Entry<String, String> requirement : requirements.entrySet()) {
                RequirementChecker checker = installedCheckers.get(requirement.getKey());
                if (checker == null || checker.checkRequirements(requirement.getValue())) {
                    continue;
                }
                return false;
            }
        } catch (Throwable error) {
            return false;
        }
        return true;
    }
}
