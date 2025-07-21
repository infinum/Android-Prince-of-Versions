package co.infinum.princeofversions

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RequirementsProcessorTest {

    @Test
    fun checkRequiredOsVersionWhenGreaterThanDevice() {
        val checker: RequirementChecker = PrinceOfVersionsDefaultRequirementsChecker { 23 }
        val processor = PrinceOfVersionsRequirementsProcessor(mapOf(PrinceOfVersionsDefaultRequirementsChecker.KEY to checker))
        val requirements = mapOf(PrinceOfVersionsDefaultRequirementsChecker.KEY to "25")

        val result = processor.areRequirementsSatisfied(requirements)

        assertThat(result).isFalse()
    }

    @Test
    fun checkRequiredOsVersionWhenLessThanDevice() {
        val checker: RequirementChecker = PrinceOfVersionsDefaultRequirementsChecker { 25 }
        val processor = PrinceOfVersionsRequirementsProcessor(mapOf(PrinceOfVersionsDefaultRequirementsChecker.KEY to checker))
        val requirements = mapOf(PrinceOfVersionsDefaultRequirementsChecker.KEY to "23")

        val result = processor.areRequirementsSatisfied(requirements)

        assertThat(result).isTrue()
    }

    @Test
    fun checkRequiredOsVersionWhenEqualToDevice() {
        val checker: RequirementChecker = PrinceOfVersionsDefaultRequirementsChecker { 23 }
        val processor = PrinceOfVersionsRequirementsProcessor(mapOf(PrinceOfVersionsDefaultRequirementsChecker.KEY to checker))
        val requirements = mapOf(PrinceOfVersionsDefaultRequirementsChecker.KEY to "23")

        val result = processor.areRequirementsSatisfied(requirements)

        assertThat(result).isTrue()
    }

    @Test
    fun checkRequiredOsVersionWhenRequirementValueIsNull() {
        val checker: RequirementChecker = PrinceOfVersionsDefaultRequirementsChecker { 23 }
        val processor = PrinceOfVersionsRequirementsProcessor(mapOf(PrinceOfVersionsDefaultRequirementsChecker.KEY to checker))
        val requirements = mapOf(PrinceOfVersionsDefaultRequirementsChecker.KEY to null)

        val result = processor.areRequirementsSatisfied(requirements)

        assertThat(result).isFalse()
    }

    @Test
    fun testRequirementWithNoCheckerReturnsFalse() {
        val processor = PrinceOfVersionsRequirementsProcessor(emptyMap()) // No checkers registered
        val requirements = mapOf("some_unregistered_key" to "some_value")

        val result = processor.areRequirementsSatisfied(requirements)

        assertThat(result).isFalse()
    }

    @Test
    fun testRequirementCheckerThrowingExceptionReturnsFalse() {
        val checker = RequirementChecker { throw IllegalStateException("Checker failed") }
        val processor = PrinceOfVersionsRequirementsProcessor(mapOf("failing_key" to checker))
        val requirements = mapOf("failing_key" to "any_value")

        val result = processor.areRequirementsSatisfied(requirements)

        assertThat(result).isFalse()
    }

    @Test
    fun testDefaultCheckerWithInvalidValueReturnsFalse() {
        val checker: RequirementChecker = PrinceOfVersionsDefaultRequirementsChecker { 23 }
        val processor = PrinceOfVersionsRequirementsProcessor(mapOf(PrinceOfVersionsDefaultRequirementsChecker.KEY to checker))
        val requirements = mapOf(PrinceOfVersionsDefaultRequirementsChecker.KEY to "not-a-number")

        val result = processor.areRequirementsSatisfied(requirements)

        assertThat(result).isFalse()
    }
}