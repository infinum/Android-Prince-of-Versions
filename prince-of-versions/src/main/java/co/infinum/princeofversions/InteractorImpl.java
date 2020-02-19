package co.infinum.princeofversions;

import android.util.Log;

final class InteractorImpl implements Interactor {

    private ConfigurationParser configurationParser;

    /**
     * Constructs interactor using provided {@link ConfigurationParser}.
     *
     * @param configurationParser object which will be used for parsing update resource.
     */
    InteractorImpl(ConfigurationParser configurationParser) {
        this.configurationParser = configurationParser;
    }

    @Override
    public CheckResult check(final Loader loader, final ApplicationConfiguration appConfig) throws Throwable {
        String content = loader.load();
        PrinceOfVersionsConfig config = configurationParser.parse(content);
        Log.d("META:", config.getMetadata().toString());

        int currentVersion = appConfig.version();

        Integer mandatoryConfigVersion = config.getMandatoryVersion();
        Integer optionalConfigVersion = config.getOptionalVersion();

        if (mandatoryConfigVersion == null && optionalConfigVersion == null) {
            // neither mandatory nor optional version is provided
            throw new IllegalStateException("Both mandatory and optional version are null.");
        }

        if (mandatoryConfigVersion != null) {
            int mandatoryVersion = mandatoryConfigVersion;

            if (currentVersion < mandatoryVersion) {
                // if mandatory update exists - notify mandatory update
                // if there is also optional update available check if its version is greater than mandatory
                // in that case notify mandatory update with optional version, otherwise notify mandatory update with mandatory version
                if (optionalConfigVersion != null) {
                    int optionalVersion = optionalConfigVersion;
                    if (optionalVersion > mandatoryVersion) {
                        // optional update also exists and has greater version than mandatory
                        return CheckResult.mandatoryUpdate(optionalVersion, config.getMetadata());
                    }
                }
                // if there is no optional update or it isn't greater than mandatory - notify mandatory version
                return CheckResult.mandatoryUpdate(mandatoryVersion, config.getMetadata());
            }
        }

        // if there is no mandatory update check for optional
        if (optionalConfigVersion != null) {
            int optionalVersion = optionalConfigVersion;
            if (currentVersion < optionalVersion) {
                return CheckResult.optionalUpdate(optionalVersion, config.getOptionalNotificationType(),
                    config.getMetadata());
            }
        }

        return CheckResult.noUpdate(currentVersion, config.getMetadata());
    }
}
