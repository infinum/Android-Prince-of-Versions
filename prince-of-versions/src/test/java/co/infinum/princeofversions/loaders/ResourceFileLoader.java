package co.infinum.princeofversions.loaders;

import java.io.IOException;

import co.infinum.princeofversions.Loader;
import co.infinum.princeofversions.util.ResourceUtils;

public class ResourceFileLoader implements Loader {

    private String filename;

    public ResourceFileLoader(String filename) {
        this.filename = filename;
    }

    @Override
    public String load() throws IOException, InterruptedException {
        return ResourceUtils.readFromFile(filename);
    }
}
