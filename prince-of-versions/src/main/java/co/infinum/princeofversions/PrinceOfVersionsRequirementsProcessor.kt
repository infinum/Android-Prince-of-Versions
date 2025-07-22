package co.infinum.princeofversions

/**
 * Processes a map of requirements against a set of registered requirement checkers.
 *
 * @param checkers A map of requirement keys to their corresponding [RequirementChecker] implementations.
 * A defensive copy is made to ensure the internal map is not modified externally.
 */
internal class PrinceOfVersionsRequirementsProcessor(checkers: Map<String, RequirementChecker> = emptyMap()) {

    private val installedCheckers: Map<String, RequirementChecker> = HashMap(checkers)

    /**
     * Checks if all provided requirements are satisfied by the installed checkers.
     *
     * This method iterates through all requirements. For a requirement to be considered satisfied,
     * a corresponding checker must be registered for its key, and that checker must return true.
     * If any requirement is not met, or if any checker throws an exception, the method returns false.
     *
     * @param requirements A map of requirement keys to their required values.
     * @return true if all requirements are satisfied, false otherwise.
     */
    fun areRequirementsSatisfied(requirements: Map<String, String?>): Boolean {
        return runCatching {
            requirements.all { (key, value) ->
                // A requirement is met if a checker exists for its key and that checker returns true.
                // If value is null, it's considered not satisfied.
                value != null && installedCheckers[key]?.checkRequirements(value) == true
            }
        }.getOrDefault(false)
    }
}
