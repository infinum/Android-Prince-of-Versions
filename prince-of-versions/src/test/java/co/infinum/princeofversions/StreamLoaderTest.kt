package co.infinum.princeofversions

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

@RunWith(MockitoJUnitRunner::class)
class StreamLoaderTest {

    @Test
    fun testLoadReturnsStringFromStream() {
        val text = "StreamLoader test"
        val input = ByteArrayInputStream(text.toByteArray())
        val loader: Loader = StreamLoader(input)
        val result = loader.load()
        assertThat(result).isEqualTo(text)
    }

    @Test
    fun testLoadThrowsIOExceptionOnBrokenStream() {
        val input: InputStream = object : InputStream() {
            override fun read(): Int {
                throw IOException("Simulated read error")
            }
        }
        val loader: Loader = StreamLoader(input)
        assertThatThrownBy { loader.load() }
            .isInstanceOf(IOException::class.java)
    }
}
