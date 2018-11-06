package co.infinum.princeofversions;

import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Represents a concrete loader that load resource from network using provided URL.
 */
public class NetworkLoader implements Loader {

    /**
     * Default request timeout in seconds.
     */
    private static final int DEFAULT_NETWORK_TIMEOUT_SECONDS = 60;

    private static final int MILLISECONDS_IN_SECOND = 1000;

    /**
     * Url representing the resource.
     */
    private String url;

    /**
     * Custom network timeout in seconds.
     */
    private int networkTimeoutMilliseconds = DEFAULT_NETWORK_TIMEOUT_SECONDS * MILLISECONDS_IN_SECOND;

    /**
     * Basic authentication username.
     */
    private String username;

    /**
     * Basic authentication password.
     */
    private String password;

    /**
     * Creates a new network loader using provided url.
     *
     * @param url Resource locator.
     */
    public NetworkLoader(String url) {
        this(url, DEFAULT_NETWORK_TIMEOUT_SECONDS);
    }

    /**
     * Creates a new network loader using url and custom network timeout.
     *
     * @param url                   Resource locator.
     * @param networkTimeoutSeconds Custom network timeout.
     */
    public NetworkLoader(String url, int networkTimeoutSeconds) {
        this(url, null, null, networkTimeoutSeconds);
    }

    /**
     * Creates a new network loader using url, default network timeout and basic authentication parameters.
     *
     * @param url      Resource locator.
     * @param username Basic authentication username.
     * @param password Basic authentication password.
     */
    public NetworkLoader(String url, String username, String password) {
        this(url, username, password, DEFAULT_NETWORK_TIMEOUT_SECONDS);
    }

    /**
     * Creates a new network loader using url, custom network timeout and basic authentication parameters.
     *
     * @param url                   Resource locator.
     * @param username              Basic authentication username.
     * @param password              Basic authentication password.
     * @param networkTimeoutSeconds Custom network timeout.
     */
    public NetworkLoader(String url, String username, String password, int networkTimeoutSeconds) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.networkTimeoutMilliseconds = networkTimeoutSeconds * MILLISECONDS_IN_SECOND;
    }

    @Override
    public String load() throws IOException {

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        try {
            if (username != null && password != null) {
                String credentials = username + ":" + password;
                String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(Charset.forName("UTF-8")), Base64.NO_WRAP);
                conn.setRequestProperty("Authorization", basicAuth);
            }
            conn.setConnectTimeout(networkTimeoutMilliseconds);
            conn.setReadTimeout(networkTimeoutMilliseconds);
            InputStream response = conn.getInputStream();
            return StreamIo.toString(response);
        } finally {
            close(conn);
        }
    }

    /**
     * Closing http connection.
     *
     * @param conn Http connection.
     */
    protected void close(HttpURLConnection conn) {
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception ignorable) { // NOPMD
                // ignorable exception
            }
        }
    }
}
