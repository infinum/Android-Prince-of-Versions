package co.infinum.princeofversions;

final class InteractorImpl implements Interactor {

    private ConfigurationParser configurationParser;

    private VersionParser versionParser;

    /**
     * Constructs interactor using provided {@link ConfigurationParser} and {@link VersionParser}.
     *
     * @param configurationParser        object which will be used for parsing update resource.
     * @param versionParser object which will be used for parsing specific version strings.
     */
    InteractorImpl(ConfigurationParser configurationParser, VersionParser versionParser) {
        this.configurationParser = configurationParser;
        this.versionParser = versionParser;
    }

    @Override
    public CheckResult check(final Loader loader, final ApplicationConfiguration appConfig) throws Throwable {
        String content = loader.load();
        PrinceOfVersionsConfig config = configurationParser.parse(content);

        Version currentVersion = versionParser.parse(appConfig.version());

        PrinceOfVersionsConfig.Version mandatoryConfigVersion = config.getMandatoryVersion();
        PrinceOfVersionsConfig.Version optionalConfigVersion = config.getOptionalVersion();

        if (mandatoryConfigVersion == null && optionalConfigVersion == null) {
            // neither mandatory nor optional version is provided
            throw new IllegalStateException("Both mandatory and optional version are null.");
        }

        if (mandatoryConfigVersion != null) {
            Version mandatoryVersion = versionParser.parse(mandatoryConfigVersion.getVersion());
            int mandatoryMinSdk = mandatoryConfigVersion.getMinSdk();

            if (currentVersion.isLessThan(mandatoryVersion) && mandatoryMinSdk <= appConfig.sdkVersionCode()) {
                // if mandatory update exists - notify mandatory update
                // if there is also optional update available check if its version is greater than mandatory
                // in that case notify mandatory update with optional version, otherwise notify mandatory update with mandatory version
                if (optionalConfigVersion != null) {
                    Version optionalVersion = versionParser.parse(optionalConfigVersion.getVersion());
                    int optionalMinSdk = optionalConfigVersion.getMinSdk();
                    if (optionalVersion.isGreaterThan(mandatoryVersion) && optionalMinSdk <= appConfig.sdkVersionCode()) {
                        // optional update also exists and has greater version than mandatory
                        return CheckResult.mandatoryUpdate(optionalVersion.value(), config.getMetadata());
                    }
                }
                // if there is no optional update or it isn't greater than mandatory - notify mandatory version
                return CheckResult.mandatoryUpdate(mandatoryVersion.value(), config.getMetadata());
            }
        }

        // if there is no mandatory update check for optional
        if (optionalConfigVersion != null) {
            Version optionalVersion = versionParser.parse(optionalConfigVersion.getVersion());
            int optionalMinSdk = optionalConfigVersion.getMinSdk();
            if (currentVersion.isLessThan(optionalVersion) && optionalMinSdk <= appConfig.sdkVersionCode()) {
                return CheckResult.optionalUpdate(optionalVersion.value(), config.getOptionalNotificationType(), config.getMetadata());
            }
        }

        return CheckResult.noUpdate(currentVersion.value(), config.getMetadata());
    }
}
