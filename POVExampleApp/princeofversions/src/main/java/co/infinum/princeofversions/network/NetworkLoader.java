package co.infinum.princeofversions.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.exceptions.UrlNotSetException;
import co.infinum.princeofversions.helpers.StreamIO;

public class NetworkLoader implements UpdateConfigLoader {

    public static final int DEFAULT_NETWORK_TIMEOUT_SECONDS = 60;

    private String url;
    private int networkTimeoutSeconds;
    private volatile boolean cancelled = false;

    public NetworkLoader(String url) {
        this(url, DEFAULT_NETWORK_TIMEOUT_SECONDS);
    }

    public NetworkLoader(String url, int networkTimeoutSeconds) {
        this.url = url;
        this.networkTimeoutSeconds = networkTimeoutSeconds;
    }

    @Override
    public String load() throws IOException, InterruptedException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(networkTimeoutSeconds * 1000);
        InputStream response = conn.getInputStream();
        ifTaskIsCancelledThrowInterrupt(); // if cancelled here no need to read stream at all
        String content = StreamIO.toString(response, new StreamIO.StreamFilter() {
            @Override
            public Command apply(String line) {
                if (cancelled) {
                    // if cancelled here no need to read anymore
                    return Command.STOP;
                } else {
                    return Command.GO;
                }
            }
        });
        return content;
    }

    private void ifTaskIsCancelledThrowInterrupt() throws InterruptedException {
        if (cancelled) {
            throw new InterruptedException();
        }
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    @Override
    public void validate() throws ValidationException {
        if (url == null) {
            throw new UrlNotSetException("Url is not set.");
        }
    }
}
