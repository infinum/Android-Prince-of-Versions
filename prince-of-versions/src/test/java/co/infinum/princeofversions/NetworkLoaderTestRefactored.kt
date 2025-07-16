package co.infinum.princeofversions

import co.infinum.princeofversions.util.ResourceUtils
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException
import java.net.SocketTimeoutException

@RunWith(MockitoJUnitRunner::class)
class NetworkLoaderTestRefactored {

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun cleanup() {
        mockWebServer.shutdown()
    }

    @Test
    fun networkNormalTest() {
        val filename = "valid_update_full.json"
        val responseBody = ResourceUtils.readFromFile(filename)
        val response = MockResponse().setBody(responseBody).setResponseCode(200)
        mockWebServer.enqueue(response)

        val networkLoader: Loader = NetworkLoader(mockWebServer.url("/").toString())
        val content = networkLoader.load()

        assertJsonEquals(content, responseBody)
    }

    @Test
    fun networkTimeoutTest() {
        val networkLoader: Loader = NetworkLoader(mockWebServer.url("/").toString(), 1)
        assertThatThrownBy { networkLoader.load() }
            .isInstanceOf(SocketTimeoutException::class.java)
    }

    @Test
    fun networkJsonMalformedTest() {
        val filename = "malformed_json.json"
        val responseBody = ResourceUtils.readFromFile(filename)
        val response = MockResponse().setBody(responseBody).setResponseCode(200)
        mockWebServer.enqueue(response)

        val networkLoader: Loader = NetworkLoader(mockWebServer.url("/").toString())
        val content = networkLoader.load()

        assertJsonEquals(content, responseBody)
    }

    @Test
    fun networkResponseMissingTest() {
        val response = MockResponse().setResponseCode(200) // No body
        mockWebServer.enqueue(response)

        val networkLoader: Loader = NetworkLoader(mockWebServer.url("/").toString())
        val content = networkLoader.load()

        assertJsonEquals(content, "")
    }

    @Test
    fun networkErrorResponseTest() {
        val response = MockResponse().setResponseCode(404)
        mockWebServer.enqueue(response)

        val networkLoader: Loader = NetworkLoader(mockWebServer.url("/").toString())
        assertThatThrownBy { networkLoader.load() }
            .isInstanceOf(IOException::class.java)
    }

    private fun assertJsonEquals(actual: String, expected: String) {
        assertThat(actual.replace("\n", "")).isEqualTo(expected.replace("\n", ""))
    }
}
