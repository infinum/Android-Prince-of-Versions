package co.infinum.princeofversions

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PrinceOfVersionsDefaultRequirementsCheckerTest {

    @Test
    fun testCheckRequirementsTrueWhenMinSdkIsLessThanOrEqualToProvided() {
        val provider = PrinceOfVersionsDefaultRequirementsChecker.ApplicationVersionProvider { 28 }
        val checker = PrinceOfVersionsDefaultRequirementsChecker(provider)

        assertThat(checker.checkRequirements("27")).isTrue()
        assertThat(checker.checkRequirements("28")).isTrue()
    }

    @Test
    fun testCheckRequirementsFalseWhenMinSdkIsGreaterThanProvided() {
        val provider = PrinceOfVersionsDefaultRequirementsChecker.ApplicationVersionProvider { 25 }
        val checker = PrinceOfVersionsDefaultRequirementsChecker(provider)

        assertThat(checker.checkRequirements("26")).isFalse()
        assertThat(checker.checkRequirements("30")).isFalse()
    }

    @Test(expected = NumberFormatException::class)
    fun testCheckRequirementsThrowsExceptionForInvalidInput() {
        val provider = PrinceOfVersionsDefaultRequirementsChecker.ApplicationVersionProvider { 25 }
        val checker = PrinceOfVersionsDefaultRequirementsChecker(provider)

        checker.checkRequirements("not_a_number")
    }

    @Test
    fun testDefaultConstructorDoesNotThrow() {
        val checker = PrinceOfVersionsDefaultRequirementsChecker()
        checker.checkRequirements("1") // Should not throw
    }
}
