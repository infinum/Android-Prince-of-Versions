package co.infinum.princeofversions.helpers.parsers;

import com.github.zafarkhaja.semver.Version;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.exceptions.ParseException;

/**
 * This class represents parser for parsing loaded update configuration in <a href="http://www.json.org/">JSON</a> format.
 * <p>After parsing JSON content, class creates VersionContext holder instance from it.</p>
 * Content must contain at least minimum version as string, but optionally optional update version and notification type can be provided.
 * Valid content is represented as one JSON object that contains key <i>android</i>. Value from <i>android</i> key is another JSON
 * object. Android object can contain keys as follow:
 * <ul>
 * <li><i>minimum_version</i></li>
 * <li><i>latest_version</i></li>
 * </ul>.
 * Valid content can also have <i>meta</i> key. Value contained in <i>meta</i> key is JSON object containing metadata ordered in key-value
 * pairs.
 * Minimum version value is represented as string, minimum version code as integer and optional update is JSON object with following
 * keys:
 * <ul>
 * <li><i>version</i></li>
 * <li><i>notification_type</i></li>
 * </ul>.
 * Again, version value is represented as string, and notification_type is string with one of following values:
 * <ul>
 * <li><i>ALWAYS</i></li>
 * <li><i>ONCE</i></li>
 * </ul>
 * <p>Minimum valid JSON content is available bellow:</p>
 * <pre>
 *  {
 *      "android": {
 *          "minimum_version": "1.2.3"
 *      }
 *  }
 * </pre>
 * <p>Full JSON content looks as follows:</p>
 * <pre>
 *  {
 *      "android": {
 *          "minimum_version": "1.2.3",
 *          "latest_version": {
 *              "version": "2.4.5",
 *              "notification_type": "ONCE"
 *          }
 *      },
 *      "meta": {
 *          "key1": "value1",
 *          "key2": "value2"
 *      }
 *  }
 * </pre>
 *
 * @see <a href="http://www.json.org/">JSON</a>
 */
public class JsonVersionConfigParser implements VersionConfigParser {

    /**
     * Android key
     */
    public static final String ANDROID = "android";

    /**
     * Minimum version key
     */
    public static final String MINIMUM_VERSION = "minimum_version";

    /**
     * Latest version key
     */
    public static final String LATEST_VERSION = "latest_version";

    /**
     * Notification type key
     */
    public static final String NOTIFICATION = "notification_type";

    /**
     * Optional update version key
     */
    public static final String VERSION = "version";

    /**
     * Metadata key
     */
    public static final String META = "meta";

    /**
     * Last minSdk key
     */
    public static final String LAST_MIN_SDK = "minimum_version_min_sdk";

    /**
     * New minSdk key
     */
    public static final String NEW_MIN_SDK = "min_sdk";

    /**
     * Application version
     */
    private VersionContext.Version currentVersion;

    /**
     * Creates a new instance of parser providing current application version as argument.
     *
     * @param currentVersion Current application version.
     */
    public JsonVersionConfigParser(VersionContext.Version currentVersion) {
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
     *
     * @param data Root object from configuration.
     * @return VersionContext holder containing all parsed data.
     * @throws JSONException if error while parsing JSON occurred.
     */
    public VersionContext parse(JSONObject data) throws JSONException {
        Version currentVersion = Version.valueOf(this.currentVersion.getVersionString());
        Version minVersion = extractMinimumVersion(data);
        Version latestVersion = extractLatestVersion(data);
        Map<String, String> metadata = extractMetadata(data);
        String notificationType = extractNotificationType(data);
        int lastMinSdk = extractLastMinSdk(data);
        int newMinSdk = extractNewMinSdk(data);

        VersionContext versionContext;

        if (minVersion != null && latestVersion != null) {
            versionContext = new VersionContext(
                    this.currentVersion,
                    new VersionContext.Version(minVersion.toString()),
                    currentVersion.lessThan(minVersion),
                    new VersionContext.UpdateContext(new VersionContext.Version(latestVersion.toString()), notificationType, lastMinSdk,
                            newMinSdk),
                    currentVersion.lessThan(latestVersion)
            );
        } else if (latestVersion != null) {
            versionContext = new VersionContext(
                    this.currentVersion,
                    null,
                    false,
                    new VersionContext.UpdateContext(new VersionContext.Version(latestVersion.toString()), notificationType),
                    currentVersion.lessThan(latestVersion)
            );
        } else if (minVersion != null) {
            versionContext = new VersionContext(
                    this.currentVersion,
                    new VersionContext.Version(minVersion.toString()),
                    currentVersion.lessThan(minVersion)
            );
        } else {
            throw new com.github.zafarkhaja.semver.ParseException("Both min and latest versions are null!");
        }

        versionContext.setMetadata(metadata);

        return versionContext;
    }

    private String extractNotificationType(JSONObject data) throws JSONException {
        if (data.has(ANDROID)) {
            JSONObject android = data.getJSONObject(ANDROID);
            if (android.has(LATEST_VERSION)) {
                JSONObject updateObject = android.getJSONObject(LATEST_VERSION);
                if (updateObject.has(NOTIFICATION)) {
                    return updateObject.getString(NOTIFICATION);
                }
            }
        }
        return null;
    }

    private Version extractLatestVersion(JSONObject data) throws JSONException {
        if (data.has(ANDROID)) {
            JSONObject android = data.getJSONObject(ANDROID);
            if (android.has(LATEST_VERSION)) {
                JSONObject updateObject = android.getJSONObject(LATEST_VERSION);
                if (updateObject.has(VERSION)) {
                    String latest = updateObject.getString(VERSION);
                    if (latest != null && !latest.equals("null")) {
                        return Version.valueOf(latest);
                    }
                }
            }
        }
        return null;
    }

    private Version extractMinimumVersion(JSONObject data) throws JSONException {
        if (data.has(ANDROID)) {
            JSONObject android = data.getJSONObject(ANDROID);
            if (android.has(MINIMUM_VERSION)) {
                String min = android.getString(MINIMUM_VERSION);
                if (min != null && !min.equals("null")) {
                    return Version.valueOf(min);
                }
            }
        }
        return null;
    }

    private Map<String, String> extractMetadata(JSONObject data) throws JSONException {
        Map<String, String> metadata = new HashMap<>();
        if (data.has(META) && data.get(META) instanceof JSONObject) {
            JSONObject metadataObject = data.getJSONObject(META);

            Iterator<String> metadataIterator = metadataObject.keys();
            while (metadataIterator.hasNext()) {
                String key = metadataIterator.next();
                Object object = metadataObject.get(key);
                if (object instanceof String) {
                    metadata.put(key, (String) object);
                }
            }
        }
        return metadata;
    }

    private int extractLastMinSdk(JSONObject data) throws JSONException {
        int lastMinSdk;
        if (data.has(ANDROID)) {
            JSONObject android = data.getJSONObject(ANDROID);
            if (android.has(LAST_MIN_SDK)) {
                lastMinSdk = android.getInt(LAST_MIN_SDK);
                if (lastMinSdk != 0) {
                    return lastMinSdk;
                }
            }
        }
        return 0;
    }

    private int extractNewMinSdk(JSONObject data) throws JSONException {
        int newMinSdk;
        if (data.has(ANDROID)) {
            JSONObject android = data.getJSONObject(ANDROID);
            if (android.has(LATEST_VERSION)) {
                JSONObject latestVersion = android.getJSONObject(LATEST_VERSION);
                if (latestVersion.has(NEW_MIN_SDK)) {
                    newMinSdk = latestVersion.getInt(NEW_MIN_SDK);
                    if (newMinSdk != 0) {
                        return newMinSdk;
                    }
                }
            }
        }
        return 0;
    }
}
