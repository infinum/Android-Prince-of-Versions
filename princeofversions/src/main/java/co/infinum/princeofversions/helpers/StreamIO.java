package co.infinum.princeofversions.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Utility class for reading from input stream into string.
 */
public class StreamIO {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    /**
     * Interface provides method for filtering based on given line.
     */
    public interface StreamLineFilter {
        /**
         * Filter action, eq. result from filtering
         */
        enum Command {
            /**
             * Stop action, stop reading and returns concatenated string until stop moment.
             */
            STOP,
            /**
             * Skip action, not including current line in result and continue reading.
             */
            SKIP,
            /**
             * No action, include current line in result and continue reading.
             */
            GO
        }

        /**
         * Method computes action based on given line.
         *
         * @param line Currently read line form stream.
         * @return Action which will be taken.
         */
        Command apply(String line);
    }

    private StreamIO() {

    }

    /**
     * Transform input stream into string using UTF-8 charset.
     *
     * @param is Input stream.
     * @return String read from stream.
     * @throws IOException if reading error occurred.
     */
    public static String toString(InputStream is) throws IOException {
        return toString(is, DEFAULT_CHARSET, new StreamLineFilter() {
            @Override
            public Command apply(String line) {
                return Command.GO;
            }
        });
    }

    /**
     * Transforming input stream into string using given charset.
     *
     * @param is      Input stream.
     * @param charset Charset used while reading stream.
     * @return String read from stream.
     * @throws IOException if reading error occurred.
     */
    public static String toString(InputStream is, Charset charset) throws IOException {
        return toString(is, charset, new StreamLineFilter() {
            @Override
            public Command apply(String line) {
                return Command.GO;
            }
        });
    }

    /**
     * Transforming input stream into string using filter on reading.
     *
     * @param is     Input stream for reading.
     * @param filter filters every line read from stream.
     * @return String read from stream and accepted by filter.
     * @throws IOException if reading error occurred.
     */
    public static String toString(InputStream is, StreamLineFilter filter) throws IOException {
        return toString(is, DEFAULT_CHARSET, new StreamLineFilter() {
            @Override
            public Command apply(String line) {
                return Command.GO;
            }
        });
    }

    /**
     * Transforming input stream into string using given charset and filter on reading.
     *
     * @param is      Input stream for reading.
     * @param charset Charset used while reading stream.
     * @param filter  filters every line read from stream.
     * @return String read from stream and accepted by filter.
     * @throws IOException if reading error occurred.
     */
    public static String toString(InputStream is, Charset charset, StreamLineFilter filter) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
        StringBuilder out = new StringBuilder();
        String line;
        StreamLineFilter.Command command;
        while ((line = reader.readLine()) != null && (command = filter.apply(line)) != StreamLineFilter.Command.STOP) {
            if (command != StreamLineFilter.Command.SKIP) {
                out.append(line);
            }
        }
        try {
            reader.close();
        } catch (Exception ignorable) {
            // ignorable exception
        }
        return out.toString();
    }
}
