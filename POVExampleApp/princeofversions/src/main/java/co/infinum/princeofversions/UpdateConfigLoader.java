package co.infinum.princeofversions;

import java.io.IOException;

public interface UpdateConfigLoader {

    String load() throws IOException, InterruptedException;

    void cancel();

    void validate() throws LoaderValidationException;

}
