package co.infinum.princeofversions.helpers.parsers;

import com.github.zafarkhaja.semver.Version;

import org.json.JSONException;
import org.json.JSONObject;

import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.exceptions.ParseException;

/**
 * This class represents parser for parsing loaded update configuration in <a href="http://www.json.org/">JSON</a> format.
 * <p>After parsing JSON content, class creates VersionContext holder instance from it.</p>
 * Content must contain at least minimum version as string, but optionally optional update version and notification type can be provided.
 * <p>
 *     Valid content is represented as one JSON object that contains key <i>android</i>. Value from <i>android</i> key is another JSON
 *     object. Android object can contain keys as follow:
 *     <ul>
 *         <li><i>minimum__version</i></li>
 *         <li><i>minimum_version_code</i></li>
 *         <li><i>optional_update</i></li>
 *     </ul>.
 *     Minimum version value is represented as string, minimum version code as integer and optional update is JSON object with following
 *     keys:
 *     <ul>
 *         <li><i>version</i></li>
 *         <li><i>version_code</i></li>
 *         <li><i>notification_type</i></li>
 *     </ul>.
 *     Again, version value is represented as string, version_code as integer and notification_type is string with one of following values:
 *     <ul>
 *         <li><i>ALWAYS</i></li>
 *         <li><i>ONCE</i></li>
 *     </ul>
 * </p>
 * <p>Minimum valid JSON content is available bellow:</p>
 * <pre>
 *  {
 *      "android": {
 *          "minimum__version": "1.2.3"
 *      }
 *  }
 * </pre>
 * <p>Full JSON content looks as follows:</p>
 * <pre>
 *  {
 *      "android": {
 *          "minimum__version": "1.2.3",
 *          "minimum_version_code": 14235,
 *          "optional_update": {
 *              "version": "2.4.5",
 *              "version_code": 42354,
 *              "notification_type": "ONCE"
 *          }
 *      }
 *  }
 * </pre>
 * @see <a href="http://www.json.org/">JSON</a>
 */
public class JSONVersionConfigParser implements VersionConfigParser {

    /**
     * Android key
     */
    public static final String ANDROID = "android";

    /**
     * Minimum version key
     */
    public static final String MINIMUM__VERSION = "minimum__version";

    /**
     * Minimum version code key
     */
    public static final String MINIMUM_VERSION_CODE = "minimum_version_code";

    /**
     * Optional update key
     */
    public static final String OPTIONAL_UPDATE = "optional_update";

    /**
     * Notification type key
     */
    public static final String NOTIFICATION = "notification_type";

    /**
     * Optional update version key
     */
    public static final String VERSION = "version";

    /**
     * Optional update version code key
     */
    public static final String VERSION_CODE = "version_code";

    /**
     * Application version
     */
    private VersionContext.Version currentVersion;

    /**
     * Creates a new instance of parser providing current application version as argument.
     * @param currentVersion Current application version.
     */
    public JSONVersionConfigParser(VersionContext.Version currentVersion) {
        this.currentVersion = currentVersion;
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

    /**
     * Starts parsing from root JSON object representing update configuration content.
     * @param data Root object from configuration.
     * @return VersionContext holder containing all parsed data.
     * @throws JSONException if error while parsing JSON occurred.
     */
    public VersionContext parse(JSONObject data) throws JSONException {
        /*  mandatory */
        JSONObject android = data.getJSONObject(ANDROID);
        String min = android.getString(MINIMUM__VERSION);
        Version currentVersion = Version.valueOf(this.currentVersion.getVersionString());
        Version minVersion = Version.valueOf(min);
        VersionContext.Version minVersionContext = new VersionContext.Version(minVersion.toString());

        if (android.has(MINIMUM_VERSION_CODE)) {
            minVersionContext.setVersionCode(android.getInt(MINIMUM_VERSION_CODE));
        }
        VersionContext versionContext = new VersionContext(
                this.currentVersion,
                minVersionContext,
                currentVersion.lessThan(minVersion)
        );

        /*  optional */
        if (android.has(OPTIONAL_UPDATE)) {
            JSONObject updateObject = android.getJSONObject(OPTIONAL_UPDATE);
            String update = updateObject.getString(VERSION);
            Version updateVersion = Version.valueOf(update);

            VersionContext.Version updateVersionContext = new VersionContext.Version(updateVersion.toString());
            if (updateObject.has(VERSION_CODE)) {
                updateVersionContext.setVersionCode(updateObject.getInt(VERSION_CODE));
            }

            VersionContext.UpdateContext updateContext = new VersionContext.UpdateContext(
                    updateVersionContext
            );
            if (updateObject.has(NOTIFICATION)) {
                updateContext.setNotificationType(updateObject.getString(NOTIFICATION));
            }
            versionContext.setOptionalUpdate(updateContext, currentVersion.lessThan(updateVersion));
        }

        return versionContext;
    }
}
