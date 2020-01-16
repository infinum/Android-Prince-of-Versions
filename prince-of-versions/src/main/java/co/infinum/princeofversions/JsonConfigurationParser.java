package co.infinum.princeofversions;

import android.support.annotation.VisibleForTesting;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * This class represents parser for parsing loaded update configuration in <a href="http://www.json.org/">JSON</a> format.
 * <p>After parsing JSON content, class creates a PrinceOfVersionsConfig holder instance.</p>
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
final class JsonConfigurationParser implements ConfigurationParser {

    /**
     * Android key
     */
    private static final String ANDROID = "android";

    /**
     * Minimum version key
     */
    private static final String MINIMUM_VERSION = "minimum_version";

    /**
     * Latest version key
     */
    private static final String LATEST_VERSION = "latest_version";

    /**
     * Notification type key
     */
    private static final String NOTIFICATION = "notification_type";

    /**
     * Optional update version key
     */
    private static final String VERSION = "version";

    /**
     * Metadata key
     */
    private static final String META = "meta";

    /**
     * Minimum SDK for mandatory version
     */
    private static final String MANDATORY_MIN_SDK = "minimum_version_min_sdk";

    /**
     * Minimum SDK for optional version
     */
    private static final String OPTIONAL_MIN_SDK = "min_sdk";

    private static final String NOTIFICATION_ALWAYS = "always";

    private RequirementChecker requirementChecker;

    JsonConfigurationParser(RequirementChecker requirementChecker){
        this.requirementChecker = requirementChecker;
    }

    @Override
    public PrinceOfVersionsConfig parse(String content) throws Throwable {
        JSONObject data = new JSONObject(content);
        PrinceOfVersionsConfig.Builder builder = new PrinceOfVersionsConfig.Builder();
        parseToBuilder(data, builder);
        return builder.build();
    }

    //TODO this parser needs to be changed in order to fit new JSON
    private void parseToBuilder(JSONObject data, PrinceOfVersionsConfig.Builder builder) throws JSONException {
        if (data.has(ANDROID)) {
            JSONObject android = data.getJSONObject(ANDROID);
            if (android.has(MINIMUM_VERSION)) {
                String min = android.getString(MINIMUM_VERSION);
                if (isNonEmpty(min)) {
                    builder.withMandatoryVersion(min);
                }
            }
            if (android.has(MANDATORY_MIN_SDK)) {
                builder.withMandatoryMinSdk(android.getInt(MANDATORY_MIN_SDK));
            }
            if (android.has(LATEST_VERSION)) {
                JSONObject updateObject = android.getJSONObject(LATEST_VERSION);
                if (updateObject.has(VERSION)) {
                    String latest = updateObject.getString(VERSION);
                    if (isNonEmpty(latest)) {
                        builder.withOptionalVersion(latest);
                    }
                }
                if (updateObject.has(OPTIONAL_MIN_SDK)) {
                    builder.withOptionalMinSdk(updateObject.getInt(OPTIONAL_MIN_SDK));
                }
                if (updateObject.has(NOTIFICATION)) {
                    String notification = updateObject.getString(NOTIFICATION);
                    builder.withOptionalNotificationType(
                            notification != null && notification.equalsIgnoreCase(NOTIFICATION_ALWAYS) ? NotificationType.ALWAYS
                                    : NotificationType.ONCE);
                }
            }
        } else {
            throw new IllegalStateException("Config resource does not contain android key");
        }
        if (data.has(META)) {
            Object meta = data.get(META);
            if (meta instanceof JSONObject) {
                builder.withMetadata(jsonObjectToMap((JSONObject) meta));
            }
        }
        requirementChecker.checkRequirements(data);
    }

    @VisibleForTesting
    Map<String, String> jsonObjectToMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap<>();
        Iterator<String> metadataIterator = object.keys();
        while (metadataIterator.hasNext()) {
            String key = metadataIterator.next();
            Object value = object.get(key);
            if (value instanceof String) {
                map.put(key, (String) value);
            }
        }
        return map;
    }

    @VisibleForTesting
    boolean isNonEmpty(@Nullable String value) {
        return value != null && value.trim().length() > 0 && !value.trim().toLowerCase().equals("null");
    }

}
