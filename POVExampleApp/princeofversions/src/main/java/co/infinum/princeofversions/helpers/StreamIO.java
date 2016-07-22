package co.infinum.princeofversions.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamIO {

    public static String toString(InputStream is) throws IOException {
        return toString(is, new StreamFilter() {
            @Override
            public Command apply(String line) {
                return Command.GO;
            }
        });
    }

    public static String toString(InputStream is, StreamFilter filter) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder out = new StringBuilder();
        String line;
        StreamFilter.Command command;
        while ((line = reader.readLine()) != null && (command = filter.apply(line)) != StreamFilter.Command.STOP) {
            if (command != StreamFilter.Command.SKIP) {
                out.append(line);
            }
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception ignorable) {}
        }
        return out.toString();
    }

    public interface StreamFilter {
        enum Command {
            STOP,
            SKIP,
            GO
        }
        Command apply(String line);
    }



}
