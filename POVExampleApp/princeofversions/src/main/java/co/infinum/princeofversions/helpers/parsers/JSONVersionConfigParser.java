package co.infinum.princeofversions.helpers.parsers;

import com.github.zafarkhaja.semver.Version;

import org.json.JSONException;
import org.json.JSONObject;

import co.infinum.princeofversions.common.VersionContext;

public class JSONVersionConfigParser implements VersionConfigParser {

    public static final String ANDROID = "android";

    public static final String MINIMUM__VERSION = "minimum__version";

    public static final String MINIMUM_VERSION_CODE = "minimum_version_code";

    public static final String OPTIONAL_UPDATE = "optional_update";

    public static final String NOTIFICATION = "notification_type";

    public static final String VERSION = "version";

    public static final String VERSION_CODE = "version_code";

    private String currentVersionString;

    public JSONVersionConfigParser(String currentVersionString) {
        this.currentVersionString = currentVersionString;
    }

    @Override
    public VersionContext parse(String content) throws ParseException {

        try {
            JSONObject data = new JSONObject(content);
            return parse(data);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    public VersionContext parse(JSONObject data) throws JSONException {
        JSONObject android = data.getJSONObject(ANDROID);
        String min = android.getString(MINIMUM__VERSION);

        JSONObject updateObject = android.getJSONObject(OPTIONAL_UPDATE);
        String update = updateObject.getString(VERSION);

        Version minVersion = Version.valueOf(min);
        Version updateVersion = Version.valueOf(update);
        Version currentVersion = Version.valueOf(currentVersionString);

        //TODO here needs to be handled the use-case where current is "1.0", the lib requires for it to be "1.0.0" at minimum, so
        //TODO if its "1.0" it just plainly crashes

        return build(currentVersion, minVersion, updateVersion, android, updateObject);
    }

    private VersionContext build(Version currentVersion, Version minVersion, Version updateVersion, JSONObject android,
                                 JSONObject updateObject) throws JSONException {
        VersionContext.Version minVersionContext = null;
        VersionContext.Version updateVersionContext = null;
        if (android.has(MINIMUM_VERSION_CODE)) {
            minVersionContext = new VersionContext.Version(minVersion.toString(), android.getString(MINIMUM_VERSION_CODE));
        } else {
            minVersionContext = new VersionContext.Version(minVersion.toString());
        }
        if (updateObject.has(VERSION_CODE)) {
            updateVersionContext = new VersionContext.Version(updateVersion.toString(), updateObject.getString(VERSION_CODE));
        } else {
            updateVersionContext = new VersionContext.Version(updateVersion.toString());
        }

        return new VersionContext(
                currentVersion.toString(),
                minVersionContext,
                currentVersion.lessThan(minVersion),
                new VersionContext.UpdateContext(
                        updateVersionContext,
                        updateObject.getString(NOTIFICATION)
                ),
                currentVersion.lessThan(updateVersion)
        );
    }
}
