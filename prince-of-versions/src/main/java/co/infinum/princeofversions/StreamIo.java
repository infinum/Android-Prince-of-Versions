package co.infinum.princeofversions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Utility class for reading from input stream into string.
 */
class StreamIo {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private StreamIo() {

    }

    /**
     * Transform input stream into string using UTF-8 charset.
     *
     * @param is Input stream.
     * @return String read from stream.
     * @throws IOException if reading error occurred.
     */
    static String toString(InputStream is) throws IOException {
        return toString(is, DEFAULT_CHARSET);
    }

    /**
     * Transforming input stream into string using given charset.
     *
     * @param is      Input stream.
     * @param charset Charset used while reading stream.
     * @return String read from stream.
     * @throws IOException if reading error occurred.
     */
    static String toString(InputStream is, Charset charset) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        try {
            reader.close();
        } catch (Exception ignorable) { // NOPMD
            // ignorable exception
        }
        return out.toString();
    }
}
