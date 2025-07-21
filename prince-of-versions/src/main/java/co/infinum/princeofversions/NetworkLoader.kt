package co.infinum.princeofversions

import android.util.Base64
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

/**
 * Represents a concrete loader that loads a resource from the network using a provided URL.
 *
 * @param url The URL representing the resource locator.
 * @param username Optional username for Basic authentication.
 * @param password Optional password for Basic authentication.
 * @param networkTimeoutSeconds The network timeout in seconds.
 */
class NetworkLoader (
    private val url: String,
    private val username: String? = null,
    private val password: String? = null,
    networkTimeoutSeconds: Int = DEFAULT_NETWORK_TIMEOUT_SECONDS
) : Loader {

    companion object {
        /**
         * Default request timeout in seconds.
         */
        private const val DEFAULT_NETWORK_TIMEOUT_SECONDS = 60
        private const val MILLISECONDS_IN_SECOND = 1000
    }

    /**
     * Custom network timeout in milliseconds.
     */
    private val networkTimeoutMilliseconds = networkTimeoutSeconds * MILLISECONDS_IN_SECOND

    @Throws(IOException::class)
    override fun load(): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            // Apply Basic Authentication if credentials are provided
            if (username != null && password != null) {
                val credentials = "$username:$password"
                val basicAuth = "Basic ${Base64.encodeToString(credentials.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)}"
                connection.setRequestProperty("Authorization", basicAuth)
            }

            connection.connectTimeout = networkTimeoutMilliseconds
            connection.readTimeout = networkTimeoutMilliseconds

            // The 'use' block automatically handles closing the input stream
            return connection.inputStream.use { responseStream ->
                StreamIo.toString(responseStream)
            }
        } finally {
            connection.disconnect()
        }
    }
}
