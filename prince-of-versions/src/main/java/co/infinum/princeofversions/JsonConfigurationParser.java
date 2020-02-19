package co.infinum.princeofversions;

import android.support.annotation.VisibleForTesting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    private static final String ANDROID_FALLBACK_KEY = "android";

    /**
     * Android2 key
     */
    private static final String ANDROID_KEY = "android2";

    /**
     * Minimum version key
     */
    private static final String MINIMUM_VERSION = "required_version";

    /**
     * Latest version key
     */
    private static final String LATEST_VERSION = "last_version_available";

    /**
     * Notification type key
     */
    private static final String NOTIFICATION = "notify_last_version_frequency";

    /**
     * Metadata key
     */
    private static final String META = "meta";

    private static final String NOTIFICATION_ALWAYS = "always";

    private static final String REQUIREMENTS = "requirements";

    private RequirementChecker requirementChecker;

    JsonConfigurationParser(RequirementChecker requirementChecker) {
        this.requirementChecker = requirementChecker;
    }

    @Override
    public PrinceOfVersionsConfig parse(String content) throws Throwable {
        JSONObject data = new JSONObject(content);
        PrinceOfVersionsConfig.Builder builder = new PrinceOfVersionsConfig.Builder();
        parseToBuilder(data, builder);
        return builder.build();
    }

    private void parseToBuilder(JSONObject data, PrinceOfVersionsConfig.Builder builder) throws JSONException {
        Object meta = null;
        if (data.has(META)) {
            meta = data.get(META);
            if (meta instanceof JSONObject) {
                builder.withMetadata(jsonObjectToMap((JSONObject) meta));
            }
        }
        if (data.has(ANDROID_KEY)) {
            handleAndroidJsonUpdate(data, builder, meta, ANDROID_KEY);
        } else if (data.has(ANDROID_FALLBACK_KEY)) {
            handleAndroidJsonUpdate(data, builder, meta, ANDROID_FALLBACK_KEY);
        } else {
            throw new IllegalStateException("Config resource does not contain android key");
        }
    }

    private void handleAndroidJsonUpdate(JSONObject data, PrinceOfVersionsConfig.Builder builder, Object meta, String androidKey) throws
        JSONException {
        Object json = data.get(androidKey);
        if (json instanceof JSONArray) {
            JSONArray android = data.getJSONArray(androidKey);
            for (int i = 0; i < android.length(); i++) {
                JSONObject update = android.getJSONObject(i);
                if (parseJsonUpdate(update, builder)) {
                    return; //return after finding the first feasible update
                }
            }
            if (android.length() > 0) {
                throw new RequirementsNotSatisfiedException(jsonObjectToMap((JSONObject) meta));
            } else {
                throw new IllegalArgumentException("JSON doesn't contain any feasible update. Check JSON update format!");
            }
        } else if (json instanceof JSONObject) {
            if (!parseJsonUpdate(data.getJSONObject(androidKey), builder)) {
                throw new RequirementsNotSatisfiedException(jsonObjectToMap((JSONObject) meta));
            }
        }
    }

    private void mergeUpdateMetaWithDefaultMeta(JSONObject update, PrinceOfVersionsConfig.Builder builder) throws
        JSONException {
        Object updateMeta;
        if (update.has(META)) {
            updateMeta = update.get(META);
            if (updateMeta instanceof JSONObject) {
                builder.withMetadata(jsonObjectToMap((JSONObject) updateMeta));
            }
        }
    }

    private void saveFirstAcceptableUpdate(JSONObject update, PrinceOfVersionsConfig.Builder builder) throws JSONException {
        if (update.has(MINIMUM_VERSION)) {
            Object min = update.get(MINIMUM_VERSION);
            if (min instanceof Integer) {
                builder.withMandatoryVersion((Integer) min);
            } else {
                throw new IllegalArgumentException("In update configuration " + MINIMUM_VERSION + " it should be int, but the actual "
                    + "value is "
                    + update.get(MINIMUM_VERSION).toString());
            }
        }
        if (update.has(LATEST_VERSION)) {
            Object latest = update.get(LATEST_VERSION);
            if (latest instanceof Integer) {
                builder.withOptionalVersion((Integer) latest);
            } else {
                throw new IllegalArgumentException("In update configuration " + LATEST_VERSION + " it should be int, but the actual "
                    + "value is "
                    + update.get(LATEST_VERSION).toString());
            }
        }
        if (update.has(NOTIFICATION)) {
            Object notification = update.get(NOTIFICATION);
            if (notification instanceof String) {
                builder.withOptionalNotificationType(
                    ((String) notification).equalsIgnoreCase(NOTIFICATION_ALWAYS) ? NotificationType.ALWAYS
                        : NotificationType.ONCE);
            } else {
                throw new IllegalArgumentException("In update configuration " + NOTIFICATION + " it should be String, but the actual "
                    + "value is "
                    + update.get(NOTIFICATION).toString());
            }
        }
    }

    private boolean parseJsonUpdate(JSONObject update, PrinceOfVersionsConfig.Builder builder) throws
        JSONException {
        if (update.has(REQUIREMENTS)) {
            JSONObject requirements = update.getJSONObject(REQUIREMENTS);
            if (requirementChecker.checkRequirements(requirements)) {
                saveFirstAcceptableUpdate(update, builder);
                mergeUpdateMetaWithDefaultMeta(update, builder);
                return true;
            }
            return false;
        } else {
            saveFirstAcceptableUpdate(update, builder);
            mergeUpdateMetaWithDefaultMeta(update, builder);
            return true;
        }
    }

    @VisibleForTesting
    Map<String, String> jsonObjectToMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap<>();
        Iterator<String> metadataIterator = object.keys();
        while (metadataIterator.hasNext()) {
            String key = metadataIterator.next();
            Object value = object.get(key);
            map.put(key, String.valueOf(value));
        }
        return map;
    }
}
