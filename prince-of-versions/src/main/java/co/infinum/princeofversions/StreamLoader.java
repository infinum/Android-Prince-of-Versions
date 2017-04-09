package co.infinum.princeofversions;

import java.io.IOException;
import java.io.InputStream;

import co.infinum.princeofversions.helpers.StreamIo;

/**
 * Represents a concrete loader that load resource from stream.
 */
public class StreamLoader implements Loader {

    /**
     * Used input stream.
     */
    private InputStream is;

    /**
     * Creates a new stream loader using provided input stream.
     *
     * @param is Input stream.
     */
    public StreamLoader(InputStream is) {
        this.is = is;
    }

    @Override
    public String load() throws IOException, InterruptedException {
        return StreamIo.toString(is);
    }
}
