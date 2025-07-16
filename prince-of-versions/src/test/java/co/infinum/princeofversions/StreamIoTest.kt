package co.infinum.princeofversions

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

@RunWith(MockitoJUnitRunner::class)
class StreamIoTest {

    @Test
    fun testToStringReadsInputStreamWithDefaultCharset() {
        val text = "Hello, world!"
        val input = ByteArrayInputStream(text.toByteArray(Charset.forName("UTF-8")))
        val result = StreamIo.toString(input)
        assertThat(result).isEqualTo(text)
    }

    @Test
    fun testToStringReadsInputStreamWithCustomCharset() {
        val text = "Čevapčići"
        val charset = Charset.forName("UTF-8")
        val input = ByteArrayInputStream(text.toByteArray(charset))
        val result = StreamIo.toString(input, charset)
        assertThat(result).isEqualTo(text)
    }

    @Test
    fun testToStringThrowsIOExceptionOnBrokenStream() {
        val input: InputStream = object : InputStream() {
            override fun read(): Int {
                throw IOException("Simulated read error")
            }
        }
        assertThatThrownBy { StreamIo.toString(input) }
            .isInstanceOf(IOException::class.java)
    }

    @Test
    fun testToStringWithEmptyStream() {
        val input = ByteArrayInputStream("".toByteArray())
        val result = StreamIo.toString(input)
        assertThat(result).isEmpty()
    }

    @Test
    fun testToStringWithOnlyNewlines() {
        val input = ByteArrayInputStream("\n\r\n\n".toByteArray())
        val result = StreamIo.toString(input)
        assertThat(result).isEmpty()
    }

    @Test
    fun testToStringIgnoresCloseException() {
        val text = "test"
        val input: InputStream = object : ByteArrayInputStream(text.toByteArray()) {
            override fun close() {
                throw IOException("Simulated close error")
            }
        }
        // Should not throw an exception, as it's caught and ignored
        val result = StreamIo.toString(input)
        assertThat(result).isEqualTo(text)
    }
}