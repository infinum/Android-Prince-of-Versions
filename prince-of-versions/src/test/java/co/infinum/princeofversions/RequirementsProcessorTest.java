package co.infinum.princeofversions;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RequirementsProcessorTest {

    @Test
    public void checkRequiredOsVersionWhenGreaterThanDevice() {
        Map<String, RequirementChecker> checkers = new HashMap<>();
        checkers.put(
            PrinceOfVersionsDefaultRequirementsChecker.KEY,
            new PrinceOfVersionsDefaultRequirementsChecker(new MockApplicationVersionProvider(23))
        );
        PrinceOfVersionsRequirementsProcessor processor = new PrinceOfVersionsRequirementsProcessor(checkers);

        Map<String, String> requirements = new HashMap<>();
        requirements.put(PrinceOfVersionsDefaultRequirementsChecker.KEY, "25");
        boolean result = processor.areRequirementsSatisfied(requirements);

        assertThat(result).isFalse();
    }

    @Test
    public void checkRequiredOsVersionWhenLessThanDevice() {
        Map<String, RequirementChecker> checkers = new HashMap<>();
        checkers.put(
            PrinceOfVersionsDefaultRequirementsChecker.KEY,
            new PrinceOfVersionsDefaultRequirementsChecker(new MockApplicationVersionProvider(25))
        );
        PrinceOfVersionsRequirementsProcessor processor = new PrinceOfVersionsRequirementsProcessor(checkers);

        Map<String, String> requirements = new HashMap<>();
        requirements.put(PrinceOfVersionsDefaultRequirementsChecker.KEY, "23");
        boolean result = processor.areRequirementsSatisfied(requirements);

        assertThat(result).isTrue();
    }

    @Test
    public void checkRequiredOsVersionWhenEqualToDevice() {
        Map<String, RequirementChecker> checkers = new HashMap<>();
        checkers.put(
            PrinceOfVersionsDefaultRequirementsChecker.KEY,
            new PrinceOfVersionsDefaultRequirementsChecker(new MockApplicationVersionProvider(23))
        );
        PrinceOfVersionsRequirementsProcessor processor = new PrinceOfVersionsRequirementsProcessor(checkers);

        Map<String, String> requirements = new HashMap<>();
        requirements.put(PrinceOfVersionsDefaultRequirementsChecker.KEY, "23");
        boolean result = processor.areRequirementsSatisfied(requirements);

        assertThat(result).isTrue();
    }
}
