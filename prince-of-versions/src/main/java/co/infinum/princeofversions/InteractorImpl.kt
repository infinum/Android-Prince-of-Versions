package co.infinum.princeofversions

/**
 * The concrete implementation of the [Interactor] that orchestrates the update check.
 *
 * @param configurationParser The parser used to interpret the update configuration resource.
 */
internal class InteractorImpl(
    private val configurationParser: ConfigurationParser
) : Interactor {

    @Throws(Throwable::class)
    override fun check(loader: Loader, appConfig: ApplicationConfiguration): CheckResult {
        val content = loader.load()
        val config = configurationParser.parse(content)
        val currentVersion = appConfig.version

        val updateInfo = UpdateInfo(
            requiredVersion = config.mandatoryVersion,
            lastVersionAvailable = config.optionalVersion,
            requirements = config.requirements,
            installedVersion = currentVersion,
            notificationFrequency = config.optionalNotificationType
        )

        val mandatoryVersion = config.mandatoryVersion
        val optionalVersion = config.optionalVersion

        // Check for mandatory update first
        if (mandatoryVersion != null && currentVersion < mandatoryVersion) {
            // If a mandatory update is available, it takes precedence.
            // The notified version will be the greater of the optional and mandatory versions.
            val versionToNotify = optionalVersion?.takeIf { it > mandatoryVersion } ?: mandatoryVersion
            return CheckResult.mandatoryUpdate(versionToNotify, config.metadata, updateInfo)
        }

        // If no mandatory update, check for an optional update
        if (optionalVersion != null && currentVersion < optionalVersion) {
            return CheckResult.optionalUpdate(optionalVersion, config.optionalNotificationType, config.metadata, updateInfo)
        }

        // If neither mandatory nor optional update is available, or if no versions are defined, return no update.
        check(!(mandatoryVersion == null && optionalVersion == null)) { "Both mandatory and optional version are null." }

        return CheckResult.noUpdate(currentVersion, config.metadata, updateInfo)
    }
}
