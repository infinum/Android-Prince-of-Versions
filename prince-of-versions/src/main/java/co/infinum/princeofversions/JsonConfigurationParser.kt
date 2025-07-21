package co.infinum.princeofversions

import androidx.annotation.VisibleForTesting
import org.json.JSONArray
import org.json.JSONObject

/**
 * This class represents a parser for update configurations in JSON format, using the org.json library.
 *
 * It parses the JSON content and creates a [PrinceOfVersionsConfig] instance.
 */
internal class JsonConfigurationParser(
    private val requirementsProcessor: PrinceOfVersionsRequirementsProcessor
) : ConfigurationParser {

    private companion object {
        // JSON Keys
        private const val ANDROID_FALLBACK_KEY = "android"
        private const val ANDROID_KEY = "android2"
        private const val MINIMUM_VERSION = "required_version"
        private const val LATEST_VERSION = "last_version_available"
        private const val NOTIFICATION = "notify_last_version_frequency"
        private const val META = "meta"
        private const val REQUIREMENTS = "requirements"

        // Notification Values
        private const val NOTIFICATION_ALWAYS = "always"
    }

    @Throws(Throwable::class)
    override fun parse(value: String): PrinceOfVersionsConfig {
        val data = JSONObject(value)
        val builder = PrinceOfVersionsConfig.Builder()
        parseRoot(data, builder)
        return builder.build()
    }

    private fun parseRoot(data: JSONObject, builder: PrinceOfVersionsConfig.Builder) {
        val meta = data.optJSONObject(META)
        meta?.let { builder.withMetadata(jsonObjectToMap(it)) }

        val androidKey = when {
            data.has(ANDROID_KEY) -> ANDROID_KEY
            data.has(ANDROID_FALLBACK_KEY) -> ANDROID_FALLBACK_KEY
            else -> throw IllegalStateException("Config resource does not contain android key")
        }

        when (val androidData = data.get(androidKey)) {
            is JSONArray -> handleAndroidJsonArray(androidData, builder, meta)
            is JSONObject -> handleAndroidJsonObject(androidData, builder, meta)
        }
    }

    private fun handleAndroidJsonArray(
        android: JSONArray,
        builder: PrinceOfVersionsConfig.Builder,
        meta: JSONObject?
    ) {
        for (i in 0 until android.length()) {
            val update = android.getJSONObject(i)
            if (parseJsonUpdate(update, builder)) {
                return // Found the first feasible update
            }
        }
        // If loop finishes, no feasible update was found.
        if (android.length() > 0) {
            throw RequirementsNotSatisfiedException(jsonObjectToMap(meta))
        } else {
            throw IllegalArgumentException("JSON doesn't contain any feasible update. Check JSON update format!")
        }
    }

    private fun handleAndroidJsonObject(
        android: JSONObject,
        builder: PrinceOfVersionsConfig.Builder,
        meta: JSONObject?
    ) {
        if (!parseJsonUpdate(android, builder)) {
            throw RequirementsNotSatisfiedException(jsonObjectToMap(meta))
        }
    }

    private fun parseJsonUpdate(update: JSONObject, builder: PrinceOfVersionsConfig.Builder): Boolean {
        val requirementsJson = update.optJSONObject(REQUIREMENTS)
        val requirements = requirementsJson?.let { parseRequirements(it) }

        if (requirements != null && !requirementsProcessor.areRequirementsSatisfied(requirements)) {
            return false // Requirements not met
        }

        saveUpdateValues(update, builder)
        mergeUpdateMetadata(update, builder)
        requirements?.let { builder.withRequirements(it) }
        return true
    }

    private fun saveUpdateValues(update: JSONObject, builder: PrinceOfVersionsConfig.Builder) {
        if (!update.isNull(MINIMUM_VERSION)) {
            val value = update.get(MINIMUM_VERSION)
            if (value is Int) {
                builder.withMandatoryVersion(value)
            } else {
                throw IllegalArgumentException("In update configuration $MINIMUM_VERSION it should be int, but the actual value is $value")
            }
        }
        if (!update.isNull(LATEST_VERSION)) {
            val value = update.get(LATEST_VERSION)
            if (value is Int) {
                builder.withOptionalVersion(value)
            } else {
                throw IllegalArgumentException("In update configuration $LATEST_VERSION it should be int, but the actual value is $value")
            }
        }
        if (!update.isNull(NOTIFICATION)) {
            val value = update.get(NOTIFICATION)
            if (value is String) {
                val notificationType = if (value.equals(NOTIFICATION_ALWAYS, ignoreCase = true)) {
                    NotificationType.ALWAYS
                } else {
                    NotificationType.ONCE
                }
                builder.withOptionalNotificationType(notificationType)
            } else {
                throw IllegalArgumentException("In update configuration $NOTIFICATION it should be String, but the actual value is $value")
            }
        }
    }

    private fun mergeUpdateMetadata(update: JSONObject, builder: PrinceOfVersionsConfig.Builder) {
        update.optJSONObject(META)?.let {
            builder.withMetadata(jsonObjectToMap(it))
        }
    }

    private fun parseRequirements(requirementsJson: JSONObject): Map<String, String> {
        val requirements = mutableMapOf<String, String>()
        for (key in requirementsJson.keys()) {
            if (!requirementsJson.isNull(key)) {
                requirements[key] = requirementsJson.get(key).toString()
            }
        }
        return requirements
    }

    @VisibleForTesting
    internal fun jsonObjectToMap(jsonObject: JSONObject?): Map<String, String?> {
        val map = mutableMapOf<String, String?>()
        if (jsonObject == null) return map
        for (key in jsonObject.keys()) {
            map[key] = if (jsonObject.isNull(key)) null else jsonObject.get(key).toString()
        }
        return map
    }
}
