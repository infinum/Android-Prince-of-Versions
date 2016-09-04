package co.infinum.princeofversions.loaders;

import java.io.IOException;
import java.io.InputStream;

import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.exceptions.LoaderValidationException;
import co.infinum.princeofversions.helpers.StreamIo;

/**
 * Represents a concrete loader that load resource from stream.
 */
public class StreamLoader implements UpdateConfigLoader {

    /**
     * Used input stream.
     */
    private InputStream is;

    /**
     * Cancellation flag.
     */
    private volatile boolean cancelled = false;

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
        String content = StreamIo.toString(is, new StreamIo.StreamLineFilter() {
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

    @Override
    public void cancel() {
        cancelled = true;
    }

    @Override
    public void validate() throws LoaderValidationException {
        if (is == null) {
            throw new LoaderValidationException("Input stream is null");
        }
    }
}
