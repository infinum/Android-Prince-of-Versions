package co.infinum.princeofversions

import java.io.IOException
import java.io.InputStream

/**
 * Represents a concrete loader that loads a resource from an [InputStream].
 *
 * @param inputStream The input stream to load the resource from.
 */
class StreamLoader(private val inputStream: InputStream) : Loader {

    @Throws(IOException::class)
    override fun load(): String = StreamIo.toString(inputStream)
}
