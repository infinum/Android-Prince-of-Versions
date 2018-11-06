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
    public CheckResult check(Loader loader, ApplicationConfiguration appConfig) throws Throwable {
        String content = loader.load();
        PrinceOfVersionsConfig config = configurationParser.parse(content);

        Version currentVersion = versionParser.parse(appConfig.version());

        if (!config.hasMandatory() && !config.hasOptional()) {
            // neither mandatory nor optional version is provided
            throw new IllegalStateException("Both mandatory and optional version are null.");
        }

        if (config.hasMandatory()) {
            Version mandatoryVersion = versionParser.parse(config.getMandatoryVersion());
            int mandatoryMinSdk = config.getMandatoryMinSdk();

            if (currentVersion.isLessThan(mandatoryVersion) && mandatoryMinSdk <= appConfig.sdkVersionCode()) {
                // if mandatory update exists - notify mandatory update
                // if there is also optional update available check if its version is greater than mandatory
                // in that case notify mandatory update with optional version, otherwise notify mandatory update with mandatory version
                if (config.hasOptional()) {
                    Version optionalVersion = versionParser.parse(config.getOptionalVersion());
                    int optionalMinSdk = config.getOptionalMinSdk();
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
        if (config.hasOptional()) {
            Version optionalVersion = versionParser.parse(config.getOptionalVersion());
            int optionalMinSdk = config.getOptionalMinSdk();
            if (currentVersion.isLessThan(optionalVersion) && optionalMinSdk <= appConfig.sdkVersionCode()) {
                return CheckResult.optionalUpdate(optionalVersion.value(), config.getOptionalNotificationType(), config.getMetadata());
            }
        }

        return CheckResult.noUpdate(currentVersion.value(), config.getMetadata());
    }
}
