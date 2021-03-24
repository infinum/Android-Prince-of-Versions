package co.infinum.princeofversions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

import androidx.annotation.VisibleForTesting;

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

    private final PrinceOfVersionsRequirementsProcessor requirementsProcessor;

    JsonConfigurationParser(PrinceOfVersionsRequirementsProcessor requirementsProcessor) {
        this.requirementsProcessor = requirementsProcessor;
    }

    @Override
    public PrinceOfVersionsConfig parse(String content) throws Throwable {
        JSONObject data = new JSONObject(content);
        PrinceOfVersionsConfig.Builder builder = new PrinceOfVersionsConfig.Builder();
        parseToBuilder(data, builder);
        return builder.build();
    }

    private void parseToBuilder(JSONObject data, PrinceOfVersionsConfig.Builder builder) throws JSONException {
        JSONObject meta = data.optJSONObject(META);
        if (meta != null) {
            builder.withMetadata(jsonObjectToMap(meta));
        }
        if (!data.isNull(ANDROID_KEY)) {
            handleAndroidJsonUpdate(data, builder, meta, ANDROID_KEY);
        } else if (!data.isNull(ANDROID_FALLBACK_KEY)) {
            handleAndroidJsonUpdate(data, builder, meta, ANDROID_FALLBACK_KEY);
        } else {
            throw new IllegalStateException("Config resource does not contain android key");
        }
    }

    private void handleAndroidJsonUpdate(
        JSONObject data,
        PrinceOfVersionsConfig.Builder builder,
        @Nullable JSONObject meta,
        String androidKey
    ) throws JSONException {

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
                throw new RequirementsNotSatisfiedException(jsonObjectToMap(meta));
            } else {
                throw new IllegalArgumentException("JSON doesn't contain any feasible update. Check JSON update format!");
            }
        } else if (json instanceof JSONObject) {
            if (!parseJsonUpdate(data.getJSONObject(androidKey), builder)) {
                throw new RequirementsNotSatisfiedException(jsonObjectToMap(meta));
            }
        }
    }

    private void mergeUpdateMetaWithDefaultMeta(JSONObject update, PrinceOfVersionsConfig.Builder builder) throws JSONException {
        if (update.has(META)) {
            Object updateMeta = update.get(META);
            if (updateMeta instanceof JSONObject) {
                builder.withMetadata(jsonObjectToMap((JSONObject) updateMeta));
            }
        }
    }

    private void saveFirstAcceptableUpdate(JSONObject update, PrinceOfVersionsConfig.Builder builder) throws JSONException {
        if (!update.isNull(MINIMUM_VERSION)) {
            Object minimumVersionJson = update.get(MINIMUM_VERSION);
            if (minimumVersionJson instanceof Integer) {
                builder.withMandatoryVersion((Integer) minimumVersionJson);
            } else {
                throw new IllegalArgumentException("In update configuration " + MINIMUM_VERSION + " it should be int, but the actual "
                    + "value is "
                    + update.get(MINIMUM_VERSION).toString());
            }
        }
        if (!update.isNull(LATEST_VERSION)) {
            Object latestVersionJson = update.get(LATEST_VERSION);
            if (latestVersionJson instanceof Integer) {
                builder.withOptionalVersion((Integer) latestVersionJson);
            } else {
                throw new IllegalArgumentException("In update configuration " + LATEST_VERSION + " it should be int, but the actual "
                    + "value is "
                    + update.get(LATEST_VERSION).toString());
            }
        }
        if (!update.isNull(NOTIFICATION)) {
            Object notificationTypeJson = update.get(NOTIFICATION);
            if (notificationTypeJson instanceof String) {
                builder.withOptionalNotificationType(
                    ((String) notificationTypeJson).equalsIgnoreCase(NOTIFICATION_ALWAYS)
                        ? NotificationType.ALWAYS
                        : NotificationType.ONCE
                );
            } else {
                throw new IllegalArgumentException("In update configuration " + NOTIFICATION + " it should be String, but the actual "
                    + "value is "
                    + update.get(NOTIFICATION).toString());
            }
        }
    }

    private boolean parseJsonUpdate(JSONObject update, PrinceOfVersionsConfig.Builder builder) throws JSONException {
        JSONObject requirementsJson = update.optJSONObject(REQUIREMENTS);
        if (requirementsJson != null) {
            Map<String, String> requirements = parseRequirements(requirementsJson);
            if (requirementsProcessor.areRequirementsSatisfied(requirements)) {
                saveFirstAcceptableUpdate(update, builder);
                mergeUpdateMetaWithDefaultMeta(update, builder);
                builder.withRequirements(requirements);
                return true;
            } else {
                return false;
            }
        } else {
            saveFirstAcceptableUpdate(update, builder);
            mergeUpdateMetaWithDefaultMeta(update, builder);
            return true;
        }
    }

    private Map<String, String> parseRequirements(JSONObject requirementsJson) throws JSONException {
        Map<String, String> requirements = new HashMap<>();
        Iterator<String> it = requirementsJson.keys();
        while (it.hasNext()) {
            String key = it.next();
            Object value = (!requirementsJson.isNull(key)) ? requirementsJson.get(key) : null;
            if (value != null) {
                requirements.put(key, String.valueOf(value));
            }
        }
        return requirements;
    }

    @VisibleForTesting
    Map<String, String> jsonObjectToMap(@Nullable JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap<>();
        if (object != null) {
            Iterator<String> metadataIterator = object.keys();
            while (metadataIterator.hasNext()) {
                String key = metadataIterator.next();
                Object value = (!object.isNull(key)) ? object.get(key) : null;
                if (value == null) {
                    map.put(key, null);
                } else {
                    map.put(key, String.valueOf(value));
                }
            }
        }
        return map;
    }
}
