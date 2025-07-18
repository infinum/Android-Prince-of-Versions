package co.infinum.princeofversions

import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Utility object for reading from an InputStream into a String.
 */
internal object StreamIo {

    private val DEFAULT_CHARSET: Charset = Charsets.UTF_8

    /**
     * Transforms an InputStream into a String, concatenating all lines.
     * Any exception thrown while closing the stream will be ignored.
     *
     * @param inputStream The InputStream to read from.
     * @param charset The charset to use for decoding. Defaults to UTF-8.
     * @return The String read from the stream.
     * @throws IOException if a reading error occurs.
     */
    @JvmStatic
    @JvmOverloads
    @Throws(IOException::class)
    fun toString(inputStream: InputStream, charset: Charset = DEFAULT_CHARSET): String {
        val reader = inputStream.bufferedReader(charset)
        val out = StringBuilder()
        try {
            var line = reader.readLine()
            while (line != null) {
                out.append(line)
                line = reader.readLine()
            }
        } finally {
            try {
                reader.close()
            } catch (ignorable: IOException) {
                // This exception is intentionally ignored to match the original Java implementation.
            }
        }
        return out.toString()
    }
}
