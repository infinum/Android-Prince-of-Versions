package co.infinum.princeofversions.loaders;

import java.io.IOException;

import co.infinum.princeofversions.BaseLoader;
import co.infinum.princeofversions.exceptions.LoaderValidationException;
import co.infinum.princeofversions.util.ResourceUtils;

public class ResourceFileLoader extends BaseLoader {

    private String filename;

    public ResourceFileLoader(String filename) {
        this.filename = filename;
    }

    @Override
    public String load() throws IOException, InterruptedException {
        return ResourceUtils.readFromFile(filename);
    }

    @Override
    public void validate() throws LoaderValidationException {

    }
}
