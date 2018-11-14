package co.infinum.princeofversions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.SocketTimeoutException;

import co.infinum.princeofversions.util.ResourceUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class NetworkLoaderTest {

    private MockWebServer mockWebServer;

    @Before
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @Test
    public void networkNormalTest() throws Throwable {
        String filename = "valid_update_full.json";
        MockResponse response = new MockResponse().setBody(ResourceUtils.readFromFile(filename)).setResponseCode(200);

        mockWebServer.enqueue(response);

        Loader networkLoader = new NetworkLoader(mockWebServer.url("/").toString());

        String content = networkLoader.load();

        assertJsonEquals(content, ResourceUtils.readFromFile(filename));
    }

    @Test(expected = SocketTimeoutException.class)
    public void networkTimeoutTest() throws Throwable {
        Loader networkLoader = new NetworkLoader(mockWebServer.url("/").toString(), 1);

        networkLoader.load();
    }

    @Test
    public void networkJsonMalformedTest() throws Throwable {
        String filename = "malformed_json.json";
        MockResponse response = new MockResponse().setBody(ResourceUtils.readFromFile(filename)).setResponseCode(200);

        mockWebServer.enqueue(response);

        Loader networkLoader = new NetworkLoader(mockWebServer.url("/").toString());

        String content = networkLoader.load();

        assertJsonEquals(content, ResourceUtils.readFromFile(filename));
    }

    @Test
    public void networkResponseMissingTest() throws Throwable {
        MockResponse response = new MockResponse().setResponseCode(200);

        mockWebServer.enqueue(response);

        Loader networkLoader = new NetworkLoader(mockWebServer.url("/").toString());

        String content = networkLoader.load();

        assertJsonEquals(content, "");
    }

    @After
    public void cleanup() {
        try {
            mockWebServer.shutdown();
        } catch (Exception ignored) {

        }
    }

    private static void assertJsonEquals(String actual, String expected) {
        assertThat(actual.replace("\n", "")).isEqualTo(expected.replace("\n", ""));
    }
}
