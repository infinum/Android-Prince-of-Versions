package com.infinum.queenofversions.mocks;

import com.infinum.queenofversions.util.ResourceUtils;

import co.infinum.princeofversions.Loader;

public class ResourceFileLoader implements Loader {

    private String filename;

    public ResourceFileLoader(String filename) {
        this.filename = filename;
    }

    @Override
    public String load() throws Throwable {
        return ResourceUtils.readFromFile(filename);
    }
}
