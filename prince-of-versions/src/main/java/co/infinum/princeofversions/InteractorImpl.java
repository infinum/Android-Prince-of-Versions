package co.infinum.princeofversions;

public class InteractorImpl implements Interactor {

    private Parser parser;

    private VersionParser versionParser;

    /**
     * Constructs interactor using provided {@link Parser} and {@link VersionParser}.
     *
     * @param parser        object which will be used for parsing update resource.
     * @param versionParser object which will be used for parsing specific version strings.
     */
    public InteractorImpl(Parser parser, VersionParser versionParser) {
        this.parser = parser;
        this.versionParser = versionParser;
    }

    @Override
    public CheckResult check(Loader loader, ApplicationConfiguration appConfig) throws Throwable {
        String content = loader.load();
        PrinceOfVersionsConfig config = parser.parse(content);
        Version currentVersion = versionParser.parse(appConfig.version());
        Version mandatoryVersion = versionParser.parse(config.getMandatoryVersion());
        int mandatoryMinSdk = config.getMandatoryMinSdk();
        if (config.hasOptional()) {
            Version optionalVersion = versionParser.parse(config.getOptionalVersion());
            int optionalMinSdk = config.getOptionalMinSdk();
            // if optional update exists
            if (currentVersion.isLessThan(optionalVersion) && optionalMinSdk <= appConfig.minSdk()) {
                // first check if mandatory exists also - if do notify mandatory with optional version
                if (currentVersion.isLessThan(mandatoryVersion) && mandatoryMinSdk <= appConfig.minSdk()) {
                    return CheckResult.mandatoryUpdate(optionalVersion.value(), config.getMetadata());
                } else {
                    return CheckResult.optionalUpdate(optionalVersion.value(), config.getOptionalNotificationType(), config.getMetadata());
                }
            }
        }
        if (currentVersion.isLessThan(mandatoryVersion) && mandatoryMinSdk <= appConfig.minSdk()) {
            return CheckResult.mandatoryUpdate(mandatoryVersion.value(), config.getMetadata());
        }
        return CheckResult.noUpdate(currentVersion.value(), config.getMetadata());
    }
}
